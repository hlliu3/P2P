


//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});

//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}
//注册的手机号验证
function checkPhone(){
	var phone = $.trim($("#phone").val());
	var flag = true;

	if("" == phone){
		showError("phone","请输入手机号！");
		return false;
	}else if(!/^1[1-9]\d{9}$/.test(phone)){//!phone.match(/^1[1-9]\d{9}&/)
		showError("phone","手机号有误！");
		return false;
	}else{
		//后台ajax请求进行手机号校验是否已存在
		$.ajax({
			url:"loan/checkPhone",
			type:"get",
			data:{
				"phone":phone
			},
			dataType:"json",
			async:false,//让后面的flag赋值，同步请求
			success:function (data) {
				if(data.code == "ok"){//可以使用
					showSuccess("phone");
					flag=true;
				}else{
					showError("phone",data.msg);
					flag=false;
				}
			},
			error:function () {
				showError("phone","服务器繁忙，请稍后重试...");
				flag=false;
			}
		});
		if(flag){
			showSuccess("phone");
		}
		return flag;
	}
}
//注册登录密码验证
function checkLoginPassword() {
	var loginPassWord = $.trim($("#loginPassword").val());
	var affirmLoginPassWord = $.trim($("#replayLoginPassword").val());
	var id = "loginPassword";
	if("" == loginPassWord){
		showError(id,"请输入登录密码！");
		return false;
	}else if(!loginPassWord.match(/^[0-9a-zA-Z]+$/)){//密码只能包含数字和字母
		showError(id,"密码只能包含数字和字母！");
		return false;
	}else if(!loginPassWord.match(/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*$/)){
		showError(id,"密码应同时包含英文或数字！");
		return false;
	}else if("" != affirmLoginPassWord && affirmLoginPassWord != loginPassWord){
		showError(id,"两次输入的密码不一致！");
		return false;
	}else if(loginPassWord.length<6||loginPassWord>20){
		showError(id,"密码长度要求为6-20！");
		return false;
	}else{
		showSuccess(id);
		return true;
	}
}
//注册登录确认密码验证
function checkAffirmLoginPassword() {
	var affirmLoginPassWord = $.trim($("#replayLoginPassword").val());
	var id = "replayLoginPassword";
	var loginPassWord = $.trim($("#loginPassword").val());

	if("" == affirmLoginPassWord){
		showError(id,"请输入确认密码！");
		return false;
	}else if("" != loginPassWord && affirmLoginPassWord != loginPassWord){
		showError(id,"两次输入的密码不一致！");
		return false;
	}else{
		showSuccess(id);
		return true;
	}
}
//校验验证码
function checkCaptcha() {
	var captcha = $.trim($("#captcha").val());
	var id = "captcha";
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
//注册
function register() {
	var phone = $.trim($("#phone").val());
	var loginPassWord = $.trim($("#loginPassword").val());
	var flagPW = checkLoginPassword();
	var flagPhone = checkPhone();
	var flagAffrimPW = checkAffirmLoginPassword();
	if(flagPhone && flagPW && flagAffrimPW){
		//md5加密
		loginPassWord = $.md5(loginPassWord);
		$("#replayLoginPassword").val(loginPassWord);
		$("#loginPassword").val(loginPassWord);
		$.ajax({
			url:"loan/regist",
			type:"post",
			data:{
				"phone":phone,
				"loginPassWord":loginPassWord
			},
			dataType:"",
			success:function (data) {
				if("success" == data.code){
					window.location.href = "realName.jsp";
				}else{
					showError("captcha",data.message);
				}
			},
			error:function () {
				showError("captcha",data.message);
			}
		});

	}else {
		return false;
	}
}