<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="o" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<o:header title="Log In" />
<script type="text/javascript">


$(function(){

	/*防刷新：检测是否存在cookie*/
	if($.cookie("captcha")){
		var count = $.cookie("captcha");
		var btn = $('#getting');
		btn.val(count+'秒后可重新获取').attr('disabled',true).css('cursor','not-allowed');
		var resend = setInterval(function(){
			count--;
			if (count > 0){
				btn.val(count+'秒后可重新获取').attr('disabled',true).css('cursor','not-allowed');
				$.cookie("captcha", count, {path: '/', expires: (1/86400)*count});
			}else {
				clearInterval(resend);
				btn.val("获取验证码").removeClass('disabled').removeAttr('disabled style');
			}
		}, 1000);
	};

	/*点击改变按钮状态，已经简略掉ajax发送短信验证的代码*/
	$('#getting').click(function(){
		htmlobj = $.ajax({
			url: 'verifycode/sms',
			type: 'GET',
			dataType: 'json',
			data: {mobile: $("#j_mobile").val()},

			success: function(data) {

		},
			error: function(err) {}
	});
	});


	$('#getting').click(function(){
		var btn = $(this);
		var count = 60;
		var resend = setInterval(function(){
			count--;
			if (count > 0){
				btn.val(count+"秒后可重新获取");
				$.cookie("captcha", count, {path: '/', expires: (1/86400)*count});
			}else {
				clearInterval(resend);
				btn.val("获取验证码").removeAttr('disabled style');
			}
		}, 1000);
		btn.attr('disabled',true).css('cursor','not-allowed');
	});

});

</script>
<o:topbar />
<div class="container-fluid main">

	<h1><spring:message code="login.login_with_mobile_and_sms"/></h1>

	<c:if test="${ param.error != null }">
		<div class="alert alert-error"><spring:message code="login.error"/></div>
	</c:if>


	<div class="row-fluid">
		<div class="span6 offset1 well">
			<form action="${ config.issuer }${ config.issuer.endsWith('/') ? '' : '/' }login" method="POST">
				<div>
					<div class="input-prepend input-block-level">
						<span class="add-on"><i class="icon-user"></i></span>
						<input type="text" placeholder="<spring:message code="login.mobile"/>" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" value="<c:out value="${ login_hint }" />" id="j_mobile" name="mobile">
					</div>
				</div>
				<div>
					<div class="input-prepend input-block-level">
						<span class="add-on"><i class="icon-lock"></i></span>
						<input type="text" placeholder="<spring:message code="login.sms"/>" autocorrect="off" autocapitalize="off" autocomplete="off" spellcheck="false" id="j_sms" name="sms"><input type="button" id="getting" value="获取验证码">
					</div>
				</div>
				<div>
					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
					<input type="submit" class="btn" value="<spring:message code="login.login-button"/>" name="submit">
				</div>
			</form>
		</div>
	</div>
</div>

<o:footer/>
