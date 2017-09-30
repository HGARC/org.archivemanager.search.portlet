<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />
<%
String themeImagePath = themeDisplay.getPathThemeImages();
%>
<c:if test="${result.contentType == 'video'}">
	<link href="http://vjs.zencdn.net/c/video-js.css" rel="stylesheet">
	<script src="http://vjs.zencdn.net/c/video.js"></script>
</c:if>
<style>
	.aui table {
	    background-color: transparent;
	    border-collapse: collapse;
	    border-spacing: 0;
	    width: 100%;
	}
	#wrapper #content .collection-detail .collection-detail-left {
    	min-height: 200px;
    	width: 50%;
    	float:left;
	}
	#wrapper #content .collection-detail .collection-detail-right {
    	min-height: 200px;
    	width: 50%;
    	float:left;
	}
	#wrapper #content .collection-detail .collection-detail-left .collection-detail-value {
	    float: left;
	    font-size: 12px;
	    min-height: 25px;
	    padding-bottom: 5px;
	    text-align: justify;
	    width: 85%;
	}
	#wrapper #content .collection-detail .collection-detail-left .collection-detail-label {
	    float: left;
	    font-size: 13px;
	    font-weight: bold;
	    padding-bottom: 10px;
	    vertical-align: top;
	    width: 100px;
	}
</style>
<div class="collection-detail">
	<div class="collection-detail-left">
		<c:if test="${result.title != null}">
			<div class="collection-detail-title">
				<c:out value="${result.title}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:if test="${not empty result.nativeContent}">
			<div class="collection-native-value">
				<c:out value="${result.nativeContent}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:if test="${result.collectionName != null && displayCollectionName != 'false'}">
			<div>
				<div class="collection-detail-label" style="">Collection:</div>
				<div class="collection-detail-value">
					<c:out value="${result.collectionName}" escapeXml="false" />
				</div>
			</div>
		</c:if>
			
		<c:if test="${missingImage != null && !(result.description == null && displayDescription != 'false') && !(result.summary != null && displaySummary != 'false')}">
			<div style="width:100%;text-align:center;padding:40px 0;">
				<img src="<c:out value="${missingImage}" />" />
			</div>
		</c:if>		
		
		<c:if test="${result.dateExpression != null && displayDateExpression != 'false'}">
			<div class="collection-detail-label" style="">Date:</div>
			<div class="collection-detail-value">
				<c:out value="${result.dateExpression}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:if test="${result.contentType != null && displayContentType != 'false'}">
			<div class="collection-detail-label" style="">Content Type:</div>
			<div class="collection-detail-value">
				<c:out value="${result.contentType}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:if test="${result.language != null && displayLanguage != 'false'}">
			<div class="collection-detail-label" style="">Language:</div>
			<div class="collection-detail-value">
				<c:out value="${result.language}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:if test="${result.container != null && displayContainer != 'false'}">
		<div>
			<div class="collection-detail-label" style="">Container:</div>
			<div class="collection-detail-value">
				<c:out value="${result.container}" escapeXml="false" />
			</div>
		</div>
		</c:if>
		
		<c:if test="${result.description != null && displayDescription != 'false'}">
			<div>
				<div class="collection-detail-label" style="">Description:</div>
				<div class="collection-detail-value">
					<c:out value="${result.description}" escapeXml="false" />
				</div>
			</div>
		</c:if>
		
		<c:if test="${result.summary != null && displaySummary != 'false'}">
			<div>
				<div class="collection-detail-label" style="">Summary:</div>
				<div class="collection-detail-value">
					<c:out value="${result.summary}" escapeXml="false" />
				</div>
			</div>
		</c:if>
		
		<c:if test="${not empty result.notes}">
			<table>				
				<c:forEach items="${result.notes}" var="note" varStatus="nstatus">
					<tr><td class="collection-detail-label" style="width:100%;margin-top:15px;"><c:out value="${note.type}" />:</td></tr>
					<tr><td class="relation subject" style="padding:0 15px;">
						<span class="collection-detail-value"><c:out value="${note.content}" escapeXml="false" /></span>
					</td></tr>
				</c:forEach>
			</table>
		</c:if>
		<c:if test="${not empty result.people}">
			<table>
			<tr><td class="collection-detail-label" style="width:100%;margin-top:15px;">Associated Personal Entities:</td></tr>
			<c:forEach items="${result.people}" var="name" varStatus="nstatus">	
				<tr><td class="relation person" style="padding:0 15px;">
					<span class="collection-detail-value">
						<c:out value="${name.title}" /> 
						<c:if test="${name.function != null}">
							(<b><c:out value="${name.function}" /></b>)
						</c:if>
					</span>
				</td></tr>
			</c:forEach>
			</table>
		</c:if>
		<c:if test="${not empty result.corporations}">
			<table>
			<tr><td class="collection-detail-label" style="width:100%;margin-top:15px;">Associated Corporate Entities:</td></tr>
			<c:forEach items="${result.corporations}" var="name" varStatus="nstatus">	
				<tr><td class="relation corporation" style="padding:0 15px;">
					<span class="collection-detail-value">
						<c:out value="${name.title}" /> 
						<c:if test="${name.function != null}">
							(<b><c:out value="${name.function}" /></b>)
						</c:if>
					</span>
				</td></tr>
			</c:forEach>
			</table>
		</c:if>
		<c:if test="${not empty result.subjects}">
			<table>
			<tr><td class="collection-detail-label" style="width:100%;margin-top:15px;">Associated Subjects:</td></tr>
			<c:forEach items="${result.subjects}" var="subject" varStatus="nstatus">
				<tr><td class="relation subject" style="padding:0 15px;">
					<span class="collection-detail-value"><c:out value="${subject.title}" /></span>
				</td></tr>
			</c:forEach>
			</table>
		</c:if>
		<c:if test="${not empty result.path}">
			<table>
				<tr><td class="collection-detail-label" style="width:100%;margin-top:15px;">Keywords:</td></tr>
				<c:forEach items="${result.path}" var="node" varStatus="nstatus">
					<tr><td class="relation subject" style="padding:0 15px;">
						<span class="collection-detail-value"><c:out value="${node.title}" escapeXml="false" /></span>
					</td></tr>
				</c:forEach>
			</table>
		</c:if>
	</div>
	<div class="collection-detail-right">
	<c:if test="${result.contentType != null && result.contentType != 'Video' && result.contentType != 'Audio'}">
		<div id="flippingbook-container">
      	<c:set var="linked" scope="session" value="false"/>
      	<c:if test="${not empty result.weblinks}">
			<c:forEach items="${result.weblinks}" var="file" varStatus="nstatus">
				<c:if test="${file.type == 'flippingbook'}">
					<c:set var="linked" scope="session" value="true"/>
					<iframe style="border:0px;width:490px;height:700px;overfow:hidden;" scrolling="no" src="<c:out value="${file.url}" />"></iframe>
				</c:if>
				<c:if test="${file.type == 'rendition'}">
					<c:set var="linked" scope="session" value="true"/>
					<img style="border:0px;width:490px;" src="<c:out value="${file.url}" />" />
				</c:if>
				<c:if test="${file.type == 'partner'}">
					<c:set var="linked" scope="session" value="true"/>
					<a href="<c:out value="${file.url}" />" target="_blank">
						<img src="<%=themeImagePath %>/collections/<c:out value="${result.collectionId}" />_large.jpg" alt="<c:out value="${result.collectionName}" escapeXml="false" />" />
					</a>
				</c:if>
			</c:forEach>
		</c:if>
      	<c:if test="${not linked}">
      		<c:if test="${not empty result.collectionUrl}">
	      		<a href="<c:out value="${result.collectionUrl}" />" target="_blank">
	      			<img src="<%=themeImagePath %>/collections/<c:out value="${result.collectionId}" />_large.jpg" alt="<c:out value="${result.collectionName}" escapeXml="false" />" />
	      		</a>
	      	</c:if>
	      	<c:if test="${empty result.collectionUrl}">
	      		<img src="<%=themeImagePath %>/collections/<c:out value="${result.collectionId}" />_large.jpg" alt="<c:out value="${result.collectionName}" escapeXml="false" />" />
	      	</c:if>
      	</c:if>
      </div>
	</c:if>
	</div>
</div>
