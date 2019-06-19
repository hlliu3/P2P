<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>动力金融网-CFCA认证的互联网金融公司</title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/trafficStatistics.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/share.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/main.css"/>
    <script type="text/javascript">

        function checkMoney(){
            var money = $.trim($("#bidMoney").val());
            var productType = "${loanInfo.productType}";
            var cycle = "${loanInfo.cycle}";
            var rate = "${loanInfo.rate}";


            if("" == money){
                $("#showError").html("请输入金额！");
                return false;
            }else if(isNaN(money)){//不是数字true
                $("#showError").html("投资金额必须为数字！");
                return false;
            }else if(0 >= money){
                $("#showError").html("投资金额必须大于0！");
                return false;
            }else if(money % 100 != 0){
                $("#showError").html("投资金额必须为100的整数！");
                return false;
            }else{
                //计算收益
                $("#showError").html("");
                var incomeMoney = "";
                if(productType == 0){//新手宝
                    incomeMoney = money * rate * cycle / 100 /365;

                }else {
                    //优选，散标
                    incomeMoney = money * rate * cycle / 100 /365 * 30;

                }
                incomeMoney = Math.round(Math.pow(10,2) * incomeMoney) / Math.pow(10,2);
                $("#shouyi").html(incomeMoney);
                return true;
            }
        }

        function invest() {
            var money = $.trim($("#bidMoney").val());
            var id = $("#loanId").val();
            var favailableMoney = "${financeAccount.availableMoney}";
            var bidMaxLimit = "${loanInfo.bidMaxLimit}";
            var bidMinLimit = "${loanInfo.bidMinLimit}";
            var leftProductMoney = "${loanInfo.leftProductMoney}";

            if(checkMoney){
                if(!(parseFloat(money) >= parseFloat(bidMinLimit))){
                    $("#showError").html("投资最小限额为"+bidMinLimit);
                    return false;
                }else if(!(parseFloat(bidMaxLimit) >= parseFloat(money))){
                    $("#showError").html("投资最大限额为"+bidMaxLimit);
                    return false;
                }else if(parseFloat(money) > parseFloat(favailableMoney)){
                    $("#showError").html("余额不足，请先充值！");
                    return false;
                }else if(parseFloat(money) > parseFloat(leftProductMoney)){
                    $("#showError").html("投资额度有限，超出了"+parseFloat(money)-parseFloat(leftProductMoney)+"元");
                    return false;
                }else{
                    $("#showError").html("");
                    $.ajax({
                        url : "${pageContext.request.contextPath}/loan/invest",
                        data : {
                            "bidMoney":money,
                            "loanid":id
                        },
                        dataType : "json",
                        type : "post",
                        success : function (data) {
                            if("success" == data.code){
                                $("#failurePayment").show();
                                $("#dialog-overlay1").show();
                            }else{
                                $("#showError").html(data.msg);
                            }

                        },
                        error : function () {
                            $("#showError").html("服务器繁忙，请稍后重试...");
                        }
                    });
                }
            }
        }
    </script>
</head>

<body>
<div id="header">
    <jsp:include page="commons/header.jsp"/>
</div>

<!--散标投资 begin-->
<div id="sbtz" class="invest-details">

    <!--页中begin-->
    <div class="mainBox pro-details-body">
        <div class="homeWap clearfix" id="huacengPar">
            <div class="pro-details-left">

                <!-- 产品详情start -->
                <div class="pro-info-details">
                    <div class="pro-name">
                        <h2><span>${loanInfo.productName}</span>
                            <fmt:formatDate value="${loanInfo.releaseTime}"
                                            pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </h2>
                    </div>
                    <div class="pro-info">
                        <ul class="clearfix">
                            <li class="info-1">
                                <p>历史年化利率</p>
                                <h3>${loanInfo.rate}<span>%</span></h3>
                                <div class="info-bt">
                                    <span>本产品采用普通利率</span>
                                </div>
                            </li>
                            <li class="info-2">
                                <p>募集金额(元)</p>
                                <h3>${loanInfo.productMoney}</h3>
                                <div class="info-bt">
                                    <c:choose>
                                        <c:when test="${loanInfo.leftProductMoney eq 0}">
                          <span>
                                已满标
                          </span>
                                        </c:when>
                                        <c:otherwise>
                          <span>
                            <!-- 只有状态为0时为募集，其它都为已满标 -->
                            募集中 | 剩余募集金额 ${loanInfo.leftProductMoney}元
                          </span>
                                        </c:otherwise>
                                    </c:choose>

                                </div>
                            </li>
                            <li class="info-3">
                                <p>投资周期</p>
                                <!-- 只有新手宝产品周期为天 -->
                                <h3>${loanInfo.cycle}
                                    <c:choose>
                                        <c:when test="${loanInfo.productType ne 0}">
                                            <span>个月</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span>天</span>
                                        </c:otherwise>
                                    </c:choose>

                                </h3>
                                <div class="info-bt"><span></span></div>
                            </li>
                        </ul>
                    </div>
                    <dl class="pro-syfs">
                        <dt><span>收益获取方式</span></dt>
                        <dd><span>收益返还：</span>到期还本付息</dd>
                    </dl>
                </div>
                <!-- 产品详情end -->

                <!-- 投资记录start -->
                <div class="pro-details-cnt">
                    <ul class="tabNav clearfix">
                        <li><a id="one3" href="javascript:void(0);" class="s">投资记录</a></li>
                    </ul>

                    <div class="invest-record" id="con_one_3" style="display:block">
                        <div class="vertical-side">投资列表</div>
                        <dl class="record-list">
                            <dt>
                                <span class="record-num">序号</span>
                                <span class="invest-user">投资人</span>
                                <span class="invest-money">投资金额(元)</span>
                                <span class="invest-time">投资时间</span>
                            </dt>
                            <c:choose>
                                <c:when test="${empty bidInfoList}">
                                    <!-- 如果投资记录为空，显示以下文字 -->
                                    <dd style="text-align:center;">该产品暂时还没有人投资，赶快去投资吧~</dd>
                                </c:when>
                                <c:otherwise>
                                    <!-- 如果有投资记录，循环遍历显示 -->
                                    <c:forEach items="${bidInfoList}" varStatus="index" var="bidInfo">
                                        <dd>
                                            <span class="record-num">${index.index+1}</span>
                                            <span class="invest-user">${fn:substring(bidInfo.user.phone,0,3)}******${fn:substring(bidInfo.user.phone,9,11)}</span>
                                            <span class="invest-money">${bidInfo.bidMoney}</span>
                                            <span class="invest-time">
                            <fmt:formatDate value="${bidInfo.bidTime}" pattern="yyyy-MM-dd HH:mm:ss"></fmt:formatDate>
                        </span>
                                        </dd>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>

                        </dl>
                    </div>
                </div>
                <!-- 投资记录end -->
            </div>

            <!--页面右侧begin-->
            <div class="pro-details-right">
                <div class="right-calculator" id="huaceng">
                    <div class="calculator-cnt">
                        <h2>立即投资</h2>
                        <dl class="profits-mode">
                            <dt>收益获取方式</dt>
                            <dd class="clearfix"><span id="fanhuan"><em>到期还本付息</em></span></dd>
                        </dl>
                        <dl class="usable">
                            <dt>我的账户可用</dt>
                            <dd>资金(元)：
                                <c:choose>
                                    <c:when test="${empty user}">
                                        <!-- 判断用户是否登录：未登录，显示登录连接 -->
                                        <span style="font-size:18px;color:#ff6161;vertical-align:bottom;"><a
                                                href="${pageContext.request.contextPath}/login.jsp">请登录</a></span>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- 判断用户是否登录：已登录，显示可用余额 -->
                                        <span style="font-size:18px;color:#ff6161;vertical-align:bottom;">${financeAccount.availableMoney}</span>
                                    </c:otherwise>
                                </c:choose>
                            </dd>
                        </dl>
                        <div class="expect-box">
                            <div class="expect-money">预计本息收入(元)：<span id="shouyi" class="money"></span><span
                                    class="prompt" style="display:block;">请在下方输入投资金额</span></div>
                            <input type="text" id="bidMoney" name="bidMoney" onblur="checkMoney();"
                                   placeholder="请输入投资金额，应为100元的整倍数" maxlength="9"/>
                            <div class="max-invest-money"><span id="showError" ></span></div>
                        </div>
                        <div class="invest-btn">
                            <a id="investNow" href="javascript:void(0)" class="btn-1" onclick="invest();">立即投资</a>
                        </div>
                        <input type="hidden" id="loanId" name="loanId" value="${loanInfo.id}"/>
                    </div>
                </div>
            </div>
            <!--页面右侧end-->
        </div>
    </div>
    <!--页中end-->

</div>
<!--散标投资 end-->

<!--遮罩层-->
<div class="dialog-overlay" id="dialog-overlay1" style="display:none;"></div>

<!--投资成功浮层start-->
<div class="layer-body failureSuccess failurePayment" id="failurePayment"
     style="display:none;width:500px;height:100px;top:75%;">
    <a class="layer-close" href="javascript:closeit();"></a>
    <div style="background:#f2f2f2; line-height:105px;text-align:center;"><font style="font-size:25px;">投资成功</font>
    </div>
</div>
<!--投资成功浮层end-->

<!--页脚start-->
<jsp:include page="commons/footer.jsp"/>
<!--页脚end-->

<script type="text/javascript">
    function closeit() {
        $("#failurePayment").hide();
        $("#dialog-overlay1").hide();
        window.location.href = "${pageContext.request.contextPath}/loan/myCenter";
    }
</script>
</body>
</html>