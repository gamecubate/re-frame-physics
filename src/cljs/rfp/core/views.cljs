(ns rfp.core.views
  (:require [re-frame.core :as rf]
            [rfp.core.subs :as s]
            [rfp.core.utils :as u]))

(declare svg shapes box disc joint point)

(defn svg []
  (fn []
    (let [[_ _ w h] @(rf/subscribe [:bounds])]
      [:svg {:width w :height h}
        [shapes]])))

(defn shapes []
  (fn []
    [:g.shapes
      (let [ss @(rf/subscribe [:shapes])]
        (for [s ss]
          (case (:type s)
            :box  ^{:key (:id s)} [box s]
            :disc ^{:key (:id s)} [disc s])))]))

(defn box [shape]
  (fn [shape]
    (let [{:keys [type id cx cy hw hh angle view-opts]} shape
          {:keys [visible dot]} view-opts
          x (- cx hw)
          y (- cy hh)
          w (* hw 2)
          h (* hh 2)
          r (min hw hh)
          dot-cy (* r 0.45)
          dot-radius (* r 0.25)
          degrees (- (u/degrees angle) 180)
          xform (str "translate(" cx "," cy ") rotate(" degrees ")")
          class (if-not visible "invisible" nil)]
      [:g.shape.box {:id id :class class :transform xform}
        [:rect {:id id :x (- hw) :y (- hh) :width w :height h}]
        (when dot
          [:circle.dot {:cy dot-cy :r dot-radius}])])))

(defn disc [shape]
  (fn [shape]
    (let [{:keys [type id cx cy r angle view-opts]} shape
          {:keys [visible dot]} view-opts
          dot-cy (* r 0.5)
          dot-radius (min (* r 0.22) 10)
          degrees (- (u/degrees angle) 180)
          xform (str "translate(" cx "," cy ") rotate(" degrees ")")
          class (if-not visible "invisible" nil)]
      [:g.shape.disc {:id id :class class :transform xform}
        [:circle {:r r}]
        (when dot
          [:circle.dot {:cy dot-cy :r dot-radius}])])))

(defn joint [shape]
  ;; TODO
  (fn [shape]
    [:box {:x 50 :y 50 :width 20 :height 20 :style {:fill :green}}]))

(defn point [shape]
  ;; TODO
  (fn [shape]
    [:circle {:cx 100 :cy 100 :r 10 :style {:fill :red}}]))
