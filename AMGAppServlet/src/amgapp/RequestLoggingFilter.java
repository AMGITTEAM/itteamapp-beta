package amgapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class RequestLoggingFilter
 */
@WebFilter("/RequestLoggingFilter")
public class RequestLoggingFilter implements Filter {
	
	public void init(FilterConfig fConfig) throws ServletException {
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		Cookie[] cookies = req.getCookies();
		String idString = null;
		if(cookies!=null) {
			for(Cookie cookie : cookies) {
				if(cookie.getName().equals("browserID")) {
					idString = cookie.getValue();
				}
			}
		}
		if(idString==null) {
			idString = UUID.randomUUID().toString();
			Cookie cookie = new Cookie("browserID",idString);
			cookie.setMaxAge(60*60*24*365*50); //50 Jahre
			((HttpServletResponse)response).addCookie(cookie);
		}
		String queries = req.getQueryString();
		String requestURL = req.getRequestURL()+"";
		if(idString.equals("ADMIN")||requestURL.endsWith(".png")||requestURL.endsWith(".jpg")||requestURL.endsWith(".css")||requestURL.endsWith(".woff2")||requestURL.contains("/amgapp?")) {
			chain.doFilter(request, response);
			return;
		}
		PrintWriter log = new PrintWriter(new FileWriter(new File("/home/ak/log.txt"),true));
		log.write(System.currentTimeMillis()+"::"+req.getRemoteAddr()+"::");
		log.flush();
		if(queries!=null) {
			log.write(requestURL+"?"+req.getQueryString());
		}
		else {
			log.write(requestURL);
		}
		log.flush();
		log.write("::"+idString);
		log.flush();
		log.write("::"+req.getHeader("user-agent"));
		log.flush();
		log.write("\n");
		log.flush();
		log.close();
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	public void destroy() {
		//we can close resources here
	}

}