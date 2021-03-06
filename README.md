# GitEnter

[![Travis CI Build Status](https://travis-ci.org/gitenter/gitenter.svg?branch=master)](https://travis-ci.org/gitenter/gitenter)
[![CircleCI](https://circleci.com/gh/gitenter/gitenter.svg?style=svg)](https://circleci.com/gh/gitenter/gitenter)

A Git based version control tool for requirement engineering, design control, verification and validation processes.

## Development

### Linting

+ Java: [Spring Java Format](https://github.com/spring-io/spring-javaformat)
+ Python: [PEP 8](https://www.python.org/dev/peps/pep-0008/)

One other possible choice is [Google Java Style Guide](https://checkstyle.sourceforge.io/styleguides/google-java-style-20180523/javaguide.html) (as well as its [eclipse formatter](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml)). We are not using it because it triggers so many changes (tab -> 2 space, `} else`, ...). We'll just wait for Spring style to be more mature.

- [ ] Setup [CheckStyle](http://maven.apache.org/plugins/maven-checkstyle-plugin/index.html) in CI. Right now the easiest way is use the maven `<reporting>` block and `mvn site` to generate the test report in HTML form. Looks no easy way to generate JUnit XML report ([`<outputFile>` doesn't work](https://maven.apache.org/plugins/maven-checkstyle-plugin/checkstyle-mojo.html#outputFile)) and CircleCI doesn't support CheckStyle output.

## Testing/Demo

GitEnter currently support multiple ways to build up the services from src for develop/test/demo proposes.

+ [Local](#local)
+ [Docker](#docker)
+ [AWS](#aws)

Automatic testing against the buildup services is discussed separately in [`selenium-test/README.md`](selenium-test/README.md).

### Local

SSH authorization (under python code of `/ssheep`) is completely trivialized, and cannot be E2E tested through this approach.

#### Prerequisite

+ Java (10/11)
+ maven
+ Postgres (tested in 11.2)
+ Redis (tested in 4.0.9) (Mac OS with multiple users may face permission error, and can be correct by [manually `redis-server` auto-start](https://medium.com/@petehouston/install-and-config-redis-on-mac-os-x-via-homebrew-eb8df9a4f298))
+ [Hugo](https://gohugo.io/) (tested in 0.60.1)

Optional:

+ Tomcat

or

+ STS
+ Linter
  + [Eclipse CheckStyle Plugin](https://checkstyle.org/eclipse-cs/)
  + [Spring Java Format eclipse plugin](https://github.com/spring-io/spring-javaformat#eclipse).
+ Lombok

#### Up-and-run

Dependencies between product cannot be fully management by parent POM (post-recieve hook JAR file should be compiled first and add into capsid WAR as a normal file). Therefore, we need multiple-step build.

```
mvn clean install
mvn compile assembly:single -f hooks/post-receive/pom.xml -DskipTests
mvn package -f capsid/pom.xml -DskipTests
```

The resulting `capsid.war` can be run directly under Tomcat as a web service.

To use STS, import the project and run `capsid` package as a Spring Boot application. Further complicities are listed below.

##### STS

(May be out-of-date with parent POM, but at least still apply to `gitar`).

It is very tricky to setup STS. For the dependencies of all maven packages, you don't want to add anything on the project's `Properties > Java Build Path > Projects`. What should be done is to `mvn install` all packages (so they are properly setup in `.m2`) and let Eclipse to load them from `.m2` just like external packages.

Reasons:

(1) Although in the final product, the testing class are completely irrelevant, STS mixed up the `src/test/java` parts of different packages. For example, it may then say errors like this:

```
Caused by: org.springframework.context.annotation.ConflictingBeanDefinitionException: Annotation-specified bean name 'testDatabaseConfig' for bean class [enterovirus.gihook.postreceive.config.TestDatabaseConfig] conflicts with existing, non-compatible bean definition of same name and class [enterovirus.protease.config.TestDatabaseConfig]
```

That seems also because STS cannot handle two classes which accidentally have the same bean name (the bean name should be by default the camelCase of the class name).

*(Consider simplify the case and report the bug to STS someday?)*

(2) STS mixed up the setup of a normal Spring project and a Spring Boot project (they may refer to different/incompatible packages regard to `pom.xml`). For example, if `capsid` (a Spring Boot project) refer to `protease` (a normal Spring project), then

```
[main] WARN org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext - Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'securityConfig': Unsatisfied dependency expressed through method 'setContentNegotationStrategy' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration$EnableWebMvcConfiguration': Invocation of init method failed; nested exception is java.lang.AbstractMethodError
[main] WARN org.springframework.boot.SpringApplication - Error handling failed (Error creating bean with name 'delegatingApplicationListener' defined in class path resource [org/springframework/security/config/annotation/web/configuration/WebSecurityConfiguration.class]: BeanPostProcessor before instantiation of bean failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration': Initialization of bean failed; nested exception is org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry' available)
[main] ERROR org.springframework.boot.SpringApplication - Application startup failed
```

##### Linting

- [ ] Use `spring-javaformat:apply` to change local files to follow spring linter. Currently we didn't apply because it cause so many differences (especially blank line with a tab). But is there a way to check (rather than directly apply change)?
- [ ] Current after installing the [Spring Java Format eclipse plugin](https://github.com/spring-io/spring-javaformat#eclipse), we can see it from `Preferences > Java > Code Style > Formatter` as well as `Preferences > Checkstyle`. However, choose it will cause a null pointer error (at least for my version of STS) so it cannot be used yet.

##### Lombok

To make Lombok work with the IDE, you need to not only add Lombok to `pom.xml`, but also setup the IDE dependency path.

For Ubuntu, simply navigate to the Lombok `.jar` folder (`.m2/repository/org/projectlombok/lombok`) and `sudo java -jar lombok-1.16.18.jar`, then a GUI will be opened. Through the GUI, `specify Location...` and choose the STS execusion path (in my case `/opt/sts-bundle/sts-3.9.0.RELEASE/STS`), then `Install/Update`. Restart STS and the generated getters and setters works in eclipse `Outline`.

For Mac OS, enable Lombok is kind of tricky, but it works by following [this link](https://nawaman.net/blog/2017-11-05).

### Docker

#### Prerequisite

+ docker ([Ubuntu installation guide](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-16-04))
+ docker-compose

#### Up-and-run

`docker-compose.yml` include containers for the product to be up and run, while `docker-compose.dev.yml` are containers for compiling, testing, etc. Assuming images are properly compiled and build, to make the product up and run executes:

```
docker-compose up
```

Then one should expect to access this software:

+ Web UI: http://localhost:8886 (direct to container) or http://localhost:8887 (nginx)
+ SSH/Git: `localhost:8822`
  + For case with customized port, `git clone ssh://git@localhost:8822/absolute/path/to/git/server.git`.
  + The shorter version `git clone git@gitenter.com:absolute/path/to/git/server.git` only work for case without customized port. Seems using nginx reverse proxy to setup an alien can't help, because we simply cannot redirect traffic to port 22 to nginx (or whatever docker container).

+ [ ] TODO: setup alien `www.gitenter.local`. Seems not working for nginx `server_name` if I don't want to modify `/etc/host`.

Clean up at the end:

```
docker-compose down
```

#### Nuke it and start over

```
docker-compose -f docker-compose.yml -f docker-compose.dev.yml down
docker system prune --volumes --all

docker-compose -f docker-compose.yml -f docker-compose.dev.yml run java-build sh docker_build_java.sh
docker-compose -f docker-compose.yml -f docker-compose.dev.yml build hugo-build
docker-compose -f docker-compose.yml -f docker-compose.dev.yml run hugo-build hugo
docker-compose build --no-cache
docker-compose up
```

### AWS

#### Prerequisite

+ Terraform (testing in `v0.11.13`)
+ Docker

#### Up-and-run

Need to manually create AWS user w/ `access_key` and `secret_key`, save them to `secret.auto.tfvars` of the corresponding folder(s), and then `terraform apply`. See [`tf-config/README.md`](tf-config/README.md) for details.

Code deployment is currently setup through CircleCI.

## Unclassified TODOs and features

+ If only one single upstream item is provided, and it is in the same document/paragraph, should the item be just `tab` of the upstream one, rather than list separately with "hard to understand" link?
+ [ ] We can smartly decide to not redo the traceability analysis if `git diff` is none for the traceable files.
+ [ ] RNAtom id generator has a bug when the item is with empty upstream but in the form of `- [tag]` rather than `-[tag]{}`.
+ [ ] Currently domain layer is in charge of mapping to database table/working with ORM, while it also have non-ORM owned attributes which fill values from git. Repositories are in charge of load both. We have pain issue that when e.g. load a one-to-many object, the git values are not bootstrapped.
    + Separate the domain layer to the database access layer. Domain layer then have dual backup layers of ORM DTO and git DTO. Each DTO has its corresponding repository/DAO. Domain layer are queries by the (stateless) service layer, which further call the repositories/DAOs.
    + May need a way to synchronize the values in the domain layer and the DTO layers. Consider observer pattern for which the boilerplate code may later on be abstract to some reflection in the superclasses (tedious but should be duable).
	+ Will **not** naturally solve the problem that the git lazy evaluation placeholders will not be setup in one-to-many navigations. However, it does have a good separation between the two data sources. May need a better git access point with some code similar to the backend implementation of JPA/Hibernate lazy evalutaion.
