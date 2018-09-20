(ns missile-command.main
  (:require [missile-command.render :as r]))

(defonce ctx (atom nil))

(defn initial-ctx [g w h]
  (let [r     (Math/min (/ w 8.0) (/ h 3.0))
        off-x (/ (- w (* r 8.0)) 2.0)
        off-y (/ (- h (* r 3.0)) 2.0)
        size  (* (/ r 2.0) 0.94)]
    (js/console.log "init:"
                    "w:" w
                    "h:" h
                    "r:" r
                    "off-x:" off-x
                    "off-y:" off-y
                    "size:" size)
    {:g     g
     :w     w
     :h     h
     :off-x off-x
     :off-y off-y
     :r     r
     :size  size}))

(defn reset-ctx! []
  (let [canvas (js/document.getElementById "app")
        g      (.getContext canvas "2d")
        width  (.-clientWidth canvas)
        height (.-clientHeight canvas)]
    (doto canvas
      (-> .-width (set! width))
      (-> .-height (set! height)))
    (swap! ctx merge (initial-ctx g width height))))

(defn animation [_]
  (swap! ctx r/render)
  (js/window.requestAnimationFrame animation))

(when-not @ctx
  (.addEventListener js/window "resize" reset-ctx!)
  (reset-ctx!)
  (animation 0))

(js/console.log "Here we go again...")
