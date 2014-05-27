(defproject opencsv-clj "2.0.2"
  :description "clojure wrapper for opencsv"
  :license {:name "Eclipse"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "http://github.com/grinnbearit/opencsv-clj"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [net.sf.opencsv/opencsv "2.3"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]
                   :plugins [[lein-midje "3.1.3"]]}})
