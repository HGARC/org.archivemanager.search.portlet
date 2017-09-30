<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/archivemanager-search-portlet/js/video.js"></script>
<link href="/archivemanager-search-portlet/css/video.css" rel="stylesheet" type="text/css" /> 
<script>
function video_search(query) {
	document.location = '/videos?query='+query;
}
$(document).ready(function() {
    $('#jsCarousel').jsCarousel({ 
    	onthumbnailclick: function(title, desc, path) {
    		zoomify(title, desc, path);
    	}, 
    	autoscroll: false,
    	masked: false
    });      
});
</script>

<div id="video-search">
	<table style="width:100%;">
		<tr>			
			<td id="video-results">
				<div class="message">
					<%@ include file="include/results_header.jsp" %>
				</div>
				<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">					
					<c:forEach items="${result.weblinks}" var="link" varStatus="lstatus">
						<c:if test="${link.type == 'avatar'}">
							<c:set var="avatar" scope="session" value="${link.url}"/>
						</c:if>
					</c:forEach>
					<div class="video-entry">
						<div class="video-image">
							<c:if test="${not empty avatar}">
								<img src="<c:out value="${avatar}" />" />
							</c:if>
							<c:if test="${empty avatar}">
								<img src="/hgarc-main-theme/images/hgarc/common/video-placeholder.png" />
							</c:if>
						</div>
						<div class="video-date"><c:out value="${result.dateExpression}" /></div>
						<div class="video-title">
							<a href="<c:out value="${detailPage}" />?id=<c:out value="${result.id}" />">
								<c:out value="${result.title}" escapeXml="false" />
							</a>
						</div>
						<div class="video-descr"><c:out value="${result.description}" escapeXml="false" /></div>
					</div>				
				</c:forEach>
				<c:if test="${empty resultset.results}">
					<div class="no-results-message">Sorry, no results found</div>
				</c:if>
			</td>
		</tr>
		<tr>
			<td id="video-paging">
				<c:forEach items="${resultset.paging}" var="page" varStatus="pstatus">
					<a href="<c:out value="${baseUrl}" />?<c:out value="${page.query}" />"><c:out value="${page.name}" /></a>
				</c:forEach>	
			</td>
		</tr>
	</table>
</div>