
//同意实名认证协议
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

//检查用户名
function checkRealName() {
	var realName = $.trim($("#realName").val());
	var id = "realName";
	if("" == realName){
		showError(id,"请输入姓名！");
		return false;
	}else if(!realName.match(/^[\u4e00-\u9fa5]{0,}$/)){//中文格式校验
		showError(id,"姓名只支持中文");
		return false;
	}else {
		showSuccess(id);
		return true;
	}
}

//检查身份证
function checkCardId() {
	var cardId = $.trim($("#idCard").val());
	var affirmCardId = $.trim($("#replayIdCard").val());
	var id = "idCard";
	if("" == cardId){
		showError(id,"请输入身份证号！");
		return false;
	}else if(!((/^\d{15}$/).test(cardId) || (/^\d{18}$/).test(cardId) || (/^\d{17}(\d|X|x)$/).test(cardId))){
		showError(id,"身份证号不正确！");
		return false;
	}else if(affirmCardId != "" && affirmCardId != cardId){
		showError(id,"身份证号和确认身份证号不一致！");
		return false;
	}else{
		showSuccess(id);
		return true;
	}
}

//检查确认身份证
function checkAffirmCardId(){
	var id = "replayIdCard";
	var affirmCardId = $.trim($("#replayIdCard").val());
	var cardId = $.trim($("#idCard").val());
	if("" == affirmCardId){
		showError(id,"请输入确认身份证号！");
		return false;
	}else if("" == cardId){
		showError(id,"请先输入身份证号！");
		return false;
	}else if(affirmCardId != "" && affirmCardId != cardId){
		showError(id,"身份证号和确认身份证号不一致！");
		return false;
	}else{
		showSuccess(id);
		return true;
	}
}

//检查验证码
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


//检查是否真实
function verifyRealName(){

	if(checkRealName() && checkCardId() && checkCaptcha()){//三个校验通过
		var realName = $.trim($("#realName").val());
		var idCard = $.trim($("#idCard").val());
		var id = "captcha";
		$.ajax({
			url:"user/checkRealUserInfo",
			type:"post",
			data:"realName="+realName+"&idCard="+idCard,
			dataType:"json",
			success:function (data) {
				if("success" == data.code){//通过
					alert("实名认证通过");

					window.location.href="index";
				}else{
					showError(id,data.msg);
				}
			},
			error:function () {
				showError(id,"服务器异常，请稍后重试...");
			}
		});
	}
}