package com.sh.auth.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;

import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * Here defined all bean related to oauth2 resource and authorization server
 * 
 * @author RamaKavanan
 */
@Configuration
@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class OAuthBeanConfig {

    @Autowired
    @Qualifier("oauthDataSource")
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Bean(name = "clientDetailService")
    @Autowired
    public ClientDetailsService clientDetailService() {
        return new JdbcClientDetailsService(dataSource);
    }

    @Bean
    @Autowired
    public UserDetailsService userDetailService(ClientDetailsService clientDetailsService) {
        return new ClientDetailsUserDetailsService(clientDetailsService);
    }

    @Bean
    @Autowired
    @Primary
    public DefaultTokenServices tokenService(TokenStore tokenStore, ClientDetailsService clientDetailsService) {
    	DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setClientDetailsService(clientDetailsService);
        return tokenServices;
    }

    @Bean
    @Autowired
    public DefaultOAuth2RequestFactory oAuth2RequestFactory(ClientDetailsService clientDetailsService) {
        DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
        return requestFactory;
    }

    @Bean
    public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        return accessDeniedHandler;
    }

    @Bean
    @Autowired
    public ClientCredentialsTokenEndpointFilter clientCredentialsTokenEndpointFilter(@Qualifier("authenticationManagerBean") AuthenticationManager authenticationManager) {

        try{
            ClientCredentialsTokenEndpointFilter accessTokenProvider = new ClientCredentialsTokenEndpointFilter();
            accessTokenProvider.setAuthenticationManager(authenticationManager);
            accessTokenProvider.setAllowOnlyPost(true);
            return accessTokenProvider;
        } catch(Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}