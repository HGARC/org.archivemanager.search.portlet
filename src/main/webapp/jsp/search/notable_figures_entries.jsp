<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<jsp:include page="include/header.jsp" />

<%
String header = (String)renderRequest.getAttribute("header");
if(header == null) header = "";
%>

<script type="text/javascript" src="<%= themeDisplay.getPathThemeJavaScript()%>/accordionview/utilities.js"></script>

<div class="search-header" style="float:left;width:100%;">
	<%=header %>
</div>
<div id="notable-figure-entry-search-results">
	<table style="width:100%;">
		<tr>
			<td>
				<table class="search-message">
					<tbody>					
						<tr>
							<td>
								<%@ include file="include/results_header.jsp" %>
							</td>					
						</tr>					
					</tbody>
				</table>
	
				<table id="search-table">
					<tbody>
						<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
						<tr>						
							<td style="padding-top:5px;padding-left:8px;">
								<div class="entry-name">
									<a class="detail-title" href="<c:out value="${resultset.baseUrl}" />/collections/notable-figure?id=<c:out value="${result.namedEntity}" />">
										<c:out value="${result.title}" escapeXml="false" />
									</a>
								</div>
								<div class="entry-collection-name">
									(<c:out value="${result.collection}" escapeXml="false" />)
								</div>								
							</td>
						</tr>
						<tr>
							<td>
								<div class="entry-description">
									<c:out value="${result.description}" escapeXml="false" />
								</div>
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
	</table>
</div>
