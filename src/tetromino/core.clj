(ns tetromino.core
  (:require [tetromino.controllers.human :as human] 
            [tetromino.controllers.random :as random]
            [tetromino.controllers.astar :as astar]
            [tetromino.game] 
            [clojure.contrib.swing-utils]
            [clojure.contrib.trace])
  (:import [tetromino.gui AppWindow TDisplay Block] 
           [java.awt Dimension]
           [java.util Calendar])
  (:gen-class))

(refer 'game)
(refer 'clojure.contrib.swing-utils)
(refer 'clojure.contrib.trace)


(def window (atom nil))

(defstruct player :name :display :controller :state )

(defn make-TDisplay
  "Creates a TDisplay object of X and Y size for the field and with a name of playername"
  [x y playername]
  (doto (TDisplay. (Dimension. x y))
    (.setName playername)))


(defn make-display 
  "Creates a display of the appropriate size"
  [playername]
  (make-TDisplay (* Block/SIZE *WIDTH*) 
                 (* Block/SIZE *HEIGHT*)
                 playername))
  
(defn add-display
  "Add a display to the window"
  [disp]
  (do-swing (. @window addTDisplay disp)))

(defn make-block-arr
  "Create an array of Block objects from a playfield to be drawn"
  [state]
  (let [height (count state)
        width (count (first state))]
    (into-array Block 
                (flatten 
                  (for [j (range height)]
                    (for [i (range width)]
                      (Block. i j ((state j) i))))))))
  
(defn draw-state
  "Draws the current state."
  [state display]
  (do-swing
    (->> state :field make-block-arr (.drawBlocks display))
    (->> state :next-tetromino :block make-block-arr (.drawNextBlock display))    
    (->> state :score (.setScore display))
    (->> state :level (.setLevel display))))


(defn create-player
  "Creates a player, takes a controller function as an argument"
  [playername controller]
  (let [disp (make-display playername)
        state (new-game)]
    (do
      (add-display disp)
      (draw-state state disp)
      (struct player playername disp controller state))))

(defn create-human-player
  "Creates a human player and does the extra setup needed to get keyboard input.
   If multiple controllers are used at once, this must be added last, otherwise keyboard input fails."
  []
  (let [player (create-player "i j k l to control" human/get-move)
        display (:display player)]
    (do 
      (do-swing (.addKeyListener display (human/make-keylistener)))
      player)))
   
(defn end-turn
  "Handle the end of a turn - switching to the next block"
  [player state]
  (let [new-state (->> state insert-block remove-rows)]
    (assoc new-state :moves *INITIAL-MOVES* :tetromino (:next-tetromino new-state) :next-tetromino (get-block)))) 


(defn run-game
  "Runs the game"
  [{state :state disp :display :as player}]    
  (loop [state state]
    (do    
      (draw-state (insert-block state) disp) ;Update the current display
      (cond (and (= (:y state) 0) (= (:x state) *X*) (collision? state)) (println "Game over") ;We lost the game
            (> (:moves state) 0) (recur ((:controller player) state)) ;Get the next move from the controller              
            (collision? (move-down state)) (recur (end-turn player state)) ;Out of moves, there's a collision below us so end the turn
            :else (recur (assoc (move-down state) :moves *INITIAL-MOVES*)))))) ;Drop the piece, we're out of moves but our turn is still going
  

(defn -main
	[& args]
	(do 
	  (. AppWindow launchWindow) ;Create the window for the game
	  (reset! window (. AppWindow window))
	  ;Create all the players in the game. Multiple players can be run concurrently, but aren't by default due to performance issues. 
	  ;(create-player "Random" random/get-move)  (create-human-player)
	  (let [players [(create-player "A*" astar/get-move)]]
		  (pmap run-game players))))