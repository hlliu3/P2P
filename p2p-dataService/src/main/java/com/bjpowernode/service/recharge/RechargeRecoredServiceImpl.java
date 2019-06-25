package com.bjpowernode.service.recharge;

import com.bjpowernode.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.mapper.user.FinanceAccountMapper;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.RechargeRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<RechargeRecord> queryRecentlyRechargeByUid(Map<String, Object> paramMapRecentRecharge) {
        return rechargeRecordMapper.selectRecentlyRechargeByUid(paramMapRecentRecharge);
    }

    @Override
    public Integer addRechareRecored(RechargeRecord rechargeRecord) {
        int insert = rechargeRecordMapper.insert(rechargeRecord);
        return insert;
    }

    @Override
    public Integer modifyRechargeRecord(RechargeRecord updateRechargeStatus) {
        return  rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeStatus);
    }


    //todo 关于service层return  false对事物有控制？
    @Override
    public Boolean recharge(HashMap<String, Object> paramMap) {
        //更新用户余额
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setUid((Integer) paramMap.get("userid"));
        financeAccount.setAvailableMoney((Double)paramMap.get("total_amount"));
        Integer integer = financeAccountMapper.updateFinaceAccountByUid(financeAccount);
        if(integer <= 0){
            return false;
        }else{
            //更新充值状态
            RechargeRecord updateRechargeStatus = new RechargeRecord();
            updateRechargeStatus.setRechargeNo((String) paramMap.get("out_trade_no"));
            updateRechargeStatus.setRechargeStatus("1");
            Integer update = rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeStatus);
            if(update <= 0){
                return false;
            }
        }
        return true;
    }

    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String out_trade_no) {

        return rechargeRecordMapper.selectRechargeRecordByRechargeNo(out_trade_no);
    }
}
