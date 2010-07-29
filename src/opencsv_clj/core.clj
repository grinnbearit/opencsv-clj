(ns opencsv-clj.core
  (:import [au.com.bytecode.opencsv CSVReader])
  (:require [clojure.java.io :as io]))

(defn- read-csv [path]
  (let [buffer (CSVReader. (io/reader path))]
    (lazy-seq
     (loop [res []]
       (if-let [nxt (.readNext buffer)]
         (recur (conj res (seq nxt)))
         res)))))

(defn- parse-csv [csv-seq]
  (let [header (first csv-seq)]
    (map #(zipmap header %) (rest csv-seq))))

(defn parse [path]
  (parse-csv (read-csv path)))
