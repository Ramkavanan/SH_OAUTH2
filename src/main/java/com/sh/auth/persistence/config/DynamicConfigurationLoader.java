package com.sh.auth.persistence.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.netflix.config.DynamicPropertyFactory;

/**
 * The helper class used to load properties
 * 
 * @author RamaKavanan
 */
@Configuration
@ComponentScan(DynamicConfigurationLoader.BASE_PACKAGE)
public class DynamicConfigurationLoader {
	
	public static final String BASE_PACKAGE = "com.sh.auth";
	
	public static String getOAuthResourceId() {
    	return DynamicPropertyFactory.getInstance().getStringProperty("oauthResourceId", "sh-resource").getValue();
    }
	
	public static boolean isGoogleOauth2Enabled() {
		return DynamicPropertyFactory.getInstance().getBooleanProperty("isGoogleOauth2Enabled", false).getValue();
	}
	
	public static String getDefaultRoleForSignUp() {
		return DynamicPropertyFactory.getInstance().getStringProperty("defaultSignupRole", "ROLE_SIGNUP").getValue();
	}
	
	public static String getMobileClientId() {
		return DynamicPropertyFactory.getInstance().getStringProperty("mobileAppClientId", "sh-restapi").getValue();
	}
	
	public static String getDefaultSignUpUrl()  {
		return DynamicPropertyFactory.getInstance().getStringProperty("defaultSignUpUrl", "/user/signup").getValue();
	}
}
