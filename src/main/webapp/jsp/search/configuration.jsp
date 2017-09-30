<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>

<%@page import="com.liferay.portlet.documentlibrary.model.DLFileEntryConstants" %><%@
page import="com.liferay.portlet.documentlibrary.model.DLFolderConstants" %><%@
page import="com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil" %><%@
page import="com.liferay.portlet.documentlibrary.service.DLAppServiceUtil" %><%@
page import="com.liferay.portlet.documentlibrary.NoSuchFolderException" %><%@
page import="com.liferay.portlet.documentlibrary.util.DLUtil" %><%@
page import="com.liferay.portal.kernel.repository.RepositoryException" %><%@
page import="com.liferay.portal.kernel.repository.model.FileEntry" %><%@
page import="com.liferay.portal.kernel.repository.model.FileVersion" %><%@
page import="com.liferay.portal.kernel.repository.model.Folder" %><%@
page import="com.liferay.portal.kernel.util.FastDateFormatFactoryUtil" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.PrefsParamUtil" %>

<%@ page import="java.util.List" %><%@
page import="java.util.ArrayList" %><%@
page import="java.text.Format" %>

<%@ page import="javax.portlet.MimeResponse" %><%@
page import="javax.portlet.PortletConfig" %><%@
page import="javax.portlet.PortletContext" %><%@
page import="javax.portlet.PortletException" %><%@
page import="javax.portlet.PortletMode" %><%@
page import="javax.portlet.PortletPreferences" %><%@
page import="javax.portlet.PortletRequest" %><%@
page import="javax.portlet.PortletResponse" %><%@
page import="javax.portlet.PortletURL" %><%@
page import="javax.portlet.ResourceURL" %><%@
page import="javax.portlet.UnavailableException" %><%@
page import="javax.portlet.ValidatorException" %><%@
page import="javax.portlet.WindowState" %>

<%@ page import="com.liferay.portal.kernel.language.LanguageUtil" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.GetterUtil" %><%@
page import="com.liferay.portal.kernel.util.ParamUtil" %><%@
page import="com.liferay.portal.kernel.util.StringBundler" %><%@
page import="com.liferay.portal.kernel.util.StringPool" %><%@
page import="com.liferay.portal.kernel.util.Validator" %><%@
page import="com.liferay.portlet.PortletPreferencesFactoryUtil" %>

<liferay-theme:defineObjects />

<portlet:defineObjects />

<%
PortletPreferences preferences = renderRequest.getPreferences();

String portletResource = ParamUtil.getString(request, "portletResource");

if (Validator.isNotNull(portletResource)) {
	preferences = PortletPreferencesFactoryUtil.getPortletSetup(request, portletResource);
}

String button = PrefsParamUtil.getString(preferences, request, "button", "");
String url = PrefsParamUtil.getString(preferences, request, "url", "");
String view = PrefsParamUtil.getString(preferences, request, "view", "");
String code = PrefsParamUtil.getString(preferences, request, "code", "");
String contentType = PrefsParamUtil.getString(preferences, request, "contentType", "");
String dateExpression = PrefsParamUtil.getString(preferences, request, "dateExpression", "");
String collection = PrefsParamUtil.getString(preferences, request, "collection", "");
String language = PrefsParamUtil.getString(preferences, request, "language", "");
String sort = PrefsParamUtil.getString(preferences, request, "sort", "");
String parms = PrefsParamUtil.getString(preferences, request, "parms", "");
String size = PrefsParamUtil.getString(preferences, request, "size", "10");
String description = PrefsParamUtil.getString(preferences, request, "description", "");
String summary = PrefsParamUtil.getString(preferences, request, "summary", "");
String detailPage = PrefsParamUtil.getString(preferences, request, "detailPage", "");
String maxFieldSize = PrefsParamUtil.getString(preferences, request, "maxFieldSize", "");
String allResults = PrefsParamUtil.getString(preferences, request, "all-results", "true");
%>

<liferay-portlet:actionURL portletConfiguration="true" var="configurationURL" />

<aui:form action="<%= configurationURL %>" method="post">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
		
	<aui:select label="View Selection" name="preferences--view--">
		<aui:option selected="<%= view.equals(\"results.jsp\") %>" value="results.jsp">Search & Navigation</aui:option>
		<aui:option selected="<%= view.equals(\"results2.jsp\") %>" value="results2.jsp">Search & Navigation 2</aui:option>
		<aui:option selected="<%= view.equals(\"videos.jsp\") %>" value="videos.jsp">Videos</aui:option>
		<aui:option selected="<%= view.equals(\"gallery1.jsp\") %>" value="gallery1.jsp">Zoomify Gallery</aui:option>
		<aui:option selected="<%= view.equals(\"gallery2.jsp\") %>" value="gallery2.jsp">Zoomify Gallery w/ Navigation</aui:option>
		<aui:option selected="<%= view.equals(\"collections.jsp\") %>" value="collections.jsp">Collection Search</aui:option>
		<aui:option selected="<%= view.equals(\"archive_results.jsp\") %>" value="archive_results.jsp">Archive Search</aui:option>
		<aui:option selected="<%= view.equals(\"notable_figures_entries.jsp\") %>" value="notable_figures_entries.jsp">Notable Figures Entries</aui:option>
		<aui:option selected="<%= view.equals(\"series.jsp\") %>" value="series.jsp">Series Listing</aui:option>
		<aui:option selected="<%= view.equals(\"series_with_notes.jsp\") %>" value="series_with_notes.jsp">Series Listing (with notes)</aui:option>
		<aui:option selected="<%= view.equals(\"subject_guides.jsp\") %>" value="subject_guides.jsp">Subject Guides</aui:option>
		<aui:option selected="<%= view.equals(\"cooke-results.jsp\") %>" value="cooke-results.jsp">Cooke Results (deprecated)</aui:option>
	</aui:select>
	
	<aui:input name="preferences--all-results--" type="checkbox" value="<%= allResults %>" label="Display All Results" />
	
	<aui:input name="preferences--code--" value="<%= code %>" label="Collection Code" />
	
	<aui:input name="preferences--sort--" value="<%= sort %>" />
	
	<aui:input name="preferences--size--" value="<%= size %>" style="width:100px;" />
	
	<aui:input name="preferences--parms--" value="<%= parms %>" />
	
	<aui:input name="preferences--detailPage--" label="Detail Page" value="<%= detailPage %>" />
	
	<aui:input name="preferences--maxFieldSize--" label="Maximum Field Size" style="width:100px;" value="<%= maxFieldSize %>" />
	
	<aui:input name="preferences--contentType--" type="checkbox" value="<%= contentType %>" />
	
	<aui:input name="preferences--dateExpression--" type="checkbox" value="<%= dateExpression %>" />
	
	<aui:input name="preferences--collection--" type="checkbox" value="<%= collection %>" />
	
	<aui:input name="preferences--description--" type="checkbox" value="<%= description %>" />
	
	<aui:input name="preferences--summary--" type="checkbox" value="<%= summary %>" />
	
	<aui:input name="preferences--language--" type="checkbox" value="<%= language %>" />
	
	<aui:button-row>
		<aui:button type="submit" />
	</aui:button-row>
</aui:form>