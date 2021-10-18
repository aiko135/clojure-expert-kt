(ns clojure-expert-kt.core
  (:gen-class)
  (:require [clara.rules :refer :all])
  (:require [clara.tools.inspect :refer :all]))

;;Можно использовать строковые константы "high" или механизм ключевых слов keywords :high
;;заголовоки как в C (т. к. прекомпилятор не знает о функциях до их объявления)
(defn insert-knowledge-base [param])
(defn examine [param])
(defn examine-item-with-conditions [param1 param2])
(defn examine-conditions [param])
(defn ask-user [param])
(defn save-answer-in-session [param1 param2 param3])
(defn examine-condition [param1 param2])

;;Свойства 
(defrecord Condition [number descr])
;;Правила
(defrecord Weapon [name conditions])
;;Ответы пользователя
(defrecord Answer [condition iscorrect])

(defquery get-weapons-query
  "перебор фактов как в прологе с механизмом связывания перменных"
  []
  [Weapon (= ?name name) (= ?cond conditions)])

(defquery get-condition-query
  "Поиск признака по номеру"
  [?number]
  [Condition (= ?number number) (= ?descr descr)])

(defquery find-answer-query
  "Поиск факта ответа с указанным свойством"
  [?condition]
  [Answer(= ?condition condition) (= ?iscorrect iscorrect)])

(defquery get-all-answers
  []
  [Answer(= ?condition condition) (= ?iscorrect iscorrect)])

(defn insert-knowledge-base
  "База знаний"
  [session]
  (-> (insert session (->Condition 1 "Нарезной ствол"))
      (insert (->Condition 2 "Гладкий ствол"))
      (insert (->Condition 3 "Дробовик"))
      (insert (->Condition 4 "Карабин"))

      (insert (->Weapon "Тигр 7.62" '(2 4)))
      (insert (->Weapon "Saiga-12 Ижмаш" '(2 3)))
      (insert (->Weapon "Застава 7.62" '(1 4)))))

(defn ask-user
  "Спрашивает пользователя о признаке"
  [condition session]
  (-> (query session get-condition-query :?number condition)
      (first)
      (get :?descr)
      (str "? [y/n]")
      (println))
  (= "y" (read-line)))

(defn save-answer-in-session
  "Сохраняет ответ пользователя. Вернет новую сессию с сохраненным ответом"
  [session condition iscorrect]
  (-> session
      (insert (->Answer condition iscorrect))
      (fire-rules)))

(defn examine-condition
  "Проверка конкретного свойства"
  [condtion session]
  (let [ query-result 
        (query session find-answer-query :?condition condtion) ]
    
    (if (empty? query-result)
      
      (let [user-answer (ask-user condtion session)]
           {:session (save-answer-in-session session condtion user-answer)
            :result user-answer}
      )
   
      {:session session :result (first query-result)})))

(defn examine-conditions
  "Рекурсиваня проверка списка свойств"
  [condition-list session]
  (let [ condition-result
         (examine-condition (first condition-list) session) ]
    (println (query (get condition-result :session) get-all-answers))
    (if (true? (get condition-result :result))

      (if (empty? (rest condition-list))
        {:session (get condition-result :session) :result true} ;;проверили все свойства до конца
        (examine-conditions (rest condition-list) (get condition-result :session)))
      
      {:session (get condition-result :session) :result false}))) ;;хотябы одно свойство отрицательное
   
(defn examine-item-with-conditions
  "Рекурсивный перебор объектов с признаками"
  [item-list session]
  ;; (println (first item-list))
    (let [ conditions-result
         (examine-conditions (get (first item-list) :?cond) session) ]
      (if (true? (get conditions-result :result))
        
        (first item-list) ;;нашли походящий

        (if (empty? (rest item-list))
          :not-found  ;;дошли до конца но так и не нашли подходящий
          (examine-item-with-conditions (rest item-list) (get conditions-result :session))))))


(defn examine
  "Провести экспертный анализ"
  [session]
  (let [exam-result
        (-> (query session get-weapons-query)
            (examine-item-with-conditions session))]
    (if (= :not-found exam-result)
      (println "Желаемый предмет не найден")
      (println "Желаемый предмет: " (get exam-result :name)))
    ))

;;----------- TODO сделать отдельную сессию для ответов пользователя
(defn -main
  "Main entery"
  [& args]
  (-> (mk-session [get-weapons-query
                   get-condition-query
                   find-answer-query
                   get-all-answers] :cache false)
      (insert-knowledge-base)
      (fire-rules) ;;Правил в сессии нет - но вызов все равно обязательно
      (examine))
  nil)

;; (defn examine
;;   "Провести экспертный анализ"
;;   [session]
;;   (-> (insert session (->Weapon "Test11" '(1 4 5)))
;;       (fire-rules)
;;       (query get-weapons-query)
;;       (println))
;;   nil)

;; (defn examine-item-with-conditions
;;   "Рекурсивный перебор объектов с признаками"
;;   [item-list session]
;;   (println item-list)
;;   (let [session (mk-session [find-answer-query] :cache false)]
;;     (-> session
;;         (insert (->Answer 2 :true))
;;         (insert (->Answer 4 :true))
;;         (fire-rules)
;;         (examine-conditions cond-list))))