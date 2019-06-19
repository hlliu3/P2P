package com.bjpowernode.web;

import com.bjpowernode.Constants;
import com.bjpowernode.model.FinanceAccount;
import com.bjpowernode.model.User;
import com.bjpowernode.service.account.FinanceAccountService;
import com.bjpowernode.vo.MsgVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-15 21:42
 */
@Controller
public class FinanceAccountController {
    @Autowired
    private FinanceAccountService financeAccountService;

    @GetMapping(value = "/financeAccount/showFinanceAccountInfo")
    @ResponseBody
    public HashMap<String,Object> showFinanceAccountService(HttpServletRequest request){

        HashMap<String,Object> resMap = new HashMap<>();
        Integer userId = ((User)request.getSession().getAttribute(Constants.USER_INFO)).getId();
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(userId);
        if(null == financeAccount){
            resMap.put(Constants.CODE, Constants.FAIL);
        }else{
            resMap.put(Constants.CODE, Constants.SUCCESS);
            resMap.put(Constants.FINANCE_ACCOUNT, financeAccount);

        }
        return resMap;
    }
}
