<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div id="archive-search-results">
	<table style="width:100%;">
		<tr>
			<td>
				<table class="archive-search-tools">
					<tbody>					
						<tr>
							<td class="archive-results-summary">
								<c:if test="${not empty resultset.results}">
									<c:if test="${resultset.start != 0}">
										Results <b><c:out value="${resultset.start}" /> - <c:out value="${resultset.end}" /></b> of <b><c:out value="${resultset.resultCount}" /></b> for <b><c:out value="${resultset.lastBreadcrumb.name}" /></b>
									</c:if>
									<c:if test="${resultset.start == 0}">
										Results <b>1 - <c:out value="${resultset.end}" /></b> of <b><c:out value="${resultset.resultCount}" /></b> for <b><c:out value="${resultset.lastBreadcrumb.name}" /></b>
									</c:if>
								</c:if>
								<c:if test="${empty resultset.results}">
									No Results for <b><c:out value="${resultset.lastBreadcrumb.name}" /></b>
								</c:if>
							</td>					
						</tr>					
					</tbody>
				</table>
	
				<table class="archive-results-table">
					<tbody>
						<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
							<tr>						
								<td class="archive-results-result">
									<div class="archive-results-title">
										<c:choose>
											<c:when test="${result.url != null}">
												<a href="${result.url}">
											</c:when>
											<c:otherwise>
												<a href="/collections/collection?id=<c:out value="${result.id}" />">
											</c:otherwise>
										</c:choose>
											<c:out value="${result.title}" escapeXml="false" />
										</a>
										<c:if test="${result.dateExpression != null}">
											<c:out value="${result.dateExpression}" />
										</c:if>																	
									</div>									
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
	
				<table class="archive-results-paging">
					<tbody><tr>
						<td></td>
						<td class="tools_right">
							<c:forEach items="${resultset.paging}" var="page" varStatus="pstatus">
								<c:choose>
									<c:when test="${page.name == currentPage}">
										<b><c:out value="${page.name}" /></b>
									</c:when>
									<c:otherwise>
										<a href="<c:out value="${baseUrl}" />?<c:out value="${page.query}" />"><c:out value="${page.name}" /></a>
									</c:otherwise>
								</c:choose>
							</c:forEach>	
						</td>
					</tr></tbody>
				</table>
			</td>		
		</tr>
	</table>
</div>