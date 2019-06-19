package com.bjpowernode.service.recharge;

import com.bjpowernode.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-18 21:35
 */
@Service
public class RechargeRecoredServiceImpl implements RechargeRecoredService {
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;
    @Override
    public List<RechargeRecord> queryRecentlyRechargeByUid(Map<String, Object> paramMapRecentRecharge) {
        return rechargeRecordMapper.selectRecentlyRechargeByUid(paramMapRecentRecharge);
    }
}
