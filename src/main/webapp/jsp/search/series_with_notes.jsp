<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="collections">
	
	<c:forEach items="${collections}" var="collection" varStatus="cstatus">
		<div class="collection">
			<div style="line-height:25px;margin-bottom:10px;"><c:out value="${collection.title}" /></div>
			<c:forEach items="${collection.series}" var="series" varStatus="sstatus">
				<div class="series">
					<a href="<c:out value="${baseUrl}" />/search?query=path:<c:out value="${series.id}" />"><c:out value="${series.title}" /></a>
					<c:if test="${!empty series.description}">
					<span id="detail${cstatus.count}${sstatus.count}" class="arrow" onclick="showDetail('${cstatus.count}${sstatus.count}')">
						<img src="<%= themeDisplay.getPathThemeImages()%>/arrow_closed.gif" />
					</span>		
					</c:if>
				</div>
				<c:if test="${!empty series.description}">
				<div id="${cstatus.count}${sstatus.count}" class="note">
					<c:out value="${series.description}" />
				</div>
				</c:if>
			</c:forEach>
		</div>
	</c:forEach>
	
</div>