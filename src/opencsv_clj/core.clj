(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala"
     :doc "This file defines a clojure wrapper for the opencsv library with laziness built in."}
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter])
  (:require [clojure.java.io :as io]))

(defn- read-csv [path]
  "Reads a csv file and generates a lazy sequence of rows"
  (let [buffer (CSVReader. (io/reader path))]
    (lazy-seq
     (loop [res []]
       (if-let [nxt (.readNext buffer)]
         (recur (conj res (seq nxt)))
         res)))))

(defn- parse-csv [csv-seq]
  "Converts a lazy sequence of rows to a lazy sequence of maps"
  (let [header (first csv-seq)]
    (map #(zipmap header %) (rest csv-seq))))

(defn parse [path]
  "Converts a csv file to a lazy sequence of maps of the values where the keys are the items in the header row"
  (parse-csv (read-csv path)))

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
