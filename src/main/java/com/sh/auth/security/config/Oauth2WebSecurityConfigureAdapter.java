package com.sh.auth.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.sh.auth.persistence.config.DynamicConfigurationLoader;
import com.sh.auth.persistence.service.OAuthUserDetailService;

/**
 * Web security adapter invoked when the http request hit and 
 * it take decision whether the request further proceed or not
 * Here register the authentication services in AuthenticationManagerBuilder
 * the builder invoked when getting access token and referred in security context
 * 
 * @author RamaKavanan
 */
@Configuration
@EnableWebSecurity
@Order(Ordered.LOWEST_PRECEDENCE-3)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class Oauth2WebSecurityConfigureAdapter extends WebSecurityConfigurerAdapter{

	private boolean isGoogleOauth2Enabled = DynamicConfigurationLoader.isGoogleOauth2Enabled();
	
	private String signUpUrl = DynamicConfigurationLoader.getDefaultSignUpUrl();

    @Autowired
    private OAuthUserDetailService userDetailsService;

    @Autowired
    private SHAuthenticationProvider authProvider;
    
    /**
     * Register authentication provider service in spring context and
     * register authenticationManager bean in spring context
     * 
     * @param auth
     * @throws Exception
     */
    @Autowired
    protected void registerAuthentication(
            final AuthenticationManagerBuilder auth) throws Exception {
    	if(isGoogleOauth2Enabled) {
    		auth.authenticationProvider(authProvider);
    	} else {
    		auth.userDetailsService(userDetailsService);
    	}
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * The configure handle all incoming request and take decision whether 
     * the request proceed further or not
     * All ignoring url pattern defined here
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
      web
        .ignoring()
           .antMatchers("/favicon.ico", "/swagger.json", "/resources/**"); // #3
      web.ignoring().antMatchers(signUpUrl);
    }
}