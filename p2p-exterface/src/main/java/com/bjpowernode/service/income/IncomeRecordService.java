package com.bjpowernode.service.income;

import com.bjpowernode.vo.IncomeRecordVO;
import com.bjpowernode.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/18  21:33
 */
public interface IncomeRecordService {
    List<IncomeRecordVO> queryIncomeRecordByUid(Map<String, Object> paramMapIncomeRecord);

    PageVO<IncomeRecordVO> queryIncomeByUidAndPage(Map<String, Object> paramMap);
}
