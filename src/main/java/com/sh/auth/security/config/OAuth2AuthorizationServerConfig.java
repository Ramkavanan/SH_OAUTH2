package com.sh.auth.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Class that provide the oauth2 token generate and end point management here
 *
 * @author RamaKavanan
 */

@Configuration
@EnableAuthorizationServer
@EnableTransactionManagement(proxyTargetClass = true)
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	@Qualifier("oauthDataSource")
	private DataSource dataSource;

	@Autowired
	@Qualifier("authenticationManagerBean")
	private AuthenticationManager authenticationManager;

	@Autowired
	private ClientDetailsService clientDetailService;

	@Autowired
	private DefaultOAuth2RequestFactory requestFactory;

	@Autowired
	private DefaultTokenServices tokenServices;

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.allowFormAuthenticationForClients();
		oauthServer.realm("sh-auth");
	}

	// remove this : unsupported grant type would be thrown
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endPoints)
			throws Exception {
		endPoints.accessTokenConverter(new DefaultAccessTokenConverter());
		endPoints.setClientDetailsService(clientDetailService);
		endPoints.requestFactory(requestFactory);
		endPoints.tokenServices(tokenServices);
		endPoints.authenticationManager(authenticationManager);
	}
}