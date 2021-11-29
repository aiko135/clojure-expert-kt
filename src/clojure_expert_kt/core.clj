(ns clojure-expert-kt.core
  (:gen-class
   :methods [^{:static true} [createsession [] java.lang.Object]
             ^{:static true} [getnextquestion [java.lang.Object] java.lang.String]
             ^{:static true} [checkstate [java.lang.Object] java.lang.String]
             ^{:static true} [answernextquestion [java.lang.Object java.lang.String] void]])
  (:require [clara.rules :refer :all])
  (:require [clara.tools.inspect :refer :all]))

;;Можно использовать строковые константы "high" или механизм ключевых слов keywords :high
;;заголовоки как в C (т. к. прекомпилятор не знает о функциях до их объявления)
(defn insert-knowledge-base [param])
(defn examine [param])
(defn examine-item-with-conditions [param1 param2])
(defn examine-conditions [param])
(defn examine-condition [param1 param2])

;;Свойства 
(defrecord Condition [number descr])
;;Правила
(defrecord Weapon [name conditions])
;;Ответы пользователя
(defrecord Answer [condition iscorrect])
;;
(defrecord Questionq [condition]) 

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
  [Answer (= ?condition condition) (= ?iscorrect iscorrect)])

(defquery get-all-answers
  []
  [Answer (= ?condition condition) (= ?iscorrect iscorrect)])

(defquery find-quest-in-q
  [?condition]
  [Questionq (= ?condition condition)])

(defquery get-question-from-q
  []
  [Questionq (= ?condition condition)])

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

(defn save-answer-in-session
  "Сохраняет ответ пользователя. Вернет новую сессию с сохраненным ответом"
  [session condition iscorrect]
  (-> session
      (insert (->Answer condition iscorrect))
      (fire-rules)))

(defn add-question-to-q
  "Добавляет вопрос в очередь"
  [session condition]
  (let [query-result
      (query session find-quest-in-q :?condition condition)]
     
      (if (empty? query-result)

        (-> session
           (insert (->Questionq condition))
           (fire-rules))
       
        session)))
  ;; (-> session
  ;;     (insert (->Answer condition iscorrect))
  ;;     (fire-rules)))

(defn examine-condition
  "Проверка конкретного свойства"
  [condtion session]
  (let [query-result
        (query session find-answer-query :?condition condtion)]
    (if (empty? query-result)

      {:session (add-question-to-q session condtion)
       :result :not-enough-answers}

      {:session session :result (first query-result)})))

(defn examine-conditions
  "Рекурсиваня проверка списка свойств"
  [condition-list session]
  (let [condition-result
        (examine-condition (first condition-list) session)]
    (print condition-list)
    (println condition-result)
  ;;  наглядная демонстрация как накапливаются факты Answer в сессии
   ;; (println (query (get condition-result :session) get-all-answers)) 
    (if (= :not-enough-answers (get condition-result :result))

      {:session (get condition-result :session) :result :not-enough-answers} ;;требуется текущий ответ пользователя
      
      (if (true? (get (get condition-result :result) :?iscorrect))

        (if (empty? (rest condition-list))
          {:session (get condition-result :session) :result true} ;;проверили все свойства до конца
          (examine-conditions (rest condition-list) (get condition-result :session)))

        {:session (get condition-result :session) :result false})))) ;;хотябы одно свойство отрицательное
    

(defn examine-item-with-conditions
  "Рекурсивный перебор объектов с признаками"
  [item-list session]
  ;; (println (first item-list))
  (let [conditions-result
        (examine-conditions (get (first item-list) :?cond) session)]
    (print "ANS---")
    (println examine-condition)
    (if (= :not-enough-answers (get conditions-result :result))

      {:session (get conditions-result :session) :result :not-enough-answers} ;;требуется текущий ответ пользователя

      (if (true? (get conditions-result :result))

        {:session nil :result (first item-list)} ;;нашли походящий

        (if (empty? (rest item-list))
          {:session nil :result :not-found}  ;;дошли до конца но так и не нашли подходящий
          (examine-item-with-conditions (rest item-list) (get conditions-result :session)))))))


(defn examine
  "Проверка всей цепочки"
  [session]
  (let [exam-result
        (-> (query session get-weapons-query)
            (examine-item-with-conditions session))]
    (if (= :not-enough-answers (get exam-result :result))
      
      (str "ANSWER_MORE")
      
      (if (= :not-found (get exam-result :result))
          (str "NOT_FOUND")
          (str "-> " (get  (get exam-result :result) :?name))))))
    

(defn get-updated-session
  [session]
  (let [exam-result
        (-> (query session get-weapons-query)
            (examine-item-with-conditions session))]
    (if (= :not-enough-answers (get exam-result :result))
      
      (get exam-result :session)
      
      session)))


(defn gen-session
  "generate new session"
  []
  (-> (mk-session [get-weapons-query
                   get-condition-query
                   find-answer-query
                   find-quest-in-q
                   get-question-from-q
                   get-all-answers] :cache false)
      (insert-knowledge-base)
      (fire-rules))) ;;Правил в сессии нет - но вызов все равно обязательно

(defn get-current-question
  [session]
  (let [cond-num   
        (-> session
          (get-updated-session)
          (query get-question-from-q)
          (first)
          (get :?condition))]
     (-> (query session get-condition-query :?number cond-num)
         (first)
         (get :?descr))))

(defn answer-current-question
  [session, answer-bool]
  (let [fact
        (-> session
            (get-updated-session)
            (query get-question-from-q)
            (first))]
    (-> session
        (get-updated-session)
        (save-answer-in-session (get fact :?condition) answer-bool)
        (retract  (->Questionq (get fact :?condition)))
        (fire-rules))))

(defn -createsession
  "creates a new session"
  []
  (gen-session))

(defn -checkstate
  "check current state"
  [session]
  "false")

(defn -getnextquestion
  "ask"
  [session]
  "Question???")

(defn -answernextquestion
  "answer"
  [session, answer] nil)

(defn -main
  "Main"
  [& args]
  ;; (-> (gen-session)
  ;;     (examine)
  ;;     (str)
  ;;     (println))
  ;; (->(gen-session)
  ;;    (get-current-question)
  ;;    (println))
  (-> (gen-session)
      (answer-current-question false)
      (answer-current-question true)
      (answer-current-question true)
      (examine)
      (println))
  (println "Clojure expert started")
  nil)
