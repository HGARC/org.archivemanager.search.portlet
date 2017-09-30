<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />	
<%
String themeImagePath = themeDisplay.getPathThemeImages();
%>

<c:if test="${not empty result.weblinks}">
	<c:forEach items="${result.weblinks}" var="file" varStatus="nstatus">
		<c:if test="${file.type == 'rendition' || file.type == 'flippingbook'}">
			<c:set var="linked" scope="session" value="true"/>
			<iframe style="width:98%;height:700px;" src="<c:out value="${file.url}" />"></iframe>
		</c:if>
		<c:if test="${file.type == 'partner'}">
			<c:set var="linked" scope="session" value="true"/>
			<a href="<c:out value="${file.url}" />" target="_blank">
				<img src="<%=themeImagePath %>/collections/<c:out value="${result.collectionId}" />_large.jpg" alt="<c:out value="${result.collectionName}" escapeXml="false" />" />
			</a>
		</c:if>
	</c:forEach>
</c:if>