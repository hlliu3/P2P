package com.bjpowernode.service.loan;

import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.vo.MsgVO;
import com.bjpowernode.vo.PageVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  11:34
 */
public interface LoanInfoService {
    /**
     * 查询历史平均年化利率
     * @return
     */
    Double queryHistoryAverageRate();
    /**
     * 查询产品信息通过产品类型
     * @return
     */
    List<LoanInfo> queryLoanInfoByLoanType(Map<String, Object> map);
    /**
     * 查询产品信息通过产品类型和分页
     * @return
     */
    PageVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

    LoanInfo queryLoanInfoById(Integer productId);

    MsgVO invest(HashMap<String, Object> paramMap);
}
