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

;; -- Rigs ------------------------------------------------------------
(defn four-discs [id w h]
  "Place a static disc at each intersection of a 3x3 grid"
  (let [body-opts {:type :static}
        min (min w h)
        v-opts {:label true}]
    [{:type "disc" :id id :cx (/ w 3)       :cy (/ h 3)       :r (* min 0.12) :body-opts body-opts :view-opts v-opts}
     {:type "disc" :id (str id "-" 2) :cx (* w (/ 2 3)) :cy (/ h 3)       :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id (str id "-" 3) :cx (* w (/ 2 3)) :cy (* h (/ 2 3)) :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id (str id "-" 4) :cx (/ w 3)       :cy (* h (/ 2 3)) :r (* min 0.12) :body-opts body-opts}]))

(defn rev-test-rig [id w h]
  (let [hw (/ w 2)
        hh (/ h 2)
        j-id id
        b-id (str id "-box")
        d-id (str id "-disc")]
    [{:type "box"  :id b-id :cx hw :cy hh :hw 5 :hh 40 :body-opts {:angle (u/radians 0)}}
     {:type "disc" :id d-id  :cx hw :cy hh :r 5 :body-opts {:type :static} :view-opts {:visible false}}
     {:type "rev-joint" :id j-id :b1-id b-id :b2-id d-id :cx hw :cy hh :joint-opts {:enableMotor true :maxMotorTorque 100000 :motorSpeed 10} :view-opts {:label true}}]))

;; -- DB ----------------------------------------------------------------------
(defn initial-state [el]
  (let [w (.-clientWidth el)
        h (.-clientHeight el)
        hw (/ w 2)
        hh (/ h 2)
        world (pl/world [0 10])
        _ (pl/assemble-in! (rigs/walls "wall" w h) world)
        _ (pl/assemble-in! (rigs/box "box" 25 25 w h) world)
        _ (pl/assemble-in! (rigs/rig-tiny-boxes "mb" 50 w h) world)
        _ (pl/assemble-in! (rigs/rig-tiny-discs "md" 50 w h) world)
        _ (pl/assemble-in! (four-discs "disc" w h) world)
        ; _ (pl/assemble-in! (rigs/spinner "spinner" w h) world)]
        _ (pl/assemble-in! (rev-test-rig "revolute joint" w h) world)]
    {:world world
     :bounds [0 0 w h]}))

;; -- Main UI -----------------------------------------------------------------
(declare overlay)

(defn root-view []
  (fn []
    [:div.demo
      [overlay]
      [views/svg]]))

(defn overlay []
  (fn []
    [:div.overlay
      [:h1 "re-frame + planck.js"]
      [:p "Source on "
        [:a {:href "https://github.com/gamecubate/re-frame-physics"} "GitHub"]]]))

;; -- Entry Point -------------------------------------------------------------
(defn run []
  (let [el (.getElementById js/document "container")]
    (rf/dispatch-sync [:initialize #(initial-state el)])
    (rf/dispatch [:start-engine])
    (re/render-component [root-view] el)))
