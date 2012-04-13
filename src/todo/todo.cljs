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

(defn vec-del-at [vc pos]
  (vec (concat 
         (subvec vc 0 pos) 
         (subvec vc (inc pos)))))

(def data [
  { :id (uuid)
    :name "group 1"
    :list [
      { :id (uuid)
        :name "todo 1"
        :done false}
      { :id (uuid)
        :name "todo 2"
        :done false}]}])

(defn- data-gid2idx [gid]
  (first (map first (filter #(= gid ((second %) :id)) (map-indexed vector data)))))

(defn- data-gid2group [gid]
  (get data (data-gid2idx gid)))

(defn- data-tid2idx [gid tid]
  (let [g (data-gid2group gid)]
    (first (map first
      (filter #(= tid ((second %) :id))
        (map-indexed vector (g :list)))))))

(defn- data-tid2todo [gid tid]
  (let [l ((data-gid2group gid) :list)]
    (get l (data-tid2idx gid tid))))

(defn- data-set-group [gid name]
  (let [i (data-gid2idx gid)]
    (def data
      (if i
        (if (= "" name)
          (vec-del-at data i)
          (assoc-in data [i :name] name))
        (assoc data (count data)
          {:id gid :name name :list []})))))

(defn- data-set-todo [gid tid name]
  (let [gi (data-gid2idx gid)
        ti (data-tid2idx gid tid)]
    (def data
      (if (= "" name)
        (update-in data [gi :list] #(vec-del-at % ti))
        (if ti
          (update-in data [gi :list] #(assoc-in % [ti :name] name))
          (update-in data [gi :list] #(assoc % (count %) {:id tid :name name :done false})))))))

(defn- render-todo [gid tid]
  (let [elem (data-tid2todo gid tid)
        tod ($ (crate/html
          [:li {:class (if (elem :done) "todo done" "todo") :id (elem :id)}
            [:div {:class "inner"}
              [:div {:class "name"} (elem :name)]]]))]
    (.appendTo tod ($ "#listview"))
    (.on ($ (+ "#" tid)) "taphold" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [t (.find tod ".name")]
          (set! window.editing true)
          (.empty t)
          (let [in ($ (crate/html [:input {:type "text" :value ((data-tid2todo gid tid) :name)}]))]
            (.append t in)
            (.focus in)
            (.bind in "blur" (fn []
              (do
                (set! window.editing false)
                (data-set-todo gid tid (.val in))
                (if (= "" (.val in))
                  (let [c (.find ($ (+ "#" gid)) ".count")]
                    (.text c (dec (js/parseInt (.text c))))
                    (if (= "0" (.text c))
                      (.attr ($ (+ "#" gid)) "class" "list empty"))
                    (.remove tod))
                  (.html t (.val in)))))))))))))

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
                (do
                  (data-set-todo gid tid (.val in))
                  (let [c (.find ($ (+ "#" gid)) ".count")]
                    (.text c (inc (js/parseInt (.text c))))
                    (if-not (= "0" (.text c))
                      (.attr ($ (+ "#" gid)) "class" "list")))
                  (render-todo gid tid))))))
          (.appendTo newtodo ($ "#listview"))
          (.appendTo in (.find newtodo ".name"))
          (.focus in)))))
    (doseq [elem list]
      (render-todo gid (elem :id)))))

(defn- render-group [gid]
  (let [g (data-gid2group gid)
        group ($ (crate/html
          [:li {:class (if (empty? (g :list)) "list empty" "list") :id (g :id)}
            [:div {:class "inner"}
              [:div {:class "name"} (g :name)]
                [:div {:class "count"} (.toString (count (g :list)))]]]))]
    (.appendTo group ($ "#home"))
    (.on ($ (+ "#" gid)) "tap" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (render-todos gid ((data-gid2group gid) :list)))))
    (.on ($ (+ "#" gid)) "taphold" (fn [e]
      (when (and (not window.editing) (not window.inAction))
        (let [t (.find ($ (+ "#" gid)) ".name")]
          (set! window.editing true)
          (.empty t)
          (let [in ($ (crate/html [:input {:type "text" :value ((data-gid2group gid) :name)}]))]
            (.append t in)
            (.focus in)
            (.bind in "blur" (fn []
              (do
                (set! window.editing false)
                (data-set-group gid (.val in))
                (let [i (data-gid2idx gid)]
                  (if (= "" (.val in))
                    (.remove group)
                    (.html t (.val in))))))))))))))

(defn- render [d]
  (do
    (.appendTo ($ (crate/html [:ul {:id "home"}]))
      ($ "#wrapper"))
    (doseq [g d]
      (render-group (g :id)))
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
                (do
                  (data-set-group gid (.val in))
                  (render-group gid))))))
          (.appendTo newg ($ "#home"))
          (.appendTo in (.find newg ".name"))
          (.focus in)))))))

(defn- create-app []
  (render data))

(defn init [& args]
  (.ready ($ js/document) create-app))



