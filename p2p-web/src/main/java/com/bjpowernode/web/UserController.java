package com.bjpowernode.web;

import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.Constants;
import com.bjpowernode.model.BidInfo;
import com.bjpowernode.model.RechargeRecord;
import com.bjpowernode.model.User;
import com.bjpowernode.service.income.IncomeRecordService;
import com.bjpowernode.service.loan.BidInfoService;
import com.bjpowernode.service.redis.RedisService;
import com.bjpowernode.service.user.UserInfoService;
import com.bjpowernode.utils.HttpClientUtils;
import com.bjpowernode.vo.IncomeRecordVO;
import com.bjpowernode.vo.MsgVO;
import com.bjpowernode.vo.PageVO;
import com.bjpowernode.vo.UserRecentBidVO;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * className:
 * DESCRIPTION:
 * user:
 * author:
 * date:2019/6/15  17:58
 */
@Controller
public class UserController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BidInfoService bidInfoService;

    @Autowired
    private IncomeRecordService incomeRecordService;
    /**
     * 查询用户信息根据phone
     * @param request
     * @param phone
     * @return
     */
    @RequestMapping(value = "/loan/checkPhone",method = RequestMethod.GET)
    @ResponseBody
    public MsgVO checkPhone(HttpServletRequest request,
                         @RequestParam(value = "phone",required = true) String phone){
        MsgVO msgVO = new MsgVO();
        if(!phone.matches("1[1-9]\\d{9}")){
            msgVO.setMsg("手机号输入有误！");

            return msgVO;
        }else{
            User user = userInfoService.queryUserInfoByPhone(phone);
            if(null == user){
                msgVO.setCode(Constants.OK);
            }else{
                msgVO.setMsg("手机号已经存在，请重新输入...");
            }
        }
        return msgVO;
    }

    /**
     * 校验验证码
     * @param request
     * @param captcha
     * @return
     */
    @RequestMapping(value = "/loan/checkCaptcha",method = RequestMethod.GET)
    @ResponseBody
    public MsgVO checkCaptcha(HttpServletRequest request,
                              @RequestParam(value = "captcha",required = true)String captcha){
        MsgVO msgVO = new MsgVO();
        String realCaptcha = (String) request.getSession().getAttribute("captcha");
        boolean flag = StringUtils.equalsIgnoreCase(realCaptcha, captcha);
        if(flag){
            msgVO.setCode("ok");
        }else{
            msgVO.setMsg("验证码输入有误！");
        }
        return msgVO;
    }


    @RequestMapping(value = "/loan/regist",method = RequestMethod.POST)
    @ResponseBody
    public HashMap<String,Object> regist(HttpServletRequest request,
                       @RequestParam(value = "phone",required = true) String phone,
                       @RequestParam(value = "loginPassWord",required = true) String password){


        MsgVO msgVO = userInfoService.registUser(phone, password);
        HashMap<String,Object> resMap = new HashMap<>();
        //成功
        if(StringUtils.equalsIgnoreCase(msgVO.getMsg(), Constants.SUCCESS)){
            //将用户信息放入session中
            User user = userInfoService.queryUserInfoByPhone(phone);
            request.getSession().setAttribute(Constants.USER_INFO,user);
            resMap.put(Constants.CODE, Constants.SUCCESS);
        }else {
            resMap.put(Constants.CODE, Constants.FAIL);
            resMap.put(Constants.MESSAGE,"注册失败");
        }
        return resMap;
    }

    /**
     * 完成实名认证，更新用户的实名数据，更新用户session
     * @param request
     * @param realName
     * @param idCard
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/checkRealUserInfo",method = RequestMethod.POST)
    @ResponseBody
    public MsgVO checkRealUserInfo(HttpServletRequest request,
                                   @RequestParam(value = "realName",required = true) String realName,
                                   @RequestParam(value = "idCard",required = true) String idCard) throws Exception {


        MsgVO msgVO = new MsgVO();

        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("appkey", "a6c58fd56cd64704629d07ce7b1a14a1");
        paramMap.put("cardNo", idCard);
        paramMap.put("realName", realName);

        //String res = HttpClientUtils.doGet("https://way.jd.com/youhuoBeijing/test", paramMap);
        //{"code":"10000","charge":false,"remain":0,"msg":"查询成功","result":{"error_code":0,"reason":"成功","result":{"realname":"刘宏利","idcard":"142622199601292518","isok":true}}}
        String res = "{\"code\":\"10000\",\"charge\":false,\"remain\":0,\"msg\":\"查询成功\",\"result\":{\"error_code\":0,\"reason\":\"成功\",\"result\":{\"realname\":\"刘宏利\",\"idcard\":\"142622199601292518\",\"isok\":true}}}";
        //System.err.println(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

        if(isok){
            //进行用户数据更新
            User userSession = ((User)request.getSession().getAttribute(Constants.USER_INFO));
            User updateUser = new User();
            updateUser.setId(userSession.getId());
            updateUser.setLastLoginTime(new Date());
            updateUser.setName(realName);
            updateUser.setIdCard(idCard);

            Integer updateUserCount = userInfoService.loginUser(updateUser);
            if(updateUserCount > 0){
                msgVO.setCode(Constants.SUCCESS);
                userSession.setName(realName);
                userSession.setIdCard(idCard);
                request.getSession().setAttribute(Constants.USER_INFO, userSession);
            }else{
                msgVO.setCode(Constants.FAIL);
                msgVO.setMsg("更新用户数据失败！");
            }

        }else{
            msgVO.setMsg("实名验证失败！姓名和身份证不匹配！");
        }

        return msgVO;
    }

    /**
     * 退出登录,回到首页
     */
    @RequestMapping(value = "/user/logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request){
        request.getSession().removeAttribute(Constants.USER_INFO);
        return "redirect:/index";
    }

    /**
     * 登录
     * @return
     */
    @RequestMapping(value = "/user/toLogin",method = RequestMethod.POST)
    @ResponseBody
    public MsgVO toLogin(HttpServletRequest request,
                         @RequestParam(value = "phone",required = true) String phone,
                         @RequestParam(value = "password",required = true) String password){

        MsgVO msgVO = new MsgVO();

        Map<String,Object> userUpdateMap = userInfoService.toLoginUser(phone,password);
        if(((Integer)(userUpdateMap.get("count"))) > 0){
            msgVO.setCode(Constants.SUCCESS);
            request.getSession().setAttribute(Constants.USER_INFO, userUpdateMap.get(Constants.USER_INFO));
        }else{
            msgVO.setCode(Constants.FAIL);
        }

        return msgVO;
    }

    @RequestMapping(value = "user/sengMessage",method = RequestMethod.POST)
    @ResponseBody
    public MsgVO sendMessage(HttpServletRequest request,
                             @RequestParam(value = "phone",required = true) String phone) throws Exception {
        String url = "https://way.jd.com/kaixintong/kaixintong";
        String randomMessageCode = getRandomMessageCode();
        String mobile = phone;
        String appkey = "8b45d0f51d3711d5714bd67a6a7dd8cd";
        String content = "【凯信通】您的验证码是："+randomMessageCode;
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("mobile", mobile);
        paramMap.put("appkey", appkey);
        paramMap.put("content", content);
        MsgVO msgVO = new MsgVO();

        //String str = HttpClientUtils.doPost(url, paramMap);

        //System.err.println("str = " + str);
        String str = "";
        str = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 0,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-883104</remainpoint>\\n <taskID>92959771</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(str);
        String code = jsonObject.getString("code");
        //通信成功
        if(StringUtils.equals("10000", code)){
            String xmlString = jsonObject.getString("result");
            Document document = DocumentHelper.parseText(xmlString);
            Node returnstatus = document.selectSingleNode("//returnstatus");
            //成功
            if(StringUtils.equals("Success", returnstatus.getText())){
                //将数据放入redis缓存中(1分钟)
                redisService.addMessageCode(randomMessageCode);
                msgVO.setCode(Constants.SUCCESS);
                msgVO.setMsg(randomMessageCode);
            }else{
                msgVO.setCode(Constants.FAIL);
                msgVO.setMsg("通信失败！");
            }
        }else{
            msgVO.setMsg("通信失败！");
        }
        return msgVO;
    }

    public String getRandomMessageCode(){
        int[] arr = {1,2,3,4,5,6,7,8,9,0};
        String code = "";
        for (int i = 0; i < 6; i++) {
            int random = (int) (Math.random()*10);
            code += arr[random];
        }
        return code;
    }

    @RequestMapping(value = "/user/checkMessageCode")
    @ResponseBody
    public MsgVO checkMessageCode(HttpServletRequest request,
                                  @RequestParam(value = "messageCode",required = true) String messageCode){

        MsgVO msgVO = new MsgVO();
        String flag = redisService.checkMessageCode(messageCode);
        if(StringUtils.equals("2", flag)){
            msgVO.setCode(Constants.SUCCESS);
        }else if(StringUtils.equals("1", flag)){
            msgVO.setMsg("验证码失效");
        }else {
            msgVO.setMsg("验证码错误");
        }
        return msgVO;
    }

    @RequestMapping(value = "/loan/myInvest")
    public String toMyInvest(HttpServletRequest request, Model model,
                             @RequestParam(value = "pageCurrent",required = false) Integer pageCurrent,
                             @RequestParam(value = "pageSize",required = false) Integer pageSize){

        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",user.getId());
        if(null == pageCurrent){
            pageCurrent = 1;
        }
        if(null == pageSize){
            pageSize = 5;
        }
        paramMap.put(Constants.PAGE_CURRENT,(pageCurrent-1)*pageSize);
        paramMap.put(Constants.PAGE_SIZE,pageSize);
        paramMap.put("RealPageCurrent",pageCurrent);
        PageVO<UserRecentBidVO> bidInfoPageVO = bidInfoService.queryBidInfoByUidAndPage(paramMap);

        model.addAttribute("bidInfoPageVO", bidInfoPageVO);
        return "myInvest";
    }

    @RequestMapping(value = "/loan/myRecharge")
    public String toMyRecharge(HttpServletRequest request, Model model,
                             @RequestParam(value = "pageCurrent",required = false) Integer pageCurrent,
                             @RequestParam(value = "pageSize",required = false) Integer pageSize){

        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",user.getId());
        if(null == pageCurrent){
            pageCurrent = 1;
        }
        if(null == pageSize){
            pageSize = 5;
        }
        paramMap.put(Constants.PAGE_CURRENT,(pageCurrent-1)*pageSize);
        paramMap.put(Constants.PAGE_SIZE,pageSize);
        paramMap.put("RealPageCurrent",pageCurrent);
        PageVO<RechargeRecord> rechargeRecordPageVO = bidInfoService.queryRechargeRecordByUidAndPage(paramMap);

        model.addAttribute("rechargeRecordPageVO", rechargeRecordPageVO);
        return "myRecharge";
    }

    @RequestMapping(value = "/loan/myIncome")
    public String toMyIncome(HttpServletRequest request, Model model,
                               @RequestParam(value = "pageCurrent",required = false) Integer pageCurrent,
                               @RequestParam(value = "pageSize",required = false) Integer pageSize){

        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("uid",user.getId());
        if(null == pageCurrent){
            pageCurrent = 1;
        }
        if(null == pageSize){
            pageSize = 5;
        }
        paramMap.put(Constants.PAGE_CURRENT,(pageCurrent-1)*pageSize);
        paramMap.put(Constants.PAGE_SIZE,pageSize);
        paramMap.put("RealPageCurrent",pageCurrent);
        PageVO<IncomeRecordVO> incomeRecordVOPageVO = incomeRecordService.queryIncomeByUidAndPage(paramMap);

        model.addAttribute("incomeRecordVOPageVO", incomeRecordVOPageVO);
        return "myIncome";
    }

    @RequestMapping(value = "/user/checkBank")
    public String checkBank(HttpServletRequest request,Model model,
                            @RequestParam(value = "accName",required = true) String accName,
                            @RequestParam(value = "cardPhone",required = true) String cardPhone,
                            @RequestParam(value = "certificateNo",required = true) String certificateNo,
                            @RequestParam(value = "cardNo",required = true) String cardNo) throws Exception {
        MsgVO msgVO = new MsgVO();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("accName",accName);
        paramMap.put("cardPhone",cardPhone);
        paramMap.put("certificateNo",certificateNo);
        paramMap.put("cardNo",cardNo);
        paramMap.put("appkey","8b45d0f51d3711d5714bd67a6a7dd8cd");
        String resStr = HttpClientUtils.doPost("https://way.jd.com/YOUYU365/keyelement", paramMap);
        resStr = "{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"serialNo\": \"5590601f953b512ff9695bc58ad49269\",\n" +
                "        \"respCode\": \"000000\",\n" +
                "        \"respMsg\": \"验证通过\",\n" +
                "        \"comfrom\": \"jd_query\",\n" +
                "        \"success\": \"true\"\n" +
                "    }\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(resStr);
        String commCode = (String) jsonObject.get("code");
        if(StringUtils.equals("10000", commCode)){
            JSONObject result = jsonObject.getJSONObject("result");
            String flag = (String) result.get("success");
            if(StringUtils.equals("true", flag)){
                msgVO.setCode(Constants.SUCCESS);
                msgVO.setMsg("验证成功");
            }else{
                msgVO.setCode(Constants.FAIL);
                msgVO.setMsg("验证失败");
            }
        }else{
            msgVO.setCode(Constants.FAIL);
            msgVO.setMsg("通信失败");
        }
        model.addAttribute("msg", msgVO);
        return "result";
    }

    @RequestMapping(value = "/user/test1Message")
    @ResponseBody
    public Object testMessage(HttpServletRequest request,
                              @RequestParam(value = "cardPhone",required = true) String cardPhone) throws Exception {
        MsgVO msgVO = new MsgVO();
        String messageCode = getRandomMessageCode();
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("content","【创信】你的验证码是："+messageCode+"，3分钟内有效！");
        paramMap.put("mobile",cardPhone);
        paramMap.put("appkey","8b45d0f51d3711d5714bd67a6a7dd8cd");
        String resStr = HttpClientUtils.doPost("https://way.jd.com/chuangxin/dxjk", paramMap);
        resStr="{\n" +
                "    \"code\": \"10000\",\n" +
                "    \"charge\": false,\n" +
                "    \"remain\": 1305,\n" +
                "    \"msg\": \"查询成功\",\n" +
                "    \"result\": {\n" +
                "        \"ReturnStatus\": \"Success\",\n" +
                "        \"Message\": \"ok\",\n" +
                "        \"RemainPoint\": 420842,\n" +
                "        \"TaskID\": 18424321,\n" +
                "        \"SuccessCounts\": 1\n" +
                "    }\n" +
                "}";
        JSONObject jsonObject = JSONObject.parseObject(resStr);
        String commCode = (String) jsonObject.get("code");
        if(StringUtils.equals("10000", commCode)){
            JSONObject result = jsonObject.getJSONObject("result");
            String flag = (String) result.get("ReturnStatus");
            if(StringUtils.equals("Success", flag)){
                msgVO.setCode(Constants.SUCCESS);
                msgVO.setMsg(messageCode);
                redisService.addMessageCode(messageCode);
            }else{
                msgVO.setCode(Constants.FAIL);
                msgVO.setMsg(messageCode);
            }
        }else{
            msgVO.setCode(Constants.FAIL);
            msgVO.setMsg("通信失败");
        }
        return msgVO;
    }
}
