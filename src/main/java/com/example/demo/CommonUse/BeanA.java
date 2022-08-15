package com.example.demo.CommonUse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;

public class BeanA {
  private static final Logger log = LoggerFactory.getLogger(BeanA.class);

  private BeanB beanB;
  private BeanC beanC;
  private String home = "";

  @Autowired
  public void setBeanB(BeanB beanB){
    log.debug(Autowire.format(beanB.toString()));
    this.beanB = beanB;
  }

  @Resource
  public void setBeanC(BeanC beanC){
    log.debug("@Resource : {}", beanC);
    this.beanC = beanC;
  }

  @Autowired
  public void setHome(@Value("${JAVA_HOME}") String home){
    log.debug(Autowire.format(home));
    this.home = home;
  }

  @Override
  public String toString() {
    return "BeanA{" +
        "beanB=" + beanB +
        ", beanC=" + beanC +
        ", home='" + home + '\'' +
        '}';
  }
}
