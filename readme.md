## Разработка сайта для Java проектов http://javaops.ru

[![Codacy Badge](https://api.codacy.com/project/badge/grade/300abd4332374403bec8fac9499b1127)](https://www.codacy.com/app/gkislin/javaops)

### Стек:
- Spring Boot
- H2
- Thymeleaf
- Рассмаривается на кандидат в UI: https://vaadin.com/elements

### Сделано:
- REST API
  - Рассылка почты через API

- Внешние запросы
  - Регистрация
  - Оплата
  - Отписка

### Задачи:
- Сделать обновляемые (как Thymeleaf) шаблоны в формате md (см. `MarkdownUtil`)
- Сделать ошибки в форме `participation.html` как в профиле проекта topjava

- Интеграция
  - Slack (приглашение в организацию)
  - Google Drive (доступ к папке)

### Работа:

   Запуск: `mvn spring-boot:run`

   Коннект к базе (только 1 коннект возможен):

   - профиль **prod** (`\config\application-prod.yaml`, не чекинится в репозиторий)
      - приложение не работает `jdbc:h2:file:~/db/javaops`
      - приложение работает:   `jdbc:h2:tcp://localhost:9092/~/db/javaops`

   - профиль **dev**
      - `jdbc:h2:tcp://localhost:9092/mem:javaops`

   - Общение в https://javaops.slack.com/messages/javaops_ru/
