(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala (grinnbearit)"}
  (:require [clojure.java.io :as io])
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter]
           [java.io File Reader StringReader]))

(defn- opencsv-read [reader sep quote]
  (let [buffer (CSVReader. reader sep quote)
        read (fn read-buffer [buffer]
               (when-let [nxt (.readNext buffer)]
                 (lazy-seq (cons (vec nxt) (read-buffer buffer)))))]
    (read buffer)))

(defn- make-reader
  [input]
  (cond 
    (string? input)
    (StringReader. input)

    (instance? Reader input)
    input

    (instance? File input)
    (io/reader input)))

(defn read-csv
  "Reads CSV-data from input (String or java.io.Reader) into a lazy
  sequence of vectors."
  [input & {:keys [separator quote]
            :or {separator CSVWriter/DEFAULT_SEPARATOR
                 quote CSVWriter/DEFAULT_QUOTE_CHARACTER}
            :as options}]
  (opencsv-read (make-reader input) separator quote))


(defn write-csv
  "Writes data to writer in CSV-format."
  [writer data & {:keys [separator quote]
                  :or {separator CSVWriter/DEFAULT_SEPARATOR
                       quote CSVWriter/DEFAULT_QUOTE_CHARACTER}
                  :as options}]
  (let [csv-writer (CSVWriter. writer separator quote)]
    (doseq [entry (map into-array data)]
      (.writeNext csv-writer entry))
    (.close csv-writer)))
