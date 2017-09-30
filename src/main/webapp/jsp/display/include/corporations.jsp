<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${not empty result.corporations}">
	<tr><td style="height:1.5em;"></td></tr>
	<tr><td class="detail-title">Associated Corporate Entities:</td></tr>
	<c:forEach items="${result.corporations}" var="name" varStatus="nstatus">	
		<tr><td style="padding-left:15px;">
			<p class="detail-text"><c:out value="${name.title}" escapeXml="false" /> (<b><c:out value="${name.function}" /></b>)</p>
		</td></tr>
	</c:forEach>
	<tr style="border-bottom: 1px solid #395AC3;"><td style="height:0.5em;"></td></tr>
</c:if>