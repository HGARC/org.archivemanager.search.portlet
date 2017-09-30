<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div id="search-results">
	<table style="width:100%;">
		<c:if test="${empty resultset.results}">
			<tr><td style="height:500px;"><b>No results were found, please try again</b></td></tr>
		</c:if>
		<c:if test="${!empty resultset.results}">
		<tr>
			<td>
				<table class="search-tools">
					<tbody>
						<tr>
							<td>
								<%@ include file="include/results_header.jsp" %>
							</td>
							<td class="tools-right"> 
								Show &nbsp; 
								<c:if test="${resultset.pageSize == 10}">
									<b>10</b>
								</c:if>
								<c:if test="${resultset.pageSize != 10}">
									<a href="?<c:out value="${resultset.query}" />&size=10">10</a>
								</c:if>
								&nbsp;
								<c:if test="${resultset.pageSize == 25}">
									<b>25</b>
								</c:if>
								<c:if test="${resultset.pageSize != 25}">
									<a href="?<c:out value="${resultset.query}" />&size=25">25</a>
								</c:if>
								&nbsp;
								<c:if test="${resultset.pageSize == 50}">
									<b>50</b>
								</c:if>
								<c:if test="${resultset.pageSize != 50}">
									<a href="?<c:out value="${resultset.query}" />&size=50">50</a>
								</c:if>
							</td>						
						</tr>
					</tbody>
				</table>
					
				<table class="search-table">
					<tbody>
						<tr>
							<th class="title-bar-start">Title/Description</th>
							<c:if test="${contentType == 'true'}">
								<th class="title-bar contentType">Content Type</th>
							</c:if>
							<c:if test="${language == 'true'}">
								<th class="title-bar language">Language</th>
							</c:if>
							<c:if test="${dateExpression == 'true'}">
								<th class="title-bar dateExpression">Date</th>
							</c:if>							
							<c:if test="${collection == 'true'}">
								<th class="title-bar collection">Collection</th>
							</c:if>							
						</tr>
						<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
						<tr>						
							<td class="title-cell">
								<div>
									<a class="title" href="<c:out value="${detailPage}" />?id=<c:out value="${result.id}" />">
										<c:out value="${result.title}" escapeXml="false" />
									</a>
								</div>
								<c:if test="${description == 'true'}">
								<div class="description-cell">
									<c:out value="${result.description}" escapeXml="false" />
								</div>
								</c:if>
								<c:if test="${summary == 'true'}">
								<div class="description-cell">
									<c:out value="${result.summary}" escapeXml="false" />
								</div>
								</c:if>
							</td>
							<c:if test="${contentType == 'true'}">
								<td class="content-cell">
									<liferay-ui:message key="${result.contentType}" />
								</td>
							</c:if>
							<c:if test="${language == 'true'}">
								<td class="language-cell">
									<liferay-ui:message key="${result.language}" />
								</td>
							</c:if>							
							<c:if test="${dateExpression == 'true'}">
								<td class="date-cell">
									<a class="title" href="<c:out value="${detailPage}" />?id=<c:out value="${result.id}" />">
										<c:out value="${result.dateExpression}" />
									</a>
								</td>
							</c:if>
							<c:if test="${collection == 'true'}">
								<td class="collection-cell">
									<c:out value="${result.collectionName}" escapeXml="false" />
								</td>
							</c:if>							
						</tr>
						<tr>
							<td colspan="4">
								<div class="search-line-border"></div>
							</td>
						</tr>
						</c:forEach>
					</tbody>
				</table>
	
				<table class="search-paging">
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
		</c:if>
	</table>
</div>