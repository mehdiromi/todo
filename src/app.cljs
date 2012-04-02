(ns app
  (:require [todo.core :as tc]))
(def $ (js* "$"))

defn init-app []
  (do
    (tc/init)
    )

($ init-app)

