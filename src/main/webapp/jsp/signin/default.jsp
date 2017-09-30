<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:choose>
	<c:when test="${signedin}">
		<div style="width:85px;padding:8px 25px;font-weight:bold;background-color:#f3e3b9;">
			<a style="color:#000000;" href="/research/digital-reserve/my-classes">MY CLASSES</a>
		</div>
	</c:when>
	<c:otherwise>
		<div style="width:230px;padding:8px 25px;font-weight:bold;background-color:#f3e3b9;">
			<a style="color:#000000;" href="/c/portal/login?p_l_id=${plid}">SIGN INTO THE DIGITAL RESERVE</a>
		</div>
	</c:otherwise>
</c:choose>