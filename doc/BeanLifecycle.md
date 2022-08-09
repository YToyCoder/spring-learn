## BeanLifecycle (bean的生命周期)

参考： 

> [一文读懂 Spring Bean 的生命周期]( https://blog.csdn.net/riemann_/article/details/118500805 )
> 
> 黑马程序员Spring视频教程，全面深度讲解spring5底层原理

### 1 Bean生命周期

```
 Instantiation (实例化 - new) -->  Properties(属性赋值 - do @Autwired | @Value | @Resource)  --> Initialization (初始化 - init | PosConstruct) --> Destruction (销毁 - destroy PreDestroy)
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

BeanPostProcessor的`postProcessAfterInitialization`和`postProcessBeforeInitialization`方法的返回值都会替换原本的`bean`.

```java

public interface BeanPostProcessor {

	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}


	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}

```
#### 2.1 BeanPostProcessor的五大接口

1. BeanPostProcessor

对象初始化前后的回调

2. InstantiationAwareBeanPostProcessor

对象实例化前后的回调处理

3. SmartInstantiationAwareBeanPostProcessor

spring框架内部使用的接口

```java
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {
    //用来返回目标对象的类型（比如代理对象通过raw class获取proxy type 用于类型匹配）
    @Nullable
    default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }
    //这里提供一个拓展点用来解析获取用来实例化的构造器（比如未通过bean定义构造器以及参数的情况下，会根据这个回调来确定构造器）
    @Nullable
    default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName)
            throws BeansException {
        return null;
    }
    //获取要提前暴露的bean的引用，用来支持单例对象的循环引用（一般是bean自身，如果是代理对象则需要取用代理引用）
    default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
```

4. MergedBeanDefinitionPostProcessor

用来将merged BeanDefinition暴露出来的回调

在bean实例化完毕后调用 可以用来修改merged BeanDefinition的一些properties 或者用来给后续回调中缓存一些meta信息使用这个算是将merged BeanDefinition暴露出来的一个回调

5. DestructionAwareBeanPostProcessor

关于处理对象销毁的前置回调

关于BeanPostProcessor中各个回调调用的顺序

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
                    ||                    
                    || <--------------- DefaultSingletonBeanRegistry#postProcessBeforeDestruction
                    \/
              Destroy (销毁)
```

1、InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation(beanClass, beanName)该方法在创建对象之前会先掉用，如果有返回实例则直接使用不会去走下面创建对象的逻辑，并在之后执行BeanPostProcessor.postProcessAfterInitialization(result, beanName)

2、SmartInstantiationAwareBeanPostProcessor.determineCandidateConstructors(beanClass, beanName)如果需要的话，会在实例化对象之前执行

3、MergedBeanDefinitionPostProcessor.postProcessMergedBeanDefinition(mbd, beanType, beanName)在对象实例化完毕 初始化之前执行

4、InstantiationAwareBeanPostProcessor.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)在bean创建完毕初始化之前执行

5、InstantiationAwareBeanPostProcessor.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName)在bean的property属性注入完毕 向bean中设置属性之前执行

6、BeanPostProcessor.postProcessBeforeInitialization(result, beanName)在bean初始化（自定义init或者是实现了InitializingBean.afterPropertiesSet()）之前执行

7、BeanPostProcessor.postProcessAfterInitialization(result, beanName)在bean初始化（自定义init或者是实现了InitializingBean.afterPropertiesSet()）之后执行

8、其中DestructionAwareBeanPostProcessor方法的postProcessBeforeDestruction(Object bean, String beanName)会在销毁对象前执行

*参考*

[BeanPostProcessor的五大接口] (https://www.cnblogs.com/zhangjianbin/p/10059191.html)

#### 2.2 InstantiationAwareBeanPostProcessor

1、postProcessBeforeInstantiation调用时机为bean实例化(Instantiation)之前 如果返回了bean实例, 则会替代原来正常通过target bean生成的bean的流程. 典型的例如aop返回proxy对象. 此时bean的执行流程将会缩短, 只会执行 BeanPostProcessor#postProcessAfterInitialization接口完成初始化。

2、postProcessAfterInstantiation调用时机为bean实例化(Instantiation)之后和任何初始化(Initialization)之前。

3、postProcessProperties调用时机为postProcessAfterInstantiation执行之后并返回true, 返回的PropertyValues将作用于给定bean属性赋值. spring 5.1之后出现以替换@Deprecated标注的postProcessPropertyValues


验证`postProcessBeforeInstantiation`方法,代码。

```java

  static final Object obj = new Object();
  static final String pre_new_name = "pre_new_name";

  static class PrenewObjProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName){
      return Objects.equals( beanName, pre_new_name ) ? obj : null;
    }
  }


  @Bean
  public PrenewObjProcessor prenewObjProcessor(){
    return new PrenewObjProcessor();
  }

  @Bean
  public Object pre_new_name(){
    return null;
  }

  @Bean
  public TestDriver test(@Qualifier(pre_new_name) Object obj){
    return new TestDriver(obj);
  }


  public static class TestDriver implements DisposableBean{
    private Object obj;

    public TestDriver(Object _obj){
      obj = _obj;
    }

    @Override
    public void destroy() throws Exception {
      log.info("static obj and autwired bean is equal ? " + Objects.equals(this.obj, RegistProcessors.obj));
    }
  }
```
如果`postProcessBeforeInstantiation`正常运行，TestDriver的obj和static的obj应该相等。

日志打印结果:

```
2022-08-08 16:50:51.000  INFO 55500 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy by DisposableBean
2022-08-08 16:50:51.001  INFO 55500 --- [ionShutdownHook] c.e.demo.CommonUse.RegistProcessors      : static obj and autwired bean is equal ? true
2022-08-08 16:50:51.002  INFO 55500 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy
```

验证`postProcessAfterInitialization`方法，代码：

```java

  static class PropertiesBean{
    String JavaHome;
    public PropertiesBean (){
      JavaHome = "not set";
    }

    @Value("${JAVA_HOME}")
    public void jh(String jh){
      JavaHome = jh;
    }
  }

  public static class StopSetPropertiesProcessor implements InstantiationAwareBeanPostProcessor{

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName){
      return ! (bean instanceof PropertiesBean );
    }

  }

  @Bean
  public PropertiesBean propertiesBean(){
    return new PropertiesBean();
  }

  @Bean
  public StopSetPropertiesProcessor stopSetPropertiesProcessor(){
    return new StopSetPropertiesProcessor();
  }

  @Bean
  public DisposableBean postProcessAfterInstantiationDriver (PropertiesBean propertiesBean){
    return () -> {
      log.info(propertiesBean.JavaHome);
    };
  }

```

`StopSetPropertiesProcessor`会阻止`PropertiesBean`设置属性(@Autowired), 在最后的时候应该打印`not set`.

日志打印结果:

```
2022-08-08 17:19:25.487  INFO 53656 --- [ionShutdownHook] com.example.demo.CommonUse.RegisteBeans  : destroy by DisposableBean
2022-08-08 17:19:25.488  INFO 53656 --- [ionShutdownHook] c.e.demo.CommonUse.RegistProcessors      : not set
```

#### `AbstractAutwireCapableBeanFactory`部分源码

`AbstractAutwireCapableBeanFactory#createBean`源码在`doCreateBean`调用了`resolveBeforeInstantiation`

```java

	/**
	 * Central method of this class: creates a bean instance,
	 * populates the bean instance, applies post-processors, etc.
	 * @see #doCreateBean
	 */
	@Override
	protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
			throws BeanCreationException {

		if (logger.isTraceEnabled()) {
			logger.trace("Creating instance of bean '" + beanName + "'");
		}
		RootBeanDefinition mbdToUse = mbd;

		// Make sure bean class is actually resolved at this point, and
		// clone the bean definition in case of a dynamically resolved Class
		// which cannot be stored in the shared merged bean definition.
		Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
		if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
			mbdToUse = new RootBeanDefinition(mbd);
			mbdToUse.setBeanClass(resolvedClass);
		}

		// Prepare method overrides.
		try {
			mbdToUse.prepareMethodOverrides();
		}
		catch (BeanDefinitionValidationException ex) {
			throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),
					beanName, "Validation of method overrides failed", ex);
		}

		try {
			// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
			Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
			if (bean != null) {
				return bean;
			}
		}
		catch (Throwable ex) {
			throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
					"BeanPostProcessor before instantiation of bean failed", ex);
		}

		try {
			Object beanInstance = doCreateBean(beanName, mbdToUse, args);
			if (logger.isTraceEnabled()) {
				logger.trace("Finished creating instance of bean '" + beanName + "'");
			}
			return beanInstance;
		}
		catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
			// A previously detected exception with proper bean creation context already,
			// or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
			throw ex;
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
		}
	}
```

```java

	/**
	 * Apply before-instantiation post-processors, resolving whether there is a
	 * before-instantiation shortcut for the specified bean.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @return the shortcut-determined bean instance, or {@code null} if none
	 */
	@Nullable
	protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
		Object bean = null;
		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
			// Make sure bean class is actually resolved at this point.
			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
				Class<?> targetType = determineTargetType(beanName, mbd);
				if (targetType != null) {
					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					if (bean != null) {
						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					}
				}
			}
			mbd.beforeInstantiationResolved = (bean != null);
		}
		return bean;
	}


	/**
	 * Apply InstantiationAwareBeanPostProcessors to the specified bean definition
	 * (by class and name), invoking their {@code postProcessBeforeInstantiation} methods.
	 * <p>Any returned object will be used as the bean instead of actually instantiating
	 * the target bean. A {@code null} return value from the post-processor will
	 * result in the target bean being instantiated.
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to use instead of a default instance of the target bean, or {@code null}
	 * @see InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation
	 */
	@Nullable
	protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
		for (InstantiationAwareBeanPostProcessor bp : getBeanPostProcessorCache().instantiationAware) {
			Object result = bp.postProcessBeforeInstantiation(beanClass, beanName);
			if (result != null) {
				return result;
			}
		}
		return null;
	}


	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessAfterInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```

根据上面的源码可以看出，如果`InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`实例化对象之后，会直接执行`InstantiationAwareBeanPostProcessor#postProcessAfterInitialization`，并不会去通过BeanFactory实例化对象。

该逻辑可以参考如下方法:

```java

  try {
    // Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
    Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
    if (bean != null) {
      return bean;
    }
  }
  catch (Throwable ex) {
    throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,
        "BeanPostProcessor before instantiation of bean failed", ex);
  }

```


#### 2.2 BeanPostProcessor 