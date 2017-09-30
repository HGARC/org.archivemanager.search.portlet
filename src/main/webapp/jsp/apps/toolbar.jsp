<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>
<%@page import="javax.portlet.WindowState"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<liferay-portlet:renderURL portletName="collections_WAR_archivemanagersearchportlet" windowState="<%=WindowState.MAXIMIZED.toString() %>" var="editorUrl">
	<portlet:param name="goto" value="IpByHourPage"/>
</liferay-portlet:renderURL>
<liferay-portlet:renderURL portletName="data_WAR_archivemanagersearchportlet" windowState="<%=WindowState.MAXIMIZED.toString() %>" var="dataUrl">
	<portlet:param name="goto" value="IpByHourPage"/>
</liferay-portlet:renderURL>
<style>
	.am-toolbar{width:90%;height:50px;margin:10px;padding:10px;border:1px solid #EEEEEE;}
	.am-icon{float:right;margin-right:9px;}
</style>

<div class="am-toolbar">		
	<a style="float:left;" href="${editorUrl}&entityId=<c:out value="${entityId}" />">
		<div style="width:50px;">
			<img class="am-icon" src="/theme/images/icons32/file_manager.png" />
			<div style="width:50px;font-size:10px;text-align:center;line-height:12px;">Edit Collection</div>
		</div>
	</a>
	<a style="float:left;" href="${dataUrl}">
		<div style="width:50px;">
			<img class="am-icon" src="/theme/images/icons32/database.png" />
			<div style="width:50px;font-size:10px;text-align:center;line-height:12px;">Manage Data</div>
		</div>
	</a>
</div>