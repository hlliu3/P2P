package com.bjpowernode.web;

import com.bjpowernode.Constants;
import com.bjpowernode.model.LoanInfo;
import com.bjpowernode.service.loan.BidInfoService;
import com.bjpowernode.service.user.UserInfoService;
import com.bjpowernode.service.loan.LoanInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  11:28
 */
@Controller
public class IndexController {

    @Autowired
    private LoanInfoService loanInfoService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private BidInfoService bidInfoService;

    @RequestMapping("/index")
    public String toIndex(Model model){

        //查询历史年化收益率（redis缓存）
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE, historyAverageRate);
        //查询注册平台总人数（redis缓存）
        Integer allUserCount = userInfoService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT, allUserCount);
        //查询平台累计投资金额（redis缓存）
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY, allBidMoney);
        //查询新手宝的产品，显示一页，每页1条
        Map<String,Object> map = new HashMap<>();
        map.put("currentPage", 0);
        map.put("pageSize", 1);
        map.put("loanType", Constants.LOAN_TYPE_X);
        List<LoanInfo> loanInfoListX = loanInfoService.queryLoanInfoByLoanType(map);
        //查询优选的产品。显示一页，每页4条
        map.put("currentPage", 0);
        map.put("pageSize", 4);
        map.put("loanType", Constants.LOAN_TYPE_U);
        List<LoanInfo> loanInfoListU = loanInfoService.queryLoanInfoByLoanType(map);
        //查询散标的产品。显示一页，每页4条
        map.put("currentPage", 0);
        map.put("pageSize", 8);
        map.put("loanType", Constants.LOAN_TYPE_S);
        List<LoanInfo> loanInfoListS = loanInfoService.queryLoanInfoByLoanType(map);

        model.addAttribute("loanInfoListX", loanInfoListX);
        model.addAttribute("loanInfoListU", loanInfoListU);
        model.addAttribute("loanInfoListS", loanInfoListS);
        return "index";
    }
}
