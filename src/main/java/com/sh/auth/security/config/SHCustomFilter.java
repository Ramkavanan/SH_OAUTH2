package com.sh.auth.security.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

//import com.sh.auth.util.GooogleTokenVerifier;

/**
 * The Custom filter class active only when the signup called from rest application
 * this provide pre authentication for google access token and client 
 * 
 * @author RamaKavanan
 */
 public class SHCustomFilter implements Filter {
	
	private static final String USER_NAME = "username";
	
	private static final String PASSWORD = "password";
	
	private static final String CLIENT_ID = "client_id";
	
	private static final String CLIENT_SECRET = "client_secret";
	
	private static final String POST = "post";
	
	private static final String SIGNUP_URL = "/user/signup";
	
	private ClientDetailsService clientDetailService;
	
	@Override
	public void destroy() {}

	/**
	 * This function validate the google access token and application client id
	 * if 
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse res = (HttpServletResponse)response;
		System.out.println("Came This filter to test ---- ");
		String signUpPath = req.getRequestURI().substring(req.getContextPath().length());
		if(req.getMethod().equalsIgnoreCase(POST) && signUpPath.equalsIgnoreCase(SIGNUP_URL)) {
			String userName = req.getParameter(USER_NAME);
			String password = req.getParameter(PASSWORD);
			String clientId = req.getParameter(CLIENT_ID);
			String clientSecret = req.getParameter(CLIENT_SECRET);
			if(StringUtils.isBlank(clientId) || StringUtils.isBlank(clientSecret)) {
				genericErrorResponse(res,"Client id/secret should not blank", "Client id/secret should not blank" );
			}
			ClientDetails clientDetail = clientDetailService.loadClientByClientId(clientId);
			if(clientDetail == null) {
				genericErrorResponse(res,"Client credentials not presented", "Client credentials not presented" );
			} 
			if(!clientDetail.getClientSecret().equalsIgnoreCase(clientSecret)) {
				genericErrorResponse(res,"Client secret mismatch", "Client secret mismatch" );
			}
			if(!StringUtils.isBlank(password)) {
				/*if(GooogleTokenVerifier.validateAccessToken(password, userName, clientId)) {
					filterChain.doFilter(req, res);
				} else {*/
					genericErrorResponse(res,"Invalid google access token", "Invalid google access token" );
			//	}
			} else {
				genericErrorResponse(res,"Password should not be null", "Password should not be null" );
			}
		} else {
			throw new HttpRequestMethodNotSupportedException(req.getMethod(), new String[] { "POST" });
		}
	}
	
	private void genericErrorResponse(HttpServletResponse response, String errorMessage, String headerMessage)  {
		response.setHeader("SIGNUP-ERROR", headerMessage); 
		throw new BadCredentialsException(errorMessage);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		clientDetailService = (ClientDetailsService) webApplicationContext.getBean("clientDetailService");
	}
}
