<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@page contentType="text/html;charset=UTF-8"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />
<%
String themeImagePath = themeDisplay.getPathThemeImages();
%>	
	<section>
      
      <div id="search-results">
      	<c:if test="${not empty resultset.results}">
	       <h3>Organized Results</h3>
	       <table class="search-tools">
				<tbody>
					<tr>
						<td>
							<%@ include file="include/results_header.jsp" %>
						</td>												
					</tr>
				</tbody>
		   </table>
	       <table id="search-result-table">	       	  
	          <tr><th>Date</th><th>Author</th><th>Title / Description</th><th>Partner</th></tr>
	          <c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
	          	<tr>
		            <td class="search-result date"><c:out value="${result.dateExpression}" /></td>
		            <td class="search-result author"><c:out value="${result.authors}" /></td>
		            <td class="search-result detail">
		            	<div class="title">
			            	<a class="title" href="<c:out value="${detailPage}" />?id=<c:out value="${result.id}" />">
			            		<c:out value="${result.title}" escapeXml="false" />
			            	</a>
			            <div>
			            <div class="description">
		            		<c:out value="${result.description}" escapeXml="false" />
		            	</div>
		            </td>
		            <td class="search-result partner">
		            	<img src="<%=themeImagePath %>/collections/<c:out value="${result.collectionId}" />.jpg" />
		            </td>
	          	</tr>          
	          </c:forEach>          
	        </table>
	        <ul class="search-result-nav">
	        	<c:forEach items="${resultset.paging}" var="page" varStatus="pstatus">
					<c:choose>
						<c:when test="${page.name == currentPage}">
							<li><b><c:out value="${page.name}" /></b></li>
						</c:when>
						<c:otherwise>
							<li><a href="<c:out value="${baseUrl}" />?<c:out value="${page.query}" />"><c:out value="${page.name}" /></a></li>
						</c:otherwise>
					</c:choose>
				</c:forEach>
	        </ul>
	     </c:if>
	     <c:if test="${empty resultset.results}">
	     	<div class="message">Sorry, no results were returned.</div>
	     </c:if>
      </div>
      <div class="clearfix"></div>
    </section>