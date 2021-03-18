### 源码DEBUG专用工程。

##### 微信搜索：'菜鸟封神记'公众号，定期分享Spring源码系列内容。
![avatar](2.png)

----------------------------------------------------------------------------------
##### 核心容器
```
spring-beans，spring-core: 这两个模块是Spring的核心模块，提供了IOC和DI的支持。
spring-context: 提供了Spring容器的支持，扩展了BeanFactory，提供了Spring中Bean生命周期的支持，在bean创建完成之后，
    也是由该模块负责来维护bean和bean之间的依赖关系。常用的ApplicationContext核心接口也是该模块中所支持的；
spring-expression: 提供了对Spring表达式语言(SPEL)的支持；
```

##### spring Web模块
```
spring-web: 为Spring提供了基础的Web功能支持，主要建立于核心容器之上，通过Servlet或者Listeners来初始化IOC容器；
spring-webmvc: 主要提供对SpringMVC的支持，例如SpringMVC中的DispatcherServlet就是该模块中提供的；
spring-websocket: 提供对WebSocket通信通能的支持，通常用来实现web端的推送功能或者客户端服务器端的即时通信功能；
spring-webflux: Spring WebFlux是Spring Framework 5.0中引入的新的反应式Web框架。与Spring MVC不同，它不需要
    Servlet API，完全异步和非阻塞， 并通过Reactor项目实现Reactive Streams规范。 并且可以在诸如Netty，Undertow
    和Servlet 3.1+容器的服务器上运行。
```

##### 切面AOP
```
spring-aop: Spring的另外一个核心模块，提供对aop核心功能的支持，在Spring中，使用JDK的动态代理为基础，实现了一系列的
    AOP横切逻辑，前置通知，后置通知，环绕通知，返回通知，异常通知等；
spring-aspects: 继承自AspectJ框架，为Spring的AOP提供另外一种实现方式；
spring-instrument: 通过JVM级别的代理技术提供了用于某些应用程序服务器的类工具支持和类加载器的实现；
```

##### 数据访问Data Access
````
spring-jdbc: 用于实现对JDBC的支持，封装了JDBC的常见操作，并简化JDBC的复杂性操作；
spring-tx: 用于实现对本地事务的支持，内部封装了JDBC的事务，通过和AOP整合之后，可以实现在某一层灵活地控制事务；
spring-orm: 即Object Relation Mapping。用于提供对其他ORM框架的支持，以集成其他ORM框架，例如常见的Hibernate,JPA等；
spring-oxm: 即Object XML Mapper。用于支持Java对象和XML文档之间进行相互转换；
spring-jmx: 及Java Message Service。通过封装Java中的JMS规范，提供对一些消息中间件的支持，简化JMS的使用；
````

##### 报文模块
```
spring-messaging: Spring4版本之后新增加的模块，提供对基础报文传送的支持，例如TCP或者SIMP等；
```

##### 测试模块
```
spring-test: 提供对UnitTest的支持，包括Mock功能及Test功能，可以很方便地用来测试一些Spring应用中的组件；
```

##### 其他支持模块
```
spring-framework-bom: 用来统一管理Spring项目与其他应用整合时所使用的Spring版本；具体使用如下"spring-framework-bom
    的使用"：
spring-context-indexer: 在SpringFramework5.0引入了一个注解@Indexed ，它可以为
    Spring的模式注解添加索引，以提升应用启动性能。比如，使用了@Service或者@Repository
    模式注解标注的组件，如果在标注了@Indexed注解之后，在执行编译的时候，会自动在classpath
    下生成一个META-INF/spring.components文件。当Spring应用上下文执行ComponentScan扫描时，
    META-INT/spring.components将会被CandidateComponentsIndexLoader 读取并加载，转换为
    CandidateComponentsIndex对象，这样的话@ComponentScan不在扫描指定的package，而是读取
    CandidateComponentsIndex对象，从而达到提升性能的目的。（有坑：在一个项目中如果引用了两个
    jar包，假设这两个jar中都使用了模式注解，在a.jar中使用了@Indexed，在b.jar中没有使用@Indexed，
    那么同时引入这两个jar包之后，通过@ComponentScan扫描项目中的bean组件，没有标注@Indexed的
    组件将不会被扫描到）
spring-context-support: 提供了spring容器对一些其他扩展功能的支持，例如: JavaMail，Cache，Scheduling等；
spring-jcl: spring5.x之后通过"适配器设计模式"封装的一个通用的日志框架，对外提供一些简单易用的接口，将日志操作委托给具
    体的日志框架；
```

##### spring-framework-bom使用
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-framework-bom</artifactId>
            <version>5.1.2.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-web</artifactId>
    </dependency>
<dependencies>
```