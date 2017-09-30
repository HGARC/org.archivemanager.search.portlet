<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:if test="${not empty result.collections}">
<div class="associated-collections">
	<div class="associated-collections-title">Collections</div>
	<table class="associated-collections-list">
		<tbody>
			<c:forEach items="${result.collections}" var="collection" varStatus="nstatus">
			<tr>
				<td class="associated-collection-title" style="padding-left:15px;">
					<c:out value="${nstatus.index + 1}" />. 
					<a href="/collections/collection?id=<c:out value="${collection.id}" />">
						<c:out value="${collection.title}" />
					</a>
				</td>
			</tr>
			</c:forEach>	
		</tbody>
	</table>
</div>
</c:if>