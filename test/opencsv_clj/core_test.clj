(ns opencsv-clj.core-test
  (:use [opencsv-clj.core] :reload-all)
  (:use [clojure.test]))

(deftest basic-functionality
  (is (= '(["a" "b" "c"]) (parse-string "a,b,c")))
  (is (= '(["", ""]) (parse-string ",")))
  (is (= '() (parse-string ""))))


(deftest mapping-to-headers
  (is (= "e"
	 (get
	  (first (parse-string "a,b,c\nd,e,f" :mapped true))
	  "b"))))

(deftest quoting
  (is (= '(["a,a", "b,b", "c,c"]) (parse-string "\"a,a\",\"b,b\",\"c,c\""))))

(deftest different-tokens
  (is (= '(["a" "b" "c"] ["d" "e" "f"])
	 (parse-string "'a';'b';'c'\n'd';'e';'f'"
		       :delimiter \;
		       :quoter \'
		       ))))

(deftest all-together-now
  (is (= "awesome"
	 (get
	  (first
	   (parse-string "+bears: dancing+:+bears: attacking+\n+awesome+:+not so awesome+"
			 :mapped true
			 :delimiter \:
			 :quoter \+))
	  "bears: dancing"))))


(deftest reading-large-files
  (is (= ["author" "book"]
         (first (parse-file "test/test.csv")))))

