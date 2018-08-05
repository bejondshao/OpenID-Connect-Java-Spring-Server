package org.mitre.openid.connect.mobile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mitre.support.SimpleResponse;
import org.mitre.util.LoginType;
import org.mitre.util.SecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 登录失败后的处理
 * ClassName: MobileFailureHandler
 * @Description: 登录失败后的处理
 * @author lihaoyang
 * @date 2018年3月1日
 */
@Component
public class MobileFailureHandler
		extends SimpleUrlAuthenticationFailureHandler
		/*implements AuthenticationFailureHandler*/ {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ObjectMapper objectMapper;
	

	@Autowired
	private SecurityProperties securityProperties;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
	                                    AuthenticationException exception) throws IOException, ServletException {
		
		logger.info("登录失败");
		if(LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
			//把authentication返回给响应
			//状态码500，服务器内部错误
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(objectMapper.writeValueAsString(new SimpleResponse(exception.getMessage())));//值返回异常信息
		}else{
			super.onAuthenticationFailure(request, response, exception);
		}
	}
}
