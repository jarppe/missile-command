(ns missile-command.game
  (:require [missile-command.render :as r]))

(def ^:const defending-v 0.1)

(defn make-city [x]
  {:missiles 16
   :damage   0
   :x        x})

(defn reset-game-state [state]
  (merge state {:incoming  ()
                :defenders ()
                :cities    {:a (make-city 100)
                            :s (make-city 400)
                            :d (make-city 700)}
                :run       true
                :debug?    true}))

(defn pause [state]
  (update state :run not))

(defn exit [state]
  (reset-game-state state))

(defn angle [x1 y1 x2 y2]
  (Math/atan2 (- y2 y1) (- x2 x1)))

(defn make-defending [ts x1 y1 x2 y2]
  (let [a  (angle x1 y1 x2 y2)
        vx (* (Math/cos a) defending-v)
        vy (* (Math/sin a) defending-v)]
    {:created ts
     :x1      x1
     :y1      y1
     :x2      x1
     :y2      y1
     :top     y2
     :vx      vx
     :vy      vy}))

(defn fire [state city-id]
  (let [city     (-> state :cities city-id)
        missiles (-> city :missiles)
        x1       (-> city :x)
        y1       r/city-y
        x2       (-> state :x)
        y2       (-> state :y)]
    (if (and x1 y1 (pos? missiles))
      (-> state
          (assoc-in [:cities city-id :missiles] (dec missiles))
          (update :defenders conj (make-defending (-> state :ts)
                                                  x1 y1
                                                  x2 y2)))
      state)))

(defn update-defender [ts defender]
  (let [age (- ts (-> defender :created))
        dx  (* (:vx defender) age)
        x2  (+ (:x1 defender) dx)
        dy  (* (:vy defender) age)
        y2  (+ (:y1 defender) dy)]
    (when (> y2 (:top defender))
      (assoc defender :x2 x2 :y2 y2))))

(defn update-defenders [defenders ts]
  (keep (partial update-defender ts) defenders))

(defn game-step [state ts]
  (-> state
      (assoc :ts ts)
      (update :defenders update-defenders ts)))

(defn mouse-move [state x y]
  (let [x1 400
        y1 450
        x2 x
        y2 y
        a  (angle x1 y1 x2 y2)
        vx (* (Math/cos a) defending-v)
        vy (* (Math/sin a) defending-v)
        x2 (+ x1 (* vx 10))
        y2 (+ y1 (* vy 10))]
    (assoc state :x x, :y y
                 :foo-x1 x1
                 :foo-y1 y1
                 :foo-x2 x2
                 :foo-y2 y2)))
