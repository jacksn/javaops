## Spring Boot generate/run
-  <a href="http://blog.jetbrains.com/idea/2015/04/webinar-recording-spring-boot-and-intellij-idea-14-1">Spring Boot and Intellij IDEA 14</a>
-  https://github.com/snicoll-demos/spring-boot-intellij-idea-webinar
-  <a href="http://docs.spring.io/spring-boot/docs/current-SNAPSHOT/reference/htmlsingle/">Spring Boot Reference Guide</a>
-  <a href="https://github.com/spring-projects/spring-boot">spring-projects/spring-boot</a>
-  <a href="http://spring.io/guides/tutorials/bookmarks/">Building REST services with Spring</a>

2. Profiles.
3. Add H2. WebServer/TcpServer
4. Model User/Role. Repository UserRepository.
5. Security/ UserDetailService/ LoggedUser
6. Add Rest
7. Add mail
8. Switch to war: http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-create-a-deployable-war-file

11. Run with active profile:
https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html#howto-set-active-spring-profiles
 ~/.bashrc
    export SPRING_PROFILES_ACTIVE=prod
 . ~/.bashrc

http://stackoverflow.com/questions/285015/linux-prevent-a-background-process-from-being-stopped-after-closing-ssh-client

alias run='cd ~/javaops; nohup mvn spring-boot:run &'

1. Modify Tomcat config
Tomcat location:
http://stackoverflow.com/questions/12280372/where-can-i-find-the-tomcat-7-installation-folder-on-linux-ami-in-elastic-beanst
/usr/share/tomcat8
http://stackoverflow.com/questions/32327522/spring-boot-yml-and-standalone-tomcat-8-server

2. Set in props
https://docs.spring.io/spring-boot/docs/current/reference/html/howto-properties-and-configuration.html#howto-set-active-spring-profiles



## AWS
<a href="http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/get-set-up-for-amazon-ec2.html">Setting Up with Amazon EC2</a>
- Sign-Up
- Launch instance
- Generate and convert key
- <a href="http://docs.aws.amazon.com/AmazonVPC/latest/GettingStartedGuide/getting-started-assign-eip.html">Assign an Elastic IP Address to Your Instance</a>
- <a href="https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/putty.html">Enter by SSH (putty)</a>
- <a href="https://serverfault.com/questions/664643/how-can-i-upgrade-to-java-1-8-on-an-amazon-linux-server#727254">Install Java JDK 1.8</a>
- <a href="https://andrewelkins.com/2012/01/08/how-to-add-git-to-an-amazon-ami-ec2-instance/">Install git</a>
- <a href="http://stackoverflow.com/questions/7532928/how-do-i-install-maven-with-yum">Install maven (without java 7)</a>

http://www.excelsior-usa.com/articles/tomcat-amazon-ec2-advanced.html
http://mtdevuk.com/2015/02/10/how-to-deploy-a-spring-boot-application-to-amazon-aws-using-elastic-beanstalk/

-  <a href="http://mtdevuk.com/2015/02/10/how-to-deploy-a-spring-boot-application-to-amazon-aws-using-elastic-beanstalk">Deploy spring boot application to amazon elastic beanstalk</a>
-  <a href="http://cloud.spring.io/spring-cloud-aws/spring-cloud-aws.html">Spring Cloud AWS</a>

## Static resources
http://www.baeldung.com/spring-mvc-static-resources

### nginx/unix

Run at 80 port: http://stackoverflow.com/questions/33703965/spring-boot-running-app-on-port-80


`sudo su`
`yum install -y nginx`
`yum install mc`

cat /etc/init.d/nginx
`Ctrl+D (exit)`

# config:      /etc/nginx/nginx.conf
# config:      /etc/sysconfig/nginx

cat /etc/sysconfig/nginx
cat /etc/nginx/nginx.conf

<a href="http://askubuntu.com/questions/376199/sudo-su-vs-sudo-i-vs-sudo-bin-bash-when-does-it-matter-which-is-used">su/sudo</a>

https://www.tollmanz.com/nginx-location-match-homepage/
http://stackoverflow.com/questions/11954255/nginx-how-to-set-index-html-as-root-file

`sudo service nginx reload/stop/start`

https://www.digitalocean.com/community/tutorials/how-to-optimize-nginx-configuration

Open 80 port: http://stackoverflow.com/questions/5004159/opening-port-80-ec2-amazon-web-services

https://en.wikipedia.org/wiki/Chmod

http://stackoverflow.com/questions/6795350/nginx-403-forbidden-for-all-files
namei -om /home/ec2-user/javaops/static/index.html

<a href="https://kb.iu.edu/d/afnz">How do I run a Unix process in the background?</a>

- <a href="https://shreevatsa.wordpress.com/2008/03/30/zshbash-startup-files-loading-order-bashrc-zshrc-etc/">Startup scripts</a>

## External properties
http://www.javabeat.net/spring-boot-external-configurations/

## H2
http://stackoverflow.com/questions/221379/hibernate-hbm2ddl-auto-update-in-production
http://stackoverflow.com/questions/18077327/hibernate-hbm2ddl-auto-possible-values-and-what-they-do-any-official-explanat
http://stackoverflow.com/questions/20089470/hibernate-is-not-auto-creating-a-table-that-does-not-exist-in-the-db

## Preconditions in java
<a href="http://www.sw-engineering-candies.com/blog-1/comparison-of-ways-to-check-preconditions-in-java">Comparison Preconditions in Java</a>

## Configuration props
Add AppConfig
http://blog.codeleak.pl/2014/09/using-configurationproperties-in-spring.html
http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#configuration-metadata-annotation-processor

## @PropertySource
http://www.jayway.com/2014/02/16/spring-propertysource/
http://www.mkyong.com/spring/spring-propertysources-example/
http://blog.jamesdbloom.com/UsingPropertySourceAndEnvironment.html
http://stackoverflow.com/questions/13728000/value-not-resolved-when-using-propertysource-annotation-how-to-configure-prop

additional-spring-configuration-metadata.json

## OAuth2
https://github.com/spring-cloud-samples/authserver

## Execution and Scheduling
<a href="http://stackoverflow.com/questions/4912228/when-should-i-use-a-completionservice-over-an-executorservice">CompletionService over an ExecutorService</a>

Omitting many details:

ExecutorService = incoming queue + worker threads
CompletionService = incoming queue + worker threads + output queue

http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling
http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#scheduling-enable-annotation-support

<a href="https://habrahabr.ru/post/260953/">10 советов по использованию ExecutorService</a>

<a href="Referencing beans across @Configuration classes">http://docs.spring.io/spring-javaconfig/docs/1.0.0.M4/reference/html/ch04s02.html</a>

## JSON
@JsonAutoDetect(fieldVisibility= ANY, getterVisibility= NONE, isGetterVisibility= NONE, setterVisibility= NONE)

Globally:
https://spring.io/blog/2014/12/02/latest-jackson-integration-improvements-in-spring#with-spring-boot

OK
http://stackoverflow.com/questions/28324352/how-to-customise-the-jackson-json-mapper-implicitly-used-by-spring-boot

http://docs.spring.io/spring-boot/docs/current/reference/html/howto-spring-mvc.html#howto-customize-the-jackson-objectmapper

https://github.com/spring-projects/spring-boot/issues/2116

https://spring.io/blog/2014/12/02/latest-jackson-integration-improvements-in-spring#customizing-the-jackson-objectmapper

http://stackoverflow.com/questions/29807879/jackson2objectmapperbuilder-enable-field-visibility-any#35326628

org/springframework/spring-webmvc/4.2.4.RELEASE/spring-webmvc-4.2.4.RELEASE-sources.jar!/org/springframework/web/servlet/mvc/method/annotation/
AbstractMessageConverterMethodProcessor.java:213

## Test
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html
http://stackoverflow.com/questions/13866533/how-to-create-a-completed-future-in-java

## Templates, Thymeleaf
http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-spring-mvc-template-engines
JSP limitation: http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/

http://blog.codeleak.pl/2014/04/how-to-spring-boot-and-thymeleaf-with-maven.html
Alt+R: http://stackoverflow.com/questions/27919315/spring-boot-hot-deployment-for-templates-and-resources-under-intellij

-  http://www.thymeleaf.org/doc/articles/standarddialect5minutes.html
-  http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html
- <a href="http://www.thymeleaf.org/doc/articles/petclinic.html">Фрагменты</a>
- <a href="http://www.thymeleaf.org/doc/articles/layouts.html">Layouts</a>
- <a href="http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#conditional-expressions">Conditional expressions</a>

text mode:
http://forum.thymeleaf.org/Thymeleaf-and-plain-text-e-mail-or-other-non-HTML-XML-templates-td4026349.html

Unescaped Text
http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#unescaped-text

Inlining
http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#inlining

Href
http://www.thymeleaf.org/doc/articles/standardurlsyntax.html

### Using multiple template resolvers
http://blog.kaczmarzyk.net/2015/01/04/loading-view-templates-from-database-with-thymeleaf/
http://stackoverflow.com/questions/26609226/using-multiple-template-resolvers-with-spring-3-2-and-thymeleaf-2-1-3-for-emails

## Bootstrap
http://itchief.ru/lessons/bootstrap-3/110-bootstrap-3-callouts


## Serving Static Web Content with Spring Boot
https://spring.io/blog/2013/12/19/serving-static-web-content-with-spring-boot

## Exception handling
http://stackoverflow.com/questions/28902374/spring-boot-rest-service-exception-handling
404: http://stackoverflow.com/questions/28902374/spring-boot-rest-service-exception-handling
--------------------------------

## Resource:
- <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/">Spring Boot Reference Guide</a>
- <a href="http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#common-application-properties">application-properties</a>
- <a href="http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/">Spring Framework Reference Documentation</a>
- <a href="http://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html">Tutorial: Using Thymeleaf</a>


## Cache
Guava
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html#boot-features-caching-provider-guava

Inner call:
http://stackoverflow.com/questions/12115996/spring-cache-cacheable-method-ignored-when-called-from-within-the-same-class
https://spring.io/blog/2012/05/23/transactions-caching-and-aop-understanding-proxy-usage-in-spring

## VK
https://habrahabr.ru/post/265563/
https://api.vk.com/method/users.get?domain=grigory.kislin&v=5.37&access_token=..
https://geektimes.ru/post/125155/

## Cascade
http://stackoverflow.com/questions/5787551/should-i-let-jpa-or-the-database-cascade-deletions
http://stackoverflow.com/questions/2856460/hibernate-doesnt-generate-cascade
https://dzone.com/articles/spring-caching-abstraction-and

## Logging
https://docs.spring.io/spring-boot/docs/current/reference/html/howto-logging.html
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-logging.html#boot-features-logging-file-output

## Error handling
http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-error-handling



org.springframework.web.servlet.DispatcherServlet.getHandler #1115

## stormpath
https://devcenter.heroku.com/articles/stormpath

## encoding
http://stackoverflow.com/questions/24054648/how-to-config-characterencodingfilter-in-springboot

## UI:
<a herf="https://www.toptal.com/front-end/javascript-front-ends-in-java-with-gwt">GWT Toolkit</a>


https://ru.wikipedia.org/wiki/Веб-компоненты

https://github.com/vaadin/gwt-polymer-elements

Веб-компоненты
http://frontender.info/shadowdom/

Building a modern GWT app using Polymer Elements
http://www.gwtproject.org/doc/latest/polymer-tutorial/introduction.html

http://stackoverflow.com/questions/28564438/spring-boot-gwt-embedded-configuraiton
https://github.com/Ekito/spring-boot-gwt
https://github.com/feedm3/spring-boot-gwt

Создаем приложение с помощью Spring и GWT.
http://alextretyakov.blogspot.ru/2011/10/4.html

http://www.springbyexample.org/static/1.1/html/simple-gwt-spring-webapp.html

## Slack
https://api.slack.com/web
https://api.slack.com/docs/oauth-test-tokens
http://stackoverflow.com/questions/30955818/slack-api-team-invitation
https://spring.io/blog/2009/03/27/rest-in-spring-3-resttemplate