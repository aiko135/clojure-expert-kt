# clojure-expert-kt

Использован сборщик Leiningen

- lein run для выполнения файла
- lein repl для работы в режиме REPL
  сборщик leiningen развернет на локальном хосте REPL сервер
  далее можно работать в терминале а можно в подключить Visual Studio к REPL
  View->Command Palete... присоединиться к REPL серверу.

Для разработки использована Visual Studio Code с плагином Calva

Использована библиотека Clara Rules
Official Docs не полные
http://www.clara-rules.org/docs/rules/
API docs
http://www.clara-rules.org/apidocs/0.19.0/clojure/clara.rules.html
Хорошие примеры
https://github.com/cerner/clara-examples

## Installation

Download from http://example.com/FIXME.

## Usage

FIXME: explanation

    $ java -jar clojure-expert-kt-0.1.0-standalone.jar [args]

## Options

FIXME: listing of options this app accepts.

## Examples

 --- Примеры --- 

 (defrecord Weapon [name type])
 (defquery print-all
   "Prints all facts"
   []
   [?weap <- Weapon (= ?name name)])

;;query вернет результат запроса - список мап - объектов со связанными свойствами
 (defn apply-query
   "test"
   [session]
   (println "Initial locations that have never been below 0: "
            (query session print-all))
   session)

;; Правила - динамическое построение нововых связей в сессии
 (defrule is-handgun
   "Test"
   [Weapon (= ?name name)(= type :handgun)]
   =>
   (println (str "Hand gun found! it is: " ?name)))

;; (defn insert-knowledge-base
   "База знаний"
   [session]
   (-> (insert session (->Weapon "Grand Power T12" :shotgun))
       (insert (->Weapon "Saiga-12 Ижмаш" :handgun))
       (insert (->Weapon "Застава 7.62" :rifle))
    ;;insert возвращает измененную сессию, если не использовать -> для последовательного вычисления функций
    ;;так что результат каждой попадает во 2 параметр следующей функции. Если вернуть просто session до факты не задействуются 
    ))

 (defn -main
   "Main entery"
   [& args]
   (-> (mk-session 'clojure-expert-kt.core)
       (insert-knowledge-base)
       (fire-rules)
       (apply-query))
   nil)

### Bugs

...

### Any Other Sections
### That You Think
### Might be Useful

## License

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.