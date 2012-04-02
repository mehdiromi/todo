(ns app
  (:require [todo.core :as todo.core]))
(def jquery (js* "$"))

defn init-app []
  (do
    (todo.core/init)
    )

(jquery init-app)

