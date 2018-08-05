package org.mitre.openid.connect.mobile;

import org.mitre.openid.connect.model.UserInfo;
import org.mitre.openid.connect.service.impl.DefaultUserInfoService;
import org.mitre.openid.connect.util.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class MobileAuthenticationProvider implements AuthenticationProvider {
	private DefaultUserInfoService defaultUserInfoService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 能走到这里说明authentication是MobileAuthenticationToken, 可以转换
		MobileAuthenticationToken mobileAuthenticationToken = (MobileAuthenticationToken) authentication;

		// 调用自定义的defaultUserInfoService认证
		UserInfo userInfo = defaultUserInfoService.getByPhoneNumber((String) mobileAuthenticationToken.getPrincipal());

		if (userInfo == null) {
			throw new InternalAuthenticationServiceException("手机号不存在:" + mobileAuthenticationToken.getPrincipal());
		}
		UserDetailsImpl userDetails = buildUserDetails(userInfo);

		// 如果user不为空, 重新构建MobileAuthenticationToken(已认证)
		MobileAuthenticationToken authenticationToken = new MobileAuthenticationToken(userDetails, userDetails.getAuthorities());
		// 把认证之前的token里存的用户信息赋值给认证后的token对象
		authenticationToken.setDetails(mobileAuthenticationToken.getDetails());
		return authenticationToken;
	}

	private UserDetailsImpl buildUserDetails(UserInfo userInfo) {
		return new UserDetailsImpl(userInfo);
	}

	/**
	 * 只有Authentication为MobileAuthenticationToken时使用此Provider认证
	 *
	 * @param authentication
	 * @return
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return MobileAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public DefaultUserInfoService getDefaultUserInfoService() {
		return defaultUserInfoService;
	}

	public void setDefaultUserInfoService(DefaultUserInfoService defaultUserInfoService) {
		this.defaultUserInfoService = defaultUserInfoService;
	}
}
