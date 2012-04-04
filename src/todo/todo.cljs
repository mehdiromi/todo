(ns todo.core
  (:require [crate.core :as crate]))

(def $ (js* "$"))
(def debug? true)

(defn debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn uuid
  "returns a type 4 random UUID: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx"
  ; https://gist.github.com/1308368
  []
  (let [r (repeatedly 30 (fn [] (.toString (rand-int 16) 16)))]
    (apply str (concat (take 8 r) ["-"]
                       (take 4 (drop 8 r)) ["-4"]
                       (take 3 (drop 12 r)) ["-"]
                       [(.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16)]
                       (take 3 (drop 15 r)) ["-"]
                       (take 12 (drop 18 r))))))

(defn- load-todos []
  [ {:name "todo 1"
     :id (uuid)
     :done false}
    {:name "todo 2"
     :id (uuid)
     :done false}])

(defn- render-todo [x]
  (.append ($ "#wrapper") (crate/html
    [:li {:class (if (x :done) "todo done" "todo")}
      [:div {:class "inner"}
        [:div {:class "name"}
          (x :name)]]])))

(defn- render-todos [todos]
  (doseq [todo todos]
    (render-todo todo)))

(defn- render [app]
  (render-todos (app :todos)))

(defn- create-app []
  (let [app
    {:todos (load-todos)
      }]
    (render app)))

(defn init [& args]
  (.ready ($ js/document) create-app))



