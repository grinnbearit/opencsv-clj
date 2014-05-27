(ns opencsv-clj.core
  #^{:author "Sidhant Godiwala (grinnbearit)"}
  (:import [au.com.bytecode.opencsv CSVReader CSVWriter]
           [java.io StringReader Reader]))


(defn- opencsv-read [reader sep quote]
  (let [buffer (CSVReader. reader sep quote)
        read (fn read-buffer [buffer]
               (when-let [nxt (.readNext buffer)]
                 (lazy-seq (cons (vec nxt) (read-buffer buffer)))))]
    (read buffer)))


;;; https://github.com/clojure/data.csv/blob/master/src/main/clojure/clojure/data/csv.clj#L66
(defprotocol Read-CSV-From
  (read-csv-from [input sep quote]))


(extend-protocol Read-CSV-From
  String
  (read-csv-from [s sep quote]
    (opencsv-read (StringReader. s) sep quote))

  Reader
  (read-csv-from [reader sep quote]
    (opencsv-read reader sep quote)))


(defn read-csv
  "Reads CSV-data from input (String or java.io.Reader) into a lazy
  sequence of vectors."
  [input & {:keys [separator quote]
            :or {separator CSVWriter/DEFAULT_SEPARATOR
                 quote CSVWriter/DEFAULT_QUOTE_CHARACTER}
            :as options}]
  (read-csv-from input separator quote))


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
