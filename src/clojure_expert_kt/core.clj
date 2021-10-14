(ns clojure-expert-kt.core
  (:gen-class)
  (:require [clara.rules :refer :all]))
;;Можно использовать строковые константы "high" или механизм ключевых слов keywords :high

;;Свойства 
(defrecord Condition [number descr])
;;Правила
(defrecord Weapon [descr conditions])

;;Ответы пользователя
(defrecord Answer [condition iscorrect])

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

(defrule consult
  "Test"
  [Weapon (= ?name name) (= ?conditions conditions)] ;;перебор фактов как в прологе с механизмом связывания перменных
  =>
  (examine-condition ?conditions))

(defn examine-condition
  "Проверка свойства в вершине списка"
  [condition-list]
  (println (str (first condition-list)))
  (if (empty? (rest condition-list))
    true
    (examine-condition (next condition-list))))

(defn ask-user
  "Спрашивает пользователя о признаке"
  [condition]
  (println (str condition "? [y/n]"))
  (= "y" (read-line)))

(defn save-session
  "сохраняет сессию в статическую переменную"
  [session]
  (def ^:dynamic MAIN-SESSION session)
  session)

(defn -main
  "Main entery"
  [& args]
  (-> (mk-session 'clojure-expert-kt.core)
      (insert-knowledge-base)
      (save-session)
      (fire-rules))
  ;; (println (str (first #{1 2})))
  nil)

;; --- Примеры --- 

;; (defrecord Weapon [name type])
;; (defquery print-all
;;   "Prints all facts"
;;   []
;;   [?weap <- Weapon (= ?name name)])

;; ;;query вернет результат запроса - список мап - объектов со связанными свойствами
;; (defn apply-query
;;   "test"
;;   [session]
;;   (println "Initial locations that have never been below 0: "
;;            (query session print-all))
;;   session)

;; Правила - динамическое построение нововых связей в сессии
;; (defrule is-handgun
;;   "Test"
;;   [Weapon (= ?name name)(= type :handgun)]
;;   =>
;;   (println (str "Hand gun found! it is: " ?name)))

;; (defn insert-knowledge-base
;;   "База знаний"
;;   [session]
;;   (-> (insert session (->Weapon "Grand Power T12" :shotgun))
;;       (insert (->Weapon "Saiga-12 Ижмаш" :handgun))
;;       (insert (->Weapon "Застава 7.62" :rifle))
;;    ;;insert возвращает измененную сессию, если не использовать -> для последовательного вычисления функций
;;    ;;так что результат каждой попадает во 2 параметр следующей функции. Если вернуть просто session до факты не задействуются 
;;       ))

;; (defn -main
;;   "Main entery"
;;   [& args]
;;   (-> (mk-session 'clojure-expert-kt.core)
;;       (insert-knowledge-base)
;;       (fire-rules)
;;       (apply-query))
;;   nil)


