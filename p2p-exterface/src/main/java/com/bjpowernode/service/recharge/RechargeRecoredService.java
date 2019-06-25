package com.bjpowernode.service.recharge;

import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.RechargeRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/18  21:33
 */
public interface RechargeRecoredService {
    List<RechargeRecord> queryRecentlyRechargeByUid(Map<String, Object> paramMapRecentRecharge);

    Integer addRechareRecored(RechargeRecord rechargeRecord);

    Integer modifyRechargeRecord(RechargeRecord updateRechargeStatus);

    RechargeRecord queryRechargeRecordByRechargeNo(String out_trade_no);

    Boolean recharge(HashMap<String, Object> paramMap);
}
