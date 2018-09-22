(ns missile-command.render)

(def ^:const PI Math/PI)
(def ^:const PIx2 (* PI 2.0))
(def ^:const PIp2 (/ PI 2.0))

(def ^:const width 800)
(def ^:const height 450)
(def ^:const city-y 430)

(def ^:const pointer-style "#3f3")
(def ^:const defending-style "#aaa")

(defn clear [g]
  (doto g
    (.clearRect 0 0 width height)))

(defn pointer [g x y]
  (when (and x y)
    (doto g
      (-> .-strokeStyle (set! pointer-style))
      (.beginPath)
      (.ellipse x y 10 10 0 0 PIx2 true)
      (.stroke))))

(defn defenders [g defenders]
  (doto g
    (-> .-strokeStyle (set! defending-style))
    (.beginPath))
  (doseq [defender defenders]
    (doto g
      (.moveTo (-> defender :x1) (-> defender :y1))
      (.lineTo (-> defender :x2) (-> defender :y2))))
  (doto g
    (.stroke)))

(defn state->debug [state]
  (concat ["Cities:"]
          (->> state :cities (map (fn [[id {:keys [missiles damage]}]]
                                    (str "  " id ": "
                                         "m: " missiles " "
                                         "d: " damage))))
          [(str "Defenders (" (-> state :defenders count) "):")]
          (->> state :defenders (map (fn [{:keys [x1 y1 x2 y2 top]}]
                                       (str "  "
                                            (.toFixed x1 0) " "
                                            (.toFixed y1 0) " -> "
                                            (.toFixed x2 0) " "
                                            (.toFixed y2 0) " "
                                            "top: " (.toFixed top)))))))

(defn debug [g state]
  (when (-> state :debug?)
    (doto g
      (-> .-fillStyle (set! "#777"))
      (-> .-font (set! "16px sans-serif")))
    (doseq [[y text] (map-indexed vector (state->debug state))]
      (.fillText g text 10 (* (inc y) 20)))))

(defn render [state]
  (doto (:g state)
    (clear)
    (pointer (:x state) (:y state))
    (defenders (:defenders state))
    (debug state))
  state)

