(ns todo.core
  (:require [crate.core :as crate]))

(def $ (js* "$"))
(def debug? true)

(defn debug-log [& vs]
  (when debug?
    (.log js/console (apply str vs))))

(defn uuid
  "returns a type 4 random UUID: xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx"
  []
  (let [r (repeatedly 30 (fn [] (.toString (rand-int 16) 16)))]
    (apply str (concat (take 8 r) ["-"]
                       (take 4 (drop 8 r)) ["-4"]
                       (take 3 (drop 12 r)) ["-"]
                       [(.toString  (bit-or 0x8 (bit-and 0x3 (rand-int 15))) 16)]
                       (take 3 (drop 15 r)) ["-"]
                       (take 12 (drop 18 r))))))

(defn- load-todos []
  [ {:name "hello world1"
     :id (uuid)
     :done false}
    {:name "hello world2"
     :id (uuid)
     :done false}])

(defn- render-todo [todos]
  (doseq [todo todos]
    (do
      (.append ($ "#wrapper") (crate/html
        [:li {:class "todo"}
          [:div {:class "inner"}
            [:div {:class "name"}
              (todo :name)]]])))))

(defn- render [app]
  (render-todo (app :todos)))

(defn- create-app []
  (let [app {:todos (load-todos)}]
    (render app)))

(defn init [& args]
  (.ready ($ js/document) create-app))



