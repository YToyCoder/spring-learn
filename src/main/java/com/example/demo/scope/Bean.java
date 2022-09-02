package com.example.demo.scope;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class Bean implements InitializingBean, DisposableBean{
  private static Logger log = LoggerFactory.getLogger(Bean.class);

  @Override
  public void destroy() throws Exception {
    log.info(this.toString() + "-destroy");
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    log.info(this.toString() + "-initializing");
  }
  
}
