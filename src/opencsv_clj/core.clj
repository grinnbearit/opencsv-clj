(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala"
     :doc "This file defines a clojure wrapper for the opencsv library with laziness built in."}
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter])
  (:require [clojure.java.io :as io]))

(defn- read-csv [reader delimiter quoter]
  "Reads a csv file and generates a lazy sequence of rows"
  (let [buffer (CSVReader. reader delimiter quoter)
        read (fn read-buffer [buffer]
               (when-let [nxt (.readNext buffer)]
                 (lazy-seq (cons nxt (read-buffer buffer)))))]
    (read buffer)))

(defn- parse-csv [csv-seq]
  "Converts a lazy sequence of rows to a lazy sequence of maps"
  (let [header (first csv-seq)]
    (map #(zipmap header %) (rest csv-seq))))

(defn parse
  "Converts a csv reader to a lazy sequence of vectors of the values or the values mapped to the header.
    Options:
    :mapped - if true, returns the values of each row mapped to the header(default false)
    :delimiter - the delimiter char used by the parser (default \\,,)
    :quoter  - the quote char used by the parser (default \\\" "
  [reader & {:keys [mapped delimiter quoter]
	     :or {mapped false delimiter \, quoter \"}}]
  (let [csv-seq (read-csv reader delimiter quoter)]
    (if mapped
      (parse-csv csv-seq)
      (map vec csv-seq))))

(defn parse-file
  "Converts a csv file to a lazy sequence of maps of the values where the keys are the items in the header row. Takes the same options as (parse)"
  [f & opts]
  (apply parse (io/reader f) opts))

(defn parse-string
  "Converts a csv string to a lazy sequence of maps of the values where the keys are the items in the header row. Takes the same options as (parse)"
  [s & opts]
  (apply parse (java.io.StringReader. s) opts))

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