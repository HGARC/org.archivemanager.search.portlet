package org.archivemanager.portal.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NoCacheFilter implements Filter {

	  @Override
	  public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
	      final HttpServletRequest httpRequest = (HttpServletRequest) request;
	      final String requestUri = httpRequest.getRequestURI();
	      final HttpServletResponse httpResponse = (HttpServletResponse) response;
	      
	      if(requestUri.contains("/archivemanager-apps-portlet/js/CollectionManager/")) {
	    	  httpResponse.addHeader("Cache-Control", "no-cache");	        
	      }
	      chain.doFilter(request, response);
	  }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
}
