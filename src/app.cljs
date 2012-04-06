(ns app
  (:require [todo.core :as tc]))
  ;(:use [jayq.core :only [$ inner]]))
(def $ (js* "$"))

defn init-app []
  (do
    (tc/init)
    )

($ init-app)

