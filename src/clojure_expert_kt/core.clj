(ns clojure-expert-kt.core
  (:gen-class
   :methods [^{:static true} [createsession [] java.lang.Object]
             ^{:static true} [getnextquestion [java.lang.Object] java.lang.String]
             ^{:static true} [checkstate [java.lang.Object] java.lang.String]
             ^{:static true} [answernextquestion [java.lang.Object java.lang.Boolean] java.lang.Object]])
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
  (-> (insert session (->Condition 1 "Гладкий ствол"))
      (insert (->Condition 2 "Нарезной ствол"))
      (insert (->Condition 3 "Ствол Ланкастера"))
      (insert (->Condition 4 "Дробовик"))
      (insert (->Condition 5 "Винтовка"))
      (insert (->Condition 6 "Карабин"))
      (insert (->Condition 7 "Пистолет"))
      (insert (->Condition 8 "Автоматический"))
      (insert (->Condition 9 "Полуавтомат"))
      (insert (->Condition 10 "Скользящий затвор"))
      (insert (->Condition 11 "Помповый механизм"))
      (insert (->Condition 12 "Ручное взведение"))
      (insert (->Condition 13 "Барабанный механизм"))
      (insert (->Condition 14 "Мелкий калибр (спортивный .22 lr)"))
      (insert (->Condition 15 "Пистолетный калибр"))
      (insert (->Condition 16 "Стандартный калибр"))
      (insert (->Condition 17 "Крупный калибр (.338 и больше)"))
      (insert (->Condition 18 "Отъёмный магазин"))
      (insert (->Condition 19 "Неотъёмный магазин"))
      (insert (->Condition 20 "Без магазина (ручное заряжание)"))
      (insert (->Condition 21 "Встроенный магазин"))
      (insert (->Condition 22 "Нет данных"))
      (insert (->Condition 23 "Бесствольное"))
      (insert (->Condition 24 "Короткоствольное (50-200мм)"))
      (insert (->Condition 25 "Среднествольное (200-300мм)"))
      (insert (->Condition 26 "Длинноствольное (450мм и больше)"))
      (insert (->Condition 27 "Механическое прицельное устройство"))
      (insert (->Condition 28 "Крепление «планка Пикатини»"))
      (insert (->Condition 29 "Крепление «ласточкин хвост»"))
      (insert (->Condition 30 "Крепление «планка Вивера»"))
      (insert (->Condition 31 "Пистолетная рукоятка"))
      (insert (->Condition 32 "Ружейное ложе"))
      (insert (->Condition 33 "Спортивная рукоятка"))
      (insert (->Condition 34 "Компановка буллпаб"))
      (insert (->Condition 35 "Прицельная дальность до 25м"))
      (insert (->Condition 36 "Прицельная дальность до 25м-150м"))
      (insert (->Condition 37 "Прицельная дальность до 150м-300м"))
      (insert (->Condition 38 "Прицельная дальность до 300м-1000м"))
      (insert (->Condition 39 "Прицельная дальность более 1000м"))
      (insert (->Condition 40 "Неудобно использовать левше"))

      (insert (->Weapon "Saiga-12 Ижмаш" '(1 4 9 16 18 26 29 31 36)))
      (insert (->Weapon "Ружье Benelli vinci 12" '(1 4 11 16 21 26 27 32 36)))
      (insert (->Weapon "Карабин Тигр 7.62х54" '(2 6 9 16 18 26 29 33 39)))
      (insert (->Weapon "Карабин Тула КО ВСС 9х39" '(2 6 9 16 18 25 28 33 38)))
      (insert (->Weapon "Карабин ОП СКС 7.62х39" '(2 6 9 16 19 26 29 32 38)))
      (insert (->Weapon "Винтовка КО Мосин 7.62х54" '(2 5 10 16 19 29 32 38)))
      (insert (->Weapon "Винтовка Orsis T-5000 338LMagnum" '(2 5 10 17 18 26 30 31 39)))
      (insert (->Weapon "Винтовка CZ 445 Varmint 22LR" '(2 5 12 14 18 26 27 32 36)))
      (insert (->Weapon "Карабин молот МА ПП-91 9х18" '(2 6 9 15 18 25 27 31 37)))
      (insert (->Weapon "Ружье Baikal MP-27 12k" '(1 4 12 16 20 26 30 32 36)))
      (insert (->Weapon "Ружье Merkel 2011d 22lr/20" '(3 4 5 12 14 16 20 26 30 32 36)))
      (insert (->Weapon "Охотничий пистолет Chippa Rhino .357" '(2 7 13 15 21 24 27 31 37)))
      (insert (->Weapon "Карабин Сайга-9 9х19" '(2 6 9 15 18 25 28 31 37)))))

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
  (examine session))

(defn -getnextquestion
  "ask"
  [session]
  (get-current-question session))

(defn -answernextquestion
  "answer"
  [session, answer]
  (answer-current-question session answer))

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


  ;; (-> (gen-session)
  ;;     (answer-current-question false)
  ;;     (answer-current-question true)
  ;;     (answer-current-question true)
  ;;     (examine)
  ;;     (println))
  

  (println "Clojure expert started")
  nil)
