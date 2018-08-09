package org.mitre.openid.connect.validate.code;

import org.mitre.openid.connect.model.ValidateCode;
import org.mitre.openid.connect.repository.ValidateCodeRepository;
import org.mitre.openid.connect.validate.code.sms.SmsCodeSender;
import org.mitre.util.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证码Control
 * ClassName: ValidateCodeController
 */
@RestController
public class ValidateCodeController {

//	@Autowired
//	private SecurityProperties securityProperties;

	@Autowired
	private ValidateCodeGenerator smsCodeGenerator;//短信验证码

	/**
	 * session存取验证码策略
	 * TODO:重构为调用存储策略存取验证码
	 * 此处注入接口，浏览器和app有不同的实现
	 */
	@Autowired
	private ValidateCodeRepository validateCodeRepository;

	@Autowired
	private SmsCodeSender smsCodeSender; //短信验证码发送接口

	/**
	 * 短信验证码
	 *
	 * @param @param request
	 * @param @param response
	 * @param @throws IOException
	 * @return void
	 * @throws org.springframework.web.bind.ServletRequestBindingException
	 */
	@GetMapping (SecurityConstants.DEFAULT_VALIDATE_CODE_URL_PREFIX + "/sms")
	@Transactional
	public void createSmsCode(HttpServletRequest request, HttpServletResponse response) throws Exception {

		//调验证码生成接口方式
		ValidateCode smsCode = smsCodeGenerator.generator();
		smsCode.setPhoneNumber(request.getParameter(SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE));
		smsCode.setApproved(false);

		/**
		 * 不能把验证码存在session了，调接口，app和browser不同实现
		 */
//		sessionStrategy.setAttribute(new ServletWebRequest(request), SESSION_KEY_SMS, smsCode);

		validateCodeRepository.save(smsCode);

		//获取手机号
		String mobile = ServletRequestUtils.getRequiredStringParameter(request, SecurityConstants.DEFAULT_PARAMETER_NAME_MOBILE);
		//发送短信验证码
		smsCodeSender.send(mobile, smsCode.getCode());
	}

}
