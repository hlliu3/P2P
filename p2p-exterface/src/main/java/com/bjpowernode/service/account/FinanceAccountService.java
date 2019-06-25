package com.bjpowernode.service.account;

import com.bjpowernode.model.FinanceAccount;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/15  21:43
 */

public interface FinanceAccountService {
    FinanceAccount queryFinanceAccountByUid(Integer userId);

    Integer modifyFinaceAccountAfterRecharge(FinanceAccount financeAccount);

    void rechargeAfterFail();
}
