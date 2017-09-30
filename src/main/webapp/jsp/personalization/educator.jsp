<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />


<div class="collections">
	<c:forEach items="${entities}" var="result" varStatus="cstatus">
		<div class="collection">
			<div style="line-height:25px;margin-bottom:10px;">
				<c:if test="${result.contentType == 'collection'}">
					<c:choose>
						<c:when test="${result.url != null}">
							<a href="${result.url}">
						</c:when>
						<c:otherwise>
							<a href="/collections/collection?id=<c:out value="${result.id}" />">
						</c:otherwise>
					</c:choose>
				</c:if>	
				<c:if test="${result.contentType == 'person' || result.contentType == 'corporation'}">
					<a href="/collections/notable-figure?id=<c:out value="${result.id}" />">
				</c:if>
				<c:if test="${result.contentType == 'subject'}">
					<a href="/collections/subject?id=<c:out value="${result.id}" />">
				</c:if>
				<c:if test="${result.contentType != 'subject' && result.contentType != 'person' && result.contentType != 'corporation' && result.contentType != 'collection'}">
					<a href="/collections/item?id=<c:out value="${result.id}" />">
				</c:if>
				<c:choose>	
					<c:when test="${result.title != null}">
						<c:out value="${result.title}" escapeXml="false" />
					</c:when>
					<c:otherwise>
						no title
					</c:otherwise>
				</c:choose>
				</a>				
			</div>
		</div>
	</c:forEach>
</div>