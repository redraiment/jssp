(ns me.zzp.jssp.options
  "Options")

(def statement-prefix
  (System/getProperty "jssp.statement.prefix" "[!"))

(def statement-suffix
  (System/getProperty "jssp.statement.suffix" "!]"))

(def expression-prefix
  (System/getProperty "jssp.expression.prefix" "[="))

(def expression-suffix
  (System/getProperty "jssp.expression.suffix" "=]"))
