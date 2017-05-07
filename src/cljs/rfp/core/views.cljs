(ns rfp.core.views
  (:require [re-frame.core :as rf]
            [rfp.core.subs :as s]
            [rfp.core.utils :as u]))

(declare svg bodies box disc point joints joint)

(defn svg []
  (fn []
    (let [[_ _ w h] @(rf/subscribe [:bounds])]
      [:svg {:width w :height h}
        [joints]
        [bodies]])))

(defn joints []
  (fn []
    [:g.joints
      (let [js @(rf/subscribe [:joints])]
        (for [j js]
          (case (:type j)
            :rev-joint  ^{:key (:id j)} [joint j])))]))

(defn bodies []
  (fn []
    [:g.bodies
      (let [bs @(rf/subscribe [:bodies])]
        (for [b bs]
          (case (:type b)
            :box  ^{:key (:id b)} [box b]
            :disc ^{:key (:id b)} [disc b])))]))

(defn box [shape]
  (fn [shape]
    (let [{:keys [type id cx cy hw hh angle view-opts]} shape
          {:keys [visible dot label]} view-opts
          x (- cx hw)
          y (- cy hh)
          w (* hw 2)
          h (* hh 2)
          r (min hw hh)
          dot-cy (* r -0.6)
          dot-radius (* r 0.15)
          degrees (u/degrees angle)
          xform (str "translate(" cx "," cy ") rotate(" degrees ")")
          class (if-not visible "invisible" nil)]
      [:g.shape.box {:id id :class class :transform xform}
        [:rect {:id id :x (- hw) :y (- hh) :width w :height h}]
        (when dot
          [:circle.dot {:cy dot-cy :r dot-radius}])
        (when label
          [:text.label {:x 0 :y 0 :dy 5 :text-anchor :middle} id])])))

(defn disc [shape]
  (fn [shape]
    (let [{:keys [type id cx cy r angle view-opts]} shape
          {:keys [visible dot label]} view-opts
          dot-cy (* r 0.6)
          dot-radius (min (* r 0.22) 10)
          degrees (u/degrees angle)
          xform (str "translate(" cx "," cy ") rotate(" degrees ")")
          class (if-not visible "invisible" nil)]
      [:g.shape.disc {:id id :class class :transform xform}
        [:circle {:r r}]
        (when dot
          [:circle.dot {:cy dot-cy :r dot-radius}])
        (when label
          [:text.label {:x 0 :y 0 :dy 5 :text-anchor :middle} id])])))

(defn joint [j]
  (fn [j]
    (let [{:keys [type id cx cy ax ay bx by view-opts]} j
          {:keys [visible label]} view-opts
          xform (str "translate(" cx "," cy ")")
          class (if-not visible "invisible" nil)]
      ; (.log js/console "joint" cx cy ax ay bx by)
      [:g.joint {:id id :class class :transform xform}
        [:circle.anchor {:cx 0 :cy 0 :r 2}]
        (when label
          [:text.label {:x 0 :y 0 :dy -15 :text-anchor :middle} id])])))
        ; [:circle.green {:cx ax :cy ay :r 5}]
        ; [:circle.black {:cx bx :cy by :r 5}]])))

(defn point [shape]
  ;; TODO
  (fn [shape]
    [:circle {:cx 100 :cy 100 :r 10 :style {:fill :red}}]))
