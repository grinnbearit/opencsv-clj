(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala"
     :doc "This file defines a clojure wrapper for the opencsv library with laziness built in."}
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter])
  (:require [clojure.java.io :as io]))


(defn- read-csv-from-reader [reader delimiter]
  (let [buffer (if (nil? delimiter)
		 (CSVReader. reader)
		 (CSVReader. reader delimiter))]
    (lazy-seq
     (loop [res []]
       (if-let [nxt (.readNext buffer)]
	 (recur (conj res (seq nxt)))
	 res)))))

(defn- read-csv [path delimiter]
  "Reads a csv file and generates a lazy sequence of rows. If delimiter is nil, the default (\\,) is used."
  (read-csv-from-reader (io/reader path) delimiter))

(defn- read-csv-from-string [str delimiter]
  "Reads a csv string and generates a lazy sequence of rows. If delimiter is nil, the default (\\,) is used."
  (read-csv-from-reader (java.io.StringReader. str) delimiter))


(defn- parse-csv [csv-seq]
  "Converts a lazy sequence of rows to a lazy sequence of maps"
  (let [header (first csv-seq)]
    (map #(zipmap header %) (rest csv-seq))))


(defn parse
  "Converts a csv file to a lazy sequence of maps of the values where the keys are the items in the header row by default.
The delimiter used by the parser can be changed by using the delimiter arg. When map-to-headers? is false, each line is returned represented as a vec."
  ([path delimiter map-to-headers?] (if map-to-headers?
				      (parse-csv (read-csv path delimiter))
				      (map vec (read-csv path delimiter))))
  ([path delimiter] (parse-csv (read-csv path delimiter false)))
  ([path] (parse-csv (read-csv path nil false))))


(defn parse-string
  "Converts a csv string to a lazy sequence of maps of the values where the keys are the items in the header row by default.
The delimiter used by the parser can be changed by using the delimiter arg. When map-to-headers? is false, each line is returned represented as a vec."
  ([path delimiter map-to-headers?] (if map-to-headers?
				      (parse-csv (read-csv-from-string path delimiter))
				      (map vec (read-csv-from-string path delimiter))))
  ([path delimiter] (parse-csv (read-csv-from-string path delimiter false)))
  ([path] (parse-csv (read-csv-from-string path nil false))))


(defn dump
  "Dumps a sequence of maps to a file given by path, pass a header to specify the order and columns to be written"
  ([path csv-seq]
     (let [header (keys (first csv-seq))]
       (dump path csv-seq header)))
  ([path csv-seq header]
     (with-open [writer (io/writer path)]
       (let [csv-writer (CSVWriter. writer)]
         (.. csv-writer  (writeNext (into-array (map str header))))
         (doseq [entry (map  (comp into-array
                                   (fn [csv-entry] (map (comp str #(get csv-entry %))
                                                       header)))
                             csv-seq)]
           (.. csv-writer (writeNext entry)))))))
