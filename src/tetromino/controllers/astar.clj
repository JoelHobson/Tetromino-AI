(ns tetromino.controllers.astar
  (:require [tetromino.game])
  (gen-class))  
  
(refer 'game)

(defn height-penalty
  "Gets the height penalty for a tetromino. Takes a state as a parameter."
  [{y :y {block :block size :size} :tetromino}]
  (let [height-pairs (map vector block (range y (+ y size)))] ;Pair each row of the block with a number indicating the height from the top
    (reduce + (for [pair height-pairs] ;Sum the following sequence
                (let [value (- *HEIGHT* (second pair))] ;Convert the y coordinate to the distance from the bottom of the field       
                  (reduce + (map #(* value %) (first pair)))))))) ;Multiply and sum each row by its height from the bottom. 

  
(defn count-gaps
  "Counts the gaps created below a block when it's placed on the field"
  [state]
  (if (< (:y state) 0) 0
  (let [size (-> state :tetromino :size)
        y (:y state)
        x (:x state)
        field (:field (insert-block state))
        columns (for [row (drop y field)]
                  (take size (drop x row)))]
    (count (filter zero? (flatten (rest columns))))))) ;ignore the first row, we don't care about it
    
(defn greatest-y
  "Lowers a block until it hits something. Returns the last non-colliding y"
  [state]
  (if (collision? state) (dec (:y state))
    (recur (assoc state :y (inc (:y state))))))

(defn multi-rot
  "Rotates a block r times"
  [r state]
  (if (zero? r) state
    (recur (dec r) (rotate state))))

(defn completes-rows
  "Returns the number of rows that would be cleared in a field"
  [field]
  (count (filter true? (for [row field]
                         (full-row? row)))))
  

(defn get-costs
  "Returns the move with the lowest heuristic cost"
  [state]
  (let [block (:tetromino state)
        costs (for [x (range -2 *WIDTH*)]
                (for [r (range (inc (:rotations block)))]
                  (let [state (multi-rot r state)
                        depth (greatest-y (assoc state :x x))
                        ;Any of the multipliers on the following lines can be changed to adjust the weights of the other heuristics
                        r (* 1 r) ;The number of rotations
                        height-cost (* 5 (height-penalty (assoc state :x x :y depth)))
                        distance (* 1 (Math/abs (- (:x state) x))) ;The x distance from the final position
                        gaps (* 5 (count-gaps (assoc state :x x :y depth))) ;The number of gaps created by placing this block
                        rows (* -10 (completes-rows (:field (insert-block state)))) ;The number of rows cleared by placing this block
                        cost (+ height-cost r distance gaps rows)] ;The final cost
                    (if (collision? (assoc state :x x :y depth)) 
                      nil
                      {:cost cost :x x :y depth :rotations r}))))]  
    (first (sort-by :cost (filter (comp not nil?) (flatten costs))))))


(defn get-move
  [state]
  (let [cost (get-costs state)
        x (:x cost)
        y (:y cost)
        r (:rotations cost)]
    (do (Thread/sleep 25) ;Sets a maximum speed for each move.
      (cond (> r 0) (rotate state)
            (< x (:x state)) (move-left state)
            (> x (:x state)) (move-right state)
            (< y (:y state)) (move-down state)
            :else (assoc state :moves 0)))))
