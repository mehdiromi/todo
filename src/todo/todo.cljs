(ns todo.core
  (:require [crate.core :as crate]))
  ;(:use [jayq.core :only [$ inner]]))

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
                       (take 12 (drop 18 r))
                       ))))

(defn- load-todo-data []
  [{:id (uuid)
    :name "group 1"
    :list [{:id (uuid)
            :name "todo 1"
            :done false}
           {:id (uuid)
            :name "todo 2"
            :done false}
          ]}])

(defn- render-todo [e]
  (do
    (.appendTo ($ (crate/html
      [:li {:class (if (e :done) "todo done" "todo") :id (e :id)}
        [:div {:class "inner"}
          [:div {:class "name"} (e :name)]]]
          ;[:input {:type "text" :value (e :name)} (e :name)]]]
          ))
      ($ "#listview"))
    (.on ($ (+ "#" (e :id))) "click" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [t (.find ($ (+ "#" (e :id))) ".name")]
          (set! window.editing true)
          (.empty t)
          (let [in ($ (crate/html [:input {:type "text" :value (e :name)}]))]
            (do
              (.append t in)
              (.bind in "blur" (fn []
                (do
                  (set! window.editing false)
                  (.html t (.val in)))
              ))))))))
      ))

(defn- render-todos [list]
  (do
    (.appendTo ($ (crate/html [:ul {:id "listview"}])) ($ "#wrapper"))
    (doseq [e list]
      (render-todo e)))
    )

(defn- render-group [g]
  (do
    (.appendTo ($ (crate/html
      [:li {:class (if (empty? (g :list)) "list empty" "list") :id (g :id)}
        [:div {:class "inner"}
          [:div {:class "name"} (g :name)]
            [:div {:class "count"} (count (g :list))]]]))
      ($ "#todo-home"))
    ;(render-todos (g :list))
    ))

(defn- render [todo-data]
  (do
    (.appendTo ($ (crate/html [:ul {:id "todo-home"}]))
      ($ "#wrapper"))
    (doseq [g todo-data]
      (render-group g))
    ))

(defn- create-app []
  (let [todo-data (load-todo-data)]
    (render todo-data)))

(defn init [& args]
  (.ready ($ js/document) create-app))



