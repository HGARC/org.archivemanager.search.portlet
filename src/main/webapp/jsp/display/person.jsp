<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="collection-detail">
	<div class="collection-detail-left">
		<table style="width:100%;">
			<tbody>
				<tr>
					<td class="collection-detail-title" colspan="2">
						<c:out value="${result.title}" escapeXml="false"/>
					</td>
				</tr>
				<c:if test="${result.dates}">
				<tr>
					<td class="collection-detail-label">
						Scope:
					</td>
					<td class="collection-detail-value">
						<c:out value="${result.dates}" escapeXml="false" />
					</td>
				</tr>
				</c:if>
				<c:if test="${result.source}">
				<tr>
					<td class="collection-detail-label">
						Source:
					</td>
					<td class="collection-detail-value">
						<c:out value="${result.source}" escapeXml="false" />
					</td>
				</tr>
				</c:if>
				
				<c:choose>
					<c:when test="${result.note}">
						<td class="collection-detail-label">
							Note:
						</td>
						<td class="collection-detail-value">
							<c:out value="${result.note}" escapeXml="false" />
						</td>
					</c:when>
					<c:otherwise>
						<td colspan="2" style="width:100%;text-align:center;padding:40px 0;">
							<img src="<c:out value="${missingImage}" />" />
						</td>
					</c:otherwise>
				</c:choose>
				
			</tbody>
		</table>
	</div>
</div>