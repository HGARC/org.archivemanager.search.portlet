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

<style>
	.am-toolbar{width:90%;height:50px;margin:10px;padding:10px;border:1px solid #EEEEEE;}
	.am-icon{float:right;margin-right:9px;}
</style>

<div class="am-toolbar">
	<c:if test="${searchable}">	
	<a style="float:left;" href="/collections/collection/search?id=<c:out value="${entityId}" />">
		<div style="width:50px;margin-right: 10px;">
			<img class="am-icon" src="/theme/images/icons32/zoom.png" />
			<div style="width:50px;font-size:10px;text-align:center;line-height:12px;">Search Collection</div>
		</div>
	</a>
	</c:if>	
</div>
<c:if test="${not empty findingAid}">
<div>
	<div style="background-color:#F3E3B9;margin:20px;">
		<div style="padding:10px;">
			<div style="background-color:#FFFFFF;color:#333333;font-weight:bold;text-align:center;">
				<div style="padding:10px;">CLICK BELOW TO VIEW THE INVENTORY OF THE COLLECTION</div>
			</div>
		</div>
		<div style="padding:10px;text-align:center;">
			<a href="<c:out value="${findingAid}" />">
				<img style="width:150px;" src="/theme/images/pdf.png" />
			</a>
		</div>	
	</div>
</div>
</c:if>