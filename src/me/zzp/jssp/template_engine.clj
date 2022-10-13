(ns me.zzp.jssp.template-engine
  "Template Engine: parse and render templates."
  (:require [clojure.string :as cs]
            [me.zzp.jssp.script-engine
             :as script-engine
             :refer [create-context
                     execute!]]
            [me.zzp.jssp.options
             :refer [*global-options*]])
  (:import java.util.regex.Pattern)
  (:gen-class))

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

(defn- trim
  "Trim spaces around statement."
  [stream]
  (if (:trim *global-options*)
    (loop [[[tag1 context1 :as element1] & elements]
           (cons [:literal "\n"] stream)

           ast []]
      (let [[[tag2 context2 :as element2]
             [tag3 context3 :as element3]
             & _]
            elements]
        (cond
          ;; End
          (nil? tag1)
          (next ast)

          (and (= :literal tag1)
               (or (re-find #"\n[^\S\n]*$" context1)
                   (re-matches #"[^\S\n]+" context1))
               (= :statement tag2)
               (or (not= :literal tag3)
                   (re-find #"^[^\S\n]*\n" context3)
                   (re-matches #"[^\S\n]+" context3)))
          (recur (->> elements
                   next
                   next
                   (cons (if tag3
                           [tag3 (-> context3
                                   (cs/replace #"^[^\S\n]+" "")
                                   (cs/replace #"^\n" ""))]
                           element3))
                   (cons element2))
                 (conj ast [tag1 (cs/replace context1 #"[^\S\n]+$" "")]))

          :else
          (recur elements (conj ast element1)))))
    stream))

(defn- parse
  "Parse template to AST."
  [template patterns]
  (-> template
    (lexical-analysis patterns)
    (syntax-analysis patterns)
    trim))

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
        expanded-template (render-recursively engine template context
                                              (get-in *global-options* [:patterns :expanding])
                                              (get *global-options* :expand-limit))]
    (if (get *global-options* :emit-code)
      expanded-template
      (render-recursively engine expanded-template context (get-in *global-options* [:patterns :executing]) 1))))

(defn render-file
  "Renders a file template with data.

  Auto choose script engine by file extension."
  ([template-file-name]
   (let [extension (last (cs/split template-file-name #"\."))
         engine (script-engine/of extension)
         template (slurp template-file-name)]
     (render-string engine template))))
