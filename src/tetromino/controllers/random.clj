(ns tetromino.controllers.random
  (:require [tetromino.game])
  (gen-class))
  
(refer 'game)

(defn get-move 
  [state]  
  (do 
    (Thread/sleep 100)
    (let [options [move-left move-right rotate move-down]]
      (loop [move ((first (shuffle options)) state)]
        (if (collision? move) (recur ((first (shuffle options)) state))
          move)))))
    