<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:if test="${not empty resultset.results}">
	<c:if test="${resultset.start != 0}">
		Results <b><c:out value="${resultset.start}" /> - <c:out value="${resultset.end}" /></b> of <b><c:out value="${resultset.resultCount}" /></b>
	</c:if>
	<c:if test="${resultset.start == 0}">
		Results <b>1 - <c:out value="${resultset.end}" /></b> of <b><c:out value="${resultset.resultCount}" /></b>
	</c:if>
</c:if>
<c:if test="${empty resultset.results}">
	<b>No Results</b>
</c:if>