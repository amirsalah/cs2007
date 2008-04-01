#| Part1 |#

;Insert new node to the existing tree t
(define (insert x t)
    (cond ((null? t) (makeTree x '() '()))
          ((= x (entry t)) t)
          ((< x (entry t)) 
           (makeTree (entry t) (insert x (leftTree t)) (rightTree t)))
          ((> x (entry t))
           (makeTree (entry t) (leftTree t) (insert x (rightTree t))))
  ))

;Return the entry of the specified tree
(define (entry tree)
  (car tree))

;The same as "cadr"
(define (leftTree tree)
  (cadr tree))

;Return the right sub-tree, the same as "caddr", rather than "cddr"
(define (rightTree tree)
  (caddr tree))
;  (car (cdr (cdr tree))))

(define (makeTree entry left right)
  (list entry left right))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(define (print-tree tree)
  (let ((makeNullNode (list '()))
        )
    (cond ((null? tree) '())
          ((and (null? (leftTree tree)) (null? (rightTree tree))) (list (entry tree)))
          (else
           (list (entry tree) 
                 (print-tree (leftTree tree)) 
                 (print-tree (rightTree tree))))))
  )
          
           
(define sampleTree (list 7 (list 3 (list 1 '() '()) (list 5 '() '())) (list 9 '() (list 11 '() '()))))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


#| Part2 |#
(define (insert2 order x t)
    (cond ((null? t) (makeTree x '() '()))
          ((order x (entry t))
           (makeTree (entry t) (insert2 order x (leftTree t)) (rightTree t)))
          ((order (entry t) x)
           (makeTree (entry t) (leftTree t) (insert2 order x (rightTree t))))
          (else t)
  ))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; 

#| Part3 |#
;Make a tree given a unordered list
(define (mktree order items_list)
  (cond ((= (length items_list) 1) (makeTree (car items_list) '() '()))
         (else 
          (insert2 order (car items_list) (mktree order (cdr items_list))))
         )
  )
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#| Part4 |#
;Tree traversal with per-order, in-order and post-order
(define (pre-order tree)
  (cond ((= (length tree) 1) (entry tree))
        ((= (length tree) 0) '())
      (else (append (list (entry tree)) (pre-order (leftTree tree)) (pre-order (rightTree tree))))
      ))

(define (in-order tree)
  (cond ((= (length tree) 1) (entry tree))
        ((= (length tree) 0) '())
      (else (append (in-order (leftTree tree)) (list (entry tree)) (in-order (rightTree tree))))
      ))  

(define (post-order tree)
    (cond ((= (length tree) 1) (entry tree))
        ((= (length tree) 0) '())
      (else (append (post-order (leftTree tree)) (post-order (rightTree tree)) (list (entry tree))))
      )) 
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

#| Part5 |#

;Sort a list in ascending order.
(define (sort-ascending givenList)
  (in-order (mktree (lambda (x y) (< x y)) givenList)))

;Sort a list in descending order.
(define (sort-descending givenList)
  (reverse (in-order (mktree (lambda (x y) (< x y)) givenList))))

;Sort a list of lists according to the length of each list element (ascending order)
(define (sort-lists givenLists)
  (in-order (mktree (lambda (x y) (< (length x) (length y))) givenLists)))










  
  
  