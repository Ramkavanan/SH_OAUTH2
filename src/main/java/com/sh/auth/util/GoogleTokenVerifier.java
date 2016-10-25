package com.sh.auth.util;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;

public class GoogleTokenVerifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(GoogleTokenVerifier.class);
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	
	public static boolean validateAccessToken(String accessToken, String userName, String clientId) {
		try{
			LOG.debug("Google token verifier, Access token {} belongs to {}",accessToken,userName);
			GoogleCredential credential = new GoogleCredential().setAccessToken( accessToken );
			Oauth2 oauth2 = new Oauth2.Builder(
				    HTTP_TRANSPORT,
				    JSON_FACTORY, credential )
				    .build();
			Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken( accessToken ).execute();
			LOG.debug("Token Info {}", tokenInfo);
			if(tokenInfo != null && tokenInfo.getEmail().equalsIgnoreCase(userName) /*&& tokenInfo.getIssuedTo().equalsIgnoreCase(clientId)*/) {
				return true;
			}
		} catch(IOException e){
			//throw new BadCredentialsException("Invalid Access token");
			return false;
		}
		return false;
	}
	
/*	private boolean validateGoogleOauthToken(String accessToken, String userName) {
		try{
			GoogleIdTokenVerifier tokenVerifier = new GoogleIdTokenVerifier.Builder(HTTP_TRANSPORT, JSON_FACTORY).setClientIds("1015495538162-c11qpa9a3t67jda7s0uvk4ifk6klsejf.apps.googleusercontent.com")
					.build();
			GoogleIdTokenVerifier test = new GoogleIdTokenVerifier(HTTP_TRANSPORT, JSON_FACTORY);
			
			GoogleIdToken idToken = test.verify(accessToken);
			if(idToken != null) {
				Payload payload = idToken.getPayload();
				LOG.debug(" Google Payload Info {}", payload);
				  // Print user identifier
				  String userId = payload.getUserId();
				  System.out.println("User ID: " + userId);

				  // Get profile information from payload
				  String email = payload.getEmail();
				  boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
				  String name = (String) payload.get("name");
				  String pictureUrl = (String) payload.get("picture");
				  String locale = (String) payload.get("locale");
				  String familyName = (String) payload.get("family_name");
				  String givenName = (String) payload.get("given_name");
				  System.out.println("Email === "+email+" EVerified  "+emailVerified+" name == "+name);
				  return true;
			}
		} catch(Exception ex) {
			System.out.println("Exception at when the data load");
			ex.printStackTrace();
		}
		return false;
	}*/
}
