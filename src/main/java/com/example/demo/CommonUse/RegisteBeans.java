package com.example.demo.CommonUse;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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

  @Bean
  public InitialBean initialBean(){
    return new InitialBean();
  }

  @Bean
  public DestroyBean destroyBean(){
    return new DestroyBean();
  }


  static class InitialBean implements InitializingBean{

    @Override
    public void afterPropertiesSet() throws Exception {
      log.info("initial by InitializingBean");
    }
  }

  static class DestroyBean implements DisposableBean{

    @Override
    public void destroy() throws Exception {
      log.info("destroy by DisposableBean");
    }
  }

}
