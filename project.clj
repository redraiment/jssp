(defproject me.zzp/jssp "0.3.0"
  :description "JVM Scripting Server page (also shortened as JSSP) is a templating system that embeds JVM scripting language into a text document, similar to JSP, PHP, ASP, and other server-side scripting languages."
  :url "https://github.com/redraiment/jssp"
  :license {:name "Apache License - Version 2.0, January 2004"
            :url "http://www.apache.org/licenses/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.0.214"]
                 [cheshire "5.11.0"]
                 ;; Script Engines
                 [org.apache-extras.beanshell/bsh "2.0b6"]
                 [org.apache.groovy/groovy-jsr223 "4.0.5"]
                 [org.jruby/jruby "9.3.8.0"]
                 ;; Web Server
                 [http-kit "2.6.0"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [javax.servlet/javax.servlet-api "4.0.0"]
                 [org.apache.commons/commons-text "1.10.0"]]
  :main ^:skip-aot me.zzp.jssp.core
  :target-path "target/%s"
  :profiles {:java15 {:dependencies [[org.openjdk.nashorn/nashorn-core "15.4"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
