package com.bjpowernode.web;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.config.AlipayConfig;
import com.bjpowernode.Constants;
import com.bjpowernode.DateUtil;
import com.bjpowernode.model.*;
import com.bjpowernode.service.account.FinanceAccountService;
import com.bjpowernode.service.income.IncomeRecordService;
import com.bjpowernode.service.loan.BidInfoService;
import com.bjpowernode.service.loan.LoanInfoService;
import com.bjpowernode.service.recharge.RechargeRecoredService;
import com.bjpowernode.service.redis.RedisService;
import com.bjpowernode.service.user.UserInfoService;
import com.bjpowernode.utils.HttpClientUtils;
import com.bjpowernode.vo.IncomeRecordVO;
import com.bjpowernode.vo.MsgVO;
import com.bjpowernode.vo.PageVO;
import com.bjpowernode.vo.UserRecentBidVO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/14  16:38
 */
@Controller
@RequestMapping(value = "/loan")
public class LoanInfoController {
    Logger logger = LogManager.getLogger(LoanInfoController.class);

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

    @Autowired
    private RedisService redisService;

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
        model.addAttribute(Constants.BID_TOP, bidInfoByUidList);
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

    /**
     * 充值通过alipay
     */
    @RequestMapping(value = "/toRechargeByAlipay")
    public String toRechargeByAlipay(HttpServletRequest request,Model model,
                                     @RequestParam(value = "userId",required = false) Integer userId,
                                     @RequestParam(value = "rechargeMoney",required = true) double rechargeMoney){
        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        if(null == userId){
            userId = user.getId();
        }
        //设置付款状态为正在支付
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeDesc("支付宝支付");
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeStatus(Constants.UNPAID);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setUid(userId);
        //设置唯一的支付流水号时间戳+redis唯一标识
        Long onlyNum = redisService.createOnlyNum();
        String onlyString = DateUtil.createTimeStamp()+onlyNum;
        rechargeRecord.setRechargeNo(onlyString);
        Integer addRechargeRecoredCount = rechargeRecoredService.addRechareRecored(rechargeRecord);
        if(addRechargeRecoredCount <= 0){
            model.addAttribute(Constants.TRADE_MSG, "系统繁忙，请稍后重试...");
            logger.error("插入充值记录失败，流水号"+rechargeRecord.getId());
            return "toRechargeBack";
        }else{
            //提交给支付宝界面进行充值
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeNo",onlyString);
            //todo 同步返回，异步返回
            model.addAttribute("alipay_return_url","");
            model.addAttribute("alipay_notify_url", "");
            model.addAttribute("alipay_pay_url", "http://localhost:9092/pay/api/alipay");
        }
        return "toAlipay";
    }

    //alipay支付成功后同步返回的页面
    @RequestMapping(value = "/alipayBack")
    public String toExecuteRechargeAfterAlipayBack(HttpServletRequest request,Model model) throws Exception {
        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);

        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            //商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

            //付款金额
            String total_amount = new String(request.getParameter("total_amount").getBytes("ISO-8859-1"),"UTF-8");
            Map<String,Object> map = new HashMap<>();
            map.put("out_trade_no",out_trade_no);
            map.put("trade_no",trade_no);
            map.put("total_amount",total_amount);
            //向支付宝发送请求，获取是否成功的响应（由于同步返回没有返回值，需要重新获取）
            String jsonString = HttpClientUtils.doPost("http://localhost:9092/pay/api/alipayResult",map);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            JSONObject resJsonObject = jsonObject.getJSONObject("alipay_trade_query_response");
            //通信成功
            if(StringUtils.equals("10000",resJsonObject.getString("code"))){
                String trade_status = (String) resJsonObject.get("trade_status");
                /*
                WAIT_BUYER_PAY	交易创建，等待买家付款
                TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                TRADE_SUCCESS	交易支付成功
                TRADE_FINISHED	交易结束，不可退款
                */

                if(StringUtils.equals("TRADE_CLOSED", trade_status)){
                    //充值失败,更新为充值失败状态
                    RechargeRecord updateRechargeStatus = new RechargeRecord();
                    updateRechargeStatus.setRechargeNo(out_trade_no);
                    updateRechargeStatus.setRechargeStatus("2");
                    rechargeRecoredService.modifyRechargeRecord(updateRechargeStatus);
                    model.addAttribute(Constants.TRADE_MSG, "充值失败");
                    return "toRechargeBack";
                }else if(StringUtils.equals("TRADE_SUCCESS", trade_status)){
                    //先查询一下充值状态，防止多线程情况下添加两次余额

                    RechargeRecord rechargeRecord = rechargeRecoredService.queryRechargeRecordByRechargeNo(out_trade_no);
                    if(rechargeRecord.getRechargeStatus().equals("0") ){
                        //执行充值成功的更新操作
                        HashMap<String,Object> paramMap = new HashMap<>();
                        paramMap.put("userid", user.getId());
                        paramMap.put("out_trade_no",out_trade_no);
                        paramMap.put("total_amount",Double.valueOf(total_amount));
                        Boolean flag = rechargeRecoredService.recharge(paramMap);
                        if(flag){
                            //成功，重定向到我的小金库
                            return "redirect:/loan/myCenter";
                        }else{
                            model.addAttribute(Constants.TRADE_MSG, "更新充值状态为已近充值失败或者更新账户余额失败");
                            return "toRechargeBack";
                        }
                    }else{
                        return "redirect:/loan/myCenter";
                    }

                }
            }else{
                model.addAttribute(Constants.TRADE_MSG, "通信失败，请重试...");
                return "toRechargeBack";
            }
            //out.println("trade_no:"+trade_no+"<br/>out_trade_no:"+out_trade_no+"<br/>total_amount:"+total_amount);
        }else {
            //out.println("验签失败");
            model.addAttribute(Constants.TRADE_MSG, "支付宝签名验证失败");
            logger.error("支付宝签名验证失败");
            return "toRechargeBack";
        }
        return "redirect:/loan/myCenter";
    }

    @RequestMapping(value = "/toRechargeByWeixinpay")
    public String toWeiXinPay(HttpServletRequest request, Model model,
                              @RequestParam(value = "rechargeMoney",required = true) Double rechargeMoney,
                              HttpServletResponse response) throws Exception {

        User user = (User) request.getSession().getAttribute(Constants.USER_INFO);
        //生成订单
        RechargeRecord rechargeRecord = new RechargeRecord();
        //充值中
        rechargeRecord.setRechargeStatus("0");
        //订单编号，全局唯一（时间戳+redis全局唯一的自增）
        rechargeRecord.setRechargeNo(DateUtil.createTimeStamp()+redisService.createOnlyNum());
        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeMoney(rechargeMoney);
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("微信充值");
        Integer addRechargeRecord = rechargeRecoredService.addRechareRecored(rechargeRecord);
        if(addRechargeRecord <= 0){
            //添加失败
            model.addAttribute("trade_msg", "添加失败");
            return "toRechargeBack";
        }else{
            model.addAttribute("rechargeNo", rechargeRecord.getRechargeNo());
            model.addAttribute("rechargeMoney", rechargeMoney);
            model.addAttribute("rechargeTime", rechargeRecord.getRechargeTime());
        }
        return "toWXpay";
    }

    @RequestMapping(value = "/createQRCode")
    public void createQRCode(HttpServletResponse response,HttpServletRequest request,Model model,
                             @RequestParam(value = "rechargeNo",required = true) String rechargeNo,
                             @RequestParam(value = "rechargeMoney",required = true) String rechargeMoney) throws Exception {

//调用第三方支付接口服务pay
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("body","微信充值");
        paramMap.put("out_trade_no",rechargeNo);
        paramMap.put("total_fee",String.valueOf(rechargeMoney));

        String resStrXML = HttpClientUtils.doPost("http://localhost:9092/pay/api/weixinpay", paramMap);
        //System.err.println(resStrXML);
            /*resStrXML = resStrXML.replaceAll("<!\\[CDATA\\[", "");
            resStrXML = resStrXML.replaceAll("]]>", "");*/
        System.err.println(resStrXML);
        Document webXmlDocument = DocumentHelper.parseText(resStrXML);

        Node node = webXmlDocument.selectSingleNode("//return_code");

        String returnCode = node.getText();
        if(StringUtils.equals("SUCCESS", returnCode)){
            //通信成功
            String resultCode = webXmlDocument.selectSingleNode("//result_code").getText();
            if(StringUtils.equals("SUCCESS", resultCode)){
                //业务结果成功

                String prepay_id = webXmlDocument.selectSingleNode("//prepay_id").getText();
                String code_url = webXmlDocument.selectSingleNode("//code_url").getText();
                String trade_type = webXmlDocument.selectSingleNode("//trade_type").getText();
                //生成code_url,生成二维码
                BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE,400,400);
                OutputStream outputStream = response.getOutputStream();
                //Path path = FileSystems.getDefault().getPath("img", "qrCode.jpg");
                MatrixToImageWriter.writeToStream(bitMatrix, "jpg", outputStream);
                //MatrixToImageWriter.writeToPath(bitMatrix, "jpg", path);
                outputStream.flush();
                outputStream.close();

            }else{
                //业务失败
                model.addAttribute("trade_msg", "业务失败");
                response.sendRedirect(request.getContextPath()+"/toRechargeBack.jsp");
            }
        }else{
            //通信失败
            model.addAttribute("trade_msg", "通信失败");
            response.sendRedirect(request.getContextPath()+"/toRechargeBack.jsp");
        }
    }

}
