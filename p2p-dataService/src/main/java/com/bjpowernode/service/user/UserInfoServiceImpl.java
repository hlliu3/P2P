package com.bjpowernode.service.user;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.bjpowernode.Constants;
import com.bjpowernode.mapper.user.FinanceAccountMapper;
import com.bjpowernode.mapper.user.UserMapper;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.User;
import com.bjpowernode.vo.MsgVO;
import org.jboss.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  16:46
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public User queryUserInfoByPhone(String phone) {
        return userMapper.selectUserInfoByPhone(phone);
    }

    /**
     * 查询平台用户的数量
     * @return
     */
    @Override
    public Integer queryAllUserCount() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        Integer allUserCount = (Integer) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
        if(null == allUserCount){
            synchronized (this){//解决redis缓存穿透问题，多线程高并发的情况下，只会执行一次数据库查询
                allUserCount = (Integer) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
                if(null == allUserCount){
                    allUserCount = userMapper.selectAllUserCount();
                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT, allUserCount, 15, TimeUnit.MINUTES);
                }
            }
        }

        return allUserCount;
    }

    /**
     * 登录用户，更新最近登录时间
     * @param phone
     * @param password
     * @return
     */
    @Override
    public Map<String,Object> toLoginUser(String phone, String password) {
        int count = 0;
        Map<String,Object> resMap = new HashMap<>();
        User user = userMapper.selectUserInfoByPhone(phone);
        if(null != user){
            if(StringUtils.isEquals(user.getLoginPassword(), password)){
                //user是上一次登陆的时间，放在session中
                resMap.put(Constants.USER_INFO, user);
                User userUpdate = new User();
                userUpdate.setId(user.getId());
                //更新本次登录时间
                userUpdate.setLastLoginTime(new Date());
                int countFlag = userMapper.updateByPrimaryKeySelective(userUpdate);
                count = countFlag;
            }else{
                count = 0;
            }
        }else{
            count = 0;
        }
        resMap.put("count", count);
        return resMap;
    }

    /**
     * 选择性更新用户
     * @param updateUser
     * @return
     */
    @Override
    public Integer loginUser(User updateUser) {
        int i = userMapper.updateByPrimaryKeySelective(updateUser);
        return i;
    }

    @Override
    public MsgVO registUser(String phone,String password){
        //添加用户
        User user = new User();
        user.setPhone(phone);
        user.setLastLoginTime(new Date());
        user.setAddTime(new Date());
        user.setLoginPassword(password);
        int regisCount = userMapper.insertSelective(user);
        MsgVO msgVO = new MsgVO();

        if(1 == regisCount){
            //查询用户id
            User usertmp = userMapper.selectUserInfoByPhone(phone);//查询未提交（脏读）

            //开立账户，添加888元注册奖励
            FinanceAccount financeAccount = new FinanceAccount();
            financeAccount.setUid(usertmp.getId());
            financeAccount.setAvailableMoney(Constants.REGIST_BOUNTY);
            int accountCount = financeAccountMapper.insert(financeAccount);
            if( 1 == accountCount){
                msgVO.setMsg(Constants.SUCCESS);
            }else{
                msgVO.setMsg(Constants.FAIL);
            }
        }else{
            msgVO.setMsg(Constants.FAIL);
        }
        return msgVO;
    }
}
