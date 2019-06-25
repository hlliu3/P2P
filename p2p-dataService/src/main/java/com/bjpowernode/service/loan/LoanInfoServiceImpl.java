package com.bjpowernode.service.loan;

import com.bjpowernode.Constants;
import com.bjpowernode.mapper.loan.BidInfoMapper;
import com.bjpowernode.mapper.user.FinanceAccountMapper;
import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.model.User;
import com.bjpowernode.service.loan.LoanInfoService;
import com.bjpowernode.mapper.loan.LoanInfoMapper;
import com.bjpowernode.vo.MsgVO;
import com.bjpowernode.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  11:37
 */
@Service
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public Double queryHistoryAverageRate() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        //不重要的数据，存入redis中
        //historyAverageRate 从redis中获取
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
        if(null == historyAverageRate){//reids中没有，在数据库中查找，放入redis中，需要制定失效时间
            synchronized(this){
                historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
                if(null == historyAverageRate){
                    historyAverageRate = loanInfoMapper.selectHistoryAverageRate();
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE, historyAverageRate, 15, TimeUnit.MINUTES);
                }
            }
        }
        return historyAverageRate;
    }


    @Override
    public List<LoanInfo> queryLoanInfoByLoanType(Map<String, Object> map) {
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByLoanType(map);

        return loanInfoList;
    }

    @Override
    public PageVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {

        Integer totalCount = loanInfoMapper.selectLoanInfoCountByLoanType(paramMap);
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoByLoanType(paramMap);

        Integer tatalPage = totalCount%((Integer) paramMap.get("pageSize"))==0?totalCount/((Integer) paramMap.get("pageSize")):totalCount/((Integer) paramMap.get("pageSize"))+1;

        PageVO pageVO = new PageVO();
        pageVO.setTotalPage(tatalPage);
        pageVO.setLoanInfoList(loanInfoList);
        pageVO.setTotalCount(totalCount);
        pageVO.setCurrentPage((Integer) paramMap.get("realCurrentPage"));

        return pageVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer productId) {
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(productId);
        return loanInfo;
    }

    @Override
    public MsgVO invest(HashMap<String, Object> paramMap) {
        MsgVO msgVO = new MsgVO();
        User user = (User) paramMap.get(Constants.USER_INFO);
        //查询产品信息表的版本号和剩余可投金额，状态
        Integer id = (Integer) paramMap.get("loanId");
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(id);
        Integer version = loanInfo.getVersion();
        Double bidMoney = (Double) paramMap.get("bidMoney");
        //剩余可投金额大于等于实际投资金额
        if(loanInfo.getLeftProductMoney()>=bidMoney){
            //更新产品信息表b_loan_info
            LoanInfo updateLoan = new LoanInfo();
            HashMap<String,Object> loanMap = new HashMap<>();
            loanMap.put("id", id);
            loanMap.put("version", version);
            loanMap.put("money", bidMoney);
            if(loanInfo.getLeftProductMoney() > bidMoney){
                loanMap.put("status", Constants.NOT_FULL_STANDARD);
            }else if(loanInfo.getLeftProductMoney().compareTo(bidMoney) == 0){
                loanMap.put("status", Constants.FULL_STANDARD);
                loanMap.put("date",new Date());
            }else if(loanInfo.getLeftProductMoney().compareTo(bidMoney) < 0){
                msgVO.setCode(Constants.FAIL);
                msgVO.setMsg("超过投资限制！");
                return msgVO;
            }
            int updateLoanCount = loanInfoMapper.updateLoanInfoAfterInvest(loanMap);
            if(updateLoanCount == 1){
                FinanceAccount fa = financeAccountMapper.selecFinanceAccountByUid(user.getId());
                if(fa.getAvailableMoney() - bidMoney < 0){
                    msgVO.setCode(Constants.FAIL);
                    msgVO.setMsg("金额不足，请充值！");
                    return msgVO;
                }else{
                    //更新用户财务表u_finance_account
                    FinanceAccount financeAccount = new FinanceAccount();
                    financeAccount.setId(fa.getId());
                    financeAccount.setAvailableMoney(fa.getAvailableMoney() - bidMoney);
                    int updateFinanceAccountCount = financeAccountMapper.updateByPrimaryKeySelective(financeAccount);
                    if(updateFinanceAccountCount == 1){
                        //新增投资记录b_bid_info
                        BidInfo bidInfo = new BidInfo();
                        bidInfo.setUid(user.getId());
                        bidInfo.setLoanId(id);
                        bidInfo.setBidMoney(bidMoney);
                        bidInfo.setBidTime(new Date());
                        bidInfo.setBidStatus(Constants.BID_STATUS);
                        int insertBidCount = bidInfoMapper.insertSelective(bidInfo);
                        if(insertBidCount != 1){
                            msgVO.setCode(Constants.FAIL);
                            msgVO.setMsg("当前人数过载，请重试！");
                        }else{
                           /* //再次查询是否满标
                            LoanInfo li = loanInfoMapper.selectByPrimaryKey(id);
                            if(0 == li.getLeftProductMoney()){
                                //满标,更新状态productStatus和时间

                            }else if(0 > li.getLeftProductMoney()){
                                 msgVO.setCode(Constants.FAIL);
                            }*/
                           //投资成功
                            msgVO.setCode(Constants.SUCCESS);
                           //将投资记录放入redis缓存中，使用zset集合存储
                            BoundZSetOperations boundZSetOperations = redisTemplate.boundZSetOps(Constants.BID_TOP);
                            //通键值对的增加值，不同的新增记录
                            boundZSetOperations.incrementScore(user.getPhone(), bidMoney);
                        }
                    }else{
                        msgVO.setCode(Constants.FAIL);
                        msgVO.setMsg("当前人数过载，请重试！");
                    }
                }

            }else{
                msgVO.setCode(Constants.FAIL);
                msgVO.setMsg("当前人数过载，请重试！");
            }
        }else{
            msgVO.setCode(Constants.FAIL);
            msgVO.setMsg("超过投资限制！");
        }
        return msgVO;
    }



}
