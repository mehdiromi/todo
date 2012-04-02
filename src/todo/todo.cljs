(ns todo.core)
(def jquery (js* "$"))

(def debug? true)
(defn- debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn main
  "I don't do a whole lot."
  [& args]
  (js/alert "Hello from ClojureScript!"))



