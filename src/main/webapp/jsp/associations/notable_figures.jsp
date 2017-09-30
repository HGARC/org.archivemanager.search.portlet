<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:if test="${not empty result.people}">
<div class="associated-notable-figures">
	<div class="associated-notable-figures-title">Notable Figures</div>
	<table class="associated-notable-figures-list">
		<tbody>
			<c:forEach items="${result.people}" var="person" varStatus="nstatus">
			<tr>
				<td class="associated-notable-figure-title" style="padding-left:15px;">
					<c:out value="${nstatus.index + 1}" />. 
					<c:choose>
						<c:when test="${links == 'true'}">
						<a href="/collections/notable-figure?id=<c:out value="${person.id}" />">
							<c:out value="${person.title}" />
						</a>
						</c:when>
						<c:otherwise>
							<c:out value="${person.title}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			</c:forEach>	
		</tbody>
	</table>
</div>
</c:if>	