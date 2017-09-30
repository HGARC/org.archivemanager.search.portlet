<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<portlet:resourceURL var="resourceURL"></portlet:resourceURL>

<style>
	.dialog-iframe-root-node {left:-10px;position:relative;top:-10px;}
	
</style>

<iframe id="mainFrame" src="${resourceURL}&entityId=<c:out value="${entityId}" />" tabIndex='-1' style="height:875px;width:1080px;position:absolute;overview:hidden;border:0"></iframe>