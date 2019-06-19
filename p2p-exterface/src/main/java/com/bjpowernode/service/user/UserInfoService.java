package com.bjpowernode.service.user;

import com.bjpowernode.model.User;
import com.bjpowernode.vo.MsgVO;

import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  16:44
 */

public interface UserInfoService {
    Integer queryAllUserCount();

    User queryUserInfoByPhone(String phone);

    MsgVO registUser(String phone,String password);

    Integer loginUser(User updateUser);

    Map<String,Object> toLoginUser(String phone, String password);
}
