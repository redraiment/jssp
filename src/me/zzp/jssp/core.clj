(ns me.zzp.jssp.core
  "JVM Scripting Server Pages"
  (:require [me.zzp.jssp
             [options :as options :refer [*global-options*]]
             [template-engine :refer [render-file]]
             [web-server :as web-server]])
  (:gen-class))

(defn -main
  [& args]
  (let [{:keys [action payload]}
        (options/validate args)]
    (case action
      :exit
      (.println System/err payload)

      :outside
      (binding [*global-options* (:options payload)]
        (print (render-file (:template payload)))
        (flush))

      :server-side
      (binding [*global-options* (:options payload)]
        (web-server/start))

      (.println System/err (str "Unknown action: " action)))))
