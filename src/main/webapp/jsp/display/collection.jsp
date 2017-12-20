<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div class="collection-detail">
	<div class="collection-detail-left">
		<div class="collection-detail-title">
			<c:out value="${result.title}" escapeXml="false" />
		</div>
		<div>
			<div class="collection-detail-label" style="">Scope:</div>
			<div class="collection-detail-value">
				<c:out value="${result.scopeNote}" escapeXml="false" />
			</div>
		</div>

	</div>
</div>