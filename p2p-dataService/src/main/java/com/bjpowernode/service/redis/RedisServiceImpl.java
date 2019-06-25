package com.bjpowernode.service.redis;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.bjpowernode.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-18 19:06
 */

@Service
public class RedisServiceImpl implements RedisService {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public void addMessageCode(String randomMessageCode) {

        BoundValueOperations<String, Object> stringObjectBoundValueOperations = redisTemplate.boundValueOps(Constants.MESSAGE_CODE);
        stringObjectBoundValueOperations.set(randomMessageCode, 1, TimeUnit.MINUTES);


    }

    @Override
    public Long createOnlyNum() {
        BoundValueOperations<String, Object> stringObjectBoundValueOperations = redisTemplate.boundValueOps(Constants.ONLY_NUM);
        Long increment = stringObjectBoundValueOperations.increment(1);//自增
        return increment;
    }

    @Override
    public String checkMessageCode(String messageCode) {
        String flag = "1";
        Object o = redisTemplate.opsForValue().get(Constants.MESSAGE_CODE);
        if(null == o){
            flag = "1";//短信失效
        }else {
            if(StringUtils.isEquals((String) o, messageCode)){
                flag = "2";//短信匹配
            }else {
                flag = "3";//短信不匹配
            }
        }
        return flag;
    }
}
