# opencsv-clj

A lazy [opencsv](http://opencsv.sourceforge.net/) wrapper in Clojure

## Usage

conforms to the protocol set by [clojure/clojure.data.csv](https://github.com/clojure/data.csv)

    (require '[opencsv-clj.core :as csv])

Get a lazy seq of vectors from a csv file:

    (csv/read-csv (reader "filename"))

or directly from a string:

    (csv/read-csv "a,b,c")
    => (["a" "b" "c"])

or from anything that implements `Read-CSV-From`, really

__options__

:separator - the separator used by the parser (default \,)

    (csv/read-csv "a+b+c" :separator \+)
    => (["a" "b" "c"])

:quote - the quote character used by the parser (default \")

    (csv/read-csv "'a,a,a','1,2,3','I,II,III'" :quote \')
    => (["a,a,a" "1,2,3" "I,II,III"])



## Installation

Leiningen:

    [opencsv-clj "2.0.1"]

Maven:

    <dependency>
      <groupId>opencsv-clj</groupId>
      <artifactId>opencsv-clj</artifactId>
      <version>2.0.1</version>
    </dependency>


## Contributors

* [Robin Ramael](https://github.com/RobinRamael)
* [Zach Tellman](https://github.com/ztellman)
* [Ryan Sundberg](https://github.com/sundbry)

## License

Copyright (C) 2010 Sidhant Godiwala

Distributed under the Eclipse Public License, the same as Clojure.
