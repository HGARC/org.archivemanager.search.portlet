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
								<%@ include file="include/results_header.jsp" %>
							</td>					
						</tr>					
					</tbody>
				</table>
	
				<table class="archive-results-table">
					<tbody>
						<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
							<tr>						
								<td class="archive-results-result">
									<table style="width:100%;">
										<tr>
											<td style="width:50px;">
												<div class="archive-results-contentType">
													<c:out value="${result.contentType}" escapeXml="false" />
												</div>
											</td>
											<td style="vertical-align:top;">
												<div class="archive-results-title">
													<c:if test="${result.contentType == 'collection'}">
														<c:choose>
															<c:when test="${result.url != null}">
																<a href="${result.url}">
															</c:when>
															<c:otherwise>
																<a href="/collections/collection?id=<c:out value="${result.id}" />">
															</c:otherwise>
														</c:choose>
													</c:if>	
													<c:if test="${result.contentType == 'person' || result.contentType == 'corporation'}">
														<a href="/collections/notable-figure?id=<c:out value="${result.id}" />">
													</c:if>
													<c:if test="${result.contentType == 'subject'}">
														<a href="/collections/subject?id=<c:out value="${result.id}" />">
													</c:if>
													<c:if test="${result.contentType != 'subject' && result.contentType != 'person' && result.contentType != 'corporation' && result.contentType != 'collection'}">
														<a href="/collections/item?id=<c:out value="${result.id}" />">
													</c:if>
														
													<c:choose>	
														<c:when test="${result.title != null}">
															<c:out value="${result.title}" escapeXml="false" />
														</c:when>
														<c:otherwise>
															no title
														</c:otherwise>
													</c:choose>
													</a>									
												</div>
											</td>
										</tr>
										<c:if test="${description == 'true'}">
										<tr>
											<td style="width:50px;"></td>
											<td style="vertical-align:top;">
												<div class="archive-results-description">
													<c:out value="${result.description}" />
												</div>
											</td>
										</tr>
										</c:if>
									</table>									
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