package com.example.demo.CommonUse;

import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;

public class RegistProcessors {
  
  static class InstantiationProcessor implements InstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName){
      return null;
    }
  }
}
