package com.bjpowernode.service.recharge;

import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.RechargeRecord;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/18  21:33
 */
public interface RechargeRecoredService {
    List<RechargeRecord> queryRecentlyRechargeByUid(Map<String, Object> paramMapRecentRecharge);
}
