package com.bjpowernode.config;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @className:IntelliJ IDEA
 * @description:
 * @author:
 * @date:2019-06-21 20:08
 */
public class AlipayConfig {


//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101000656797";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCY5CuewkQTsdOH0DvE5hu2irKSWZZuEitMiuWNRO+fW+lHfKos7RMEuG0D8OBcWFhKjMBtPulEevWCFHBc+pbz9/OfGdf3Ewsb+3Llq1INw6Vloh4d2rlZhk3Y8GmEjHmxuSfykcSd75eV8WBQTHB7XG0UqOhnUYfA9EGsGf5u0AzsenLE+m4Ri9Ugc0oBJh9R5dkRwINptG7rMvmBK7NAkSiBNRrse30DxK9UqcCGMheNL31HhwKKn+OYai8mG+5HBXKFRM/a5fCQLKhp41lzHY/yfdrp0eUtqN+2BNnZ+KMGchzlCB2Bs0aS7G0jinpKl2QyIJjAtyEIE1xq8LK3AgMBAAECggEAfd1ZC5lh8dSzD1ud2LqWlfAYkeKutG7AtoKh92qI2egQY4l9jv6caYZqOJRUH7Ga/cRhpY3k+zs+YZfx6JljK/Uz2gasg/ppat/V9rzTEAHAZ6rOoEUv0UN/UtiYBUaf6G9FM6xM8LgNUqMjZveGoge0xQ1IRcYYvABZEBVWIGJWl2xF5ssttURWwuf8eyg6WKO0s7N7Nl81rvpFn56r73nhPWXj2PHkL99wOveyRhY2qusNmtktXgyTqJT7AScq3kH0nLiQ3Tc8PSMHtlZrJVWP0kuY+qIeCptbF7ZuLaUldsR1fEis1HZ8K/52PMy9nU3lee5QrRVT/PzZTylkAQKBgQD9E4jmFfz1po75VYo6aR2u1DDwuFIF2JelUhRoP9PHAg3AknKhSMKpxZWbjGN8XR4jiwiwc4PYkImFI9RI3vLkEfV/t4J7mAWN9yyT+ilhAy0z2RvOdSCzjxNtS4/hNNCb9UBswH829s7F9W7MvQPDwO+HiLPCqkattqSLELFL3wKBgQCaqFdyLFPr0C2xjA4cyfct+0lXsBe4SBrBopwKTLYCCrVu9WggD2ydoq8TqDLrf0gzeLq5+OCBekSYglyjquhzjrz0evHKSdKZUJOk3dF/mAVdbazaBA0XSn8Zf+9TRgN+4yPQBnq8fy9uiN2618ijNOAHcXRgvPpQnKo7lSX0KQKBgG37TFY5ngofjSjcPMsvnV+6BV7Hq5Ww0A5fglDf1Cg7Q0C6JCjlVvuyy1ajByUnzo5BwM/2E4lNHwaGUJ4ujb4B8//B/2m44kkU7wGMCOmJGN7cblIl4sVF23p3CRsk+bedOJdKfL6lOa0cAT/msLkkorPy+7QQjdC2fAGXioVfAoGAXCgr03DuoPnwAHhAgI8op1Gxpnrb54evyeqnqMuFOTpAWtoJLfGC+3vRb5kunn29/E3YlrDitsSw01/669wPn6xSlCweoDfVMl59XCSBjdJ8f+u2sELxugs+KWe+qJRzyMx4rqDtyhzPmsIbvhaPDBGZ3VWRz4NMdXSlS9PYReECgYB0zXsIxqawMyGqXw5uXAaMoSKwhX41BXIkGvMznzjju2m9gp1BrRdGP6zC39w3g3pJSnZ3fzSGxOEe8fG71AZe46gaSAYOG59efIXBX9xPsmMfly323GoNHi0OaVR2gxVh5SrHOleQiUQG3JdV6EKogsEui8avZCo8ExB7EihEeg==";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAk73e5iyBbxSTQ4oEd0w+wYrAJtViQSyraExm+LoqSaeMh0tCs6+6Rha7k2NAbrr1V79gFjco0tyaVNNh52R78OYEY1E5r/d3cNGpbgGNY6XQT3MAN4JdZjucU3RtPDKjYfimMBc4upp03pnN193PdbW65O9yoMaAvanzvMsy8c+7Be4NCYB19Mds0THxQ6+UMLn4MlD/YMsktouRIy5Tnqamarhxz8/9UZ4VpADIyowYdW00EzK4XRIC1BkRre1L6Fu9pBgKTLqDBuTpsZ+aUcDEc4FMvEZLS1HNlAaXRewnjVZBNm6sW0zlRijO//TGXV9ukm1dgDCTTLUOgtOiiwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/p2p/loan/alipayNotify";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/p2p/loan/alipayBack";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     *
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis() + ".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
