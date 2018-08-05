package org.mitre.openid.connect.validate.code;

import org.apache.commons.lang.RandomStringUtils;
import org.mitre.openid.connect.model.ValidateCode;
import org.mitre.util.SecurityProperties;
import org.mitre.util.SmsCodeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Calendar;
import java.util.Date;

/**
 * 短信验证码生成类
 * ClassName: ImageCodeGenerator
 *
 * @author lihaoyang
 * @Description: TODO
 * @date 2018年3月2日
 */
@Component ("smsCodeGenerator")
public class SmsCodeGenerator implements ValidateCodeGenerator {

	@Autowired
	private SecurityProperties securityProperties;

	@Override
	public ValidateCode generator() {
		//生成验证码，长度从配置读取
		SmsCodeProperties properties = securityProperties.getCode().getSms();
		String code = RandomStringUtils.randomNumeric(properties.getLength());
		int expires = properties.getExpireIn();
		Calendar cal = Calendar.getInstance();
		Date createDate = cal.getTime();
		cal.add(Calendar.SECOND, expires);
		Date expiration = cal.getTime();
		return new ValidateCode(code, createDate, expiration);
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}


}
