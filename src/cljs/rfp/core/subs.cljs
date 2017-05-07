(ns rfp.core.subs
  (:require [re-frame.core :as rf]
            [rfp.core.planck :as pl]))

(rf/reg-sub
  :bounds
  (fn [db _]
    (-> db
        :bounds)))

(rf/reg-sub
  :bodies
  (fn [db _]
    (for [b (pl/bodies (:world db))]
      (let [{:keys [cx cy angle]} (pl/pos-angle b)
            {:keys [type id r hw hh view-opts]} (pl/user-data b)]
        ; (.log js/console (pl/obj-id b (:world db)))
        (case type
          "box"  {:id id :type :box  :cx cx :cy cy :hw hw :hh hh :angle angle :view-opts view-opts}
          "disc" {:id id :type :disc :cx cx :cy cy :r r          :angle angle :view-opts view-opts})))))

(rf/reg-sub
  :joints
  (fn [db _]
    (for [j (pl/joints (:world db))]
      (let [{:keys [type id cx cy view-opts]} (pl/user-data j)
            {:keys [ax ay]} (pl/anchor-a j)
            {:keys [bx by]} (pl/anchor-b j)]
        ; (.log js/console (pl/user-data j))
        ; (.log js/console (.getType j))
        ; (.log js/console (.getAnchorA j) ax ay)
        ; (.log js/console (.getAnchorB j) bx by)
        ; (.log js/console (pl/anchorA j))
        ; (.log js/console (pl/anchorB j))
        ; (let [{:keys [cx cy angle]} (pl/pos-angle b)
        ;       {:keys [type id r hw hh view-opts]} (pl/user-data b)]
        (case type
          "rev-joint" {:id id :type :rev-joint :cx cx :cy cy :ax ax :ay ay :bx bx :by by :view-opts view-opts})))))
