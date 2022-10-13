(ns me.zzp.jssp.template-engine-test
  (:require [me.zzp.jssp
             [options :refer [*global-options*]]
             [template-engine :refer [render-file]]]
            [clojure.test :refer [deftest is are]]))

(deftest polyglot-test
  (let [expected "# Languages

* JavaScript
* Groovy
* JRuby
* BeanShell
"]
    (is (= expected (render-file "examples/local-mode/languages.md.js")))
    (is (= expected (render-file "examples/local-mode/languages.md.rb")))
    (is (= expected (render-file "examples/local-mode/languages.md.bsh")))
    (is (= expected (render-file "examples/local-mode/languages.md.groovy")))))

(deftest trimming-test
  (binding [*global-options* (assoc *global-options* :trim false)]
    (is (= "
# Languages


* JavaScript

* Groovy

* JRuby

* BeanShell

" (render-file "examples/local-mode/languages.md.js")))))

(deftest context-data-test
  (binding [*global-options* (assoc *global-options* :context {"languages" ["JavaScript" "Groovy" "JRuby" "BeanShell"]})]
    (is (= "# Languages

* JavaScript
* Groovy
* JRuby
* BeanShell
" (render-file "examples/context-data/languages.md.rb")))))

(deftest embedded-patterns-test
  (binding [*global-options* (assoc-in *global-options* [:patterns :executing]
                                       {:statement {:prefix "<!--%" :suffix "%-->"}
                                        :expression {:prefix "<!--=" :suffix "=-->"}})]
    (is (= "<h1>Languages</h1>
<ul>
  <li>JavaScript</li>
  <li>Groovy</li>
  <li>JRuby</li>
  <li>BeanShell</li>
<ul>
" (render-file "examples/embedded-patterns/languages.html.groovy")))))

(deftest nested-include-test
  (let [css (render-file "examples/nested-include/index.css.groovy")]
    (is (= (* 6 5) (count (re-seq #"h[1-6]\.(?:normal|primary|success|warning|danger)" css))))))
