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
    (.append ($ "#wrapper") (crate/html
      [:li {:class "todo"}
        [:div {:class "inner"}
          [:div {:class "name"}
            "Tap me"]]]))
    (.append ($ "#wrapper") (crate/html
      [:li {:class "todo"}
        [:div {:class "inner"}
          [:div {:class "name"}
            "Tap me"]]]))
    ))

(defn- create-app []
  (let [app {
      :todos (store)
    }]
    (render app)))

(defn init [& args]
  (.ready ($ js/document) create-app))



