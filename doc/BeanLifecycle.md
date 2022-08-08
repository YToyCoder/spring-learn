## BeanLifecycle (bean的生命周期)

参考： 

> [一文读懂 Spring Bean 的生命周期]( https://blog.csdn.net/riemann_/article/details/118500805 )
> 
> 黑马程序员Spring视频教程，全面深度讲解spring5底层原理

### 1 Bean生命周期

```
 Instantiation (实例化 - new) -->  Properties(属性赋值 - do autwire)  --> Initialization (初始化 - init | PosConstruct) --> Destruction (销毁 - destroy PreDestroy)
```

查看以下类的打印结果

```java

@Component
public class RegisteBeans {
  static final Logger log = LoggerFactory.getLogger(RegisteBeans.class); 

  public RegisteBeans(){
    log.info("created");
  }

  @Autowired
  public void setJV(@Value("${JAVA_HOME}") String jh){
    log.info(jh);
  }

  @PostConstruct
  public void init(){
    log.info("init");
  }

  @PreDestroy
  public void destroy(){
    log.info("destroy");
  }

}
```
```

2022-08-08 14:58:39.164  INFO 35048 --- [           main] com.example.demo.CommonUse.RegisteBeans  : created
2022-08-08 14:58:39.171  INFO 35048 --- [           main] com.example.demo.CommonUse.RegisteBeans  : E:/JDK-/jdk-17
2022-08-08 14:58:39.171  INFO 35048 --- [           main] com.example.demo.CommonUse.RegisteBeans  : init
2022-08-08 14:58:39.231  INFO 35048 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 0.635 seconds (JVM running for 0.867)
2022-08-08 14:58:39.236  INFO 35048 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy
```

注册init方法的不同方式

`InitializingBea`

```java
class InitialBean implements InitializingBean{

  @Override
  public void afterPropertiesSet() throws Exception {
    log.info("initial by InitializingBean");
  }
}
```
日志打印结果

```

2022-08-08 15:14:03.404  INFO 2016 --- [           main] com.example.demo.CommonUse.RegisteBeans  : created
2022-08-08 15:14:03.412  INFO 2016 --- [           main] com.example.demo.CommonUse.RegisteBeans  : E:/JDK-/jdk-17
2022-08-08 15:14:03.413  INFO 2016 --- [           main] com.example.demo.CommonUse.RegisteBeans  : init
2022-08-08 15:14:03.414  INFO 2016 --- [           main] com.example.demo.CommonUse.RegisteBeans  : initial by InitializingBean
2022-08-08 15:14:03.473  INFO 2016 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 0.645 seconds (JVM running for 0.882)
2022-08-08 15:14:03.477  INFO 2016 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy
```

注册destroy方法的不同方式

```java

  class DestroyBean implements DisposableBean{

    @Override
    public void destroy() throws Exception {
      log.info("destroy by DisposableBean");
    }
  }
```

日志打印结果

```
2022-08-08 15:22:29.358  INFO 35944 --- [           main] com.example.demo.CommonUse.RegisteBeans  : created
2022-08-08 15:22:29.362  INFO 35944 --- [           main] com.example.demo.CommonUse.RegisteBeans  : E:/JDK-/jdk-17
2022-08-08 15:22:29.362  INFO 35944 --- [           main] com.example.demo.CommonUse.RegisteBeans  : init
2022-08-08 15:22:29.363  INFO 35944 --- [           main] com.example.demo.CommonUse.RegisteBeans  : initial by InitializingBean
2022-08-08 15:22:29.411  INFO 35944 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 0.593 seconds (JVM running for 0.827)
2022-08-08 15:22:29.415  INFO 35944 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy by DisposableBean
2022-08-08 15:22:29.415  INFO 35944 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy
```

### 2 BeanPostProcessor

BeanPostProcessor提供一些bean的后置处理方法的接口,这些接口的实现类是独立于`Bean`的，并且会注册到Spring容器中。在 Spring容器创建任何`Bean`的时候，这些后处理器都会发生作用。

```
                BeanDefinition
                    ||
                    ||  <------------------+  postProcessBeforeInstantiation
                    \/                     |
                Instantiation(实例化)       +-------- InstantiationAwareBeanPostProcessor
                    ||                     |
                    ||  <------------------+ postProcessAfterInitialization
                    \/
              setProperties(属性赋值)
                    ||
                    || <------------------+ postProcessBeforeInstantiation
                    \/                    |
              Intitializing(初始化)        +--------- BeanPostProcessor
                    ||                    |
                    || <------------------+ postProcessAfterInitialization
                    \/
              Destroy (销毁)
```

#### InstantiationAwareBeanPostProcessor

1、postProcessBeforeInstantiation调用时机为bean实例化(Instantiation)之前 如果返回了bean实例, 则会替代原来正常通过target bean生成的bean的流程. 典型的例如aop返回proxy对象. 此时bean的执行流程将会缩短, 只会执行 

 BeanPostProcessor#postProcessAfterInitialization接口完成初始化。

2、postProcessAfterInstantiation调用时机为bean实例化(Instantiation)之后和任何初始化(Initialization)之前。

3、postProcessProperties调用时机为postProcessAfterInstantiation执行之后并返回true, 返回的PropertyValues将作用于给定bean属性赋值. spring 5.1之后出现以替换@Deprecated标注的postProcessPropertyValues

4、postProcessPropertyValues已经被标注@Deprecated，后续将会被postProcessProperties取代。