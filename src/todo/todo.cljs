(ns todo.core)
(def jquery (js* "$"))
(def debug? true)

(defn- debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn main [& args]
  (js/alert "Hello from ClojureScript!"))



