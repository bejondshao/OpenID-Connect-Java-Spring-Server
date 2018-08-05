/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the pig4cloud.com developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package org.mitre.openid.connect.mobile;

import org.mitre.openid.connect.filter.MobileAuthenticationFilter;
import org.mitre.openid.connect.service.impl.DefaultUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
public class MobileSecurityConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
	@Autowired
	private AuthenticationSuccessHandler mobileLoginSuccessHandler;

	@Autowired
	private AuthenticationFailureHandler mobileLoginFailureHandler;

	@Autowired
	private DefaultUserInfoService defaultUserInfoService;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		// 自定义MobileAuthenticationFiler过滤器
		MobileAuthenticationFilter mobileAuthenticationFilter = new MobileAuthenticationFilter();
		mobileAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
		mobileAuthenticationFilter.setAuthenticationSuccessHandler(mobileLoginSuccessHandler);
		mobileAuthenticationFilter.setAuthenticationFailureHandler(mobileLoginFailureHandler);

		// 自定义MboileAuthenticationProvider认证器defaultUserInfoService
		MobileAuthenticationProvider mobileAuthenticationProvider = new MobileAuthenticationProvider();
		mobileAuthenticationProvider.setDefaultUserInfoService(defaultUserInfoService);
		// 在UsernamePasswordAuthenticationFiler前执行
		http.authenticationProvider(mobileAuthenticationProvider)
			.addFilterAfter(mobileAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
	}
}
