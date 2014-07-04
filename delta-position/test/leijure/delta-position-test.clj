(ns leijure.delta-position-test
  (:use [leijure.delta-position]))

;; Trivial tests
(def ts1 "a\nbc defgh ij\n\n") ;; no tab: confuses the LNPR.

(def ts2 "abc\r\ndefg\thi \n\njk\rl") ;; variety of CR/LF

(defn slurp-cl [ts]
  (let [s (clojure.lang.LineNumberingPushbackReader.
           (java.io.StringReader. ts))
        i (fn [s]
            (let [l (.getLineNumber s)
                  c (.getColumnNumber s)
                  x (to-char (.read s))]
              [x l c]))]
    (loop [[x l c] (i s) result []]
      (if x
        (recur (i s) (conj result [x l c]))
        result))))

(defn rmcr [x]
  ;; The LineNumberingPushbackReader normalizes all CR/LF to a single LF
  ;; so we remove them all before comparing positions for the rest.
  (filter #(not (#{\newline \return} (first %))) x))

(defn all-equal
  ;; [& x] (= 1 (.size (into #{} x))) ;; shorter definition, less efficient.
  ([] true)
  ([x & y] (loop [y y] (or (empty? y) (if (= x (first y)) (recur (rest y)) false)))))

(assert
 (all-equal
  (rmcr (positioned-stream ts1 {:line-offset 1 :column-offset 1}))
  (rmcr (slurp-cl ts1))
  '([\a 1 1] [\b 2 1] [\c 2 2] [\space 2 3] [\d 2 4] [\e 2 5] [\f 2 6]
      [\g 2 7] [\h 2 8] [\space 2 9] [\i 2 10] [\j 2 11])))
