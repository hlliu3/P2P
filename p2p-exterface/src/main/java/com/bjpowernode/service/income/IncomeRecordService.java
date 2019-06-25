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

    /**
     * 查询收益根据用户
     */
    List<IncomeRecordVO> queryIncomeRecordByUid(Map<String, Object> paramMapIncomeRecord);

    /**
     * 分页查询收益根据用户
     */
    PageVO<IncomeRecordVO> queryIncomeByUidAndPage(Map<String, Object> paramMap);

    /**
     * 生成收益计划
     */
    void addIncomeSchedules();
    /**
     * 生成收益
     */
    void addIncome();
}
