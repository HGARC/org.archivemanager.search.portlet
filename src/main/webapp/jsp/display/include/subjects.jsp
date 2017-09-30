<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${not empty result.subjects}">
	<tr><td style="height:1.5em;"></td></tr>
	<tr><td class="detail-title">Associated Subjects:</td></tr>
	<c:forEach items="${result.subjects}" var="subject" varStatus="nstatus">
		<tr><td style="padding-left:15px;">
			<p class="detail-text"><c:out value="${subject.title}" escapeXml="false" /></p>
		</td></tr>
	</c:forEach>
	<tr style="border-bottom: 1px solid #395AC3;"><td style="height:0.5em;"></td></tr>
</c:if>