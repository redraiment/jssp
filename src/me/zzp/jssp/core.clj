(ns me.zzp.jssp.core
  "JVM Scripting Server Pages"
  (:require [me.zzp.jssp
             [options :as options :refer [*global-options*]]
             [template-engine :refer [render-file]]])
  (:gen-class))

(defn -main
  [& args]
  (let [{:keys [action payload]}
        (options/validate args)]
    (case action
      :exit (.println System/err payload)
      (binding [*global-options* (:options payload)]
        (println (render-file (:template payload)))))))
