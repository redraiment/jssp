(defproject me.zzp/jssp "0.1.0-SNAPSHOT"
  :description "JVM Scripting Server page (also shortened as JSSP) is a templating system that embeds JVM scripting language into a text document, similar to JSP, PHP, ASP, and other server-side scripting languages."
  :url "https://github.com/redraiment/jssp"
  :license {:name "Apache License - Version 2.0, January 2004"
            :url "http://www.apache.org/licenses/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.cli "1.0.214"]
                 [cheshire "5.11.0"]
                 [org.apache-extras.beanshell/bsh "2.0b6"]
                 [org.codehaus.groovy/groovy-jsr223 "3.0.13"]
                 [org.jruby/jruby "9.3.8.0"]]
  :main ^:skip-aot me.zzp.jssp.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
