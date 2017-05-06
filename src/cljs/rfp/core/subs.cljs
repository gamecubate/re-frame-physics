(ns rfp.core.subs
  (:require [re-frame.core :as rf]
            [rfp.core.planck :as pl]))

(rf/reg-sub
  :bounds
  (fn [db _]
    (-> db
        :bounds)))

(rf/reg-sub
  :shapes
  (fn [db _]
    (for [b (pl/bodies (:world db))]
      (let [{:keys [cx cy angle]} (pl/pos-angle b)
            {:keys [type id r hw hh view-opts]} (pl/user-data b)]
        (case type
          "box"  {:id id :type :box  :cx cx :cy cy :hw hw :hh hh :angle angle :view-opts view-opts}
          "disc" {:id id :type :disc :cx cx :cy cy :r r          :angle angle :view-opts view-opts})))))
