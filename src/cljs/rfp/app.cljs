(ns rfp.app
  (:require [rfp.core.planck :as pl]
            [rfp.core.rigs :as rigs]
            [reagent.core :as re]
            [re-frame.core :as rf]
            [rfp.core.events]
            [rfp.core.subs]
            [rfp.core.views :as views]
            [rfp.core.utils :as u]))
            ;[rfp.core.devtools]))

;; -- Extra Rigs --------------------------------------------------------------
(defn four-discs [id w h]
  "Place a static disc at each intersection of a 3x3 grid"
  (let [body-opts {:type :static}
        min (min w h)
        v-opts {:label true}]
    [{:type "disc" :id id :cx (/ w 3)       :cy (/ h 3)       :r (* min 0.12) :body-opts body-opts :view-opts v-opts}
     {:type "disc" :id (str id "-" 2) :cx (* w (/ 2 3)) :cy (/ h 3)       :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id (str id "-" 3) :cx (* w (/ 2 3)) :cy (* h (/ 2 3)) :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id (str id "-" 4) :cx (/ w 3)       :cy (* h (/ 2 3)) :r (* min 0.12) :body-opts body-opts}]))

(defn rev-joint [id w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        j-id id
        b-id (str id "-box")
        d-id (str id "-disc")]
    [{:type "box"  :id b-id :cx hw :cy hh :hw 5 :hh 40 :body-opts {:angle (u/radians 0)}}
     {:type "disc" :id d-id  :cx hw :cy hh :r 5 :body-opts {:type :static} :view-opts {:visible false}}
     {:type "rev-joint" :id j-id :b1-id b-id :b2-id d-id :cx hw :cy hh :joint-opts {:enableMotor true :maxMotorTorque 100000 :motorSpeed 10} :view-opts {:label true}}]))

;; -- Demos -------------------------------------------------------------------
(defn demo-1 [w h world]
  (concat
    (rigs/walls "wall" w h)
    (rigs/box "box" (/ w 2) 60 25 25)))

(defn demo-2 [w h world]
  (concat
    (rigs/walls "wall" w h)
    (rigs/disc  "disc" (- (/ w 2) 20) 150 40)
    (rigs/box   "box"  (/ w 2)         60 25 25)))

(defn demo-3 [w h world]
  (concat
    (rigs/walls "w" w h)
    (rev-joint "revolute joint" w h)))

(defn rand-gravity! [world]
  (.setGravity world (pl/vec2 (u/rand-int-3 -10 10) (u/rand-int-3 -10 10))))

(defn demo-4 [w h world]
  (concat
    (rigs/walls "wall" w h)
    (rigs/box "box" (/ w 2) 60 25 25)
    (rigs/rig-tiny-boxes "mb" 50 w h)
    (rigs/rig-tiny-discs "md" 50 w h)
    (four-discs "disc" w h)
    (rev-joint "revolute (motor) joint" w h)
    (rigs/interval-timer #(rand-gravity! world) 2500 2500)))

;; -- DB ----------------------------------------------------------------------
(defn rig-loader [rig-maker w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        world (pl/world [0 10])
        specs (rig-maker w h world)
        _ (pl/assemble-in! specs world)]
    {:world world
     :bounds [0 0 w h]
     :raf nil
     :timer-ids []}))

(defn name->demo [s]
  (case s
    "demo-1" demo-1
    "demo-2" demo-2
    "demo-3" demo-3
    "demo-4" demo-4))

(defn load-demo [name w h]
  (rf/dispatch-sync [:init #(rig-loader (name->demo name) w h)]))

;; -- Main UI -----------------------------------------------------------------
(defn root-view [w h]
  (fn []
    [:div#demos
      [:div.overlay
        [:h1 "re-frame + planck.js"]
        [:p "Source on " [:a {:href "https://github.com/gamecubate/re-frame-physics"} "GitHub"]]
        [:select {:defaultValue "rig-1" :on-change #(load-demo (-> % .-target .-value) w h)}
          [:option {:value "demo-1"} "Box"]
          [:option {:value "demo-2"} "Box and Disc"]
          [:option {:value "demo-3"} "Revolute joint"]
          [:option {:value "demo-4"} "Boxes, discs and joints"]]]
      [views/svg]]))

;; -- Entry Point -------------------------------------------------------------
(defn run []
  (let [el (.getElementById js/document "container")
        w (.-clientWidth el)
        h (.-clientHeight el)]
    (load-demo "demo-1" w h)
    (re/render-component [root-view w h] el)))
