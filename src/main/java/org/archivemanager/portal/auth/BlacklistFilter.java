package org.archivemanager.portal.auth;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class BlacklistFilter implements Filter {
private Log LOG = LogFactoryUtil.getLog(getClass());
	
	private static String[] exclusions = new String[] {
		"128.197.152.22"
	};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		boolean canProceed = true;
		String address = request.getRemoteAddr();
		if(address != null) {
			for(String exclusion : exclusions) {
				if(exclusion.equals(address) || address.contains(exclusion)) {
					canProceed = false;
					break;
				}
			}
		}
		if (canProceed) {
			chain.doFilter( request, response );
		} else {
			if (response instanceof HttpServletResponse) {
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
	}
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}
	@Override
	public void destroy() {

	}
}
