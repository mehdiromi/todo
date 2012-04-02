(ns todo.core
  (:require [crate.core :as crate]))

(def $ (js* "$"))
(def debug? true)

(defn debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn- store []
  [])

(defn- render [app]
  (do
    (.append ($ "#wrapper") (crate/html [:p {:id "1"} "Hello 1!"]))
    (.append ($ "#wrapper") (crate/html [:p {:id "2"} "Hello 2!"]))
    ))

(defn- create-app []
  (let [app {
      :todos (store)
    }]
    (render app)))

(defn init [& args]
  (.ready ($ js/document) create-app))



