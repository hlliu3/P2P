package com.bjpowernode.timer;

import com.bjpowernode.service.income.IncomeRecordService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;


/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-20 20:00
 */
@Component
public class IncomeController {

    private Logger logger = LogManager.getLogger(IncomeController.class);

    @Autowired
    private IncomeRecordService incomeRecordService;
    //生成收益计划
    @Scheduled(cron = "0/5 * * * * ?")//每5秒执行一次
    public void incomeSchedule(){
        logger.info("-------生成收益计划开始-------");
        incomeRecordService.addIncomeSchedules();

        logger.info("-------生成受益计划结束-------");
    }
    //生成收益
    @Scheduled(cron = "0/5 * * * * ?")//每5秒执行一次
    public void income(){
        logger.info("-------生成收益开始--------");
        incomeRecordService.addIncome();
        logger.info("-------生成收益结束--------");
    }

}
