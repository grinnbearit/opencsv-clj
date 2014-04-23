(ns opencsv-clj.core-test
  (:use midje.sweet
        opencsv-clj.core)
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


(facts
 "reading"
 (let [csv (read-csv simple)]
   (count csv) => 3
   (count (first csv)) => 3
   (first csv) => ["Year" "Make" "Model"]
   (last csv) => ["2000" "Mercury" "Cougar"])
 (let [csv (read-csv simple-alt-sep :separator \;)]
   (count csv) => 3
   (count (first csv)) => 3
   (first csv) => ["Year" "Make" "Model"]
   (last csv) => ["2000" "Mercury" "Cougar"])
 (let [csv (read-csv complicated)]
   (count csv) => 4
   (count (first csv)) => 5
   (first csv) => ["1997" "Ford" "E350" "ac, abs, moon" "3000.00"]
   (last csv) => ["1996" "Jeep" "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded" "4799.00"]))


(facts
 "reading-and-writing"
 (let [string-writer (StringWriter.)]
   (write-csv string-writer (read-csv simple)
              :quote CSVWriter/NO_QUOTE_CHARACTER)
   (str string-writer) => simple))
