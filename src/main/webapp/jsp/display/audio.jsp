<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<jsp:include page="include/header.jsp" />
<link href="http://vjs.zencdn.net/c/video-js.css" rel="stylesheet">
<script src="http://vjs.zencdn.net/c/video.js"></script>

<table class="audio-result">
	<tbody>
		<tr>
			<td style="width:50%">
				<%@ include file="item.jsp" %>
			</td>
			<td class="right">
				<c:if test="${not empty result.digitalObjects}">
					<c:forEach items="${result.digitalObjects}" var="file" varStatus="nstatus">
						<c:if test="${file.audio}">
							<div class="audio-container">
								<c:if test="${not empty result.avatar}">
									<img class="avatar" src="<c:out value="${result.avatar}" />" />
								</c:if>
								<c:if test="${empty result.avatar}">
									<img class="avatar" src="/hgarc-main-theme/images/hgarc/common/video-placeholder.png" />
								</c:if>
								<audio class="player" src="/openapps/media/stream/<c:out value="${file.id}" />" type="audio/mp3" controls="controls"></audio> 
							</div>
						</c:if>
					</c:forEach>
				</c:if>
				<c:if test="${not empty result.weblinks}">
					<c:forEach items="${result.weblinks}" var="file" varStatus="nstatus">
						<c:if test="${file.type == 'rendition'}">
							<div class="audio-container">
								<c:if test="${not empty result.avatar}">
									<img class="avatar" src="<c:out value="${result.avatar}" />" />
								</c:if>
								<c:if test="${empty result.avatar}">
									<img class="avatar" src="/hgarc-main-theme/images/hgarc/common/video-placeholder.png" />
								</c:if>
								<audio class="player" src="<c:out value="${file.url}" />" type="audio/mp3" controls="controls" preload="auto"></audio> 
							</div>
						</c:if>
					</c:forEach>
				</c:if>
								
			</td>
		</tr>
	</tbody>
</table>