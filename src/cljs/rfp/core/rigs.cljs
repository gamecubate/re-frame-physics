(ns rfp.core.rigs
  (:require [rfp.core.utils :as u]))

;; TODO UPDATE INSTRUCTIONS
;;; Rigs
;;
;;  A rig is a vector of specifications.
;;
;;  A specification describes either a disc, a box, a joint, or a group thereof.
;;
;;  Discs:
;;  :disc id cx cy r body-options fixture-options view-options
;;
;;  Boxes:
;;  :disc id cx cy hw hh body-options fixture-options view-options
;;
;;  Revolute Joints:
;;  :rev-joint id body1-id body2-id anchor-position joint-options view-options

;;  Rigs are assembled in a world, at which each rig becomes visible
;;  and is subjected to the various forces of that world.
;;
;;  Example whereby we add 2 rigs to a world:
;;  (assemble-in! (my-rig w h) my-world)
;;  (assemble-in! (my-second-rig w h) my-world)

(defn walls
  "Walls to enclose a space, thus preventing bodies within from
   escaping"
  [w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        min (min w h)]
    [{:type "box" :id "wall-n" :cx hw :cy 0  :hw hw :hh  1 :body-opts {:type "static"}}
     {:type "box" :id "wall-s" :cx hw :cy h  :hw hw :hh  1 :body-opts {:type "static"}}
     {:type "box" :id "wall-e" :cx  w :cy hh :hw 1  :hh hh :body-opts {:type "static"}}
     {:type "box" :id "wall-w" :cx  0 :cy hh :hw 1  :hh hh :body-opts {:type "static"}}]))

(defn disc-and-box
  "A static disc and, above it, a dynamic 10px x 10px box"
  [w h]
  [{:type "disc" :id "r1d1" :cx (/ w 4) :cy (* h 0.75) :r 20 :body-opts {:type :static :angle 0} :fixt-opts {:restitution 0.5 :friction 0.5} :view-opts {:dot true}}
   {:type "box"  :id "r1b1" :cx (* w 0.8) :cy (* h 0.25) :hw 5 :hh 100 :body-opts {:angle (u/radians 45)} :fixt-opts {:restitution 0.7 :friction 0.5} :view-opts {:dot true}}])

(defn box
  "A box of half-dimensions hw and hh"
  [id hw hh w h]
  [{:type "box" :id id :cx (/ w 2) :cy (+ hh 10) :hw hw :hh hh :fixt-opts {:restitution 0.8} :view-opts {:dot true}}])

(defn spinner
  "2 discs, joined by a revolute joint"
  [w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        min (min h w)]
    [{:type "disc" :id "s-d1" :cx hw :cy hh :r 5 :body-opts {:type "static"} :view-opts {:visible false}}
     {:type "disc" :id "s-d2" :cx (* w 0.7) :cy hh :r (* min 0.15) :body-opts {:angle (u/radians -90)} :fixt-opts {:restitution 0.8} :view-opts {:dot true}}
     {:type "rev-joint" :id "s-j1" :b1-id "s-d1" :b2-id "s-d2" :cx hw :cy hh :joint-opts {:enableMotor true :motorSpeed 5 :maxMotorTorque 100}}]))

(defn rig-mini-boxes
  "n little boxes"
  [n w h]
  (for [i (range n)]
    {:type "box" :id (str "rmb-b" i) :cx (rand-int w) :cy (rand-int h) :hw (+ 3 (rand-int 6)) :hh (+ 3 (rand-int 6)) :fixt-opts {:restitution 0.8} :view-opts {:dot true}}))

(defn rig-mini-discs
  "n little discs"
  [n w h]
  (for [i (range n)]
    {:type "disc" :id (str "rmb-d" i) :cx (rand-int w) :cy (rand-int h) :r (+ 5 (rand-int 5)) :fixt-opts {:restitution 0.8} :view-opts {:dot true}}))
