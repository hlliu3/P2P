package com.bjpowernode;

/**
 * DESCRIPTION:
 * user:
 * date:2019/6/13  11:52
 */
public class Constants {
    /**
     * 平台年化平均收益率
     */
    public static final String HISTORY_AVERAGE_RATE = "historyAverageRate";
    /**
     * 平台总用户人数
     */
    public static final String ALL_USER_COUNT = "allUserCount";
    /**
     * 平台总投资金额
     */
    public static final String ALL_BID_MONEY = "allBidMoney";
    /**
     * 新手宝编号0
     */
    public static final Object LOAN_TYPE_X = 0;
    /**
     * 优选编号1
     */
    public static final Object LOAN_TYPE_U = 1;
    /**
     * 散标编号2
     */
    public static final Object LOAN_TYPE_S = 2;
    /**
     * ok 请求返回状态
     */
    public static final String OK = "ok";
    /**
     * 图形验证码
     */
    public static final String CAPTCHA = "captcha";
    /**
     * 成功标记success
     */
    public static final String SUCCESS = "success";
    /**
     * 失败标记fali
     */
    public static final String FAIL = "fali";
    /**
     * 注册奖励金额
     */
    public static final Double REGIST_BOUNTY = 888.0;
    /**
     * 用户信息session的key
     */
    public static final String USER_INFO = "user";
    /**
     * 返回状态标记的key
     */
    public static final String CODE = "code";
    /**
     * 返回信息
     */
    public static final String MESSAGE = "message";
    /**
     * 账户信息key
     */
    public static final String FINANCE_ACCOUNT = "financeAccount";
    /**
     * 短信
     */
    public static final String MESSAGE_CODE = "messageCode";

    public static final String PAGE_SIZE = "pageSize";

    public static final String PAGE_CURRENT = "pageCurrent";
    /**
     * 未满标
     */
    public static final Integer NOT_FULL_STANDARD = 0;

    /**
     * 满标
     */
    public static final Integer FULL_STANDARD = 1;
    /**
     * bidstatus
     */
    public static final Integer BID_STATUS = 1;
    /**
     * bidTop
     */
    public static final String BID_TOP = "bidTop";
    /**
     * 未支付
     */
    public static final String UNPAID = "0";
    /**
     * redis唯一标识
     */
    public static final String ONLY_NUM = "onlyNum";
    /**
     * 充值错误信息的key
     */
    public static final String TRADE_MSG = "trade_msg";
}
