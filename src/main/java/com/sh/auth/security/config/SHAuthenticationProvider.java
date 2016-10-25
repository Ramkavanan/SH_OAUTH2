package com.sh.auth.security.config;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.sh.auth.persistence.config.DynamicConfigurationLoader;
import com.sh.auth.persistence.service.OAuthUserDetailService;
//import com.sh.auth.util.GoogleTokenVerifier;

/**
 * Authentication provider verify the valid user or not
 * then return the authenticate with grant authorities
 * 
 * @author RamaKavanan
 */

@Configuration
public class SHAuthenticationProvider implements AuthenticationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(SHAuthenticationProvider.class);
	
	private static final String CLIENT_ID = DynamicConfigurationLoader.getMobileClientId().trim();
	
	@Autowired
    private OAuthUserDetailService userDetailsService;
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String userName = authentication.getName().trim();
		String password = authentication.getCredentials().toString().trim();
		LOG.debug("Authentication provider {}", authentication);
		UserDetails user = userDetailsService.loadUserByUsername(userName);
		if(user == null){
			throw new UsernameNotFoundException("Username "+userName+" not found");
		} else {
			if(!StringUtils.isBlank(user.getPassword()) && !StringUtils.isBlank(password) && password.equalsIgnoreCase(user.getPassword())) {
				Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
				return new UsernamePasswordAuthenticationToken(user, user.getPassword(), authorities);
			} else {
				if(!StringUtils.isBlank(password)) {
					LOG.debug("App user login way with param  password ");
					/*if(GooogleTokenVerifier.validateAccessToken(password, user.getUsername(), CLIENT_ID)) {
						Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
				        return new UsernamePasswordAuthenticationToken(user, password, authorities);
					} else {*/
						throw new BadCredentialsException("Invalid google access token");
					//}
				} else {
					throw new BadCredentialsException("Password should not be null");
				}
			}
		}
	}
	
	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
