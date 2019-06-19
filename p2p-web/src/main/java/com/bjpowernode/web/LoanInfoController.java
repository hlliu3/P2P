package com.bjpowernode.web;

import com.bjpowernode.Constants;
import com.bjpowernode.model.*;
import com.bjpowernode.service.account.FinanceAccountService;
import com.bjpowernode.service.income.IncomeRecordService;
import com.bjpowernode.service.loan.BidInfoService;
import com.bjpowernode.service.loan.LoanInfoService;
import com.bjpowernode.service.recharge.RechargeRecoredService;
import com.bjpowernode.service.user.UserInfoService;
import com.bjpowernode.vo.IncomeRecordVO;
import com.bjpowernode.vo.MsgVO;
import com.bjpowernode.vo.PageVO;
import com.bjpowernode.vo.UserRecentBidVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/14  16:38
 */
@Controller
@RequestMapping(value = "/loan")
public class LoanInfoController {
    @Autowired
    private LoanInfoService loanInfoService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private FinanceAccountService financeAccountService;

    @Autowired
    private RechargeRecoredService rechargeRecoredService;

    @Autowired
    private IncomeRecordService incomeRecordService;
    //我要投资
    @RequestMapping(value = "/loan")
    public String investment(Model model,
                           @RequestParam(value = "ptype",required = false) Integer ptype,
                           @RequestParam(value = "currentPage",required = false) Integer currentPage,
                           @RequestParam(value = "pageSize",required = true) Integer pageSize){
        //--显示产品列表
        Map<String,Object> paramMap = new HashMap<>();

        paramMap.put("loanType", ptype);
        if(currentPage == null){
            currentPage = 1;
        }
        paramMap.put("realCurrentPage", currentPage);
        paramMap.put("currentPage", (currentPage-1)*pageSize);
        paramMap.put("pageSize", pageSize);
        PageVO<LoanInfo> pageVO = loanInfoService.queryLoanInfoByPage(paramMap);
        model.addAttribute("pageVO", pageVO);
        model.addAttribute("productType", ptype);
        //--显示用户的投资排行榜
        List<Map<String,Object>> bidInfoByUidList = bidInfoService.queryBidInfoByUid();
        return "loan";
    }

    //投资详情
    @RequestMapping(value = "/loanInfoDetail")
    public String loanInfoDetail(HttpServletRequest request,Model model,
                                 @RequestParam(value = "productId",required = true) Integer productId,
                                 @RequestParam(value = "currentPage",required = false) Integer currentPage){
        //投资详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(productId);

        model.addAttribute("loanInfo", loanInfo);

        Map<String,Object> paramMap = new HashMap<>();
        if(null == currentPage){
           currentPage = 1;
        }

        paramMap.put("pageSize", 10);
        paramMap.put("currentPage", (currentPage-1)*10);
        paramMap.put("productId", productId);
        //投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryRecentlyBidInfoByLoanId(paramMap);
        model.addAttribute("bidInfoList", bidInfoList);

        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        Integer id = user.getId();
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(id);
        model.addAttribute(Constants.FINANCE_ACCOUNT, financeAccount);
        return "loanInfo";

    }

    @RequestMapping(value = "/loadLoginPageOtherInfo")
    @ResponseBody
    public Map<String,Object> loadLoginPageOtherInfo(){
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        Integer allUserCount = userInfoService.queryAllUserCount();
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        Map<String,Object> resMap = new HashMap<>();
        resMap.put(Constants.HISTORY_AVERAGE_RATE, historyAverageRate);
        resMap.put(Constants.ALL_USER_COUNT, allUserCount);
        resMap.put(Constants.ALL_BID_MONEY,allBidMoney);
        return resMap;
    }

    /**
     * 进入我的小金库
     */
    @RequestMapping(value = "/myCenter")

    public String toMyCenter(HttpServletRequest request,Model model){
        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        Integer id = user.getId();
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(id);


        //最近投资

        Map<String,Object> paramMapRecentBid = new HashMap<>();
        paramMapRecentBid.put(Constants.PAGE_CURRENT,0);
        paramMapRecentBid.put(Constants.PAGE_SIZE, 5);
        paramMapRecentBid.put("uid", id);
        List<UserRecentBidVO> userRecentBidVOList = bidInfoService.queryRecentlyBidInfoByUid(paramMapRecentBid);

        //最近充值
        Map<String,Object> paramMapRecentRecharge = new HashMap<>();
        paramMapRecentRecharge.put(Constants.PAGE_CURRENT,0);
        paramMapRecentRecharge.put(Constants.PAGE_SIZE, 5);
        paramMapRecentRecharge.put("uid", id);
        List<RechargeRecord> userRecentRechargeVOList = rechargeRecoredService.queryRecentlyRechargeByUid(paramMapRecentRecharge);


        //最近收益
        Map<String,Object> paramMapIncomeRecord = new HashMap<>();
        paramMapIncomeRecord.put(Constants.PAGE_CURRENT,0);
        paramMapIncomeRecord.put(Constants.PAGE_SIZE, 5);
        paramMapIncomeRecord.put("uid", id);
        List<IncomeRecordVO> userIncomeRecordList = incomeRecordService.queryIncomeRecordByUid(paramMapIncomeRecord);

        model.addAttribute(Constants.FINANCE_ACCOUNT, financeAccount);
        model.addAttribute("userRecentBidVOList", userRecentBidVOList);
        model.addAttribute("userRecentRechargeVOList", userRecentRechargeVOList);
        model.addAttribute("userIncomeRecordList", userIncomeRecordList);

        return "myCenter";
    }

    /**
     * 投资
     */
    @RequestMapping(value = "/invest")
    @ResponseBody
    public MsgVO invest(HttpServletRequest request,
                       @RequestParam(value = "loanid",required = true) Integer loanId,
                       @RequestParam(value = "bidMoney",required = true) Double bidMoney){

        HashMap<String,Object> paramMap = new HashMap<>();
        paramMap.put("loanId", loanId);
        paramMap.put("bidMoney", bidMoney);
        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        paramMap.put(Constants.USER_INFO, user);
        MsgVO msgVO = loanInfoService.invest(paramMap);
        return msgVO;
    }
}
