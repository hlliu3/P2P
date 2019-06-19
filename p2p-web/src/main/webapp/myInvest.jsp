<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <title>动力金融网 - 专业的互联网金融信息服务平台</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/fund-guanli.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/cashbox-share.css"/>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/base.css"/>
    <script type="text/javascript" language="javascript"
            src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/trafficStatistics.js"></script>
    <script type="text/javascript">
        $(function () {
            $("#myCash").attr("class", "on");

            $(".selectBox").hover(function () {
                $("ul", this).show();
            }, function () {
                $("ul", this).hide();
            });
            $(".selectBox li a").click(function () {
                var selectName = $(this).text();
                $(".select-selected h3").text(selectName);
                $(".selectBox ul").hide();
            });
            $(".select-selected").hover(function () {
                $(this).addClass('select-hover');
            }, function () {
                $(this).removeClass('select-hover');
            });
        })
        $(document).ready(function () {
            $("#overView").removeClass("on");
            $("#myAccount").addClass("on");
        });
        $(document).ready(function () {
            $("#touzijilu").addClass("on");
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
                                <span class="deal-time">投资时间</span>
                                <span class="deal-name">投资产品</span>
                                <span class="deal-type" style="width:120px">投资状态</span>
                                <span class="deal-money">投资金额(元)</span>
                            </dt>
                            <c:forEach items="${bidInfoPageVO.loanInfoList}" var="bid">
                                <dd>
                                    <div class="deal-time">${bid.bidTime}</div>
                                    <div class="deal-name">${bid.productName}</div>
                                    <div class="deal-type" style="width:120px">
                                        <c:choose>
                                            <c:when test="${bid.status eq 1}">
                                                <!-- 判断投资状态是否为1，即成功 -->
                                                投资成功
                                            </c:when>
                                            <c:otherwise>
                                                投资失败
                                            </c:otherwise>
                                        </c:choose>

                                    </div>
                                    <div class="deal-money"><fmt:formatNumber value="${bid.bidMoney}" type="currency"></fmt:formatNumber></div>
                                </dd>
                            </c:forEach>

                            <div class="touzi_fenye" style="width:100%;text-align:center;">
                                共${bidInfoPageVO.totalCount}条${bidInfoPageVO.totalPage}页　当前为第 ${bidInfoPageVO.currentPage} 页
                                <c:choose>
                                    <c:when test="${bidInfoPageVO.currentPage eq 1}">
                                        &nbsp;&nbsp;首页&nbsp;&nbsp;|&nbsp;&nbsp;上一页&nbsp;&nbsp;
                                    </c:when>
                                    <c:otherwise>
                                        <a id="linkHomePage" href="${pageContext.request.contextPath}/loan/myInvest">首页</a>
                                        <a id="linkPreviousPage"
                                           href="${pageContext.request.contextPath}/loan/myInvest?pageCurrent=${bidInfoPageVO.currentPage-1}">上一页</a>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${bidInfoPageVO.currentPage eq bidInfoPageVO.totalPage}">
                                        下一页&nbsp;&nbsp;|&nbsp;&nbsp;尾页
                                    </c:when>
                                    <c:otherwise>
                                        <a id="linkNextPage"
                                           href="${pageContext.request.contextPath}/loan/myInvest?pageCurrent=${bidInfoPageVO.currentPage+1}">下一页 </a>
                                        <a id="linkLastPage"
                                           href="${pageContext.request.contextPath}/loan/myInvest?pageCurrent=${bidInfoPageVO.totalPage}">尾页</a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
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