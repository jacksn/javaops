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
- Рассылка сообщений в slack: https://api.slack.com/interactive-messages
SSL: https://www.emaro-ssl.ru/shop/Comodo/positive-ssl/ - 690р в год.
Есть бесплатные с пробным периодом 1-3 месяца. Но вижу бесплатные и на 2 года, что странно https://www.startcomca.com/
Ограничения самого дешевого сертификата: 1 домен example.com и 1 поддомен www.example.com, но у каждого разные правила
Сертификат заказывается для доменного имени, поэтому при заказе сразу понадобиться либо почта admin@example.com, либо на свой хостинг положить файл, чтобы доказать, что доменное имя принадлежат тебе. См. другие способы валидации https://thehost.ua/wiki/SSL
Далее Comodo или другой сертификационный центр присылает сертификат на домен и пачку ssl-рутовых - всё это добро я скармливал nginx, а под ним tomcat.
Потом там начинаются проблемы с редиректами для логина на google.com/fb.com/vk.com
С этим всем можно 2 недели провозиться. Первый раз я пробовал всё настраивать на тестовом домене и хостинге.
emaro-ssl.ru

https://thehost.ua/wiki/SSL
https://www.emaro-ssl.ru/shop/Comodo/positive-ssl/


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

