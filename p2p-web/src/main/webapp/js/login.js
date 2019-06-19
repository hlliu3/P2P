var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


//错误提示
function showError(id,msg) {
	$("#showId").hide();
	$("#showId").html("<i></i><p>"+msg+"</p>");
	$("#showId").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#showId").hide();
	$("#showId").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#showId").hide();
	$("#showId").html("");
	$("#showId").show();
	$("#"+id).removeClass("input-red");
}
//检查手机号
function checkPhone() {
	var phone = $.trim($("#phone").val());
	var id = "showId";
	if("" == phone){
		showError(id,"请输入手机号！");
		return false;
	}else if(!(/^1[1-9]\d{9}$/).test(phone)){
		showError(id,"手机号不正确");
	}else{
		showSuccess(id);
		return true;
	}
}
//检查登录密码
function checkPassWord() {
	var password = $.trim($("#loginPassword").val());
	var id = "showId";
	if("" == password){
		showError(id,"请输入密码！");
		return false;
	}else if(!(/^[0-9a-zA-Z]+$/).test(password)){
		showError(id,"密码字符只可使用数字和大小写英文字母！");
		return false;
	}else if(!(/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*$/).test(password)){
		showError("密码应同时包含英文或数字");
		return false;
	}else if(password.length>20 || password.length<6){
		showError("密码长度在6-20之间");
		return false;
	}else{
		showSuccess(id);
		return true;
	}
}

//检查验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var id = "showId";
	var flag = true;
	if("" == captcha){
		showError(id,"请输入验证码！");
		return false;
	}else{
		$.ajax({
			url:"loan/checkCaptcha",
			type:"get",
			data:"captcha="+captcha,
			dataType:"json",
			async:false,
			success:function (data) {
				if(data.code == "ok"){
					showSuccess(id);
					flag = true;
				}else{
					flag = false;
					showError(id,data.msg);
				}
			},
			error:function () {
				showError(id,"服务器繁忙，请稍后重试...");
				flag = false;
			}
		});
	}
	if(flag){
		showSuccess(id);
	}
	return flag;
}


function userLogin() {
	if(checkPhone() && checkPassWord() && checkCaptcha() && checkMessageCode()){

		var phone = $.trim($("#phone").val());
		var password = $.trim($("#loginPassword").val());
		var md5_password = $.md5(password);
		$("#loginPassword").val(md5_password);
		$.ajax({
			url:"user/toLogin",
			type:"post",
			data:{
				"phone":phone,
				"password":md5_password
			},
			dataType:"json",
			success:function (data) {
				if("success" == data.code){
					if("" == referrer){
						window.location.href = "index";
					}else{
						window.location.href = referrer;
					}
				}else{
					showError("captcha","登录失败，请重试！")
				}
			},
			error:function () {
				showError("captcha","服务器繁忙，请稍后再试...")
			}
		});
	}

}

//页面加载完成调用
$(function () {
	loadInfo();
	//发送短信
	$("#dateBtn1").click(sendMessage);
	/*$("#dateBtn1").click(function () {
		var _this = $("#dateBtn1");
		if(!_this.hasClass("on")){
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
	});*/

});

//加载登录页面的其他资源
function loadInfo() {
	$.ajax({
		url:"loan/loadLoginPageOtherInfo",
		type:"get",
		dataType:"json",
		success:function (data) {
			$("#historyAverageRate").html(data.historyAverageRate);
			$("#user").html(data.allUserCount);
			$("#gold").html(data.allBidMoney);
		}
	});
}
//短信发送
function sendMessage() {
	$.ajax({
		url:"user/sengMessage",
		type:"post",
		data:"phone="+$.trim($("#phone").val()),
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
				$("#showMessageInfo").html(data.msg);
			}
		},
		error:function () {
			$("#showMessageInfo").html("服务器繁忙，请稍后重试...");
		}
	});
}
//校验短信
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
					$("#showMessageInfo").html(data.msg);
				}
			},
			error:function () {
				$("#showMessageInfo").html("服务器繁忙，请稍后重试...");
				flag = false;
			}
		});
	}
	return flag;
}