package org.mitre.openid.connect.validate.code;

import org.apache.commons.lang.StringUtils;
import org.mitre.openid.connect.model.ValidateCode;
import org.mitre.openid.connect.repository.ValidateCodeRepository;
import org.mitre.util.SecurityConstants;
import org.mitre.util.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 短信验证码过滤器
 * ClassName: ValidateCodeFilter
 *
 * @author lihaoyang
 * @Description: 继承OncePerRequestFilter：spring提供的工具，保证过滤器每次只会被调用一次
 * 实现 InitializingBean接口的目的：
 * 在其他参数都组装完毕的时候，初始化需要拦截的urls的值
 * @date 2018年3月2日
 */
public class SmsCodeFilter extends OncePerRequestFilter implements InitializingBean {

	private Logger logger = LoggerFactory.getLogger(getClass());

	//认证失败处理器
	private AuthenticationFailureHandler authenticationFailureHandler;

	private ValidateCodeRepository validateCodeRepository;


	//需要拦截的url集合
	private Set<String> urls = new HashSet<>();
	//读取配置
	private SecurityProperties securityProperties;
	//spring工具类
	private AntPathMatcher antPathMatcher = new AntPathMatcher();

	/**
	 * 重写InitializingBean的方法，设置需要拦截的urls
	 */
	@Override
	public void afterPropertiesSet() throws ServletException {
		super.afterPropertiesSet();
		//读取配置的拦截的urls
		String[] configUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(securityProperties.getCode().getSms().getUrl(), ",");
		//如果配置了需要验证码拦截的url，不判断，如果没有配置会空指针
		if (configUrls != null && configUrls.length > 0) {
			for (String configUrl : configUrls) {
				logger.info("ValidateCodeFilter.afterPropertiesSet()--->配置了验证码拦截接口:" + configUrl);
				urls.add(configUrl);
			}
		} else {
			logger.info("----->没有配置拦验证码拦截接口<-------");
		}
		//短信验证码登录一定拦截
		urls.add("/login");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		//如果是 登录请求 则执行
//		if(StringUtils.equals("/authentication/form", request.getRequestURI())
//				&&StringUtils.equalsIgnoreCase(request.getMethod(), "post")){
//			try {
//				validate(new ServletWebRequest(request));
//			} catch (ValidateCodeException e) {
//				//调用错误处理器，最终调用自己的
//				authenticationFailureHandler.onAuthenticationFailure(request, response, e);
//				return ;//结束方法，不再调用过滤器链
//			}
//		}


		/**
		 * 可配置的验证码校验
		 * 判断请求的url和配置的是否有匹配的，匹配上了就过滤
		 */
		boolean action = false;
		for (String url : urls) {
			if (antPathMatcher.match(url, request.getRequestURI())) {
				action = true;
			}
		}
		if (action) {
			try {
				validate(new ServletWebRequest(request));
			} catch (ValidateCodeException e) {
				//调用错误处理器，最终调用自己的
				authenticationFailureHandler.onAuthenticationFailure(request, response, e);
				return;//结束方法，不再调用过滤器链
			}
		}

		//不是登录请求，调用其它过滤器链
		filterChain.doFilter(request, response);
	}

	/**
	 * 校验验证码
	 *
	 * @param @param request
	 * @param @throws ServletRequestBindingException
	 * @return void
	 * @throws ValidateCodeException
	 * @Description: 校验验证码
	 * @author lihaoyang
	 * @date 2018年3月2日
	 */
	private void validate(ServletWebRequest request) throws ServletRequestBindingException {
		//拿出session中的ImageCode对象
//		ValidateCode smsCodeInSession = (ValidateCode) sessionStrategy.getAttribute(request, ValidateCodeController.SESSION_KEY_SMS);
		//根据不同的存储策略调用不同的获取方式
		ValidateCode validateCode = validateCodeRepository.getByPhoneNumber(request.getParameter("phoneNumber"));

/*		//拿出请求中的验证码
		String imageCodeInRequest = ServletRequestUtils.getStringParameter(request.getRequest(), SecurityConstants.DEFAULT_PARAMETER_NAME_CODE_SMS);
		//校验
		if (StringUtils.isBlank(imageCodeInRequest)) {
			throw new ValidateCodeException("验证码不能为空");
		}*/
		if (validateCode == null) {
			throw new ValidateCodeException("验证码不存在，请刷新验证码");
		}
		Date now = new Date();
		boolean expired = (now.compareTo(validateCode.getExpiration()) > 0) ? true : false;
		if (expired) {
			//从session移除过期的验证码
//			sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY_SMS);
			//validateCodeRepository.remove(request, ValidateCodeType.SMS);
			throw new ValidateCodeException("验证码已过期，请重新获取验证码");
		}
		/*if (!StringUtils.equalsIgnoreCase(validateCode.getCode(), imageCodeInRequest)) {
			throw new ValidateCodeException("验证码错误");
		}*/
		//验证通过，移除session中验证码
//		sessionStrategy.removeAttribute(request, ValidateCodeController.SESSION_KEY_SMS);
		//validateCodeRepository.remove(request, ValidateCodeType.SMS);
		validateCode.setApproved(true);
		validateCodeRepository.save(validateCode);
	}

	public AuthenticationFailureHandler getAuthenticationFailureHandler() {
		return authenticationFailureHandler;
	}

	public void setAuthenticationFailureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
		this.authenticationFailureHandler = authenticationFailureHandler;
	}

	public SecurityProperties getSecurityProperties() {
		return securityProperties;
	}

	public void setSecurityProperties(SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	public ValidateCodeRepository getValidateCodeRepository() {
		return validateCodeRepository;
	}

	public void setValidateCodeRepository(ValidateCodeRepository validateCodeRepository) {
		this.validateCodeRepository = validateCodeRepository;
	}


}
