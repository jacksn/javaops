## Разработка сайта для Java проектов http://javaops.ru

[![Codacy Badge](https://api.codacy.com/project/badge/grade/300abd4332374403bec8fac9499b1127)](https://www.codacy.com/app/gkislin/javaops)

### Стек:
- Spring Boot
- H2
- Thymeleaf
- Рассмаривается на кандидаты в UI: 
  - https://vaadin.com/elements
  - https://www.sencha.com/products/gxt
  - <a href="https://angular.io">Angular 2</a>

### Сделано:
- REST API
  - Рассылка почты через API

- Внешние запросы
  - Регистрация
  - Оплата
  - Отписка

### Задачи:
- Сделать редактирование личных данных с авторизацией через github
- Сделать обновляемые (как Thymeleaf) шаблоны в формате md (см. `MarkdownUtil`) с кэшированием
- Сделать ошибки в форме `participation.html` как в профиле проекта topjava

### Работа:

   Запуск: `mvn spring-boot:run`

   Коннект к базе (только 1 коннект возможен):

   - профиль **prod** (`\config\application-prod.yaml`, не чекинится в репозиторий)
      - приложение не работает `jdbc:h2:file:~/db/javaops`
      - приложение работает:   `jdbc:h2:tcp://localhost:9092/~/db/javaops`

   - профиль **dev**
      - `jdbc:h2:tcp://localhost:9092/mem:javaops`

   - Общение в https://javaops.slack.com/messages/javaops_ru/
