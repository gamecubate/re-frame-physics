(ns rfp.core.planck)

;; -- Conversion (pixels to meters / meters to pixels) ------------------------
;;
(def SCALING 0.01)

(defn px->m [px]
  (* px SCALING))

(defn m->px [m]
  (/ m SCALING))

(def P js/planck)

(defn vec2 [x y]
  (.Vec2 P x y))

;; -- World -------------------------------------------------------------------
(defn world [gravity]
  (let [[gx gy] gravity]
    (.World. P (vec2 gx gy))))

(defn step [world]
  (.step world (/ 1 60)))

(defn bodies [world]
  (loop [b (.getBodyList world)
         res []]
    (if-not b
      res
      (recur (.getNext b)
             (conj res b)))))

(defn fixtures [body]
  (loop [f (.getFixtureList body)
         res []]
    (if-not f
      res
      (recur (.getNext f)
             (conj res f)))))

(defn joints [world]
  (loop [j (.getJointList world)
         res []]
    (if-not j
      res
      (recur (.getNext j)
             (conj res j)))))

;; -- Shapes ------------------------------------------------------------------
(defn box [hw hh]
  (.Box P (px->m hw) (px->m hh)))

(defn circle [radius]
  (.Circle P (px->m radius)))

;; -- Bodies ------------------------------------------------------------------
;; BodyDef
;;
;; type           Body types are static, kinematic, or dynamic. Note: if a dynamic
;;                body would have zero mass, the mass is set to one.
;; position       The world position of the body. Avoid creating bodies at the
;;                origin since this can lead to many overlapping shapes.
;; angle          The world angle of the body in radians.
;; linearVelocity The linear velocity of the body's origin in world
;;                co-ordinates.
;; linearDamping  Linear damping is use to reduce the linear velocity. The
;;                damping parameter can be larger than 1.0 but the damping effect becomes
;;                sensitive to the time step when the damping parameter is large.
;; angularDamping Angular damping is use to reduce the angular velocity.
;;                The damping parameter can be larger than 1.0 but the damping effect
;;                becomes sensitive to the time step when the damping parameter is large.
;; fixedRotation  Should this body be prevented from rotating? Useful for
;;                characters.
;; bullet         Is this a fast moving body that should be prevented from
;;                tunneling through other moving bodies? Note that all bodies are
;;                prevented from tunneling through kinematic and static bodies. This
;;                setting is only considered on dynamic bodies. Warning: You should use
;;                this flag sparingly since it increases processing time.
;; active         Does this body start out active?
;; awake          Is this body initially awake or sleeping?
;; allowSleep     Set this flag to false if this body should never fall
;;                asleep. Note that this increases CPU usage.)
(def body-defaults {:type :dynamic
                    :position (vec2 0 0)
                    :angle 0
                    :linearVelocity (vec2 0 0)
                    :angularVelocity 0
                    :linearDamping 0
                    :angularDamping 0
                    :fixedRotation false
                    :bullet false
                    :gravityScale 1
                    :allowSleep true
                    :awake true
                    :active true
                    :userData nil})

(defn create-body [cx cy opts user-data world]
  (let [opts' (merge opts {:position (vec2 (px->m cx) (px->m cy)) :userData user-data})]
    (.createBody world (clj->js opts'))))

(defn position [body]
  (.getPosition body))

(defn angle [body]
  (.getAngle body))

(defn user-data! [obj data]
  (.setUserData obj (clj->js data)))

(defn user-data [obj]
  (js->clj (.getUserData obj) :keywordize-keys true))

(defn pos-angle [obj]
  (let [pos (.getPosition obj)
        cx (m->px (.-x pos))
        cy (m->px (.-y pos))
        a (angle obj)]
    {:cx cx :cy cy :angle a}))

(defn obj-id [obj world]
  (:id (user-data obj)))

(defn find-body [id world]
  (let [bs (bodies world)]
    (first (filter #(= id (obj-id % world)) bs))))

;; -- Fixtures ----------------------------------------------------------------
;;
;; Definitions
;;
;; A fixture definition is used to create a fixture. This class defines an
;; abstract fixture definition. You can reuse fixture definitions safely.
;;
;; friction           The friction coefficient, usually in the range [0,1]
;; restitution        The restitution (elasticity) usually in the range [0,1]
;; density            The density, usually in kg/m^2
;; isSensor           A sensor shape collects contact information but never
;;                    generates a collision response
;; userData
;; filterGroupIndex   Zero, positive or negative collision group. Fixtures with
;;                    same positive groupIndex always collide and fixtures with
;;                    same negative groupIndex never collide.
;; filterCategoryBits Collision category bit or bits that this fixture belongs
;;                    to. If groupIndex is zero or not matching, then at least
;;                    one bit in this fixture categoryBits should match other
;;                    fixture maskBits and vice versa.
;; filterMaskBits     Collision category bit or bits that this fixture accept
;;                    for collision.
(def fixture-defaults {:userData nil
                       :friction .2
                       :restitution 0
                       :density 1
                       :isSensor false
                       :filterGroupIndex 0
                       :filterCategoryBits 1
                       :filterMaskBits 65535})

(defn create-fixture [body shape opts]
  (.createFixture body shape (clj->js opts)))

;; -- Joints ---------------------------------------------------------
(defn anchor-a [j]
  (let [anchor (.getAnchorA j)
        x (m->px (.-x anchor))
        y (m->px (.-y anchor))]
    {:ax x :ay y}))

(defn anchor-b [j]
  (let [anchor (.getAnchorB j)
        x (m->px (.-x anchor))
        y (m->px (.-y anchor))]
    {:bx x :by y}))

;; -- Revolute Joints ---------------------------------------------------------
;;
;; Definitions
;;
;; TODO Document
(def rev-joint-defaults {:lowerAngle 0
                         :upperAngle 0
                         :maxMotorTorque 0
                         :motorSpeed 0
                         :enableLimit false
                         :enableMotor false})

;; -- Render Hints ------------------------------------------------------------
;;
;; visible        Should renderer show this body?
;; dot            Should a dot be added to rendered view to help show angles?
;;
(def view-defaults {:visible true
                    :dot false
                    :label false})

;; -- Makers ------------------------------------------------------------------
(defn assemble-disc' [spec world]
  ; (.log js/console "assemble-disc'" spec)
  (let [{:keys [type id cx cy r body-opts fixt-opts view-opts]} spec
        body-opts' (merge body-defaults body-opts)
        fixt-opts' (merge fixture-defaults fixt-opts)
        view-opts' (merge view-defaults view-opts)
        user-data (assoc spec :body-opts body-opts' :fixt-opts fixt-opts' :view-opts view-opts')
        body (create-body cx cy body-opts' user-data world)
        circ (circle r)
        fixt (create-fixture body circ fixt-opts')]
    fixt))

(defn assemble-box' [spec world]
  ; (.log js/console "assemble-box'" spec)
  (let [{:keys [type id cx cy hw hh body-opts fixt-opts view-opts]} spec
        body-opts' (merge body-defaults body-opts)
        fixt-opts' (merge fixture-defaults fixt-opts)
        view-opts' (merge view-defaults view-opts)
        user-data (assoc spec :body-opts body-opts' :fixt-opts fixt-opts' :view-opts view-opts')
        body (create-body cx cy body-opts' user-data world)
        box  (box hw hh)
        fixt (create-fixture body box fixt-opts')]
    fixt))

(defn assemble-rev-joint' [spec world]
  ; (.log js/console "assemble-rev-joint'" spec)
  (let [{:keys [type id b1-id b2-id cx cy joint-opts view-opts]} spec
        joint-opts' (merge rev-joint-defaults joint-opts)
        view-opts' (merge view-defaults view-opts)
        user-data (assoc spec :joint-opts joint-opts' :view-opts view-opts')
        b1 (find-body b1-id world)
        b2 (find-body b2-id world)
        anchor (vec2 (px->m cx) (px->m cy))
        joint (.RevoluteJoint P (clj->js joint-opts') b1 b2 anchor)
        world-joint (.createJoint world joint)
        _ (.setUserData world-joint user-data)]
    world-joint))

(defn assemble-in! [specs world]
  (doseq [s specs]
    (let [{:keys [type]} s]
      (case type
        "disc"       (assemble-disc' s world)
        "box"        (assemble-box' s world)
        "rev-joint"  (assemble-rev-joint' s world)))))
