<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:if test="${not empty result.entries}">
<div class="associated-entries">
	<div class="associated-entries-title">Notable Figures</div>
	<table class="associated-entries-list">
		<tbody>
			<c:forEach items="${result.entries}" var="entry" varStatus="nstatus">
			<tr>
				<td class="associated-entry-title">
					<c:out value="${nstatus.index + 1}" />. 
					<c:out value="${entry.collection}" />
				</td>
			</tr>
			<tr>
				<td class="associated-entry-description">
					<c:out value="${entry.description}" escapeXml="false" />
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</c:if>