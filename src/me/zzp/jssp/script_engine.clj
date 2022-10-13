(ns me.zzp.jssp.script-engine
  "Script Engine: wrap javax.script.ScriptEngine to execute template AST.
  Support JavaScript, Groovy, JRuby, BeanShell."
  (:require [clojure.string :as cs])
  (:import java.lang.StringBuffer
           java.util.concurrent.ConcurrentHashMap
           java.util.function.Function
           java.util.function.BiFunction
           javax.script.ScriptEngineManager)
  (:gen-class))

;;;; Output Buffer

(def ^:private BUFFER-NAME
  "_buf")

(defn- buffer-append
  "Generate append to buffer statement."
  [content]
  (str BUFFER-NAME ".append(" content ")"))

;;;; Bindings

(defprotocol SymbolPool
  "Symbol Manager"
  (generate [this]
    "Generate an unique symbol.")
  (assign [this name value]
    "Binding the name with the value."))

;;; Define a ScriptContext record to wrap javax.script.Bindings
(defrecord ScriptContext [bindings generate-sequence]

  SymbolPool

  (generate [{:keys [bindings generate-sequence]}]
    "Generate an available variable name."
    (loop [index (swap! generate-sequence inc)]
      (let [symbol (str "_" index "_")]
        (if (contains? bindings symbol)
          (recur (swap! generate-sequence inc))
          symbol))))

  (assign [{:keys [bindings]} name value]
    "Save the value to symbol of name and return the name then."
    (.put bindings name value)
    name))

;;;; ScriptEngine

(defprotocol Executor
  "A template AST instructions executor"
  (create-context [this data]
    "Create an new script context and initialized with context data (hash-map).")
  (compile! [this instructions context]
    "Generate the source code from instructions.
NOTE: this function may have side effects.")
  (execute! [this instructions] [this instructions context]
    "Execute template AST instructions with ScriptContext."))

(defrecord ScriptEngine [engine preamble postamble]

  Executor

  (create-context [{:keys [engine]} data]
    "Create an new ScriptContext record.
Initial with specified context data and built-in functions."
    (let [cache (ConcurrentHashMap.)
          generate-sequence (atom 0)
          include (reify Function
                    (apply [this path]
                      (slurp path)))
          readOnce (reify BiFunction
                     (apply [this path content]
                       (if (nil? content)
                         (slurp path)
                         "")))
          includeOnce (reify Function
                        (apply [this path]
                          (.compute cache path readOnce)))]
      (->ScriptContext (doto (.createBindings engine)
                         (.putAll data)
                         (.put BUFFER-NAME (StringBuffer.))
                         (.put "include" include)
                         (.put "includeOnce" includeOnce))
                       generate-sequence)))

  (compile! [{:keys [engine preamble postamble]} instructions context]
    "Compile the instructions to source code.

NOTE: this function may change the bindings of context.

Instructions preprocessing:
- statement content: output directly.
- expression content: append to buffer.
- literal content: assign the content to a temporary variable, then append to buffer.

Concat the scripts with below order:
1. preamble
2. scripts preprocessed from instructions, which joined by javax.script.ScriptEngineFactory#getProgram
3. postamble"
    (let [scripts (->> instructions
                    (map (fn [[tag content]]
                           (case tag
                             :statement content
                             :expression (buffer-append content)
                             (buffer-append (assign context (generate context) content)))))
                    (into-array String)
                    (.getProgram (.getFactory engine)))]
      (str preamble scripts postamble)))

  (execute! [this instructions]
    "Execute instructions as script with empty context, and return the output."
    (execute! this instructions (create-context this {})))

  (execute! [{:keys [engine] :as this} instructions context]
    "Execute instructions as script with script context, and return the output."
    (let [code (compile! this instructions context)
          bindings (:bindings context)
          buffer (doto (get bindings BUFFER-NAME)
                   (.setLength 0))]
      (. engine eval code bindings)
      (str buffer))))

;;;; Engine Instances

(def ^:private script-engines
  "List of Script Engines"
  (atom []))

(def ^:private extensions-engines
  "Map of File Name Extension & Script Engine"
  (atom {}))

(defn of
  "Obtains script engine by file name extension."
  [extension]
  (get @extensions-engines extension))

(when-not *compile-files*
  "Register Script Engines"
  (doseq [factory (.getEngineFactories (ScriptEngineManager.))
          :let [script-engine (->ScriptEngine (.getScriptEngine factory) "" "")]]
    (swap! script-engines conj script-engine)
    (doseq [extension (.getExtensions factory)]
      (swap! extensions-engines conj [extension script-engine]))))
