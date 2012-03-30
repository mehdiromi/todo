(ns todo.core)

(def debug? true)
(defn debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn hello
  "I don't do a whole lot."
  [& args]
  (js/alert "Hello from ClojureScript!"))



