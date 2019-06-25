package com.bjpowernode.service.income;

import com.bjpowernode.Constants;
import com.bjpowernode.DateUtil;
import com.bjpowernode.mapper.loan.BidInfoMapper;
import com.bjpowernode.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.mapper.loan.LoanInfoMapper;
import com.bjpowernode.mapper.user.FinanceAccountMapper;
import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.IncomeRecord;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.vo.IncomeRecordVO;
import com.bjpowernode.vo.PageVO;
import com.sun.tools.internal.jxc.ap.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-18 21:34
 */
@Service
public class IncomeRecordServiceImpl implements IncomeRecordService {
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public void addIncomeSchedules() {

        //获取已经满标的产品
        List<LoanInfo> loanInfoList = loanInfoMapper.selectAllFullScaleLoan();
        //循化所有已满标的产品
        for (LoanInfo loanInfo : loanInfoList) {
            //根据产品查询所有的投资记录
            Integer loanInfoId = loanInfo.getId();
            List<BidInfo> bidInfoList = bidInfoMapper.selectAllBidByLoanId(loanInfoId);
            //循化投资记录
            for (BidInfo bidInfo : bidInfoList) {
                //计算每一条投资记录的收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setBidId(bidInfo.getId());
                //设置没有获取收益状态
                incomeRecord.setIncomeStatus(0);
                incomeRecord.setLoanId(loanInfoId);
                incomeRecord.setUid(bidInfo.getUid());

                if(Constants.LOAN_TYPE_X.equals(loanInfo.getProductType())){
                    //新手宝
                    Date incomeDate = DateUtil.getDateAfterDays(loanInfo.getCycle());
                    double incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * Math.pow(10, 2);
                    incomeRecord.setIncomeMoney(Math.round(incomeMoney*Math.pow(10, 2))/Math.pow(10, 2));
                    incomeRecord.setIncomeDate(incomeDate);
                }else{
                    //其他（优选，散标）
                    Date incomeDate = DateUtil.getDateAfterMonths(loanInfo.getCycle());
                    double incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * Math.pow(10, 2) * 30 ;
                    incomeRecord.setIncomeMoney(Math.round(incomeMoney*Math.pow(10, 2))/Math.pow(10, 2));
                    incomeRecord.setIncomeDate(incomeDate);

                    //插入记录
                    int insertIncomeCount = incomeRecordMapper.insert(incomeRecord);
                    if(insertIncomeCount != 1){
                        throw new RuntimeException();
                    }
                }

            }
            //更新产品记录为满标且生成受益计划2
            LoanInfo updateLoan = new LoanInfo();
            updateLoan.setId(loanInfoId);
            updateLoan.setProductStatus(2);
            int updateLoanInfoCount = loanInfoMapper.updateByPrimaryKeySelective(updateLoan);
            if(updateLoanInfoCount != 1){
                throw  new RuntimeException("更新产品记录为满标且生成受益计划失败");
            }
        }

    }

    @Override
    public void addIncome() {
        //产生收益
        //查询所有的收益记录（没有收益的），当前时间是收益时间的
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectAllIncomeRecordByDueDate();
        //循化遍历收益计划
        for (IncomeRecord incomeRecord : incomeRecordList) {
            //将收益添加到对应账户的账单中
            HashMap<String,Object> paramMap = new HashMap<>();
            paramMap.put("userid",incomeRecord.getUid());
            paramMap.put("incomemoney",incomeRecord.getIncomeMoney());
            paramMap.put("bidmoney",incomeRecord.getBidMoney());
            Integer updateFinaceAccountCount = financeAccountMapper.updateFinanceAfterIncome(paramMap);
            if(updateFinaceAccountCount != 1){
                throw new RuntimeException("获取收益，更新用户账单异常");
            }
            //更新当前收益计划为已经返还
            IncomeRecord updateIncome = new IncomeRecord();
            updateIncome.setId(incomeRecord.getId());
            updateIncome.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(updateIncome);
        }
    }


    @Override
    public List<IncomeRecordVO> queryIncomeRecordByUid(Map<String, Object> paramMapIncomeRecord) {

        return incomeRecordMapper.selectIncomeRecordByUid(paramMapIncomeRecord);
    }

    @Override
    public PageVO<IncomeRecordVO> queryIncomeByUidAndPage(Map<String, Object> paramMap) {

        Integer incomeCount = incomeRecordMapper.selectAllIncomeRecordCountByUid(paramMap);
        List<IncomeRecordVO> incomeRecordVOList = incomeRecordMapper.selectIncomeRecordByUid(paramMap);

        Integer pageSize = (Integer) paramMap.get(Constants.PAGE_SIZE);
        Integer currentPage = (Integer) paramMap.get("RealPageCurrent");
        Integer totalPage = incomeCount%pageSize==0?incomeCount/pageSize:incomeCount/pageSize+1;
        PageVO<IncomeRecordVO> pageVO = new PageVO<>();
        pageVO.setTotalCount(incomeCount);
        pageVO.setLoanInfoList(incomeRecordVOList);
        pageVO.setTotalPage(totalPage);
        pageVO.setCurrentPage(currentPage);

        return pageVO;
    }

}
