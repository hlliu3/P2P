package com.bjpowernode.pay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.bjpowernode.config.AlipayConfig;
import com.bjpowernode.utils.HttpClientUtils;
import com.github.wxpay.sdk.WXPayUtil;
import org.apache.http.protocol.ResponseDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.attribute.standard.RequestingUserName;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-21 20:09
 */
@Controller
public class PayController {

    @RequestMapping(value = "/api/alipay")
    public String toAlipay(HttpServletRequest request, Model model) throws UnsupportedEncodingException, AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        //同步返回页面路径
        alipayRequest.setReturnUrl(AlipayConfig.return_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_url);

        //商户订单号，商户网站订单系统中唯一订单号，必填
        String out_trade_no = new String(request.getParameter("outTradeNo").getBytes("ISO-8859-1"),"UTF-8");
        //付款金额，必填
        String total_amount = new String(request.getParameter("totalAmount").getBytes("ISO-8859-1"),"UTF-8");
        //订单名称，必填
        String subject = new String(request.getParameter("subject").getBytes("ISO-8859-1"),"UTF-8");
        //商品描述，可空
        String body = new String(request.getParameter("body").getBytes("ISO-8859-1"),"UTF-8");

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //若想给BizContent增加其他可选请求参数，以增加自定义超时时间参数timeout_express来举例说明
        //alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
        //		+ "\"total_amount\":\""+ total_amount +"\","
        //		+ "\"subject\":\""+ subject +"\","
        //		+ "\"body\":\""+ body +"\","
        //		+ "\"timeout_express\":\"10m\","
        //		+ "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        //请求参数可查阅【电脑网站支付的API文档-alipay.trade.page.pay-请求参数】章节

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //输出
        //out.println(result);
        //todo
        model.addAttribute("result", result);//result中是一个表单页面，填写用户支付的一些登录信息？

        return "payToAlipay";
    }

    @RequestMapping(value = "/api/alipayResult")
    @ResponseBody
    public Object getAlipayResult(HttpServletRequest request,
                                  @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                                  @RequestParam(value = "trade_no",required = false) String trade_no) throws UnsupportedEncodingException, AlipayApiException {
//获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);

        //设置请求参数
        AlipayTradeQueryRequest alipayRequest = new AlipayTradeQueryRequest();

        //商户订单号，商户网站订单系统中唯一订单号
        //String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
        //支付宝交易号
        //String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
        //请二选一设置

        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"}");

        //请求
        String result = alipayClient.execute(alipayRequest).getBody();

        //输出
        //out.println(result);
        return result;
    }

    //微信pay
    @RequestMapping(value = "/api/weixinpay")
    @ResponseBody
    public String weiXinPay(HttpServletRequest request,
                                         @RequestParam(value = "body",required = true) String reqbody,
                                         @RequestParam(value = "out_trade_no",required = true) String out_trade_no,
                                         @RequestParam(value = "total_fee",required = true) String total_fee) throws Exception {
        //随机数生成，随机字符串，长度要求在32位以内。推荐随机数生成算法
        String nonce_str = WXPayUtil.generateNonceStr();
        //支持IPV4和IPV6两种格式的IP地址。用户的客户端IP
        String spbill_create_ip = request.getServerName()+"";
        spbill_create_ip = "127.0.0.1";
        //notify_url异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        String notify_url = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/"+"loan/Mycenter";
        BigDecimal bigDecimal = new BigDecimal(total_fee);
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(100));
        int i =multiply.intValue();
        String trade_type = "NATIVE ";
        String key = "367151c5fd0d50f1e34a68a802d6bbca";
        String body = reqbody;
        String mch_id = "1361137902";
        String appid = "wx8a3fcf509313fd74";
        //签名 通过签名算法计算得出的签名值，详见签名生成算法
        Map<String,String> requestParamMap = new HashMap<>();
        requestParamMap.put("appid", appid);
        requestParamMap.put("mch_id",mch_id);
        requestParamMap.put("body",body);
        requestParamMap.put("notify_url",notify_url);
        requestParamMap.put("spbill_create_ip",spbill_create_ip);
        requestParamMap.put("nonce_str",nonce_str);
        requestParamMap.put("out_trade_no",out_trade_no);
        requestParamMap.put("total_fee",i+"");
        requestParamMap.put("trade_type",trade_type);
        String sign = WXPayUtil.generateSignature(requestParamMap, key);

        String wxpayUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        requestParamMap.put("sign", sign);

        String requestParamXML = WXPayUtil.mapToXml(requestParamMap);
        String responseDataXml = HttpClientUtils.doPostByXml(wxpayUrl, requestParamXML);

        Map<String,String> responstDataMap = new HashMap<>();
        responstDataMap = WXPayUtil.xmlToMap(responseDataXml);
        return responseDataXml;
    }
}
