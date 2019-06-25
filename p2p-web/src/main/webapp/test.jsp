<%--
  Created by IntelliJ IDEA.
  User: LHL
  Date: 2019/6/13
  Time: 12:30
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <title>Title</title>
    <script language="javascript" type="text/javascript"
            src="${pageContext.request.contextPath}/js/jquery-1.7.2.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/leftTime.min.js"></script>
    <style type="text/css">
        .testBtn-a{display: inline-block;height:30px;line-height:30px;padding:0 8px; border:0; border-radius:5px;color:#fff;background:rgb(65,133,244);cursor: pointer;}
        .testBtn-a.on{background:#c9c9c9;color:#666;cursor: default;}
    </style>
    <script>
        function sendMessage() {
            $.ajax({
                url:"${pageContext.request.contextPath}/user/test1Message",
                type:"post",
                data:"cardPhone="+$.trim($("#phone").val()),
                success:function (data) {
                    if("success" == data.code){
                        //开始倒计时
                        var _this = $("#dateBtn1");
                        if(!_this.hasClass("on")){
                            alert(data.msg);
                            $.leftTime(60,function (d) {
                                if(d.status){
                                    _this.addClass("on");
                                    _this.html((d.s=="00"?"60":d.s)+"秒后重新获取");

                                }else{
                                    _this.removeClass("on");
                                    _this.html("获取验证码");
                                }
                            });
                        }
                    }else{
                        //失败

                    }
                },

            });
        }
        function checkMessageCode() {
            var flag = true;
            var messageCode = $.trim($("#messageCode").val());
            if("" == messageCode){
                $("#showMessageInfo").html("请输入短信验证码！");
                return false;
            }else{//验证短信的真确性
                $("#showMessageInfo").html("");

                $.ajax({
                    url:"user/checkMessageCode",
                    type:"get",
                    data:"messageCode="+messageCode,
                    success:function (data) {
                        if("success" == data.code){
                            flag = true;
                        }else{
                            flag = false;
                        }
                    },

                });
            }
            return flag;
        }
        function sendReq() {
            //alert(checkMessageCode());
            var accName = $("#accName").val();
            var cardPhone = $("#phone").val();
            var certificateNo = $("#certificateNo").val();
            var cardNo = $("#cardNo").val();
            if(checkMessageCode()){
                window.location.href = "${pageContext.request.contextPath}/user/checkBank?accName="+accName+"&cardPhone="+cardPhone+"&certificateNo="+certificateNo+"&cardNo="+cardNo;
            }else{
                alert("验证码不正确");

            }
        }
    </script>
</head>
<body>
<form>
    <table>
        <tr>
            <td>姓名</td>
            <td><input type="text" name="accName" id="accName"/></td>
        </tr>
        <tr>
            <td>手机号</td>
            <td><input type="text" name="cardPhone" id="phone" /></td>
        </tr>
        <tr>
            <td>身份证号</td>
            <td><input type="text" name="certificateNo" id="certificateNo"/></td>
        </tr>
        <tr>
            <td>银行卡号</td>
            <td><input type="text" name="cardNo" id="cardNo"/></td>
        </tr>
        <tr>
            <td>短信</td>
            <td><input type="text" name="code" id="messageCode"/></td>
            <td> <a style='cursor:pointer;'>
                <button type="button" class="testBtn-a" id="dateBtn1" onclick="sendMessage()">获取验证码</button>
            </a></td>

        </tr>
        <tr>
            <td>提交</td>
            <td><input type="button" value="提交" onclick="sendReq()"/></td>

        </tr>
    </table>
</form>


</body>
</html>
