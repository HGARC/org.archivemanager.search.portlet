<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<div id="loadingWrapper">
	<div id="loading">
	    <div class="loadingIndicator">
        	<img src="/theme/images/logo/ArchiveManager200.png" style="margin-right:8px;float:left;vertical-align:top;"/>
	        <div id="versionMsg">Data Manager 1.0</div>
	        <div id="loadingMsg">Loading styles and images...</div>
	    </div>
	</div>
</div>

<%@include file="smartclient.jspf" %>

<script>
	var mapping_processors = {'none':'Not Included','text':'Text/String','number':'Number','relation':'Relationship'};
	var file_formats = {'spreadsheet':'Spreadsheet','oaxml':'OpenAppsXML'}
	
	var service_path = '${serviceUrl}';
	
	var user = {'username':'${openapps_user.username}','fullname':'${openapps_user.firstName} ${openapps_user.lastName}'};
	var user_roles = ${roles};
	
	var height = '850px';
	var width = '1078px';
</script>

<div class="sc">
	<div id="gwt" style=""width:1080px;height:850px;"></div>
	<script type="text/javascript" language="javascript" src="/archivemanager-search-portlet/js/DataManager/DataManager.nocache.js"></script>
</div>