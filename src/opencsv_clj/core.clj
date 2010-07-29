(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala"
     :doc "This file defines a clojure wrapper for the opencsv library with laziness built in."}
  (:import [au.com.bytecode.opencsv CSVReader])
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
