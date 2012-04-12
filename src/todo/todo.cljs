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

(defn- render-todo [elem]
  (do
    (.appendTo ($ (crate/html
      [:li {:class (if (elem :done) "todo done" "todo") :id (elem :id)}
        [:div {:class "inner"}
          [:div {:class "name"} (elem :name)]]]
          ;[:input {:type "text" :value (elem :name)} (elem :name)]]]
          ))
      ($ "#listview"))
    (.on ($ (+ "#" (elem :id))) "tap" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [t (.find ($ (+ "#" (elem :id))) ".name")]
          (set! window.editing true)
          (.empty t)
          (let [in ($ (crate/html [:input {:type "text" :value (elem :name)}]))]
            (.append t in)
            (.focus in)
            (.bind in "blur" (fn []
              (do
                (set! window.editing false)
                (.html t (.val in)))
            )))))))
      ))

(defn- render-todos [list]
  (do
    (.appendTo ($ (crate/html [:ul {:id "listview"}])) ($ "#wrapper"))
    (.on ($ "#listview") "taphold" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (.remove ($ "#listview")))))
    (.on ($ "#listview") "swipeleft" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [tid (uuid)
              in ($ (crate/html [:input {:type "text" :value ""} ""]))
                newtodo ($ (crate/html
                  [:li {:class "todo" :id tid}
                    [:div {:class "inner"}
                      [:div {:class "name"}]]]))]
          (set! window.editing true)
          (.bind in "blur" (fn []
            (do
              (set! window.editing false)
              (.remove newtodo)
              (if-not (= "" (.val in))
                (render-todo {:id tid :name (.val in) :done false})))))
          (.appendTo newtodo ($ "#listview"))
          (.appendTo in (.find newtodo ".name"))
          (.focus in)
          ))))
    (doseq [elem list]
      (render-todo elem))))

(defn- render-group [g]
  (do
    (.appendTo ($ (crate/html
      [:li {:class (if (empty? (g :list)) "list empty" "list") :id (g :id)}
        [:div {:class "inner"}
          [:div {:class "name"} (g :name)]
            [:div {:class "count"} (.toString (count (g :list)))]]]))
      ($ "#todo-home"))
    (.on ($ (+ "#" (g :id))) "tap" (fn [e]
      (render-todos (g :list))))
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



