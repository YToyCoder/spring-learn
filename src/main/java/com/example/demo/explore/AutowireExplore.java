package com.example.demo.explore;

import com.example.demo.CommonUse.BeanA;
import com.example.demo.CommonUse.BeanB;
import com.example.demo.CommonUse.BeanC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;

public class AutowireExplore {
  private static Logger log = LoggerFactory.getLogger(AutowireExplore.class);

  public static void main(String[] args) {
    DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
    beanFactory.registerSingleton("singleton", new Object());
    beanFactory.registerSingleton("bean_b", new BeanB());
    beanFactory.registerSingleton("bean_c", new BeanC());
    beanFactory.setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());

    //
    AutowiredAnnotationBeanPostProcessor processor = new AutowiredAnnotationBeanPostProcessor();
    processor.setBeanFactory(beanFactory);

    BeanA beanA = new BeanA();
    log.debug("Before autowire : {}",beanA);
    processor.postProcessProperties(null, beanA, "beanA");
    log.debug("After autowire : {}",beanA);
  }
}
