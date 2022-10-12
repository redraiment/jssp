(ns me.zzp.jssp.template-engine
  "Template Engine: parse and render templates."
  (:require [clojure.string :as cs]
            [me.zzp.jssp.script-engine
             :as script-engine
             :refer [create-context
                     execute!]]
            [me.zzp.jssp.options
             :refer [*global-options*]])
  (:import java.util.regex.Pattern))

(defn- lexical-analysis
  "Split the template with patterns, return the sequence of tokens.
  Pattern format:
    {:statement {:prefix STRING :suffix STRING}
     :expression {:prefix STRING :suffix STRING}}"
  [template patterns]
  (->> patterns
    ;; get patterns
    vals
    (mapcat vals)
    (into #{})

    ;; convert to regular expression pattern
    (map #(Pattern/quote %))
    (cs/join "|")
    (format "(?=%1$s)|(?<=%1$s)")
    (re-pattern)

    ;; tokenizate
    (cs/split template)))

(defn- syntax-analysis
  "Convert tokens to AST.
  - statement element: tokens between statement patterns.
  - expression element: tokens between statement patterns.
  - literal: others tokens.

  Pattern format same with lexical analysis.
  Element format: [TAG TOKEN]
  AST format: [ELEMENT...]"
  [tokens {{statement-prefix :prefix
            statement-suffix :suffix}
           :statement
           {expression-prefix :prefix
            expression-suffix :suffix}
           :expression}]
  (loop [[token & tokens] tokens
         tag :literal
         ast []]
    (cond
      ;; End
      (nil? token)
      ast

      ;; Exit statement & expression context
      (or (and (= tag :statement)
               (= token statement-suffix))
          (and (= tag :expression)
               (= token expression-suffix)))
      (recur tokens :literal ast)

      ;; Enter statement context
      (and (= tag :literal)
           (= token statement-prefix))
      (recur tokens :statement ast)

      ;; Enter expression context
      (and (= tag :literal)
           (= token expression-prefix))
      (recur tokens :expression ast)

      ;; Collect element with context
      :else
      (recur tokens tag (conj ast [tag token])))))

(defn- parse
  "Parse template to AST."
  [template patterns]
  (-> template
    (lexical-analysis patterns)
    (syntax-analysis patterns)))

(defn- render-recursively
  "Renders repeatedly a string template with data until stop the limit times is reached, or no more prefix patterns can be found if the limit is nil."
  ([engine template context patterns]
   (render-recursively engine template context patterns nil))
  ([engine template context patterns limit]
   (let [prefixes (map :prefix (vals patterns))]
     (loop [expanded-template template
            times 0]
       (if (and (some (partial (memfn contains pattern) expanded-template)
                      prefixes)
                (or (nil? limit)
                    (< times limit)))
         (recur (execute! engine (parse expanded-template patterns) context)
                (inc times))
         expanded-template)))))

(defn render-string
  "Renders a string template with data.

  There are two render phases:
  1. Expanding phase: render repeatedly with expanding patterns until no more patterns can be found, to compute another template which will in turn render.
  2. Executing phase: render with executing patterns once."
  [engine template]
  (let [context (create-context engine (:context *global-options*))
        expanded-template (render-recursively engine template context (get-in *global-options* [:patterns :expanding]))]
    (render-recursively engine expanded-template context (get-in *global-options* [:patterns :executing]))))

(defn render-file
  "Renders a file template with data.

  Auto choose script engine by file extension."
  ([template-file-name]
   (let [extension (last (cs/split template-file-name #"\."))
         engine (script-engine/of extension)
         template (slurp template-file-name)]
     (render-string engine template))))
