(ns me.zzp.jssp.options
  "Global Options: command line arguments parser."
  (:require [clojure.string :as cs]
            [clojure.java.io :as io]
            [clojure.tools.cli :refer [parse-opts]]
            [cheshire.core :refer [parse-string]])
  (:gen-class))

(def ^:dynamic *global-options*
  "Global options:
  * patterns: embedded patterns for expanding & executing.
  * context: context data object"
  nil)

(defn- pattern-parse
  "Parse pattern pair."
  [pattern]
  (let [[prefix suffix] (cs/split pattern #"\s+")]
    {:prefix prefix
     :suffix suffix}))

(defn- pattern-validate
  "Validate pattern pair: prefix & suffix MUST not be blank."
  [{:keys [prefix suffix]}]
  (not (or (cs/blank? prefix)
           (cs/blank? suffix))))

(defn- context-file-parse
  "Parse JSON format context data file."
  [file-name]
  (when (and (or (cs/ends-with? file-name ".json")
                 (cs/ends-with? file-name ".edn"))
             (.exists (io/file file-name)))
    (parse-string (slurp file-name))))

(def ^:private command-line-options
  [["-c" "--context-string JSON" "context data string"
    :default {}
    :parse-fn parse-string]
   ["-f" "--context-file JSON-FILE" "context data file name"
    :parse-fn context-file-parse
    :validate [(comp not nil?) "invalid file content"]]
   [nil "--expanding-statement PATTERN" "expanding statement pattern pair"
    :default {:prefix "@!" :suffix "!@"}
    :default-desc "@! !@"
    :parse-fn pattern-parse
    :validate [pattern-validate "both prefix and suffix must not be empty."]]
   [nil "--expanding-expression PATTERN" "expanding expression pattern pair"
    :default {:prefix "@=" :suffix "=@"}
    :default-desc "@= =@"
    :parse-fn pattern-parse
    :validate [pattern-validate "both prefix and suffix must not be empty."]]
   [nil "--executing-statement PATTERN" "executing statement pattern pair"
    :default {:prefix "[!" :suffix "!]"}
    :default-desc "[! !]"
    :parse-fn pattern-parse
    :validate [pattern-validate "both prefix and suffix must not be empty."]]
   [nil "--executing-expression PATTERN" "executing expression pattern pair"
    :default {:prefix "[=" :suffix "=]"}
    :default-desc "[= =]"
    :parse-fn pattern-parse
    :validate [pattern-validate "both prefix and suffix must not be empty."]]
   ["-h" "--help" "show help and exit"]])

(defn usage
  "Report program usage"
  [summary]
  (str "OVERVIEW

  JVM Scripting Server page (also shortened as JSSP) is a polyglot templating system
  that embeds JVM scripting language into a text document, similar to JSP, PHP, ASP,
  and other server-side scripting languages.

SYNOPSIS

  jssp [options] FILE
  jssp [-s | --server] [options] WORK-DIRECTORY

OPTIONS

"
       summary
       "

Home page: https://github.com/redraiment/jssp
E-mail bug reports to: redraiment@gmail.com"))

(defn validate
  "Validate command line arguments."
  [args]
  (let [{:keys [options arguments errors summary]}
        (parse-opts args command-line-options)]
    (cond
      (:help options)
      {:action :exit
       :payload (usage summary)}

      errors
      {:action :exit
       :payload (cs/join \newline errors)}

      :else
      {:action :render
       :payload {:options options
                 :template (first arguments)}})))
