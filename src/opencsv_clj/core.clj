(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala (grinnbearit)"}
  (:refer-clojure :exclude [quote])
  (:require [clojure.java.io :as io])
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter]
           [java.io File Reader StringReader]))

(defn- make-reader
  [input]
  (cond 
    (string? input)
    (StringReader. input)

    (instance? Reader input)
    input

    (instance? File input)
    (io/reader input)))

(defn- make-csv-reader
  [reader {:keys [separator quote escape]}]
  (cond
    (and (nil? separator) (nil? quote) (nil? escape))
    (CSVReader. reader)

    (and (nil? quote) (nil? escape))
    (CSVReader. reader separator)

    (or (nil? escape)
        (and (= \" quote) (= \" escape))
        (and (nil? quote) (= CSVWriter/DEFAULT_QUOTE_CHARACTER escape)))
    (CSVReader. reader 
                (or separator CSVWriter/DEFAULT_SEPARATOR)
                (or quote CSVWriter/DEFAULT_QUOTE_CHARACTER))

    :else (CSVReader. reader 
                      (or separator CSVWriter/DEFAULT_SEPARATOR)
                      (or quote CSVWriter/DEFAULT_QUOTE_CHARACTER)
                      escape)))

(defn read-csv
  "Reads CSV-data from input (String or java.io.Reader) into a lazy
  sequence of vectors."
  [input & {:keys [separator quote escape] :as options}]
  (let [buffer (-> input make-reader (make-csv-reader options))
        read-row-seq (fn read-buffer [buf]
                       (when-let [nxt (.readNext buf)]
                         (lazy-seq (cons (vec nxt) (read-buffer buf)))))]
    (read-row-seq buffer)))

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
