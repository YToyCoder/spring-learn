package com.example.demo;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;

public class Utils {

  public static DefaultListableBeanFactory getBeanFactory(){
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
    new AutowiredAnnotationBeanPostProcessor().setBeanFactory(beanFactory);
    return beanFactory;
  }

  public static BeanDefinition getDefinition(String scope, Class<?> class1){
    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
    beanDefinition.setBeanClass(class1);
    beanDefinition.setScope(scope);
    return beanDefinition;
  }

  public static void loop_in_range(int start, int end, Runnable run){
    for(int i=0; i<end; i++){
      run.run();
    }
  }

  public static void sleep(long time){
    try{
      Thread.sleep(time);
    }catch(InterruptedException e){
      e.printStackTrace();
    }
  }

}
