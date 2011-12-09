(ns tetromino.controllers.human
  (:require [tetromino.game])
  (:import (java.awt.event KeyAdapter))
  (gen-class))

(refer 'game)

(def move (atom :nothing))
(def valid-state (atom nil))
(def final-choice (atom :nothing))

(defn do-nothing [x] x)

(def moves {:left move-left :right move-right 
            :rotate rotate  :drop move-down 
            :nothing do-nothing})

(def *SLEEP-TIME* 25)

(defn try-move
  [state]
  (let [m @move
        attempt ((m moves) state)]    
    (if (not= m :nothing)
      (if (not (collision? attempt)) 
        (do 
          (reset! valid-state attempt)
          (reset! final-choice @move)
          (reset! move :nothing))))))


(defn get-move
  "Give player the state and deal with their response"
  [state]
  (let [keep-going (atom true)
        start-time (System/currentTimeMillis)]
    (do
      (while (and @keep-going (= @valid-state nil))
        (do                                       
          (if (> (- (System/currentTimeMillis) start-time) *SLEEP-TIME*)
            (reset! keep-going false))
          (try-move state)))
      (if (nil? @valid-state) (assoc state :moves (dec (:moves state)))
        (do (let [new-state @valid-state] 
              (reset! valid-state nil)               
              new-state))))))
                         
(defn listener
  [keyevent]
  (let [c (.getKeyChar keyevent)]    
    (cond (= c \j) (reset! move :left)
          (= c \l) (reset! move :right)
          (= c \i) (reset! move :rotate)
          (= c \k) (reset! move :drop))))



(defn make-keylistener
  []
  (proxy [KeyAdapter] []
    (keyPressed [event]
                (listener event))))
  