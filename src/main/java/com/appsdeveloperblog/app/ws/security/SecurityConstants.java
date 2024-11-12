package com.appsdeveloperblog.app.ws.security;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;
import org.springframework.core.env.Environment;

public class SecurityConstants {

    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";

    public static String getTokenSecret() {
        Environment environment = (Environment) SpringApplicationContext.getBean("environment");
        return environment.getProperty("tokenSecret");
    }
}
