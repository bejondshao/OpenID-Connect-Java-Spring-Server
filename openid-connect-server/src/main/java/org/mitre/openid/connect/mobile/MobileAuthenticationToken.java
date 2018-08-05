package org.mitre.openid.connect.mobile;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

public class MobileAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	// 手机号
	private final Object principal;

	/**
	 * MobileAuthenticationFilter中构建的未认证的Authentication
	 *
	 * @param mobile
	 */
	public MobileAuthenticationToken(String mobile) {
		super(null);
		this.principal = mobile;
		setAuthenticated(false);
	}

	/**
	 * MobileAuthenticationFilter中构建的已认证的Authentication
	 *
	 * @param principal
	 * @param authorities
	 */
	public MobileAuthenticationToken(Object principal,
	                                 Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		// 由于覆盖了父类方法, 这里必须用父类方法
		super.setAuthenticated(true);
	}

	public Object getPrincipal() {
		return this.principal;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if (isAuthenticated) {
			throw new IllegalArgumentException(
				"Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
	}
}
