package com.bjpowernode.service.income;

import com.bjpowernode.Constants;
import com.bjpowernode.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.vo.IncomeRecordVO;
import com.bjpowernode.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
