package com.example.demo.autowire;

public class AutowireByName {

  public static class NamedBean {
  }

  private NamedBean bean;

  public void setBean(NamedBean bean){
    this.bean = bean;
  }

  @Override
  public String toString() {
    return "AutowireByName [bean=" + bean + "]";
  }

}
