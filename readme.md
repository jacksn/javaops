https://api.travis-ci.org/gkislin/javaops.svg?branch=master

## Разработка сайта для Java проектов http://javaops.ru

[![Codacy Badge](https://api.codacy.com/project/badge/grade/300abd4332374403bec8fac9499b1127)](https://www.codacy.com/app/gkislin/javaops)
[![Build Status](https://travis-ci.org/gkislin/javaops.svg?branch=master)](https://travis-ci.org/gkislin/javaops)
[![Dependency Status](https://dependencyci.com/github/gkislin/javaops/badge)](https://dependencyci.com/github/gkislin/javaops)
[![codebeat badge](https://codebeat.co/badges/37249a90-da08-4f9b-804f-2197296f91ff)](https://codebeat.co/projects/github-com-gkislin-javaops)

### Стек:
- Spring Boot
- H2
- Thymeleaf
- Рассмаривается на кандидаты в UI: 
  - https://vaadin.com/elements
  - https://www.sencha.com/products/gxt

### Задачи:
- Селать авторизацию по oAuth2 с github: https://spring.io/guides/tutorials/spring-boot-oauth2/
  - Пример не на boot:
      - <a href="http://rblik-topjava.herokuapp.com">Авторизация в приложение через GitHub аккаунт по OAuth2</a></b><br/>
      - <a href="https://github.com/rblik/topjava/tree/oauth">ветка oauth в гитхабе</a>

- Рассылка сообщений в slack: https://api.slack.com/interactive-messages

### Работа:
   Создать пустой ./config/sql.properties
   Закомментировать GoogleAdminSDKDirectoryService.init()
   
   Запуск: `mvn spring-boot:run`

   Коннект к базе (только 1 коннект возможен):

   - профиль **prod** (`\config\application-prod.yaml`, не чекинится в репозиторий)
      - приложение не работает `jdbc:h2:file:~/db/javaops`
      - приложение работает:   `jdbc:h2:tcp://localhost:9092/~/db/javaops`

   - профиль **dev**
      - `jdbc:h2:tcp://localhost:9092/mem:javaops`

   - Общение в https://javaops.slack.com/messages/javaops_ru/

