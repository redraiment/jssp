(ns me.zzp.jssp.web-server
  "Inner Web Server"
  (:require [clojure.string :as cs]
            [clojure.walk :refer [stringify-keys]]
            [org.httpkit.server :as http]
            [ring.util
             [response :refer [response
                               not-found
                               content-type]]
             [mime-type :refer [ext-mime-type]]]
            [ring.middleware
             [json :refer [wrap-json-body
                           wrap-json-response]]
             [multipart-params :refer [wrap-multipart-params]]
             [nested-params :refer [wrap-nested-params]]
             [params :refer [wrap-params]]
             [content-type :refer [wrap-content-type]]
             [cookies :refer [wrap-cookies]]
             [head :refer [wrap-head]]
             [resource :refer [wrap-resource]]
             [not-modified :refer [wrap-not-modified]]]
            [me.zzp.jssp
             [file :as file]
             [options :refer [*global-options*]]
             [template-engine :refer [executable?
                                      render-file]]])
  (:gen-class))

(defn handler
  "请求处理器"
  [{:keys [uri] :as request}]
  (binding [*global-options*
            (assoc-in *global-options* [:context "request"]
                      (-> request
                        (select-keys [:remote-addr :server-port :server-name
                                      :content-type :content-length
                                      :headers :cookies :character-encoding
                                      :request-method :scheme :uri
                                      :params :form-params :query-params :multipart-params
                                      :query-string :body])
                        stringify-keys))]
    (let [file-name (subs uri 1)]
      (if (file/exists? file-name)
        (-> file-name
          ((if (executable? file-name)
             render-file
             slurp))
          response
          (content-type (-> file-name
                          file/strip-extension
                          ext-mime-type)))
        (not-found uri)))))

(def routes
  (when-not *compile-files*
    (-> handler
      wrap-json-response
      (wrap-json-body {:keywords? true})
      wrap-multipart-params
      wrap-nested-params
      wrap-params
      wrap-content-type
      wrap-cookies
      wrap-head
      wrap-not-modified)))

(defn start
  "Start the inner web server."
  []
  (http/run-server
   routes
   {:host "0.0.0.0"
    :port (get-in *global-options* [:server :port])}))
