<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:if test="${not empty result.subjects}">
<div class="associated-subjects">
	<div class="associated-subjects-title">Associated Subjects</div>
	<table class="associated-subjects-list">
		<tbody>
			<c:forEach items="${result.subjects}" var="subject" varStatus="nstatus">
			<tr>
				<td class="associated-subject-title" style="padding-left:15px;">
					<c:out value="${nstatus.index + 1}" />. 
					<c:choose>
						<c:when test="${links == 'true'}">
						<a href="/collections/subject?id=<c:out value="${subject.id}" />">
							<c:out value="${subject.title}" />
						</a>
						</c:when>
						<c:otherwise>
							<c:out value="${subject.title}" />
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</div>
</c:if>