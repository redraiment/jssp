(defproject me.zzp/jssp "0.1.0-SNAPSHOT"
  :description "JVM Scripting Server page (also shortened as JSSP) is a templating system that embeds JVM scripting language into a text document, similar to JSP, PHP, ASP, and other server-side scripting languages."
  :url "https://github.com/redraiment/jssp"
  :license {:name "Apache License - Version 2.0, January 2004"
            :url "http://www.apache.org/licenses/"}
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :main ^:skip-aot me.zzp.jssp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
