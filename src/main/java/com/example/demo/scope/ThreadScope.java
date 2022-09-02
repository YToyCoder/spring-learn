package com.example.demo.scope;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ThreadScope implements Scope{
  private final Logger log = LoggerFactory.getLogger(ThreadScope.class);

  ThreadLocal<Map<String, Object>> local = new ThreadLocal<>(){

    @Override
    public Map<String, Object> initialValue() {
        return new HashMap<>();
    }

  };

  @Override
  public Object get(String arg0, ObjectFactory<?> arg1) {
    return local.get().computeIfAbsent(arg0, key -> arg1.getObject());
  }

  @Override
  public String getConversationId() {
    return Thread.currentThread().getName();
  }

  @Override
  public void registerDestructionCallback(String arg0, Runnable arg1) {
    log.info("reisterDestruction-{}",arg0);
  }

  @Override
  public Object remove(String arg0) {
    return local.get().remove(arg0);
  }

  @Override
  public Object resolveContextualObject(String arg0) {
    return null;
  }
}
