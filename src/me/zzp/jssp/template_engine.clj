(ns me.zzp.jssp.template-engine
  "Template Engine"
  (:refer-clojure :exclude [apply])
  (:require [clojure.string :as cs]
            [me.zzp.jssp.options :as options])
  (:import java.util.HashMap
           java.util.regex.Pattern
           javax.script.ScriptEngineManager))

(def ^:private script-engine-manager
  (ScriptEngineManager.))

(def script-engines
  {:Nashorn {:engine (. script-engine-manager getEngineByName "Nashorn")
             :header ""
             :prefix "Packages.java.lang.System.out.print("
             :suffix ");"}
   :BeanShell {:engine (. script-engine-manager getEngineByName "beanshell")
               :header ""
               :prefix "System.out.print("
               :suffix ");"}
   :Jython {:engine (. script-engine-manager getEngineByName "jython")
            :header "import java.lang.System\n"
            :prefix "java.lang.System.out.print("
            :suffix ");"}
   :JRuby {:engine (. script-engine-manager getEngineByName "jruby")
           :header ""
           :prefix "java.lang.System.out.print("
           :suffix ");"}
   :Groovy {:engine (. script-engine-manager getEngineByName "Groovy")
            :header ""
            :prefix "System.out.print("
            :suffix ");"}})

(def script-engine-extensions
  (->> script-engines
    (mapcat (fn [[name {:keys [engine]}]]
              (map #(vector % name) (.. engine getFactory getExtensions))))
    (into {})))

(def script-engine-of
  (comp script-engines
        script-engine-extensions))

(def separator
  (->> #{options/statement-prefix
         options/statement-suffix
         options/expression-prefix
         options/expression-suffix}
    (map #(Pattern/quote %))
    (cs/join "|")
    (format "(?=%1$s)|(?<=%1$s)")
    (re-pattern)))

(defn parse
  [content]
  (loop [[token & tokens] (cs/split content separator)
         tag :literal
         ast []]
    (cond
      (nil? token)
      ast

      (or (and (= tag :statement)
               (= token options/statement-suffix))
          (and (= tag :expression)
               (= token options/expression-suffix)))
      (recur tokens :literal ast)

      (and (= tag :literal)
           (= token options/statement-prefix))
      (recur tokens :statement ast)

      (and (= tag :literal)
           (= token options/expression-prefix))
      (recur tokens :expression ast)

      :else
      (recur tokens tag (conj ast [tag token])))))

(defn apply
  ([template-file-name]
   (apply template-file-name {}))
  ([template-file-name context]
   (let [extension (last (cs/split template-file-name #"\."))
         {:keys [engine header prefix suffix]}
         (script-engine-of extension)
         content (slurp template-file-name)

         bindings (doto (. engine createBindings)
                    (.putAll context))
         var-name-seq (atom 0)
         next-var-name (fn []
                         (str "$_" (swap! var-name-seq inc) "_$"))
         next-available-var-name (fn []
                                   (loop [var-name (next-var-name)]
                                     (if (contains? bindings var-name)
                                       (recur (next-var-name))
                                       var-name)))

         program (->> content
                   parse
                   (map (fn [[tag token]]
                          (case tag
                            :literal (let [var-name (next-available-var-name)]
                                       (. bindings put var-name token)
                                       (str prefix var-name suffix))
                            :expression (str prefix token suffix)
                            :statement token)))
                   (cons header)
                   (cs/join ""))]
     (. engine eval program bindings))))
