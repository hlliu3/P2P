package com.bjpowernode.service.redis;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-18 19:03
 */
public interface RedisService {

    public void addMessageCode(String randomMessageCode);

    String checkMessageCode(String messageCode);
}
