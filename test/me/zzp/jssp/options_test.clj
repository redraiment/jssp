(ns me.zzp.jssp.options-test
  "Test options parser."
  (:require [me.zzp.jssp.options :refer [*global-options* validate]]
            [clojure.test :refer [deftest is are]]
            [clojure.string :as cs]))

(deftest help-test
  (let [{:keys [action payload]} (validate ["-h"])]
    (is (= :exit action))
    (is (cs/starts-with? payload "OVERVIEW"))))

(deftest invaild-patterns-test
  (let [{:keys [action payload]}
        (validate ["--expanding-statement="
                   "--expanding-expression="
                   "--executing-statement="
                   "--executing-expression="
                   "examples/local-mode/hello-world.md.js"])]
    (is (= :exit action))
    (is (= "Failed to validate \"--expanding-statement \": both prefix and suffix of expanding statement must not be empty.
Failed to validate \"--expanding-expression \": both prefix and suffix of expanding expression must not be empty.
Failed to validate \"--executing-statement \": both prefix and suffix of executing statement must not be empty.
Failed to validate \"--executing-expression \": both prefix and suffix of executing expression must not be empty." payload)))

  (let [{:keys [action payload]}
        (validate ["--expanding-statement=A"
                   "--expanding-expression=B"
                   "--executing-statement=C"
                   "--executing-expression=D"
                   "examples/local-mode/hello-world.md.js"])]
    (is (= :exit action))
    (is (= "Failed to validate \"--expanding-statement A\": both prefix and suffix of expanding statement must not be empty.
Failed to validate \"--expanding-expression B\": both prefix and suffix of expanding expression must not be empty.
Failed to validate \"--executing-statement C\": both prefix and suffix of executing statement must not be empty.
Failed to validate \"--executing-expression D\": both prefix and suffix of executing expression must not be empty." payload))))

(deftest default-value-test
  (let [{:keys [action payload]}
        (validate ["examples/local-mode/hello-world.md.js"])

        {:keys [options template]}
        payload]
    (is (= :local action))
    (is (= "examples/local-mode/hello-world.md.js" template))
    (is (= *global-options* options))))

(deftest context-data-test
  (let [{:keys [action payload]}
        (validate ["-c" "{\"skills\": [\"JavaScript\", \"Groovy\", \"JRuby\", \"BeanShell\"]}"
                   "examples/context-data/hello-world.md.js"])

        {:keys [options template]}
        payload]
    (is (= :local action))
    (is (= "examples/context-data/hello-world.md.js" template))
    (is (= {"skills" ["JavaScript" "Groovy" "JRuby" "BeanShell"]}
           (:context options)))))

(deftest trim-test
  (let [{:keys [action payload]}
        (validate ["-t" "false"
                   "examples/local-mode/hello-world.md.js"])]
    (is (= :local action))
    (is (not (get-in payload [:options :trim])))))

(deftest emit-code-test
  (let [{:keys [action payload]}
        (validate ["-x" "examples/local-mode/hello-world.md.js"])]
    (is (= :local action))
    (is (get-in payload [:options :emit-code]))))

(deftest max-expand-times-test
  (let [{:keys [action payload]}
        (validate ["-m" "12"
                   "examples/local-mode/hello-world.md.js"])]
    (is (= :local action))
    (is (= 12 (get-in payload [:options :expand-limit])))))
