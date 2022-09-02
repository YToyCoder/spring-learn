## spring bean 的作用域( scope )

### 1 singleton

当scope的值设置为singleton的时候,整个spring容器中只会存在一个bean实例,通过容器多次查找bean的时候(调用BeanFactory的getBean方法或者bean之间注入依赖的bean对象的时候),返回的都是同一个bean对象,singleton是scope的默认值.

### 2 prototype

如果scope被设置为prototype类型的了,表示这个bean是多例的,通过容器每次获取的bean都是不同的实例,每次获取都会重新创建一个bean实例对象。

### 3 request, session, application

request、session、application都是在springweb容器环境中才会有的。

1. request : 对每个http请求都会创建一个bean实例,request结束的时候,这个bean也就结束了
2. session : 这个和request类似,也是用在web环境中,session级别共享的bean,每个会话会对应一个bean实例
3. application : 全局web应用级别的作用于,也是在web环境中使用的,一个web应用程序对应一个bean实例

**application 和 singleton 的不同**

singleton是每个spring容器中只有一个bean实例,一般我们的程序只有一个spring容器,但是,一个应用程序中可以创建多个spring容器,不同的容器中可以存在同名的bean,但是sope=aplication的时候,不管应用中有多少个spring容器,这个应用中同名的bean只有一个。

### 4 自定义scope

1. 实现 Scope 接口
2. 将自定义的scope注册到容器
3. 使用自定义的scope
