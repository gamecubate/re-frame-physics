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
  [id w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        min (min w h)]
    [{:type "box" :id (str id "-n") :cx hw :cy 0  :hw hw :hh  1 :body-opts {:type "static"}}
     {:type "box" :id (str id "-s") :cx hw :cy h  :hw hw :hh  1 :body-opts {:type "static"}}
     {:type "box" :id (str id "-e") :cx  w :cy hh :hw 1  :hh hh :body-opts {:type "static"}}
     {:type "box" :id (str id "-w") :cx  0 :cy hh :hw 1  :hh hh :body-opts {:type "static"}}]))

(defn box
  "A box of half-dimensions hw and hh"
  [id cx cy hw hh]
  ; (.log js/console cx cy hw hh)
  [{:type "box" :id id :cx cx :cy cy :hw hw :hh hh :body-opts {:angle (u/radians 12)} :fixt-opts {:restitution 0.7} :view-opts {:label true}}])

(defn disc
  "A disc"
  [id cx cy r]
  [{:type "disc" :id id :cx cx :cy cy :r r :fixt-opts {:restitution 0.7} :view-opts {:label true}}])

(defn rig-tiny-boxes
  "n little boxes"
  [id n w h]
  (for [i (range n)]
    {:type "box" :id (str id "-" i) :cx (rand-int w) :cy (rand-int h) :hw (+ 3 (rand-int 6)) :hh (+ 3 (rand-int 6)) :fixt-opts {:restitution 0.8} :view-opts {:dot true}}))

(defn rig-tiny-discs
  "n little discs"
  [id n w h]
  (for [i (range n)]
    {:type "disc" :id (str id "-" i) :cx (rand-int w) :cy (rand-int h) :r (+ 5 (rand-int 5)) :fixt-opts {:restitution 0.8} :view-opts {:dot true}}))

(defn disc-and-box
  "A static disc and, above it, a dynamic 10px x 10px box"
  [id w h]
  [{:type "disc" :id (str id "-d") :cx (/ w 4) :cy (* h 0.75) :r 20 :body-opts {:type :static :angle 0} :fixt-opts {:restitution 0.5 :friction 0.5} :view-opts {:dot true}}
   {:type "box"  :id (str id "-b") :cx (* w 0.8) :cy (* h 0.25) :hw 5 :hh 100 :body-opts {:angle (u/radians 45)} :fixt-opts {:restitution 0.7 :friction 0.5} :view-opts {:dot true}}])

(defn spinner
  "2 discs, joined by a revolute joint"
  [id w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        min (min h w)
        id1 (str id "-d1")
        id2 (str id "-d2")
        idj  id]
    [{:type "disc" :id id1 :cx hw :cy hh :r 5 :body-opts {:type "static"} :view-opts {:visible false}}
     {:type "disc" :id id2 :cx (* w 0.7) :cy hh :r (* min 0.15) :body-opts {:angle (u/radians -90)} :fixt-opts {:restitution 0.8} :view-opts {:dot true :label false}}
     {:type "rev-joint" :id idj :b1-id id1 :b2-id id2 :cx hw :cy hh :joint-opts {:enableMotor true :motorSpeed 4 :maxMotorTorque 100} :view-opts {:label true}}]))

(defn interval-timer
  ([f freq]
   (interval-timer f freq 0))
  ([f freq delay]
   [{:type "interval-timer" :f f :freq freq :delay delay}]))
