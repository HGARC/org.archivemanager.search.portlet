<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@page contentType="text/html;charset=UTF-8"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
String themeCssPath = themeDisplay.getPathThemeCss();
String themeJavascriptPath = themeDisplay.getPathThemeJavaScript();
%>

<style type="text/css" media="screen">
@import "<%=themeCssPath %>/accordionview/assets/skins/hgarc/reset-fonts.css"; 
@import "<%=themeCssPath %>/accordionview/assets/skins/hgarc/accordionview.css";
</style>

<div id="sub-section2" class="yui-skin-sam">
	<ul id="mymenu" class="yui-accordionview" role="tree">
	<c:forEach items="${resultset.attributes}" var="attribute" varStatus="astatus">
		<c:if test="${attribute.display}">
			<li class="yui-accordion-panel" role="presentation">
				<a id="mymenu-1-label" href="#toggle" tabindex="0" class="yui-accordion-toggle" role="treeitem" style="color:#FFF;">
					<c:out value="${attribute.name}" escapeXml="false"/> (<c:out value="${attribute.count}" />)<span class="indicator"></span>
				</a>
				<div class="yui-accordion-content hidden">
					<ul class="submenu">
					<c:forEach items="${attribute.values}" var="value" varStatus="vstatus">
						<li><a href="<c:out value="${baseUrl}" /><c:out value="${resultset.query}" escapeXml="false"/> <c:out value="${value.query}" escapeXml="false"/>"><c:out value="${value.name}" escapeXml="false"/> (<c:out value="${value.count}" />)</a></li>
					</c:forEach>
					</ul>
				</div>
			</li>
		</c:if>
	</c:forEach>
	</ul>
</div>

<script type="text/javascript" src="<%=themeJavascriptPath %>/accordionview/utilities.js"></script>
<script type="text/javascript" src="<%=themeJavascriptPath %>/accordionview/accordionview.js"></script>

<script type="text/javascript">
	var menu1 = new YAHOO.widget.AccordionView('mymenu', {collapsible: true, expandable: false, width: '100%', animate: true, animationSpeed: '0.5'});		
</script>