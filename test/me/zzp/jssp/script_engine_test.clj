(ns me.zzp.jssp.script-engine-test
  (:require [me.zzp.jssp.script-engine :as script-engine :refer [execute]]
            [clojure.test :refer [deftest is are]])
  (:import java.lang.StringBuffer
           [javax.script
            SimpleScriptContext
            ScriptEngineManager]))

(deftest script-engine-js-test
  (is (= "<ol>
  <li>Hello JavaScript</li>
  <li>Hello JavaScript</li>
  <li>Hello JavaScript</li>
</ol>
"
         (execute (script-engine/of "js")
                  [[:literal "<ol>\n"]
                   [:statement " for (var times = 0; times < 3; times++) {"]
                   [:literal "  <li>Hello "]
                   [:expression " language "]
                   [:literal "</li>\n"]
                   [:statement " } "]
                   [:literal "</ol>\n"]]
                  {"language" "JavaScript"}))))

(deftest script-engine-groovy-test
  (is (= "<ol>
  <li>Hello Groovy</li>
  <li>Hello Groovy</li>
  <li>Hello Groovy</li>
</ol>
"
         (execute (script-engine/of "groovy")
                  [[:literal "<ol>\n"]
                   [:statement "3.times {"]
                   [:literal "  <li>Hello "]
                   [:expression " language "]
                   [:literal "</li>\n"]
                   [:statement "}"]
                   [:literal "</ol>\n"]]
                  {"language" "Groovy"}))))

(deftest script-engine-beanshell-test
  (is (= "<ol>
  <li>Hello BeanShell</li>
  <li>Hello BeanShell</li>
  <li>Hello BeanShell</li>
</ol>
"
         (execute (script-engine/of "bsh")
                  [[:literal "<ol>\n"]
                   [:statement "for (int times = 0; times < 3; times++) {"]
                   [:literal "  <li>Hello "]
                   [:expression " language "]
                   [:literal "</li>\n"]
                   [:statement "}"]
                   [:literal "</ol>\n"]]
                  {"language" "BeanShell"}))))

(deftest script-engine-jruby-test
  (is (= "<ol>
  <li>Hello JRuby</li>
  <li>Hello JRuby</li>
  <li>Hello JRuby</li>
</ol>
"
         (execute (script-engine/of "rb")
                  [[:literal "<ol>\n"]
                   [:statement "3.times do"]
                   [:literal "  <li>Hello "]
                   [:expression " language "]
                   [:literal "</li>\n"]
                   [:statement "end"]
                   [:literal "</ol>\n"]]
                  {"language" "JRuby"}))))
