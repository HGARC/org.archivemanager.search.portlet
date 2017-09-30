<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<style>
.jscarousal-horizontal {width:743px;height:165px;background-color:#121212;border:solid 1px #7A7677;margin:0;padding:0;padding:10px 8px 25px 9px;position:relative;}
.jscarousal-horizontal-back, .jscarousal-horizontal-forward{float:left;width:23px;height:98px;background-color:#121212;color:White;position:relative;top:35px;cursor:pointer;}
.jscarousal-horizontal-back{background-image:url(/archivemanager-search-portlet/css/left_arrow.jpg);background-repeat:no-repeat;background-position:left;}
.jscarousal-horizontal-forward{background-image: url(/archivemanager-search-portlet/css/right_arrow.jpg);background-repeat:no-repeat;background-position:right;}
.jscarousal-contents-horizontal{width:675px;height:124px;float:left;position:relative;overflow:hidden;}
.jscarousal-contents-horizontal > div{position:absolute;width:100%;height:124px;}
.jscarousal-contents-horizontal > div > div{float:left;margin-left:8px;margin-right:8px;}
.jscarousal-contents-horizontal img{width:120px;height:140px;border:solid 1px #7A7677;}
.jscarousal-vertical{width:140px;height:460px;background-color:#121212;border:solid 1px #7A7677;margin:0;padding:0;position:relative;overflow:hidden;}
.jscarousal-vertical-back, .jscarousal-vertical-forward{width:100%;height:30px;background-color:#121212;color:White;position:relative;cursor:pointer;z-index:100;}
.jscarousal-vertical-back{background-image:url(top_arrow.jpg);background-repeat: no-repeat;background-position: bottom;}
.jscarousal-vertical-forward{background-image: url(bottom_arrow.jpg);background-repeat: no-repeat;background-position: top;}
.jscarousal-contents-vertical{overflow: hidden;width: 140px;height: 410px;}
.jscarousal-contents-vertical > div{position: absolute;top: 40px;width: 100%;height: 820px;overflow: hidden;}
.jscarousal-contents-vertical > div > div{width: 140px;height: 125px;margin: 8px;margin-left: 14px;}
.jscarousal-contents-vertical > div > div span{display: block;width: 70%;text-align: center;}
.jscarousal-contents-vertical img{width:120px;height:140px;border: solid 1px #7A7677;}
.hidden{display: none;}
.visible{display: block;}
.thumbnail-active{filter: alpha(opacity=100);opacity: 1.0;cursor: pointer;}
.thumbnail-inactive{filter: alpha(opacity=20);opacity: 0.2;cursor: pointer;}
.thumbnail-text{color: #7A7677;font-weight: bold;text-align: left;display: block;text-wrap:normal;width:120px;padding: 10px 2px 2px 0px;text-align:center;}
</style>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/archivemanager-search-portlet/js/jsCarousel-2.0.0.js"></script>
<script type="text/javascript" src="/archivemanager-search-portlet/js/tree.jquery.js"></script>
<link rel="stylesheet" href="/archivemanager-search-portlet/css/jqtree.css" />
<script>
var data = [
<c:forEach items="${collections}" var="collection" varStatus="cstatus">
	{
        id:'<c:out value="${collection.id}" />', label: '<c:out value="${collection.title}" />',
        children: [
        	<c:forEach items="${collection.series}" var="series" varStatus="sstatus">
        		{id:'<c:out value="${series.id}" />', label: '<c:out value="${series.title}" />', "load_on_demand": true},        	
        	</c:forEach>
        ]
   	},
</c:forEach>
];
function zoomify(title, desc, path) {

}
$(document).ready(function() {	    
    $('#navigation-tree').tree({
        data: data,
        autoOpen:0
    });
    $('#navigation-tree').bind(
    	'tree.click',
    	function(event) {
        	// The clicked node is 'event.node'
        	var node = event.node;
        	
        	var flickerAPI = "/delegate/service/archivemanager/collection/documents/fetch.json?id="+node.id;
		  	$.getJSON( flickerAPI, {
		    	format: "json"
		  	}).done(function( data ) {
		  		var html = '<div class="jscarousal-horizontal" id="jsCarousel">';
		    	$.each( data, function( i, item ) {		        	
		        	html += '<div style="cursor: pointer;float:right;"><img height="140" width="120" title="'+item.label+'" desc="'+item.path+'" path="'+item.id+'" src="/delegate/service/archivemanager/content/stream/'+item.id+'" alt="" /></div>';		        	
		      	});		      	
				html += '</div>';
				$('#scroller-navigation').html(html);
				$('#jsCarousel').jsCarousel({ 
				   	onthumbnailclick: function(title, desc, path) {
				   		$('#img_viewer_image').attr("src", '/delegate/service/archivemanager/content/stream/' + path);
				   		$('#image_download').attr("href", '/delegate/service/archivemanager/content/stream/original/' + path);
				   		$('#image_download').css("visibility", "visible");
				   		//$('#image_request').attr("href", "mailto:archives@bu.edu?subject=Bay State Banner Request");
				   		//$('#image_request').css("visibility", "visible");
				   		$('#image_title').html(title);
				   		$('#image_title').css("visibility", "visible");
				   	}, 
				   	autoscroll: false,
				   	masked: false
				});
		    });
    	}
	);
});
</script>
<%
String breadcrumb = "";
%>

<div id="sub-section2" class="yui-skin-sam" style="margin:10px;border-left:0px;">
	<table style="width:100%;">
		<tr>
			<td style="width:300px;"><div id="navigation-tree" data-url="/delegate/service/archivemanager/collection/categories/fetch.json"></div></td>
			<td>
				<div style="width:760px;;height:35px;background-color:#121212;">
					<div id="image_title" style="width:600px;float:left;color:#FFFFFF;font-weight:bold;text-align:left;visibility:hidden;"></div>
					<a id="image_download" target="_blank" style="float:right;color:#FFFFFF;text-align:right;font-weight:bold;padding:0 5px;visibility:hidden;">Download</a>
					<!--a id="image_request" style="float:right;color:#FFFFFF;text-align:right;font-weight:bold;padding:0 5px;visibility:hidden;">Request</a-->
				</div>
				<div style="width:760px;;height:100%;padding-bottom:10px;color:#FFFFFF;background-color:#121212;">					
					<div id="img_viewer" style="height:700px;width:680px;margin:0 40px;">
						<img id="img_viewer_image" src="/hgarc-main-theme/images/hgarc/common/image-placeholder.png" style="display:block;margin-left:auto;margin-right:auto;" />
					</div>
				</div>
				<div id="scroller-navigation">
					<div class="jscarousal-horizontal" id="jsCarousel"></div>
				</div>				
			</td>
		</tr>
	</table>
</div>