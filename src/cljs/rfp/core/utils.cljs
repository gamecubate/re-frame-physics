(ns rfp.core.utils)

(def PI (.-PI js/Math))
(def TWO_PI (* PI 2))
(def HALF_PI (/ PI 2))

(defn radians [degrees]
  (* degrees (/ PI 180)))

(defn degrees [radians]
  (* radians (/ 180 PI)))

(defn sin [n]
  (.sin js/Math n))

(defn cos [n]
  (.cos js/Math n))

(defn abs [n]
  (.abs js/Math n))

(defn round [n]
  (.round js/Math n))

(defn ceil [n]
  (.ceil js/Math n))

(defn floor [n]
  (.floor js/Math n))

(defn between? [n min max]
  (and (>= n min) (<= n max)))

(defn mid [min max]
  (+ min (/ (- max min) 2)))

(defn lower-mult-of-10 [n]
  (* 10 (.floor js/Math (/ n 10))))

(defn round-to [digits n]
  (let [coeff (.pow js/Math 10 digits)
        n' (* n coeff)
        n'' (round n')]
    (/ n'' coeff)))

(defn sum [nums]
  (reduce + nums))

(defn average [nums]
  (/ (sum nums) (count nums)))

(defn median [nums]
  (let [sorted (sort nums)
        n (count sorted)]
    (if (even? n)
      (average
        (vector
          (nth sorted (dec (/ n 2)))
          (nth sorted (/ n 2))))
      (nth sorted (floor (/ n 2))))))

(defn rand-int-2 [min limit]
  (+ (rand-int (- limit min)) min))

(defn rand-int-3 [min max]
  (rand-int-2 min (inc max)))
