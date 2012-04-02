(ns app
  (:require [todo.core :as todo.core]))

(def jquery (js* "$"))

defn init-app []
  (todo.core/main)

(jquery init-app)

