(ns clojure-expert-kt.core
  (:gen-class)
  (:require [clara.rules :refer :all])
  (:require [clara.tools.inspect :refer :all]))

;;Можно использовать строковые константы "high" или механизм ключевых слов keywords :high
 ;;заголовоки как в C (т. к. прекомпилятор не знает о функциях до их объявления)
(defn examine-conditions [param])
(defn insert-knowledge-base [param])
(defn ask-user [param])

;;Свойства 
(defrecord Condition [number descr])
;;Правила
(defrecord Weapon [descr conditions])
;;Ответы пользователя
(defrecord Answer [condition iscorrect])

(defrule consult
  "Производит экспертный анализ по базе знаний"
  [Weapon (= ?name name) (= ?conditions conditions)] ;;перебор фактов как в прологе с механизмом связывания перменных
  =>
  (examine-conditions ?conditions))

(defquery find-answer
  "Поиск факта ответа с указанным свойством"
  []
  [?condition <- Answer (= ?cond condition) (= ?state iscorrect)])

;;в этот момент будет создана сессия. Обязательно объявить все rules и queries перед этим вызовом
(defsession my-session 'clojure-expert-kt.core)

(defn insert-knowledge-base
  "База знаний"
  [session]
  (-> (insert session (->Condition 1 "Нарезной ствол"))
      (insert (->Condition 2 "Гладкий ствол"))
      (insert (->Condition 3 "Дробовик"))
      (insert (->Condition 4 "Карабин"))

      (insert (->Answer 2 :true))
      (insert (->Answer 4 :true))

      (insert (->Weapon "Тигр 7.62" '(2 4)))
      (insert (->Weapon "Saiga-12 Ижмаш" '(2 3)))
      (insert (->Weapon "Застава 7.62" '(1 4)))))


(defn ask-user
  "Спрашивает пользователя о признаке"
  [condition]
  (println (str condition " ? [y/n]"))
  (= "y" (read-line)))

(defn print-ses
  [session]
  (println (inspect session))
  session)

(defn examine-conditions
  "Рекурсиваня проверка списка свойств"
  [condition-list]
  ;;(ask-user (first condition-list))
  (println (str (first condition-list)))
  (print-ses my-session)
  ;; (println (query my-session find-answer))
  (if (empty? (rest condition-list))
    true
    (examine-conditions (next condition-list)))) ;;рекурсия

(defn -main
  "Main entery"
  [& args]
  
  (-> my-session
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


