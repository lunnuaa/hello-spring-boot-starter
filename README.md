# 自定义 xxx-spring-boot-starter

## 前言

本文主要参考 SpringBoot 官方文档，以及 mybatis-spring-boot-starter，自定义 hello-spring-boot-starter 并进行测试和使用。

## 1、整体架构 / 准备工作

根据官方文档的说法，一般一个 starter 需要两个模块， `autoconfigure` 模块是真正的自动配置， `starter` 模块是提供依赖项，因此先初始化项目结构：

- 创建 xxx-spring-boot-starter 模块
- 创建 xxx-spring-boot-autoconfigure 模块
- 编写父工程的 pom.xml

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.lun.hello.spring.boot</groupId>
  <artifactId>hello-spring-boot</artifactId>
  <version>0.1-SNAPSHOT</version>
  <packaging>pom</packaging>

  <modules>
    <module>hello-spring-boot-autoconfigure</module>
    <module>hello-spring-boot-starter</module>
  </modules>

  <properties>
    <spring-boot.version>2.7.18</spring-boot.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <version>${spring-boot.version}</version>
      </dependency>
      <dependency>
        <groupId>com.lun.hello.spring.boot</groupId>
        <artifactId>hello-spring-boot-starter</artifactId>
        <version>${project.version}</version>
      </dependency>
      <dependency>
        <groupId>com.lun.hello.spring.boot</groupId>
        <artifactId>hello-spring-boot-autoconfigure</artifactId>
        <version>${project.version}</version>
      </dependency>
    </dependencies>
  </dependencyManagement>
</project>
```

## 2、编写 starter 模块

starter 模块实质是一个空的 jar 包，唯一的目的是提供使用库所需的依赖项。

因此，只需要在 starter 模块中提供一个 pom.xml 即可。

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
    
  <parent>
    <groupId>com.lun.hello.spring.boot</groupId>
    <artifactId>hello-spring-boot</artifactId>
    <version>0.1-SNAPSHOT</version>
  </parent>
    
  <artifactId>hello-spring-boot-starter</artifactId>
  <name>hello-spring-boot-starter</name>
    
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>
      <!-- 你编写的 autoconfigure 模块 --> 
     <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>hello-spring-boot-autoconfigure</artifactId>
    </dependency>
  </dependencies>
</project>
```

## 3、编写 autoconfigure 模块

### 3.1 pom.xml

```xml
<dependencies>
  <!-- Compile dependencies -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure</artifactId>
  </dependency>

  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-autoconfigure-processor</artifactId>
    <optional>true</optional>
  </dependency>

  <!-- @ConfigurationProperties annotation processing 和上面的依赖有点小区别 -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
  </dependency>
  
   <!-- Optional dependencies 特定的依赖  ...  -->
    <dependency>
      <groupId>org.mybatis</groupId>
      <artifactId>mybatis</artifactId>
      <optional>true</optional>
    </dependency>
    
  <!-- Test dependencies -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
  </dependency>
  <!--  ... -->

</dependencies>
```

### 3.2 创建目录结构

我的 demo 的完整目录结构如下：

![image-20240204223231483](https://blog-note-picture.oss-cn-shanghai.aliyuncs.com/note/image-20240204223231483.png)

下图为 MyBatis 的目录结构

![image-20240204225049144](https://blog-note-picture.oss-cn-shanghai.aliyuncs.com/note/image-20240204225049144.png)

### 3.3 创建配置文件

所有配置文件放在单独的类中，并以项目的名称（通常）为前缀命名

```java
@ConfigurationProperties(prefix =  HelloProperties.HELLO_PREFIX)
public class HelloProperties {

    public static final String HELLO_PREFIX = "hello";

    private String name = "hello-spring-boot-starter";

   	// 需要提供 get/set 方法 
   
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

> 这里如果出现 @ConfigurationProperties 报错
>
> 是提示需要 @EnableConfigurationProperties(xxxProperties.class)

### 3.4 编写核心自动配置类

一般命名为 xxxAutoConfiguration。

```java
@Configuration
@EnableConfigurationProperties(HelloProperties.class)
public class HelloAutoConfiguration {
    private final HelloProperties properties;

    public HelloAutoConfiguration(HelloProperties properties) {
        this.properties = properties;
    }
    @Bean
    @ConditionalOnMissingBean(User.class)
    public User user(){
        return new User(properties.getName());
    }
}
```

### 3.5 编写 SpringBoot 配置文件

在src/main/resources新建文件夹META-INF，新建一个spring.factories文件，列出自动配置类如下：

```bash
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
{自动配置类的全类名},\
# example:
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.lun.hello.spring.boot.autoconfigure.HelloAutoConfiguration
```

Spring Boot 2.x 与 Spring Boot 3 的配置文件不同，为了上下兼容，还需要做下面这一步配置：

在src/main/resources/META-INF下，创建spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 文件，列出{自动配置类的全类名}，每行一个。

```
com.lun.hello.spring.boot.autoconfigure.HelloAutoConfiguration
```

### 3.6 补充说明

关于 pom.xml：

``` xml
<!-- 用于读取 META-INF/spring-autoconfigure-metadata.properties 的文件的配置项的 -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-autoconfigure-processor</artifactId>
  <optional>true</optional>
</dependency>
```

autoconfigure 模块的大多数的依赖都应该加上：

```xml
<optional>true</optional>
```

关于自动配置类，即 xxxAutoConfiguration 类，不应该扫描其它类，也不能被其他类扫描到，可以使用 @Import 注解来引入类。

关于VFS，MyBatis 的 autocinfigure 包下有一个 SpringBootVFS 部分源码如下：

```java
public class SpringBootVFS extends VFS {
   public SpringBootVFS() {  
       this.resourceResolver
       = new PathMatchingResourcePatternResolver(classLoaderSupplier.get());
  }
}
```

VFS，VirtualFileSystem，虚拟文件系统，用于从应用或应用服务器中寻找类 （例如： 类型别名的目标类，类型处理器类） 。

> [引自官网](http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/#Using_the_SpringBootVFS)：The VFS is used for searching classes (e.g. target class of type alias,  type handler class) from an application (or application server). If you  run a Spring Boot application using the executable jar, you need to use  the SpringBootVFS. The auto-configuration feature provided by the  MyBatis-Spring-Boot-Starter used it automatically, but it does not use  automatically by a manual configuration (e.g. when uses multiple  DataSource).

## 4、测试

由于 condition ，@Bean 等因素的干扰，测试通常比较费劲，因此 SpringBoot 提供了 ApplicationContextRunner ，可以方便地进行测试。

```java
class HelloAutoConfigurationTest {
	
    // 如果需要在 serlet 环境下测试，使用 WebApplicationContextRunner 
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HelloAutoConfiguration.class));

    @Test
    void testUserAlreadyExists() {
        // withUserConfiguration 方法，指定当前的配置,环境
        this.contextRunner.withUserConfiguration(UserConfiguration.class).run(
                context -> {
                    // 在 run 方法中使用 assertThat 来测试
                    assertThat(context.getBeanNamesForType(User.class)).hasSize(1);
                });
    }
    @Test
    void testUserAutoConfigure() {
        this.contextRunner.run(
                context -> {
                    assertThat(context.getBeanNamesForType(User.class)).hasSize(1);
                });
    }

    @Configuration
    static class UserConfiguration {
        @Bean
        User user() {
            return new User();
        }
    }
}
```

这里再看一个 MyBatis 的例子：

``` java
class MybatisAutoConfigurationTest {

  private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
      .withConfiguration(AutoConfigurations.of(MybatisAutoConfiguration.class));

	 @Test
 	void testDefaultConfiguration() {
     // 存在 @MapperScan 注解的情况
     // 不再引入 @Import(AutoConfiguredMapperScannerRegistrar.class)
    this.contextRunner
        .withUserConfiguration(MybatisScanMapperConfiguration.class)
        .run(context -> {
			assertThat(sqlSessionFactory.getConfiguration().
           getMapperRegistry().getMappers()).hasSize(1);
            // ... 
   		});
  	}

	// withUserConfiguration 的配置
	@Configuration
	@MapperScan(basePackages = "com.example.mapper", lazyInitialization = "${mybatis.lazy-initialization:false}")
	static class MybatisScanMapperConfiguration {
	}
}
```

## 5、引入该依赖并使用

另起一个 SpringBoot 项目，引入该包：

```xml
<dependency>
    <groupId>com.lun.hello.spring.boot</groupId>
    <artifactId>hello-spring-boot-starter</artifactId>
    <version>0.1-SNAPSHOT</version>
</dependency>
```

运行测试：

```java
public static void main(String[] args) {
    ConfigurableApplicationContext run = SpringApplication.run(MainApplication.class, args);
    User bean = run.getBean(User.class);
    System.out.println(bean.getName());
}
```

顺利打印出 `hello-spring-boot-starter`，如下：

``` 
 ... : Started MainApplication in 4.771 seconds (JVM running for 5.783)
hello-spring-boot-starter
```

### 参考文档

[Creating Your Own Starter](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.developing-auto-configuration.custom-starter)





