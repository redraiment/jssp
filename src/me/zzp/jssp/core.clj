(ns me.zzp.jssp.core
  (:gen-class))

#_(def manager (ScriptEngineManager.))

#_(doseq [factory (. manager getEngineFactories)
        :let [engine-name (. factory getEngineName)
              engine-version (. factory getEngineVersion)
              language-name (. factory getLanguageName)
              language-version (. factory getLanguageVersion)
              names (. factory getNames)
              extensions (. factory getExtensions)
              mime-types (. factory getMimeTypes)]]
  (println (format "%s@%s - %s@%s
  names: %s
  extensions: %s
  mime-types: %s
"
                   language-name language-version
                   engine-name engine-version
                   (cs/join ", " names)
                   (cs/join ", " extensions)
                   (cs/join ", " mime-types))))

(defn -main
  [& args]
  (println "Hello JSSP"))
