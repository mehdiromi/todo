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
                       (take 12 (drop 18 r))))))

(def todo-data
  [{:id (uuid)
    :name "group 1"
    :list [{:id (uuid)
            :name "todo 1"
            :done false}
           {:id (uuid)
            :name "todo 2"
            :done false}
            ]}])


(defn- todo-data-gid2idx [gid]
  (peek (map first (filter #(= gid ((second %) :id)) (map-indexed vector todo-data)))))

(defn- todo-data-tid2idx [list tid]
  (map first
    (filter #(= gid ((second %) :id))
      (map-indexed vector list))))

(defn- set-todo [gid tid name]
  (js/alert (todo-data-gid2idx gid)))
  ; (let [gi (todo-data-gid2idx gid)
  ;       g (get todo-data gi)
  ;       xx (js/alert gi)]))
    ;     tid (todo-data-tid2idx list tid)]
    ; (def todo-data (assoc todo-data gi
    ;   (assoc g :list
    ;     (if (= "" name)
    ;       (dissoc list ti)
    ;       (assoc list ti
    ;         (assoc (get list ti) :name name))))))))

(defn- render-todo [gid elem]
  (let [tod ($ (crate/html
    [:li {:class (if (elem :done) "todo done" "todo") :id (elem :id)}
      [:div {:class "inner"}
        [:div {:class "name"} (elem :name)]]]))]
    (.appendTo tod ($ "#listview"))
    (.on ($ (+ "#" (elem :id))) "taphold" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [t (.find tod ".name")]
          (set! window.editing true)
          (.empty t)
          (let [in ($ (crate/html [:input {:type "text" :value (elem :name)}]))]
            (.append t in)
            (.focus in)
            (.bind in "blur" (fn []
              (do
                (set! window.editing false)
(js/alert gid)
(js/alert (elem :id))
                (set-todo gid (elem :id) (.val in))
(js/alert ((get ((get todo-data 0) :list) 0) :name))
                (if (= "" (.val in))
                  (do
                    (.remove tod))
                  (do
                    (.html t (.val in))))))))))))))

(defn- render-todos [gid list]
  (do
    (.appendTo ($ (crate/html [:ul {:id "listview"}])) ($ "#wrapper"))
    (.hide ($ "#home"))
    (.on ($ "#listview") "swipeleft" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (.remove ($ "#listview"))
        (.show ($ "#home")))))
    (.on ($ "#listview") "swiperight" (fn [e]
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
                (render-todo gid {:id tid :name (.val in) :done false})))))
          (.appendTo newtodo ($ "#listview"))
          (.appendTo in (.find newtodo ".name"))
          (.focus in)))))
    (doseq [elem list]
      (render-todo gid elem))))

(defn- render-group [g]
  (let [group ($ (crate/html
    [:li {:class (if (empty? (g :list)) "list empty" "list") :id (g :id)}
      [:div {:class "inner"}
        [:div {:class "name"} (g :name)]
          [:div {:class "count"} (.toString (count (g :list)))]]]))]
    (.appendTo group ($ "#home"))
    (.on ($ (+ "#" (g :id))) "tap" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (render-todos (g :id) (g :list)))))
    (.on ($ (+ "#" (g :id))) "taphold" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [t (.find ($ (+ "#" (g :id))) ".name")]
          (set! window.editing true)
          (.empty t)
          (let [in ($ (crate/html [:input {:type "text" :value (g :name)}]))]
            (.append t in)
            (.focus in)
            (.bind in "blur" (fn []
              (do
                (set! window.editing false)
                (let [i (todo-data-gid2idx (g :id))]
(js/alert i)
                  (if (= "" (.val in))
                    (do
                      (.remove group)
                      (dissoc todo-data i))
                    (do
                      (.html t (.val in))
                      (assoc todo-data i
                        (assoc (get todo-data i) :name (.val in))))))))))))))))

(defn- render [todo-data]
  (do
    (.appendTo ($ (crate/html [:ul {:id "home"}]))
      ($ "#wrapper"))
    (doseq [g todo-data]
      (render-group g))
    (.on ($ "#home") "swiperight" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [gid (uuid)
              in ($ (crate/html [:input {:type "text" :value ""} ""]))
              newg ($ (crate/html
                [:li {:class "list empty" :id gid}
                  [:div {:class "inner"}
                    [:div {:class "name"}]]]))]
          (set! window.editing true)
          (.bind in "blur" (fn []
            (do
              (set! window.editing false)
              (.remove newg)
              (if-not (= "" (.val in))
                (render-group {:id gid :name (.val in) :list []})))))
          (.appendTo newg ($ "#home"))
          (.appendTo in (.find newg ".name"))
          (.focus in)))))))

(defn- create-app []
  (render todo-data))

(defn init [& args]
  (.ready ($ js/document) create-app))



