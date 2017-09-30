<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<script type="text/javascript">
function loadPage(url) {
	$.ajax({	//create an ajax request to load_page.php
		type: "GET",
	    url: url,
	    dataType: "html",	//expect html to be returned
	    success: function(msg){
	    	if(parseInt(msg)!=0) {
	        	$('#search-results').html(msg);
	        }
	    }
	});
}
</script>

<div class="my-collections">
	<c:forEach items="${entities}" var="result" varStatus="cstatus">
		<div class="collection">
			<div style="line-height:25px;margin-bottom:10px;">				
				<c:if test="${result.title != null}">
					<a onClick="loadPage('<c:out value="${baseUrl}" />?id=<c:out value="${result.id}" />')" href="#">
						<c:out value="${result.title}" escapeXml="false" />
					</a>
				</c:if>
			</div>
		</div>
	</c:forEach>	
</div>
<div id="search-results"></div>