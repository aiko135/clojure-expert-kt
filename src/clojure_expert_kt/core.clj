(ns clojure-expert-kt.core
  (:gen-class)
  (:require [clara.rules :refer :all])
  (:require [clara.tools.inspect :refer :all]))

;;Можно использовать строковые константы "high" или механизм ключевых слов keywords :high
 ;;заголовоки как в C (т. к. прекомпилятор не знает о функциях до их объявления)
(defn examine-conditions [param])
(defn insert-knowledge-base [param])
(defn ask-user [param])
(defn examine-item-with-conditions [param1 param2])

;;Свойства 
(defrecord Condition [number descr])
;;Правила
(defrecord Weapon [descr conditions])
;;Ответы пользователя
(defrecord Answer [condition iscorrect])

(defrule consult-rule
  "Производит экспертный анализ по базе знаний"
  [Weapon (= ?name name) (= ?conditions conditions)] ;;перебор фактов как в прологе с механизмом связывания перменных
  =>
  (examine-item-with-conditions ?name ?conditions))

(defquery find-answer-query
  "Поиск факта ответа с указанным свойством"
  []
  [Answer(= ?test condition)])

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
  [condition]
  (println (str condition " ? [y/n]"))
  (= "y" (read-line)))

(defn examine-item-with-conditions
  "Test"
  [item cond-list]
  (let [session (mk-session [find-answer-query] :cache false)]
    (-> session
        (insert (->Answer 2 :true))
        (insert (->Answer 4 :true))
        (fire-rules)
        (examine-conditions cond-list)) 
    ))

(defn examine-conditions
  "Рекурсиваня проверка списка свойств"
  [session condition-list]
  ;;(ask-user (first condition-list))
  (println (str (first condition-list)))
  (println (query session find-answer-query))
  ;;(print-ses my-session)
  (if (empty? (rest condition-list))
    true
    (examine-conditions session (next condition-list)))) ;;рекурсия

(defn -main
  "Main entery"
  [& args]
  
  (-> (mk-session [consult-rule] :cache false)
      (insert-knowledge-base)
      (fire-rules))
  ;; (println (str (first #{1 2})))
  nil)


;; (defn examine-item
;;   "Проверка текущего предмета со списком свойств"
;;   [item condition-list]
;;   (if (examine-conditions condition-list)
;;       (println (str "Экспертный анализ завершен. Вам подоходит " item))
;;        nil)
;; )


