(ns missile-command.game
  (:require [missile-command.render :as r]))

(def ^:const defending-v 0.09)
(def ^:const offending-v 0.06)

(defn make-city [x]
  {:missiles 16
   :damage   0
   :x        x})

(defn reset-game-state [state]
  (merge state {:elements ()
                :cities   {:a (make-city 100)
                           :s (make-city 400)
                           :d (make-city 700)}
                :run      true}))

(defn pause [state]
  (update state :run not))

(defn exit [state]
  (reset-game-state state))

(defn mouse-move [state x y]
  (assoc state :x x, :y y))

(defn angle [x1 y1 x2 y2]
  (Math/atan2 (- y2 y1) (- x2 x1)))

(defn make-offender [ts]
  (let [x1 (* (rand) r/width)
        y1 0
        x2 (* (rand) r/width)
        y2 r/city-y
        a  (angle x1 y1 x2 y2)
        vx (* (Math/cos a) offending-v)
        vy (* (Math/sin a) offending-v)]
    {:type    :offender
     :created ts
     :style   r/offender-style
     :x1      x1
     :y1      y1
     :x2      x1
     :y2      y1
     :cont    (fn [{:keys [y2] :as self}]
                (when (< y2 r/city-y)
                  self))
     :vx      vx
     :vy      vy}))

(defn make-defender [ts x1 y1 x2 y2]
  (let [a   (angle x1 y1 x2 y2)
        vx  (* (Math/cos a) defending-v)
        vy  (* (Math/sin a) defending-v)
        top y2]
    {:type    :defender
     :created ts
     :style   r/defender-style
     :x1      x1
     :y1      y1
     :x2      x1
     :y2      y1
     :cont    (fn [{:keys [y2] :as self}]
                (when (> y2 top)
                  self))
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
          (update :elements conj (make-defender (-> state :ts)
                                                x1 y1
                                                x2 y2)))
      state)))

(defn update-element [ts element]
  (let [age     (- ts (-> element :created))
        dx      (* (:vx element) age)
        x2      (+ (:x1 element) dx)
        dy      (* (:vy element) age)
        y2      (+ (:y1 element) dy)
        element ((:cont element) (assoc element :x2 x2 :y2 y2))]
    (when element
      (cons element nil))))

(defn make-offenders [elements ts]
  (when (and (->> elements
                  (filter (comp (partial = :offender) :type))
                  (count)
                  (> 10))
             (->> (Math/random)
                  (< 0.995)))
    [(make-offender ts)]))

(defn update-elements [elements ts]
  (->> elements
       (mapcat (partial update-element ts))
       (concat (make-offenders elements ts))))

(defn game-step [state ts]
  (-> state
      (assoc :ts ts)
      (update :elements update-elements ts)))

