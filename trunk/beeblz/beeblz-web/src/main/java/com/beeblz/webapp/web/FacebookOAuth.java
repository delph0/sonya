/**
 * Copyright (c) 2010, beeblz.com
 * All rights reserved.
 */
package com.beeblz.webapp.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.visural.common.StringUtil;

/**
 * Fiter to get the facebook oAuth access token.
 * 
 * @author YoungGue Bae
 */
public class FacebookOAuth implements Filter {
	
	private static final Logger log = LoggerFactory.getLogger(FacebookOAuth.class);
	
	public void init(FilterConfig fc) throws ServletException {
		log.debug("FacebookOAuth init...........");		
	}

	public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc)
			throws IOException, ServletException {
		log.debug("FacebookOAuth doFilter...........");
		
		HttpServletRequest req = (HttpServletRequest) sr;
		HttpServletResponse res = (HttpServletResponse) sr1;
		String code = sr.getParameter("code");
		String token = sr.getParameter("fb_access_token");
		
		log.debug("facebook:code == " + code);
		
		if (StringUtil.isNotBlankStr(token)) {
			return;
		} else if (StringUtil.isBlankStr(code)) {
			String loginURL = Facebook.getLoginRedirectURL();
			log.debug("facebook:loginURL == " + loginURL);
			
			res.sendRedirect(loginURL);
			return;
		} else {
			String authURL = Facebook.getAuthURL(code);
			log.debug("facebook:authURL == " + authURL);
			URL url = new URL(authURL);
			try {
				String result = readURL(url);
				Integer expires = null;
				String[] pairs = result.split("&");
				
				String accessToken = null;
				
				for (String pair : pairs) {
					String[] kv = pair.split("=");
					if (kv.length != 2) {
						throw new RuntimeException("Unexpected auth response");
					} else {
						if (kv[0].equals("access_token")) {
							accessToken = kv[1];
						}
						if (kv[0].equals("expires")) {
							expires = Integer.valueOf(kv[1]);
						}
					}
				}
				//if (accessToken != null && expires != null) {
				if (accessToken != null) {
					log.debug("accessToken == " + accessToken);
					res.sendRedirect("http://bee-blz.appspot.com/facebook/socialGraph.jsp?fb_access_token="+accessToken);
					return;					
				} else {
					throw new RuntimeException("Access token and expires not found");
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				throw new RuntimeException(e);
			} 
		}
	}

	private String readURL(URL url) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream is = url.openStream();
		int r;
		while ((r = is.read()) != -1) {
			baos.write(r);
		}
		return new String(baos.toByteArray());
	}

	public void destroy() {
	}
}
