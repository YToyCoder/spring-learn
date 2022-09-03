package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AutowireTests {
  private static final Logger log = LoggerFactory.getLogger(AutowireTests.class);

  private static final String autowire_by_name = "classpath:/beans/autowireByName.xml";

  @Test
  public void autowire_by_name_test(){
    try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(autowire_by_name)) {
      // applicationContext.refresh();
      log.info(applicationContext.getBean("autowired").toString());
      log.info(applicationContext.getBean("autowiredNo").toString());
      log.info(applicationContext.getBean("autowiredType").toString());
    } catch (BeansException e) {
      e.printStackTrace();
    }
  }
}
