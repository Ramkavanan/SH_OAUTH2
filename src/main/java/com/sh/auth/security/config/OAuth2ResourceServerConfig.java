package com.sh.auth.security.config;
import java.util.List;

import javax.sql.DataSource;

import com.sh.auth.persistence.config.DynamicConfigurationLoader;
import com.sh.auth.persistence.dao.RBACPermissionDao;
import com.sh.auth.persistence.model.RBACPermission;
import com.sh.auth.persistence.service.OAuthUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Provide authenticated person to access the protected resource
 * the protected resource information defined in configure function
 * 
 * @author RamaKavanan
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter  {

    private static final String REST_RESOURCE_ID = "sh-resource";
    
    private static final String SCOPE_READ = "read";
    
    private static final String SCOPE_WRITE = "write";
    
    private static final String SCOPE_DELETE = "delete";

    @Autowired
    @Qualifier("oauthDataSource")
    DataSource dataSource;

    @Autowired
    private RBACPermissionDao rbacPermissionDao;
    
    @Autowired
    private OAuthUserDetailService userDetailsService;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(REST_RESOURCE_ID).stateless(false);
    }

    @Bean(name="oauthAuthenticationEntryPoint")
    public OAuth2AuthenticationEntryPoint oauthAuthenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint entryPoint = new OAuth2AuthenticationEntryPoint();
        entryPoint.setRealmName("sh-auth");
        return entryPoint;
    }

    @Bean
    public OAuth2AccessDeniedHandler oauthAccessDeniedHandler() {
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        return accessDeniedHandler;
    }

    @Autowired
    private OAuth2AuthenticationEntryPoint oauthAuthenticationEntryPoint;

    @Autowired
    private OAuth2AccessDeniedHandler oauthAccessDeniedHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
    	//configureRBAC(http);
        http.userDetailsService(userDetailsService)
                .anonymous().disable();
        http.authorizeRequests().antMatchers("/**").fullyAuthenticated();
               http
                .httpBasic()
//                .authenticationEntryPoint(oauthAuthenticationEntryPoint)
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().accessDeniedHandler(oauthAccessDeniedHandler);
    }
    
    /**
     * The RBAC setting dynamically load from the database and configured
     * 
     * @param http
     * @throws Exception
     */
    private void configureRBAC(HttpSecurity http) throws Exception {
    	String resourceId = DynamicConfigurationLoader.getOAuthResourceId();
    	List<RBACPermission> rbacs = rbacPermissionDao.getByResourceId(resourceId);
    	for(RBACPermission rbac : rbacs) {
    		if(rbac.getScope() != null) {
    			if(rbac.getScope().contains(",")) {
    				for(String scope : rbac.getScope().split(",")) {
    					roleAndScopeBasedAccess(rbac, scope, http);
    				}
    			} else {
    				roleAndScopeBasedAccess(rbac, rbac.getScope(), http);
    			}
    		} else {
    			// Setting url access permission with out scope
    			http.authorizeRequests()
    			.regexMatchers(rbac.getUrl()).access("hasAnyRole("+rbac.getRolesAsString()+")");
    		}
    	}
    }
    
    /**
     * Function to provide the resource server configuration with the 
     * regmax to provide the authorize resource server access in oauth server
     * here scope to validate the authenticated user have the scope or not
     * and the role has been verified whether the authenticated principle have the 
     * rights to access the resources. The RBAC setting carried here
     * 
     * @param rbac
     * @param scope
     * @param http
     * @throws Exception
     */
    private void roleAndScopeBasedAccess(RBACPermission rbac, String scope, HttpSecurity http) throws Exception{
    	if(scope.equalsIgnoreCase(SCOPE_READ)) {
			http.authorizeRequests()
			.regexMatchers(HttpMethod.GET, rbac.getUrl()).access("#oauth2.hasScope('read')  and hasAnyRole("+rbac.getRolesAsString()+")");
		} else if(scope.equalsIgnoreCase(SCOPE_WRITE)){
			http.authorizeRequests()
			.regexMatchers(HttpMethod.POST, rbac.getUrl().trim()).access("#oauth2.hasScope('write') and hasAnyRole("+rbac.getRolesAsString()+")")
			.regexMatchers(HttpMethod.PUT, rbac.getUrl()).access("#oauth2.hasScope('write')  and hasAnyRole("+rbac.getRolesAsString()+")");
		} else if(scope.equalsIgnoreCase(SCOPE_DELETE)) {
			http.authorizeRequests()
			.regexMatchers(HttpMethod.DELETE, rbac.getUrl()).access("#oauth2.hasScope('delete')  and hasAnyRole("+rbac.getRolesAsString()+")");
		}
    }
}