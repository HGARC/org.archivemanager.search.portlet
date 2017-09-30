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
				<div class="series" style="width:<c:out value="${maxFieldSize}" />;">
					<a href="<c:out value="${baseUrl}" />/search?query=path:<c:out value="${series.id}" />"><c:out value="${series.title}" /></a>
				</div>
			</c:forEach>
		</div>
	</c:forEach>
	
</div>