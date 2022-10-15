(ns me.zzp.jssp.web-server-test
  (:require [me.zzp.jssp.web-server :refer [handler]]
            [clojure.test :refer [deftest is are]]))

(deftest handler-test
  (is (= {:status 200
          :headers {"Content-Type" "text/html"}
          :body "<!DOCTYPE html>
<html lang=\"zh-CN\">
  <head>
    <meta charset=\"UTF-8\" />
    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\" />
    <meta name=\"renderer\" content=\"webkit\" />
    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no\" />
    <title>Hello JSSP</title>
    <link rel=\"stylesheet\" href=\"styles/index.css.js\" />
  </head>
  <body>
    <h1>Hello JVM Scripting Server Pages</h1>
    <dl>
      <dt>uri</dt>
      <dd>/examples/server-mode/index.html.groovy</dd>
    </dl>
  </body>
</html>
"}
         (handler {:uri "/examples/server-mode/index.html.groovy"}))))
