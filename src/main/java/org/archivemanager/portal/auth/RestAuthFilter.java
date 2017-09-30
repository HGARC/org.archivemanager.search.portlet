package org.archivemanager.portal.auth;

import com.liferay.portal.NoSuchUserException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CookieKeys;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.KeyValuePair;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.Authenticator;
import com.liferay.portal.security.auth.CompanyThreadLocal;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ClassNameLocalServiceUtil;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class RestAuthFilter implements Filter {
	private Log LOG = LogFactoryUtil.getLog(getClass());
	
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		boolean canProceed = false;
		try {
			if(request instanceof HttpServletRequest) {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				com.liferay.portal.model.User user = PortalUtil.getUser(httpRequest);
				if(user == null) user = getUserByRememberMe(httpRequest, httpResponse);
				if(user == null) { //check for api_key in request
					String api_key = request.getParameter("api_key");
					if(api_key != null && api_key.length() > 0) {
						user = getUserByApiKey(api_key);
					}
				}
				if(user == null) {
					final String auth = httpRequest.getHeader("Authorization");
					if( auth != null ) {
						LOG.info("Has basic auth...");
						final int index = auth.indexOf( ' ' );
						if( index > 0 ) {
							final String[] credentials =
								StringUtils.split( new String(Base64.decodeBase64(auth.substring( index ).getBytes()), "UTF-8"), ':' );							
							if(credentials.length == 2) {								
							    canProceed = loginLiferayUser(credentials[0], credentials[1]);
							}
						}
					}
				}
				if(user != null) {
					setUser(user);
					CompanyThreadLocal.setCompanyId(PortalUtil.getCompanyId(httpRequest));
					canProceed = true;
				} else {
					long companyId = PortalUtil.getDefaultCompanyId();
					user = UserLocalServiceUtil.getDefaultUser(companyId);
					canProceed = true;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			LOG.error("User is not logged in. Caused by: " + ex.getMessage(), ex);
		}		
		if(canProceed) {
			chain.doFilter( request, response );
		} else {
			if(response instanceof HttpServletResponse) {
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
	}
	
	private User getUserByApiKey(String api_key) throws Exception {
		String customAttributeName = "api_key";
		long companyId = PortalUtil.getDefaultCompanyId();
		long classNameId = ClassNameLocalServiceUtil.getClassNameId(User.class);
		 
		List<ExpandoValue> values = ExpandoValueLocalServiceUtil.getColumnValues(companyId,classNameId, ExpandoTableConstants.DEFAULT_TABLE_NAME,customAttributeName,api_key,-1,-1);
		User user = null;      
		for(int i = 0; i < values.size(); i++) {
			long userId = values.get(i).getClassPK();
		    try {
		    	user = UserLocalServiceUtil.getUser(userId);
		    	if(user != null) {
		    		setUser(user);
		            CompanyThreadLocal.setCompanyId(companyId);
		    	}
		    } catch(NoSuchUserException e) {
		    	LOG.error(e);
		    }
		}
		return user;
	}
	protected User getUserByRememberMe(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String autoUserId = CookieKeys.getCookie(request, CookieKeys.ID, false);
		String autoPassword = CookieKeys.getCookie(request, CookieKeys.PASSWORD, false);
		String rememberMe = CookieKeys.getCookie(request, CookieKeys.REMEMBER_ME, false);
		String proxyPath = PortalUtil.getPathProxy();
		String contextPath = PortalUtil.getPathContext();		
		if (proxyPath.equals(contextPath)) {
			if (Validator.isNotNull(request.getContextPath())) {
				rememberMe = Boolean.TRUE.toString();
			}
		} else {
			if (!contextPath.equals(request.getContextPath())) {
				rememberMe = Boolean.TRUE.toString();
			}
		}
		String[] credentials = null;
		if (Validator.isNotNull(autoUserId) &&
			Validator.isNotNull(autoPassword) &&
			Validator.isNotNull(rememberMe)) {
			Company company = PortalUtil.getCompany(request);
			KeyValuePair kvp = null;
			if (company.isAutoLogin()) {
				kvp = UserLocalServiceUtil.decryptUserId(company.getCompanyId(), autoUserId, autoPassword);
				credentials = new String[3];
				credentials[0] = kvp.getKey();
				credentials[1] = kvp.getValue();
				credentials[2] = Boolean.FALSE.toString();
			}
		}
		if(credentials != null) {
			Company company = PortalUtil.getCompany(request);
			User defaultUser = UserLocalServiceUtil.getDefaultUser(company.getCompanyId());
			long userId = GetterUtil.getLong(credentials[0]);
			if(defaultUser.getUserId() == userId) {
				removeCookies(request, response);
				return null;
			} else {
				User user = UserLocalServiceUtil.getUserById(userId);
				return user;
			}
		}
		return null;
	}
	private boolean loginLiferayUser(String username, String password) throws Exception {
	    boolean isLoggedIn = false;
	    
		Company company = CompanyLocalServiceUtil.getCompanyByMx(PropsUtil.get(PropsKeys.COMPANY_DEFAULT_WEB_ID));
		User user = null;
		
		long result = UserLocalServiceUtil.authenticateForBasic(company.getCompanyId(), CompanyConstants.AUTH_TYPE_SN, username, password);
    	if(result != Authenticator.DNE && result != Authenticator.FAILURE) {
        	user = UserLocalServiceUtil.fetchUserById(result);
       		isLoggedIn = true;
   		} else {
   		    LOG.warn("Error logging in with basic auth user: " + username);
   		}
		
		if (isLoggedIn) {
		    setUser(user);
            CompanyThreadLocal.setCompanyId(company.getCompanyId());
		}
		return isLoggedIn;
	}
	protected void removeCookies(HttpServletRequest request, HttpServletResponse response) {
		Cookie cookie = new Cookie(CookieKeys.ID, StringPool.BLANK);
		cookie.setMaxAge(0);
		cookie.setPath(StringPool.SLASH);
		CookieKeys.addCookie(request, response, cookie);
		cookie = new Cookie(CookieKeys.PASSWORD, StringPool.BLANK);
		cookie.setMaxAge(0);
		cookie.setPath(StringPool.SLASH);
		CookieKeys.addCookie(request, response, cookie);
	}
	private void setUser(User user) throws Exception  {
		PrincipalThreadLocal.setName(user.getUserId());		
		PermissionChecker permissionChecker = PermissionCheckerFactoryUtil.create(user);
		PermissionThreadLocal.setPermissionChecker(permissionChecker);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
	}

}
