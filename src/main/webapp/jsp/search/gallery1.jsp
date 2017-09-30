<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/archivemanager-search-portlet/js/jsCarousel-2.0.0.js"></script>
<link href="/archivemanager-search-portlet/css/jsCarousel-2.0.0.css" rel="stylesheet" type="text/css" /> 
<script>
function zoomify(title, desc, path) {
	//Z.showImage("img_viewer", path);
	var flashObj = '<object style="display:block;" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=9,0,28,0" width="680" height="400" id="ZoomifyFullScreenViewer">';
	flashObj += '<param name="flashvars" value="zoomifyImagePath=http://hgar-pub1.bu.edu'+path+'&zoomifyToolbarLogo=0&zoomifySplashScreen=0">';
	flashObj += '<param name="menu" value="false">';
	flashObj += '<param name="allowFullScreen" value="true" />';
	flashObj += '<param name="src" value="/media/ZoomifyFullScreenViewer.swf">';
	flashObj += '<embed flashvars="zoomifyImagePath=http://hgar-pub1.bu.edu'+path+'&zoomifyToolbarLogo=0&zoomifySplashScreen=0" src="/media/ZoomifyFullScreenViewer.swf" menu="false" allowFullScreen="true" pluginspage="http://www.adobe.com/go/getflashplayer" type="application/x-shockwave-flash" width="680" height="400" name="ZoomifyFullScreenViewer"></embed>';
	flashObj += '</object>';
	$('#img_viewer').html(flashObj);
	$('#img_name').html(title); 
	$('#img_desc').html(desc); 
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
<%
String breadcrumb = "";
%>

<div id="sub-section2" class="yui-skin-sam" style="margin:10px;border-left:0px;">
	<table style="width:100%;">
		<tr>
			<td>
				<div style="width:740px;;height:100%;padding-bottom:10px;color:#FFFFFF;background-color:#121212;border:1px solid #7A7677;">
					<div id="breadcrumb" style="height:10px;width:680px;margin:10px;">
						<c:if test="${not empty query}">
							<%=breadcrumb %>
						</c:if>
					</div>
					<div id="img_viewer" style="height:400px;width:680px;margin:15px 40px;border:1px solid #7A7677;"></div>
					<div id="img_name" style="padding-left:40px;padding-bottom:10px;width:100%;font-weight:bold;text-transform:uppercase;"></div>
					<div id="img_desc" style="padding-left:40px;padding-right:40px;text-align:justify;"></div>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="jscarousal-horizontal" id="jsCarousel">
					<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
						<c:forEach items="${result.weblinks}" var="link" varStatus="lstatus">
							<c:if test="${link.type == 'zoomify'}">
								<div style="cursor: pointer;float:right;">
                    				<img height="90" width="120" title="<c:out value="${result.title}" />" desc="<c:out value="${result.description}" />" path="<c:out value="${link.url}" />" src="<c:out value="${link.url}" />/TileGroup0/0-0-0.jpg" alt="" />
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