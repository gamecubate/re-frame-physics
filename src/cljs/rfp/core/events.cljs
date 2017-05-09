(ns rfp.core.events
  (:require [re-frame.core :as rf]
            [rfp.core.planck :as pl]
            [rfp.core.utils :as u]))

(rf/reg-fx
  :cancel-raf
  (fn [raf]
    (when raf
      (.cancelAnimationFrame js/window raf))))

(rf/reg-fx
  :cancel-timers
  (fn [timer-ids]
    (doseq [id timer-ids]
      (.clearInterval js/window id))))

(rf/reg-fx
  :step-world
  (fn [w]
    (pl/step w)))

(rf/reg-event-fx
  :init
  (fn [{{:keys [raf timer-ids]} :db} [_ loader]]
    {:db (loader)
     :cancel-raf raf
     :cancel-timers timer-ids
     :dispatch-later [{:ms 500 :dispatch [:start-physics]}]}))

(rf/reg-event-fx
  :start-physics
  (fn [cofx _]
    (let [db (:db cofx)
          raf (.requestAnimationFrame js/window #(rf/dispatch [:tick %]))]
      {:db (assoc-in db [:raf] raf)})))

(rf/reg-event-fx
  :tick
  (fn [cofx _]
    (let [db (:db cofx)
          world (:world db)
          raf (:raf db)]
      {:step-world world
       :cancel-raf raf
       :db (assoc-in db [:raf] (.requestAnimationFrame js/window #(rf/dispatch [:tick %])))})))
