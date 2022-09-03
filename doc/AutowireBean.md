## spring autowire

### 1 spring 依赖注入

spring中依赖注入主要分为手动注入和自动注入.

将被依赖方注入到依赖方,通常有2种方式:构造函数的方式和set属性的方式,spring中也是通过这两种方式实现注入的

#### 1.1 手动注入

手动注入需要我们明确配置需要注入的对象

**构造器注入**

构造器的参数就是被依赖的对象,构造器注入又分为3种注入方式:
  - 根据构造器参数索引注入
  - 根据构造器参数类型注入
  - 根据构造器参数名称注入

*索引注入:*

```xml

<bean id="name" class="bean-class">
  <constructor-arg index="0" value="Java"/>
  <constructor-arg index="1" value="constructor"/>
</bean>

```

> constructor-arg用户指定构造器的参数
> 
> index:构造器参数的位置,从0开始
> 
> value:构造器参数的值,value只能用来给简单的类型设置值,value对应的属性类型只能为
> 
> byte,int,long,float,double,boolean,Byte,Long,Float,Double,枚举,spring容器内部注入的时候会
> 
> 将value的值转换为对应的类型。

*参数类型注入:*

```xml
<bean id="name" class="bean-class">
  <constructor-arg type="int" value="30"/>
  <constructor-arg type="java.lang.String" value="StringgValue"/>
</bean>
```

*构造器参数名称*

```xml

  <bean id="name" class="bean-class" >
    <constructor-arg name="arg-name" value="value"/>
  </bean>

```
> *关于方法参数名称的问题*
> java通过反射的方式可以获取到方法的参数名称,不过源码中的参数通过编译之后会变成class对象,通常情况下源码变成class文件之后,参数真实名称会丢失,参数的名称会变成arg0,arg1,arg2这样的,和实际参数名称不一样了

参数名称可能不稳定的问题,spring提供了解决方案,通过ConstructorProperties注解来定义参数的名称,将这个注解加在构造方法上面:

```java

public class CName {

  @ConstructorProperties({"first-param-name", "second-param-name"})
  public CName(String p1, String p2){
  }

}

```

**setter 注入**

> java bean
> 
> 属性都是private访问级别的
> 
> 属性通常情况下通过一组setter(修改器)和getter(访问器)方法来访问
> 
> setter方法,以 set 开头,后跟首字母大写的属性名,如:setUserName,简单属性一般只有一
> 
> 个方法参数,方法返回值通常为void;
> 
> getter方法,一般属性以 get 开头,对于boolean类型一般以 is 开头,后跟首字母大写的属性
> 
> 名,如:getUserName,isOk;

spring对符合javabean特点类,提供了setter方式的注入,会调用对应属性的setter方法将被依赖的对象注入进去。

example 

```xml

  <bean id="name" class="bean-class">
    <property name="property-name" value="value"/>
  </bean>

```

**注入容器中的bean**

注入容器中的bean有两种写法:
  - ref属性方式
  - 内置bean的方式

```xml
  <constructor-arg ref="bean-name"/>
```

```xml

  <property name="property-name" ref="bean-name"/>

```


#### 1.2 自动注入

自动注入是采用约定大约配置的方式来实现的,程序和spring容器之间约定好,遵守某一种都认同的规则,来实现自动注入。

xml设置自动注入:

```xml

  <bean id="name" class="bean-class" autowire="byType|byName|constructor|default" />

```

### 2 byName 按照名称进行注入

spring容器会按照set属性的名称去容器中查找同名的bean对象,然后将查找到的对象通过set方法注入到对应的bean中,未找到对应名称的bean对象则set方法不进行注入.

需要注入的set属性的名称和被注入的bean的名称必须一致。

### 3 byType 按照类型注入

spring容器会遍历x类中所有的set方法,会在容器中查找和set参数类型相同的bean对象,将其通过set方法进行注入,未找到对应类型的bean对象则set方法不进行注入。

```xml

  <bean id="name" class="bean-class" autowire="byType"/>

```

### 4 constructor 按照构造函数进行自动注入

spring会找到x类中所有的构造方法(一个类可能有多个构造方法),然后将这些构造方法进行排序(先按修饰符进行排序,public的在前面,其他的在后面,如果修饰符一样的,会按照构造函数参数数量倒叙,也就是采用贪婪的模式进行匹配,spring容器会尽量多注入一些需要的对象)得到一个构造函数列表,会轮询这个构造器列表,判断当前构造器所有参数是否在容器中都可以找到匹配的bean对象,如果可以找到就使用这个构造器进行注入,如果不能找到,那么就会跳过这个构造器,继续采用同样的方式匹配下一个构造器,直到找到一个合适的为止。

### 5 autowire = default

bean xml的根元素为beans,注意根元素有个 default-autowire 属性,这个属性可选值有(no|byName|byType|constructor|default),这个属性可以批量设置当前文件中所有bean的自动注入的方式,bean元素中如果省略了autowire属性,那么会取 default-autowire 的值作为其 autowire 的值,而每个bean元素还可以单独设置自己的 autowire 覆盖 default-autowire 的配置,如下:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
  http://www.springframework.org/schema/beans/spring-beans-4.3.xsd"
  default-autowire="byName">

</beans>
```

### 6 Depend-On

> depend-on指定的bean在当前bean之前先创建好,销毁的时候在当前bean之后进行销毁。

```xml

<bean id="bean" class="bean-class" depend-on="bean1,bean2;bean4 bean5" />

```

> depend-on:设置当前bean依赖的bean名称,可以指定多个,多个之间可以用”,;空格“进行分割

### 7 primary

当希望从容器中获取到一个bean对象的时候,容器中却找到了多个匹配的bean,会抛出`org.springframework.beans.factory.NoUniqueBeanDefinitionException`异常.

spring中可以通过bean元素的primary属性来解决这个问题,可以通过这个属性来指定当前bean为主要候选者,当容器查询一个bean的时候,如果容器中有多个候选者匹配的时候,此时spring会返回主要的候选者。
