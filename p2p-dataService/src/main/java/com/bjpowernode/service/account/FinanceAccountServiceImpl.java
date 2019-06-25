package com.bjpowernode.service.account;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.Constants;
import com.bjpowernode.http.HttpClientUtils;
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
 * @date:2019-06-15 21:52
 */
@Service
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer userId) {

        return financeAccountMapper.selecFinanceAccountByUid(userId);
    }

    @Override
    public Integer modifyFinaceAccountAfterRecharge(FinanceAccount financeAccount) {

        return financeAccountMapper.updateFinaceAccountByUid(financeAccount);
    }

    @Override
    public void rechargeAfterFail() {
        String status = "0";
        //查询所有充值状态为0的充值信息
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectAllRechargeRecordByRechargeStatus(status);
        //循环所有可能有问题的订单
        for (RechargeRecord rechargeRecord : rechargeRecordList) {
            String rechargeNo = rechargeRecord.getRechargeNo();
            Map<String,Object> map = new HashMap<>();
            map.put("out_trade_no",rechargeNo);

            //向支付宝发送请求，获取是否成功的响应（由于同步返回没有返回值，需要重新获取）//查询支付宝中的充值记录
            String jsonString = null;
            try {
                jsonString = HttpClientUtils.doPost("http://localhost:9092/pay/api/alipayResult",map);
                JSONObject jsonObject = JSONObject.parseObject(jsonString);
                JSONObject resJsonObject = jsonObject.getJSONObject("alipay_trade_query_response");
                //通信成功
                if(StringUtils.equals("10000",resJsonObject.getString("code"))){
                    String trade_status = (String) resJsonObject.get("trade_status");
                /*
                WAIT_BUYER_PAY	交易创建，等待买家付款
                TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                TRADE_SUCCESS	交易支付成功
                TRADE_FINISHED	交易结束，不可退款
                */

                    if(StringUtils.equals("TRADE_CLOSED", trade_status)){
                        //充值失败,更新为充值失败状态
                        RechargeRecord updateRechargeStatus = new RechargeRecord();
                        updateRechargeStatus.setRechargeNo(rechargeNo);
                        updateRechargeStatus.setRechargeStatus("2");
                        rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeStatus);

                    }else if(StringUtils.equals("TRADE_SUCCESS", trade_status)){
                        //先查询一下充值状态，防止多线程情况下添加两次余额

                        RechargeRecord queryRechargeRecord = rechargeRecordMapper.selectRechargeRecordByRechargeNo(rechargeNo);
                        if(queryRechargeRecord.getRechargeStatus().equals("0") ){
                            //执行充值成功的更新操作
                            FinanceAccount financeAccount = new FinanceAccount();
                            financeAccount.setUid(rechargeRecord.getUid());
                            financeAccount.setAvailableMoney(rechargeRecord.getRechargeMoney());
                            Integer integer = financeAccountMapper.updateFinaceAccountByUid(financeAccount);
                            if(integer <= 0){

                            }else{
                                RechargeRecord rechargeRecordUpdate = new RechargeRecord();
                                rechargeRecordUpdate.setRechargeNo(rechargeRecord.getRechargeNo());
                                rechargeRecordUpdate.setRechargeStatus("1");
                                Integer updateRechargeRecordCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecordUpdate);

                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

