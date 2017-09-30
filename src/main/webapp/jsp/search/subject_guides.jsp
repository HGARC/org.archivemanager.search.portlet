<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />
<style>
.aui a {
    text-decoration: underline;
}
</style>
<script>
function showDetail(id) {
	if($('#'+id).is(":visible")) {
		$('#'+id).hide(400);
		$('#detail'+id).html('<img src="../images/arrow_closed.gif" />');
	} else {
		$('#'+id).show(400);
		$('#detail'+id).html('<img src="../images/arrow_open.gif" />');
	}
}
</script>
<c:if test="${!empty collections}">
	<div class="collections">	
		<div style="font-size:20px;font-weight:bold;margin:10px 0;"><c:out value="${subject.title}" escapeXml="false" /></div>
		<div style="margin:12px 0;"><c:out value="${subject.description}" escapeXml="false" /></div>
		<c:forEach items="${collections}" var="collection" varStatus="cstatus">
			<div>				
				<div style=""><c:out value="${collection.abstractNote}" escapeXml="false" /></div>
			</div>
			<div style="margin-bottom:15px;">
				<c:if test="${fn:length(collection.subjects) gt 1}">
					<!-- div style="margin-left:15px;float:left;">Subject Guides:</div-->
					<div style="margin-left:25px;">
						<ul style="">	
						<c:forEach items="${collection.subjects}" var="subj" varStatus="nstatus">
							<c:if test="${subj.source == 'Subject Guide' && subj.title != subject.title}">
								<li><a href="?id=<c:out value="${subj.id}" />"><c:out value="${subj.title}" escapeXml="false" /></a></li>
							</c:if>
						</c:forEach>
						</ul>
					</div>
				</c:if>
			</div>
		</c:forEach>	
	</div>
</c:if>

<c:if test="${!empty resultset}">
	<div class="journal-content-article"> 
		<h2>ARCHIVAL HOLDINGS &nbsp; | &nbsp; SUBJECT GUIDES</h2> 
		<p>To help you find what you're looking for, we have created several subject guides. Each guide is a group of collections and material arranged by topic. A list of subject guides is provided below. Please note that this list, as well as the content of the guides, is revised on a regular basis.</p> 
		<p><span lang="en-US"><font size="3" face="Times New Roman,serif"><span style="font-size:12pt;background-color:#FBFBFB;"><font size="2" face="Arial,sans-serif" color="#333333"><span style="font-size:11pt;"><b>Click on a SUBJECT GUIDE Title below for more details:</b></span></font></span></font></span></p> 
		<p>List of subject guides:</p> 
	</div>
	<ul style="">	
		<c:forEach items="${resultset.results}" var="result" varStatus="cstatus">
			<c:choose>
				<c:when test="${fn:contains(result.name, ' -- ')}">
					<c:if test="${fn:length(heading) == 0}">
						<li style="padding-bottom:5px;text-decoration:underline;cursor:pointer" onclick="showDetail('${cstatus.index}')"><c:out value="${fn:substring(result.name, 0, fn:indexOf(result.name, ' -- '))}" escapeXml="false" /></li>
	    				<ul id="${cstatus.index}" style="display: none;">
	    			</c:if>					
					<c:set var="heading" scope="session" value="${fn:substring(result.name, 0, fn:indexOf(result.name, ' -- '))}"/>
					<c:set var="entry" scope="session" value="${fn:substring(result.name, fn:indexOf(result.name, ' -- ')+3, fn:length(result.name))}"/>
				</c:when>
	    		<c:otherwise>
	    			<c:if test="${fn:length(heading) > 0}">
	    				</ul>
	    			</c:if>
	        		<c:set var="heading" scope="session" value=""/>
	        		<c:set var="entry" scope="session" value="${result.name}"/>
	    		</c:otherwise>
	    	</c:choose>
			<li style="padding-bottom:5px;"><a href="?id=<c:out value="${result.id}" />"><c:out value="${entry}" escapeXml="false" /></a></li>			
		</c:forEach>	
	</ul>
</c:if>