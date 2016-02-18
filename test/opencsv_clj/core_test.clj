(ns opencsv-clj.core-test
  (:require [clojure.test :refer :all]
            [opencsv-clj.core :as csv])
  (:import [java.io StringWriter]
           [au.com.bytecode.opencsv CSVWriter]))

(def ^{:private true} simple
  "Year,Make,Model
1997,Ford,E350
2000,Mercury,Cougar
")


(def ^{:private true} simple-alt-sep
  "Year;Make;Model
1997;Ford;E350
2000;Mercury;Cougar
")


(def ^{:private true} complicated
  "1997,Ford,E350,\"ac, abs, moon\",3000.00
1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00
1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",\"\",5000.00
1996,Jeep,Grand Cherokee,\"MUST SELL!
air, moon roof, loaded\",4799.00")


(deftest read-csv
  (testing
    "reading"
    (let [csv (csv/read-csv simple)]
      (is (= 3 (count csv)))
      (is (= 3 (count (first csv))))
      (is (= ["Year" "Make" "Model"] (first csv)))
      (is (= ["2000" "Mercury" "Cougar"] (last csv))))
    (let [csv (csv/read-csv simple-alt-sep :separator \;)]
      (is (= 3 (count csv)))
      (is (= 3 (count (first csv))))
      (is (= ["Year" "Make" "Model"] (first csv)))
      (is (= ["2000" "Mercury" "Cougar"] (last csv))))
    (let [csv (csv/read-csv complicated)]
      (is (= 4 (count csv)))
      (is (= 5 (count (first csv))))
      (is (= ["1997" "Ford" "E350" "ac, abs, moon" "3000.00"] (first csv)))
      (is (= ["1996" "Jeep" "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded" "4799.00"] (last csv))))))

(deftest read-and-write
  (testing
    "reading-and-writing"
    (let [string-writer (StringWriter.)]
      (csv/write-csv string-writer (csv/read-csv simple)
                 :quote CSVWriter/NO_QUOTE_CHARACTER)
      (is (= simple (str string-writer))))))
