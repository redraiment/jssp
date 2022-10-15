(ns me.zzp.jssp.file
  "Utility functions for file name."
  (:require [clojure.string :as cs]
            [clojure.java.io :as io])
  (:gen-class))

(defn exists?
  "Return true if and only if file exists."
  [file-name]
  (.exists (io/file file-name)))

(defn extensions
  "Return the extension list of file name."
  [file-name]
  (->> (cs/split file-name #"\.")
    (remove cs/blank?)
    next
    vec))

(defn major-extension
  "Return the latest extension of file name."
  [file-name]
  (last (extensions file-name)))

(defn minor-extension
  "Return the penultimate extension of file name."
  [file-name]
  (last (butlast (extensions file-name))))

(defn strip-extension
  "Strip the latest file extension."
  [file-name]
  (cs/replace file-name #"\b\.[^.]+$" ""))
