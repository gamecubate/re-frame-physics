(ns rfp.app
  (:require [rfp.core.planck :as pl]
            [rfp.core.rigs :as rigs]
            [reagent.core :as re]
            [re-frame.core :as rf]
            [rfp.core.events]
            [rfp.core.subs]
            [rfp.core.views :as views]
            [rfp.core.utils :as u]
            [rfp.core.devtools]))

;; -- An extra rig ------------------------------------------------------------
(defn four-discs [w h]
  "Place a static disc at each intersection of a 3x3 grid"
  (let [body-opts {:type :static}
        min (min w h)]
    [{:type "disc" :id "fd-d1" :cx (/ w 3)       :cy (/ h 3)       :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id "fd-d2" :cx (* w (/ 2 3)) :cy (/ h 3)       :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id "fd-d3" :cx (* w (/ 2 3)) :cy (* h (/ 2 3)) :r (* min 0.12) :body-opts body-opts}
     {:type "disc" :id "fd-d4" :cx (/ w 3)       :cy (* h (/ 2 3)) :r (* min 0.12) :body-opts body-opts}]))

;; -- DB ----------------------------------------------------------------------
(defn initial-state [el]
  (let [w (.-clientWidth el)
        h (.-clientHeight el)
        world (pl/world [0 10])
        _ (pl/assemble-in! (rigs/walls w h) world)
        _ (pl/assemble-in! (rigs/box "b1" 25 25 w h) world)
        _ (pl/assemble-in! (rigs/rig-mini-boxes 50 w h) world)
        _ (pl/assemble-in! (rigs/rig-mini-discs 50 w h) world)
        _ (pl/assemble-in! (four-discs w h) world)]
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
      [:p.disabled "Source on "
        [:a {:href "https://github.com/gamecubate/re-frame-physics"} "GitHub"]]]))

;; -- Entry Point -------------------------------------------------------------
(defn run []
  (let [el (.getElementById js/document "container")]
    (rf/dispatch-sync [:initialize #(initial-state el)])
    (rf/dispatch [:start-engine])
    (re/render-component [root-view] el)))
