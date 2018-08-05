package org.mitre.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 自定义配置项
 * ClassName: SecurityProperties
 *
 * @author lihaoyang
 * @Description: 自定义配置项
 * 这个类会读取application.properties里所有以imooc.security开头的配置项
 * <p/>
 * imooc.security.browser.loginPage = /demo-login.html
 * 其中的browser的配置会读取到BrowserProperties中去
 * 这是以点分割的，一级一级的和类的属性对应
 * @date 2018年2月28日
 */
@Component
@ConfigurationProperties (prefix = "imooc.security")
public class SecurityProperties {

	//浏览器相关配置
	private BrowserProperties browser = new BrowserProperties();

	//验证码相关配置
	private ValidateCodeProperties code = new ValidateCodeProperties();

	public BrowserProperties getBrowser() {
		return browser;
	}

	public void setBrowser(BrowserProperties browser) {
		this.browser = browser;
	}

	public ValidateCodeProperties getCode() {
		return code;
	}

	public void setCode(ValidateCodeProperties code) {
		this.code = code;
	}

}
