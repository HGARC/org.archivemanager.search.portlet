<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<jsp:include page="include/header.jsp" />

<div style="padding:5px;"><a href="<c:out value="${baseUrl}" />/<c:out value="${query}" />">Back To Search Results</a></div>

<table style="width:100%">


</table>