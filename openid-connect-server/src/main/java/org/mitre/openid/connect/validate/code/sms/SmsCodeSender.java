package org.mitre.openid.connect.validate.code.sms;

/**
 * 短信验证码发送接口
 * ClassName: SmsCodeSender
 */
public interface SmsCodeSender {

	/**
	 * 发送验证码短信
	 *
	 * @param @param mobile 接收验证码的手机号
	 * @param @param code 验证码
	 * @Description: 短信发送
	 */
	void send(String mobile, String code);
}
