(ns todo.core)
(def jquery (js* "$"))
(def debug? true)

(defn- debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn init [& args]
  (do
    (js/alert "Hello from ClojureScript!"))
    )



