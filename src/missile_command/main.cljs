(ns missile-command.main
  (:require [missile-command.render :as r]
            [missile-command.game :as g]))

(defonce state (atom nil))

(defn reset-graph-ctx [state]
  (let [c (js/document.getElementById "main")
        w (.-clientWidth c)
        h (.-clientHeight c)
        g (.getContext c "2d")]
    (doto c
      (-> .-width (set! w))
      (-> .-height (set! h)))
    (doto g
      (.scale (/ w r/width) (/ h r/height)))
    (assoc state :g g :w w :h h)))

(def key-handlers {"KeyA"   (fn [_] (swap! state g/fire :a))
                   "KeyS"   (fn [_] (swap! state g/fire :s))
                   "KeyD"   (fn [_] (swap! state g/fire :d))
                   "Space"  (fn [_] (swap! state g/pause))
                   "Escape" (fn [_] (swap! state g/exit))
                   "F1"     (fn [_] (swap! state update :debug? not))})

(defn keydown! [e]
  (js/console.log (-> e .-code))
  (when-let [handler (-> e .-code key-handlers)]
    (.preventDefault e)
    (when-not (-> e .-repeat)
      (handler e))))

(defn mouse-move! [e]
  (let [s  @state
        w  (:w s)
        h  (:h s)
        cx (-> e .-clientX)
        cy (-> e .-clientY)
        x  (* cx (/ r/width w))
        y  (* cy (/ r/height h))]
    (swap! state g/mouse-move x y)))

(defn mouse-leave! [_]
  (swap! state g/mouse-move nil nil))

(defn resize! [_]
  (swap! state reset-graph-ctx))

(defn game-step [state ts]
  (-> state
      (g/game-step ts)
      (r/render)))

(defn animation [ts]
  (swap! state game-step ts)
  (js/window.requestAnimationFrame animation))

(when-not @state
  (js/console.log "Initializing game...")
  (doto js/window
    (.addEventListener "resize" resize!)
    (.addEventListener "keydown" keydown!))
  (doto (js/document.getElementById "main")
    (.addEventListener "mousemove" mouse-move!)
    (.addEventListener "mouseleave" mouse-leave!))
  (reset! state (-> {}
                    (reset-graph-ctx)
                    (g/reset-game-state)
                    (assoc :debug? false)))
  (animation 0))

(js/console.log "Game running...")
