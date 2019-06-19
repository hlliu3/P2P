package com.bjpowernode.service.account;

import com.bjpowernode.mapper.user.FinanceAccountMapper;
import com.bjpowernode.model.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-15 21:52
 */
@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer userId) {

        return financeAccountMapper.selecFinanceAccountByUid(userId);
    }


}
