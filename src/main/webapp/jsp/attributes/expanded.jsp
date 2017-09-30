<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<dl class="accordion">
	<c:forEach items="${resultset.attributes}" var="attribute" varStatus="astatus">
		<c:if test="${attribute.display}">
			<dt>
				<a id="mymenu-1-label" href="#toggle" tabindex="0" class="yui-accordion-toggle" role="treeitem">
					<c:out value="${attribute.name}" escapeXml="false"/> (<c:out value="${attribute.count}" />)<span class="indicator"></span>
				</a>
			</dt>
			<dd>
				<ul class="submenu">
				<c:forEach items="${attribute.values}" var="value" varStatus="vstatus">
					<li><a href="<c:out value="${baseUrl}" /><c:out value="${resultset.query}" escapeXml="false"/> <c:out value="${value.query}" escapeXml="false"/>"><c:out value="${value.name}" escapeXml="false"/> (<c:out value="${value.count}" />)</a></li>
				</c:forEach>
				</ul>
			</dd>
		</c:if>
	</c:forEach>
</dl>

<script type="text/javascript">
	(function($) {
    	var allPanels = $('.accordion > dd').hide();
    
	  	$('.accordion > dt > a').click(function() {
	    	allPanels.slideUp();
	    	if(!$(this).parent().next().is(':visible'))
	    		$(this).parent().next().slideDown();
	    	return false;
	  	});
	})(jQuery);
</script>