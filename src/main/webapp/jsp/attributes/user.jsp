<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<ul>
	<c:forEach items="${attributes}" var="attribute" varStatus="astatus">
		<c:forEach items="${attribute.values}" var="value" varStatus="vstatus">
			<li>
				<c:choose>
					<c:when test="${value.count}">
						<a href="<c:out value="${baseUrl}" />query=<c:out value="${value.query}" escapeXml="false"/>"><c:out value="${value.name}" escapeXml="false"/> (<c:out value="${value.count}" />)</a>
					</c:when>
					<c:otherwise>
						<a href="<c:out value="${baseUrl}" />query=<c:out value="${value.query}" escapeXml="false"/>"><c:out value="${value.name}" escapeXml="false"/></a>
					</c:otherwise>
				</c:choose>						
			</li>
		</c:forEach>
	</c:forEach>
</ul>
