# enterovirus

Enterovirus is not a computer virus!

## Development

### STS

It is very tricky to setup STS. For the dependencies of all maven packages, you don't want to add anything on the project's `Properties > Java Build Path > Projects`. What should be done is to `mvn install` all packages (so they are properly setup in `.m2`) and let Eclipse to load them from `.m2` just like external packages.

Reasons:

(1) Although in the final product, the testing class are completely irrelavent, STS mixed up the `src/test/java` parts of different packages. For example, it may then say errors like this:

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

## Deployment

### Docker

(Re-)build image

```
sudo docker stop gitar
#sudo docker rm gitar
sudo docker rm $(sudo docker ps -a -q -f status=exited)
sudo docker rmi ozooxo/enterovirus
sudo docker build -t ozooxo/enterovirus .
sudo docker run -d -p 52022:22 -p 52418:9418 -p 58080:8080 --name gitar ozooxo/enterovirus
#sudo docker port gitar
```

Access shell (need to stop containers first...)

```
sudo docker run -it ozooxo/enterovirus sh
```

Log in by SSH

```
ssh git@0.0.0.0 -p 52022
```

Clone git repository (not working)

```
git clone ssh://git@0.0.0.0:52418/home/git/server.git
git clone git@0.0.0.0:52418/home/git/server.git
```

Connect to Tomcat server (should have "Hello enterovirus capsid!" return in the browser)

```
http://0.0.0.0:58080/capsid-0.0.1-alpha
```
