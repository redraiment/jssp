(ns me.zzp.jssp.file-test
  (:require [me.zzp.jssp.file
             :as file
             :refer [extensions
                     major-extension
                     minor-extension
                     strip-extension]]
            [clojure.test :refer [deftest is are]]))

(deftest extensions-test
  (is (= ["css" "groovy"] (extensions "index.css.groovy")))
  (is (= ["groovy"] (extensions "index.groovy")))
  (is (= [] (extensions ".gitignore")))
  (is (= [] (extensions "Dockerfile"))))

(deftest major-extension-test
  (is (= "groovy" (major-extension "index.css.groovy")))
  (is (= "groovy" (major-extension "index.groovy")))
  (is (nil? (major-extension ".gitignore")))
  (is (nil? (major-extension "Dockerfile"))))

(deftest minor-extension-test
  (is (= "css" (minor-extension "index.css.groovy")))
  (is (nil? (minor-extension "index.css")))
  (is (nil? (minor-extension ".gitignore")))
  (is (nil? (minor-extension "Dockerfile"))))

(deftest strip-extension-test
  (is (= "index.css" (strip-extension "index.css.js")))
  (is (= "index" (strip-extension "index.css")))
  (is (= ".gitignore" (strip-extension ".gitignore")))
  (is (= "Dockerfile" (strip-extension "Dockerfile"))))
