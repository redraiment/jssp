(ns me.zzp.jssp.script-engine
  "Script Engine: wrap javax.script.ScriptEngine to execute template AST.
  Support JavaScript, Groovy, JRuby, BeanShell."
  (:require [clojure.string :as cs])
  (:import javax.script.ScriptEngineManager)
  (:gen-class))

(defn- next-available-symbol
  "Generate an available global variable name"
  [sequence bindings]
  (loop [id (swap! sequence inc)]
    (let [symbol (str "_" id "_")]
      (if (contains? bindings symbol)
        (recur (swap! sequence inc))
        symbol))))

(defprotocol Executable
  (execute [this instructions context]))

(defrecord ScriptEngine [engine preamble postamble output-wrapper]
  Executable
  (execute [this instructions context]
    "Execute instructions as script with context data."
    (let [{:keys [engine output-wrapper preamble postamble]} this
          bindings (doto (.createBindings engine)
                     (.putAll context))
          symbol-seq (atom 0)
          gemsym #(next-available-symbol symbol-seq bindings)
          script (->> instructions
                   (map (fn [[tag content]]
                          (case tag
                            :statement content
                            :expression (output-wrapper content)
                            (let [symbol (gemsym)]
                              (.put bindings symbol content)
                              (output-wrapper symbol)))))
                   (into-array String)
                   (.getProgram (.getFactory engine)))]
      (with-out-str
        (.eval engine (str preamble script postamble) bindings)))))

(def ^:private script-engines
  "List of Script Engines"
  (atom []))

(def ^:private extensions-engines
  "Map of File Name Extension & Script Engine"
  (atom {}))

(defn- register
  "Register Script Engine"
  [& {:keys [engine] :as options}]
  (let [script-engine (map->ScriptEngine (merge {:preamble ""
                                                 :postamble ""}
                                                options))]
    (swap! script-engines conj script-engine)
    (doseq [extension (.. engine getFactory getExtensions)]
      (swap! extensions-engines conj [extension script-engine]))))

(when-not *compile-files*
  "Initial Script Engines"
  (let [manager (ScriptEngineManager.)]
    (register :engine (. manager getEngineByName "Nashorn")
              :output-wrapper #(str "Packages.java.lang.System.out.print(" % ")"))
    (register :engine (. manager getEngineByName "beanshell")
              :output-wrapper #(str "System.out.print(" % ")"))
    (register :engine (. manager getEngineByName "Groovy")
              :output-wrapper #(str "System.out.print(" % ")"))
    (register :engine (. manager getEngineByName "jruby")
              :output-wrapper #(str "java.lang.System.out.print(" % ")"))))

(defn of
  "Obtains script engine by file name extension."
  [extension]
  (get @extensions-engines extension))
