<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>动力金融网 - 专业的互联网金融信息服务平台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/cashbox-share.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fund-guanli.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/share.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/base.css"/>
    <script type="text/javascript" language="javascript"
            src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" language="javascript"
            src="${pageContext.request.contextPath}/js/trafficStatistics.js"></script>
    <script type="text/javascript">
        $(document).ready(function () {
            $("#overView").removeClass("on");
            $("#myAccount").addClass("on");
            $("#shouyijilu").addClass("on");
        });
    </script>
</head>

<body>
<!--页头start-->
<div id="header">
    <jsp:include page="commons/header.jsp"/>
</div>
<!--页头end-->

<!-- 二级导航栏start -->
<jsp:include page="commons/subNav.jsp"/>
<!-- 二级导航栏end -->

<!--页中start-->
<div class="mainBox">
    <div class="homeWap">
        <div class="fund-guanli clearfix">
            <div class="left-nav">
                <jsp:include page="commons/leftNav.jsp"/>
            </div>
            <div class="right-body">
                <div class="right-wap">
                    <div class="deal-data">
                        <dl>
                            <dt>
                                <span class="deal-time">收益日期</span>
                                <span class="deal-name">收益金额</span>
                                <span class="deal-type" style="width:120px">投资产品</span>
                                <span class="deal-money">投资金额(元)</span>
                                <span class="deal-balance">收益状态</span>
                            </dt>
                            <c:forEach items="${incomeRecordVOPageVO.loanInfoList}" var="income">
                                <dd>
                                    <div class="deal-time">${income.incomeTime}</div>
                                    <div class="deal-name"><fmt:formatNumber value="${income.incomeMoney}" type="currency"></fmt:formatNumber></div>
                                    <div class="deal-type" style="width:120px">${income.projectName}</div>
                                    <div class="deal-money">${income.bidMoney}</div>
                                    <c:choose>
                                        <c:when test="${income.status eq 0}">
                                            <div class="deal-balance">收益未返还</div>
                                        </c:when>
                                        <c:when test="${income.status eq 1}">
                                            <div class="deal-balance">收益已返还</div>
                                        </c:when>
                                    </c:choose>
                                </dd>
                            </c:forEach>
                           <%-- <dd>
                                <div class="deal-time">2016-10-12 15:19:53</div>
                                <div class="deal-name">120.34</div>
                                <div class="deal-type" style="width:120px">新手宝</div>
                                <div class="deal-money">10000.0</div>
                                <div class="deal-balance">收益未返</div>
                            </dd>--%>
                            &nbsp;&nbsp;

                            <div class="touzi_fenye" style="width:100%;text-align:center;">
                                共${incomeRecordVOPageVO.totalCount}条${incomeRecordVOPageVO.totalPage}页　当前为第 ${incomeRecordVOPageVO.currentPage} 页
                                <c:choose>
                                    <c:when test="${incomeRecordVOPageVO.currentPage eq 1}">
                                        &nbsp;&nbsp;首页&nbsp;&nbsp;|&nbsp;&nbsp;上一页&nbsp;&nbsp;
                                    </c:when>
                                    <c:otherwise>
                                        <a id="linkHomePage" href="${pageContext.request.contextPath}/loan/myIncome">首页</a>
                                        <a id="linkPreviousPage"
                                           href="${pageContext.request.contextPath}/loan/myIncome?pageCurrent=${incomeRecordVOPageVO.currentPage-1}">上一页</a>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${incomeRecordVOPageVO.currentPage eq incomeRecordVOPageVO.totalPage}">
                                        下一页&nbsp;&nbsp;|&nbsp;&nbsp;尾页
                                    </c:when>
                                    <c:otherwise>
                                        <a id="linkNextPage"
                                           href="${pageContext.request.contextPath}/loan/myIncome?pageCurrent=${incomeRecordVOPageVO.currentPage+1}">下一页 </a>
                                        <a id="linkLastPage"
                                           href="${pageContext.request.contextPath}/loan/myIncome?pageCurrent=${incomeRecordVOPageVO.totalPage}">尾页</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <%--<div class="touzi_fenye" style="width:100%;text-align:center;">
                                共21条3页　当前为第2页
                                <a id="linkHomePage" href="${pageContext.request.contextPath}/loan/myIncome">首页</a>
                                <a id="linkPreviousPage"
                                   href="${pageContext.request.contextPath}/loan/myIncome?currentPage=${currentPage-1}">上一页</a>
                                <a id="linkNextPage"
                                   href="${pageContext.request.contextPath}/loan/myIncome?currentPage=${currentPage+1}">下一页 </a>
                                <a id="linkLastPage"
                                   href="${pageContext.request.contextPath}/loan/myIncome?currentPage=${totalPage}">尾页</a>
                            </div>--%>
                        </dl>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<!--页中end-->
<!--页脚start-->
<jsp:include page="commons/footer.jsp"/>
<!--页脚end-->
</body>
</html>