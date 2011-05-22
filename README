# opencsv-clj

A lazy opencsv (http://opencsv.sourceforge.net/) wrapper in Clojure

## Usage

(require '[opencsv-clj.core :as csv])

Get a lazy seq of the lines from a csv file:
    (csv/parse-file "filename")

or directly from a string:
   (csv/parse-string "a,b,c")
   => (["a" "b" "c"])

or from any reader you want, really:
   (csv/parse (MyAwesomeReader.))

These three functions can take three options: 

* :mapped - when true, the function returns a map for each line with the csv's header (first line) as keys (default: false)

      (get (second (csv/parse-string "1,2,3\n2,4,6\n3,6,9" :mapped true)) "2")
      ==> "6"

* :delimiter - the delimiterchar used by the parser (default \,)

    (csv/parse-string "a+b+c")
    => (["a" "b" "c"])

* :quoter - the quote character used by the parser (default \")

    (csv/parse-string "'a,a,a','1,2,3','I,II,III'" :quoter \')
    => (["a,a,a" "1,2,3" "I,II,III"])



## Installation

Leiningen: 

    [opencsv-clj "1.0.0-SNAPSHOT"]

Maven:

    <dependency>
      <groupId>opencsv-clj</groupId>
      <artifactId>opencsv-clj</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>

## License

Copyright (C) 2010 Sidhant Godiwala

Distributed under the Eclipse Public License, the same as Clojure.