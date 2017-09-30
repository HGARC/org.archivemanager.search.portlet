<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<%
String rootUrl = themeDisplay.getURLCurrent();
if(rootUrl.contains("?")) rootUrl = rootUrl.substring(0, rootUrl.indexOf("?"));
String query = (String)renderRequest.getAttribute("query");
String button = (String)renderRequest.getAttribute("button");
%>

<script type="text/javascript">
function showDetail(id) {
	if($('#'+id).is(":visible")) {
		$('#'+id).hide(400);
		$('#detail'+id).html('<img src="<%= themeDisplay.getPathThemeImages()%>/arrow_closed.gif" />');
	} else {
		$('#'+id).show(400);
		$('#detail'+id).html('<img src="<%= themeDisplay.getPathThemeImages()%>/arrow_open.gif" />');
	}
}
function processSearchForm(form,event) {
	var newQuery = form.query.value;
	if(form.sis.checked) {
		var q = form.query.value;
		newQuery = '<%= query%>/'+q;
	}
	document.location = '<%= rootUrl%>/'+newQuery;
	return false;
}
</script>