(ns rfp.core.events
  (:require [re-frame.core :as rf]
            [rfp.core.planck :as pl]
            [rfp.core.utils :as u]))

(rf/reg-event-db
  :initialize
  (fn [_ [_ f]]
    (f)))

(rf/reg-event-fx
  :start-engine
  (fn [cofx _]
    (let [db (:db cofx)
          raf (.requestAnimationFrame js/window #(rf/dispatch [:tick %]))]
      {:db (assoc-in db [:raf] raf)
       :dispatch-later [{:ms 2000 :dispatch [:start-gravity-fx]}]})))

(rf/reg-event-fx
  :tick
  (fn [cofx [_ dt]]
    (let [db (:db cofx)
          {:keys [world raf]} db]
      (.cancelAnimationFrame js/window raf)
      (pl/step world)
      {:db (assoc-in db [:raf] (.requestAnimationFrame js/window #(rf/dispatch [:tick %])))})))

(rf/reg-event-fx
  :start-gravity-fx
  (fn []
    (.setInterval js/window #(rf/dispatch [:change-gravity]) (u/rand-int-3 1000 3000))
    {}))

(rf/reg-event-fx
  :change-gravity
  (fn [cofx _]
    (let [db (:db cofx)
          world (:world db)
          grav (pl/vec2 (u/rand-int-3 -10 10) (u/rand-int-3 -10 10))]
      (.setGravity world grav))))
