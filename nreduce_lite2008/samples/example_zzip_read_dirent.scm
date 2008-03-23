printlist list=
          (if list
              (append (head list)
                      (cons " "
                             (printlist (tail list))))
               nil)

printstrings lst =
                 (if lst
                     (append (printlist (head lst)) 
                             (cons '\n' 
                                   (printstrings (tail lst))))
                     nil)


main = (printstrings (zzip_read_dirent "/home/smaxll/Project/workspace/zipc/test1.zip"))
//main = (head (cons "README" nil))

//main =  (tail (tail (cons (cons (cons "2" (cons "2" (cons "7" (cons "9 " nil))))
//               (cons (cons "1" (cons "1" (cons "4" (cons "9" nil)))) nil)) nil)))
