package com.example.demo.CommonUse;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RegistProcessors {

  public static final Logger log = LoggerFactory.getLogger(RegistProcessors.class);
  
  static class InstantiationProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName){
      return null;
    }
  }


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
  public PostProcessBeforeInstantiationDriver test(@Qualifier(pre_new_name) Object obj){
    return new PostProcessBeforeInstantiationDriver(obj);
  }


  public static class PostProcessBeforeInstantiationDriver implements DisposableBean{
    private Object obj;

    public PostProcessBeforeInstantiationDriver(Object _obj){
      obj = _obj;
    }

    @Override
    public void destroy() throws Exception {
      log.info("static obj and autwired bean is equal ? " + Objects.equals(this.obj, RegistProcessors.obj));
    }
  }

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

}
