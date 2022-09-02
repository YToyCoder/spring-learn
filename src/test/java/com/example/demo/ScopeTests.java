package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.example.demo.scope.Bean;
import com.example.demo.scope.ThreadScope;

public class ScopeTests {

  private static final Logger log = LoggerFactory.getLogger(ScopeTests.class);

  @Test
  public void scope_test(){
    DefaultListableBeanFactory beanFactory = Utils.getBeanFactory();
    beanFactory.registerScope("thread", new ThreadScope());
    beanFactory.registerBeanDefinition("thread", Utils.getDefinition("thread", Bean.class));

    Utils.loop_in_range(
      0,
      2,
      () -> {
        new Thread(() -> {
          log.info(Thread.currentThread().getName() + beanFactory.getBean("thread").toString());
        }).start();;
      }
    );

    Utils.sleep(100L);
  }
}
