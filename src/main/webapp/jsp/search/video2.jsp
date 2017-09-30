<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/archivemanager-search-portlet/js/video.js"></script>
<link href="/archivemanager-search-portlet/css/video.css" rel="stylesheet" type="text/css" /> 
<script>

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
<%
String breadcrumb = "";
%>

<div id="sub-section2" class="yui-skin-sam" style="margin:10px;border-left:0px;">
	<table style="width:100%;">
		<tr>
			<td>
				<div class="jscarousal-horizontal" id="jsCarousel">
					<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
						<c:forEach items="${result.weblinks}" var="link" varStatus="lstatus">
							<c:if test="${link.type == 'zoomify'}">
								<div style="cursor: pointer;float:right;width:31%;height:31%;border: 1px solid #7A7677;margin-bottom: 8px;">
                    				<div class="thumbnail-image">
                    					<img height="125" width="250" title="<c:out value="${result.title}" />" desc="<c:out value="${result.description}" />" path="<c:out value="${link.url}" />" src="<c:out value="${link.url}" />/TileGroup0/0-0-0.jpg" alt="" />
                        			</div>
                        			<div class="thumbnail-text">
                        				<c:choose>
                        					<c:when test="${not empty link.title}">
                        						<c:out value="${link.title}" />
                        					</c:when>
                        					<c:otherwise>
                        						<c:out value="${result.title}" />
                        					</c:otherwise>
                        				</c:choose>
                        			</div>
                    			</div>
                    		</c:if>
                    	</c:forEach>					
					</c:forEach>
            	</div>
			</td>
		</tr>
	</table>
</div>