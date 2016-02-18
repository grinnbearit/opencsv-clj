(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala (grinnbearit)"}
  (:refer-clojure :exclude [quote])
  (:require [clojure.java.io :as io])
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter]
           [java.io File Reader StringReader StringWriter Writer]))

(defn- make-reader
  [input]
  (cond 
    (instance? Reader input)
    input

    (instance? File input)
    (io/reader input)

    (string? input)
    (StringReader. input)))

(defn- make-writer
  [output]
  (cond 
    (instance? Writer output)
    output

    (instance? File output)
    (io/writer output)

    (string? output)
    (let [wr (StringWriter.)]
      (.write wr output)
      wr)))

(defmacro opencsv-construct
  [constructor arg options]
  `(let [separator# (:separator ~options)
         quote# (:quote ~options)
         escape# (:escape ~options)]
     (cond
       (and (nil? separator#) (nil? quote#) (nil? escape#))
       (~constructor ~arg)

       (and (nil? quote#) (nil? escape#))
       (~constructor ~arg separator#)

       (or (nil? escape#)
           (and (= \" quote#) (= \" escape#))
           (and (nil? quote#) (= CSVWriter/DEFAULT_QUOTE_CHARACTER escape#)))
       (~constructor ~arg
                     (or separator# CSVWriter/DEFAULT_SEPARATOR)
                     (or quote# CSVWriter/DEFAULT_QUOTE_CHARACTER))

       :else (~constructor ~arg
                           (or separator# CSVWriter/DEFAULT_SEPARATOR)
                           (or quote# CSVWriter/DEFAULT_QUOTE_CHARACTER)
                           escape#))))

(defn read-csv
  "Reads CSV-data from input (String, java.io.Reader, java.io.File) into a lazy
  sequence of vectors."
  [input & {:keys [separator quote escape] :as options}]
  (let [reader (make-reader input) 
        buffer (opencsv-construct CSVReader. reader options)
        read-row-seq (fn read-buffer [buf]
                       (when-let [nxt (.readNext buf)]
                         (lazy-seq (cons (vec nxt) (read-buffer buf)))))]
    (read-row-seq buffer)))

(defn write-csv
  "Writes data to output (String, java.io.Writer, java.io.File) in CSV-format."
  [output data & {:keys [separator quote escape] :as options}]
  (let [writer (make-writer output)
        csv-writer (opencsv-construct CSVWriter. writer options)]
    (doseq [entry (map into-array data)]
      (.writeNext csv-writer entry))
    (.close csv-writer)
    output))
