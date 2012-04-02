(ns todo.core)
(def jquery (js* "$"))
(def debug? true)

(defn- debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))


(defn- create-app []
    {:ENTER_KEY 13
    }
  )


(defn init [& args]
  (do
    (let [app create-app])
      (js/alert "Hello from ClojureScript!"))
    )



