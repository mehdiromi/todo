(ns todo.core)
(def $ (js* "$"))
(def debug? true)

(defn debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn- store []
  [])

(defn- render [app]
  (do
    (.append ($ "#wrapper") "<p>1 Hello World!</p>")
    (.append ($ "#wrapper") "<p>2 Hello World!</p>")
    ))

(defn- create-app []
  (let [app
    {
      :todos (store)
    }]
    (render app)))

(defn init [& args]
  (.ready ($ js/document) create-app))



