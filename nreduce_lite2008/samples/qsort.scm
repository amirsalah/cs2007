printstrings str =
                 (if str
                     (append (numtostring (head str))
                     (cons " " (printstrings (tail str)))
                     )
                     nil)

//Return a list containing elements smaller than pivot
selectSItems pivot numbers = 
                           (if (>= 1 (len numbers)) 
                               (if (<= pivot (item 0 numbers))
                                   nil
                                   (cons (item 0 numbers) nil))
                               (if (<= pivot (item 0 numbers))
                                   (selectSItems pivot (skip 1 numbers))
                                   (cons (item 0 numbers) (selectSItems pivot (skip 1 numbers)))))


//Return a list containing elements equal pivot
selectEItems pivot numbers =
                       (filter (!x.== x pivot) numbers)

//                            (if (>= 1 (len numbers))
//                                (if (== pivot (item 0 numbers))
//                                    (cons (item 0 numbers) nil)
//                                     nil)
//                                (letrec firstItem = (item 0 numbers)
//                                  in
//                                      (if (== pivot firstItem)
//                                           (cons firstItem (selectEItems pivot (skip 1 numbers)))
//                                           (selectEItems pivot (skip 1 numbers)))))


//Return a list containing elements larger than pivot
selectLItems pivot numbers =
                           (filter (!x.> x pivot) numbers)
                           
//                           (if (>= 1 (len numbers))
//                               (if (>= pivot (item 0 numbers))
//                                     nil
//                                    (cons (item 0 numbers) nil))
//                               (letrec firstItem = (item 0 numbers)
//                                 in
//                                 (if (>= pivot firstItem)
//                                     (selectLItems pivot (skip 1 numbers))
//                                     (cons firstItem (selectLItems pivot (skip 1 numbers))))))



quick_sort numbers =
                   (if (>= 1 (len numbers))
                       (cons (item 0 numbers) nil)
                       (letrec pivot = (item 0 numbers)
                               leftItems = (selectSItems pivot numbers)
//                               equalItems = (selectEItems pivot numbers)
                               rightItems = (selectLItems pivot numbers)
                         in
                               (append (selectEItems pivot numbers) (quick_sort rightItems))))

//quick sort
main =
(letrec
  strings = 
            (cons 10
            (cons 3
            (cons 8
            (cons 10
            (cons 66
            (cons 1
            (cons 33
            (cons 7 nil))))))))
 in
  (printstrings (quick_sort strings)))
