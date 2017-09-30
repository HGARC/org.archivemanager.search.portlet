<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>
<%@ taglib uri="http://liferay.com/tld/security" prefix="liferay-security" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/util" prefix="liferay-util" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@page import="com.liferay.portal.kernel.portlet.LiferayWindowState"%>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<style>
	.am-toolbar{margin:10px;padding:10px;border:1px solid #EEEEEE;;width:90%;}
</style>

<div class="am-toolbar">		
	<aui:button name="editor" id="editor" value="edit"></aui:button>
	<aui:button name="content" id="content" value="content"></aui:button>
</div>

<aui:script>
	AUI().use('aui-base','aui-io-plugin-deprecated','liferay-util-window','liferay-portlet-url','aui-dialog-iframe-deprecated',
	function(A) {
		A.one('#<portlet:namespace />editor').on('click',
			function(event) {
				var url =Liferay.PortletURL.createRenderURL();
				url.setPortletId("collections_WAR_archivemanagerappsportlet")  
				url.setWindowState('pop_up');
				var popUpWindow=Liferay.Util.Window.getWindow(
				{
					dialog: {
						cache: true,
						centered: true,
						constrain2view: true,
						modal: true,
						resizable: false,
						width: 1230,
						height:760
					}
				}
			).plug(A.Plugin.DialogIframe,
				{
					autoLoad: false,
					iframeCssClass: 'dialog-iframe',
					uri:url.toString() + '&entityId=<c:out value="${entityId}" />'
				}
			).render();
			popUpWindow.show();
		});
		A.one('#<portlet:namespace />content').on('click',
			function(event) {
				var url =Liferay.PortletURL.createRenderURL();
				url.setPortletId("content_WAR_archivemanagerappsportlet")  
				url.setWindowState('pop_up');
				var popUpWindow=Liferay.Util.Window.getWindow(
				{
					dialog: {
						cache: true,
						centered: true,
						constrain2view: true,
						modal: true,
						resizable: false,
						width: 1230,
						height:760
					}
				}
			).plug(A.Plugin.DialogIframe,
				{
					autoLoad: false,
					iframeCssClass: 'dialog-iframe',
					uri:url.toString()
				}
			).render();
			popUpWindow.show();
		});
	});
</aui:script>