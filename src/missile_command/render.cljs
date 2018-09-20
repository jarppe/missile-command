(ns missile-command.render)

(def ^:const circle-style "#333")
(def ^:const pointer-style "#aaa")

(defn render [{:as state :keys [g w h off-x off-y r size]}]
  (doto g
    (.clearRect 0 0 w h)
    (-> .-strokeStyle (set! pointer-style))
    (.beginPath)
    (.moveTo 0 0)
    (.lineTo w h)
    (.stroke))
  state)

