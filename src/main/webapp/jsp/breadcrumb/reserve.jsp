<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div style="font-weight:bold;font-size: 18px;"><c:out value="${collection_name}" escapeXml="false" /></div>
<nav id="breadcrumbs" class="site-breadcrumbs">
	<ul class="breadcrumb" aria-label="Breadcrumb"> 
		<c:forEach items="${breadcrumbs}" var="breadcrumb" varStatus="rstatus">
		<li class="first breadcrumb-truncate">
			<c:if test="${!rstatus.last}">
			<a href="<c:out value="${breadcrumb.query}" />">
				<c:out value="${breadcrumb.name}" escapeXml="false" />
			</a>
			<span class="divider">/</span>
			</c:if>
			<c:if test="${rstatus.last}">
				<c:out value="${breadcrumb.name}" escapeXml="false" />
			</c:if>			
		</li>
		</c:forEach>
	</ul>
</nav>