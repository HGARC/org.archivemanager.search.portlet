<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<c:if test="${result.contentType == 'video'}">
	<link href="http://vjs.zencdn.net/c/video-js.css" rel="stylesheet">
	<script src="http://vjs.zencdn.net/c/video.js"></script>
</c:if>

<table class="video-detail">
	<tbody>
		<tr>
			<td style="width:100%">
				<table class="left">
					<tbody>
						<c:if test="${not empty result.rendition}">
							<tr><td style="padding:35px 0 25px 100px;">
							<c:if test="${not empty result.avatar}">
								<video id="my_video_1" class="video-js vjs-default-skin" controls
					  				preload="auto" width="550" height="300" poster="<c:out value="${result.avatar}" />"
					  				data-setup="{}">
					  				<source src="<c:out value="${result.rendition}" />" type='video/mp4'>
								</video>
							</c:if>
							<c:if test="${empty result.avatar}">
								<video id="my_video_1" class="video-js vjs-default-skin" controls
					  				preload="auto" width="550" height="300" poster="/hgarc-main-theme/images/hgarc/common/video-placeholder.png"
					  				data-setup="{}">
					  				<source src="<c:out value="${result.rendition}" />" type='video/mp4'>
								</video>
							</c:if>
							</td></tr>
						</c:if>	
						<tr>
							<td>
								<%@ include file="item.jsp" %>
							</td>
						</tr>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>
