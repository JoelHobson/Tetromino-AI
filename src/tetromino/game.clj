(ns game)

;Globals
(def *WIDTH* 10) ;The width of the game field
(def *HEIGHT* 22) ;The height of the game field
(def *X* (- (/ *WIDTH* 2) 1)) ;The initial x position of a block
(def *INITIAL-MOVES* 10) ;The number of moves you can make before the block is forced to drop.
;This doesn't do anything for any of the current controllers.

(defstruct tetromino :rotations :colour :size :block)
(defstruct game-state :x :y :tetromino :next-tetromino :field :score :level :moves)

(def square-piece (struct tetromino 
                    0
                    2
                    2
                  [[1 1]
                   [1 1]]))

(def line-piece (struct tetromino 
                    4
                    3
                    4
                [[0 0 0 0]
                 [1 1 1 1]
                 [0 0 0 0]
                 [0 0 0 0]]))

(def l-piece  (struct tetromino 
                  4
                  4
                  3
                [[0 0 1]
                 [1 1 1]
                 [0 0 0]]))

(def r-piece (struct tetromino 
               4
               5
               3
               [[1 0 0]
                [1 1 1]
                [0 0 0]]))

(def lz-piece (struct tetromino 
                4
                6
                3
                [[0 1 1]
                 [1 1 0]
                 [0 0 0]]))

(def rz-piece (struct tetromino 
                4
                7
                3
                [[1 1 0]
                 [0 1 1]
                 [0 0 0]]))

(def t-piece (struct tetromino 
               4
               8
               3
               [[0 1 0]
                [1 1 1]
                [0 0 0]]))
  
(def blocks [square-piece line-piece l-piece r-piece lz-piece rz-piece t-piece])

(defn rotate
  "Rotate a block"    
  [{{block :block :as tetro} :tetromino :as state}]
  (assoc state :moves (dec (:moves state)) :tetromino (assoc tetro :block  (->> block reverse (apply map vector) (into [])))))

(defn move-left
  [{x :x :as state}]
  (assoc state :moves (dec (:moves state)) :x (dec x)))

(defn move-right
  [{x :x :as state}]
  (assoc state :moves (dec (:moves state)) :x (inc x)))

(defn move-down
  [{y :y :as state}]
  (assoc state :moves (dec (:moves state)) :y (inc y)))

(defn init-field
  "Create an empty game field"
  []
  (let [row (into [] (repeat *WIDTH* 0))
        field (into [] (repeat *HEIGHT* row))]
   field))  

(defn get-block
  "Gets a random block"
  []
  (first (shuffle blocks)))

(defn new-game
  "Initialize a state struct for a new game."
  []
  (let [x (- (/ *WIDTH* 2) 1)
        y 0
        block (get-block)
        next-block (get-block)
        field (init-field)]
    (struct game-state x y block next-block field 0 1 *INITIAL-MOVES*)))


(defn add-sub
  "Add a smaller vector to a larger one starting at pos. "
  [pos insert orig] 
  (let [pos (if (< pos 0) 0 pos)
        start (take pos orig)
        until-end (+ pos (count insert))
        overwrite (take until-end (drop pos orig))
        end (drop until-end orig)]
      (into [] (concat start (map max insert overwrite) end))))


(defn trim-block
  "Removes the parts of a block that are outside of the field"
  [x y block size]
  (cond (>= (+ y size) *HEIGHT*) (trim-block x 0 (take (- size (- (+ y size) *HEIGHT*)) block) size)
        (>= (+ x size) *WIDTH*) (trim-block 0 0 (map #(take (- size (- (+ x size) *WIDTH*)) %) block) size)
        (< x 0) (trim-block 0 0 (map #(drop (Math/abs x) %) block) size)        
        :else block)) 
          
(defn insert-block 
  "Inserts the current block in state"
  [{:keys [x y] 
    {block :block size :size colour :colour} :tetromino 
    field :field 
    score :score
    level :level 
    moves :moves :as state}]
  (let [block (trim-block x y block size)
        size (count block)
        block-indicies (range size)
        field-indicies (range y (+ y size))
        pairs (map vector block-indicies field-indicies)
        add-colour #(* colour %)
        insertion (fn [a b] (add-sub x (map add-colour (nth block a)) (nth field b)))
        rows (into [] (map insertion block-indicies field-indicies))      
        new-field (reduce #(assoc %1 (second %2) (rows (first %2))) field pairs)]
   (struct game-state *X* 0 (:tetromino state) (:next-tetromino state) new-field score level moves))) 

(defn outside?
  "Check if any non-zero part of a block is outside the field"
  [x y block size]
  (let [hori (apply map + block)
        vert (map #(reduce + %) block)
        l-zeros (count (take-while zero? hori))
        r-zeros (count (take-while zero? (reverse hori)))
        v-zeros (count (take-while zero? (reverse vert)))
        outside-x-left? (cond (>= x 0) false
                              (< l-zeros (Math/abs x)) true
                              :else false)                                 
        outside-x-right? (cond (< (+ x size) *WIDTH*) false
                               (< r-zeros (- (+ x size) *WIDTH*)) true                              
                               :else false)
        outside-y? (cond (< (+ y size) *HEIGHT*) false                         
                         (< v-zeros (- (+ y size) *HEIGHT*)) true
                         :else false)]
    (or outside-x-left? outside-x-right? outside-y?)))
                  

(defn collision?  
  "Check if there's a collision. Takes a state struct as a parameter"
  [{:keys [x y] 
    field :field
    {:keys [block size]} :tetromino}]
  (if (outside? x y block size) true
    (let [block (trim-block x y block size)
          overlap-rows (take size (drop y field))
          overlap-square (for [row overlap-rows] (take (count (first block)) (drop x row)))
          collisions (map #(or (zero? %1) (zero? %2)) (flatten block) (flatten overlap-square))]
      (if (reduce #(and %1 %2) true collisions) false
        true))))
                                                
  
(defn full-row?
  "Returns true if a row is full - if all elements are non-zero"
  [row]
  (not-any? zero? row))

(defn remove-rows
  "Remove completed rows from the playfield."
  [{:keys [field score] :as state}]
  (let [removed (remove full-row? field)
        line-count (- *HEIGHT* (count removed))
        blanks (into [] (for [x (range line-count)] (into [] (repeat *WIDTH* 0))))
        new-field (into [] (concat blanks removed))]
    (assoc state :field new-field :score (+ score line-count))))

  
