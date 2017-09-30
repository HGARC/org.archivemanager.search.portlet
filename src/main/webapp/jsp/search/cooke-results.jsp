<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />

<jsp:include page="include/header.jsp" />

<%
String header = (String)renderRequest.getAttribute("header");
if(header == null) header = "";
%>

<script type="text/javascript" src="<%= themeDisplay.getPathThemeJavaScript()%>/accordionview/utilities.js"></script>

<div class="detail-text" style="padding:5px;"><a href=".">Back To Home</a></div>
<div class="search-header" style="float:left;width:100%;">
	<%=header %>
</div>
<div id="sub-section2" class="yui-skin-sam" style="border-left:0px;">
<table style="width:100%;">
	<tr>
		<td>
			<table class="search_tools">
				<tbody>
					<tr>
						<td style="padding: 10px 20px 0 0;">
							<p>Search Results &gt; 
								<c:forEach items="${resultset.breadcrumbs}" var="breadcrumb" varStatus="rstatus">
									<c:if test="${!rstatus.last}">
										<a href="<c:out value="${resultset.baseUrl}" />/<c:out value="${breadcrumb.query}" />"><c:out value="${breadcrumb.name}" /></a> > 
									</c:if>
									<c:if test="${rstatus.last}">
										<c:out value="${breadcrumb.name}" />
									</c:if>
								</c:forEach>
							</p>
						</td>
					</tr>
					
					<tr>
						<td>Results <b><c:out value="${resultset.start}" /> - <c:out value="${resultset.end}" /></b> of <b><c:out value="${resultset.resultCount}" /></b> for	<b><c:out value="${resultset.label}" /></b>		</td>
						<td class="tools_right"> 
							Show &nbsp; 
							<c:if test="${resultset.pageSize == 10}">
								<b>10</b>
							</c:if>
							<c:if test="${resultset.pageSize != 10}">
								<a href="<c:out value="${baseUrl}" />query=<c:out value="${resultset.query}" />&size=10">10</a>
							</c:if>
							&nbsp;
							<c:if test="${resultset.pageSize == 25}">
								<b>25</b>
							</c:if>
							<c:if test="${resultset.pageSize != 25}">
								<a href="<c:out value="${baseUrl}" />query=<c:out value="${resultset.query}" />&size=25"">25</a>
							</c:if>
							&nbsp;
							<c:if test="${resultset.pageSize == 50}">
								<b>50</b>
							</c:if>
							<c:if test="${resultset.pageSize != 50}">
								<a href="<c:out value="${baseUrl}" />query=<c:out value="${resultset.query}" />&size=50"">50</a>
							</c:if>
						</td>						
					</tr>
					
				</tbody>
			</table>

			<table id="search_table">
				<tbody>
					<tr>
						<th style="search-title-bar-start">Title/Description</th>
						<th nowrap="nowrap" class="search-title-bar">Content Type</th>
						<th nowrap="nowrap" class="search-title-bar">Date</th>
					</tr>
					<c:forEach items="${resultset.results}" var="result" varStatus="rstatus">
						<tr>						
							<td style="padding-top:5px;">
								<div><a class="detail-title" href="<c:out value="${detailPage}" />?id=<c:out value="${result.id}" />"><c:out value="${result.title}" escapeXml="false" /></a></div>
								<div style="padding:0 0 5px 15px;">
									<!--
									<c:forEach items="${result.notes}" var="note" varStatus="nstatus">
										<div id="note-0-<c:out value="${nstatus.count}" />" style="text-align:justify;"><c:out value="${note.content}" escapeXml="false" /></div>
									</c:forEach>
									-->
									<c:out value="${result.description}" />
								</div>
							</td>
							<td width="100" style="padding-top:5px;text-align:center;">
								<div><c:out value="${result.contentType}" /></div>
							</td>
							<td width="100" style="padding-top:5px;text-align:center;">
								<div><c:out value="${result.dateExpression}" /></div>
							</td>
						</tr>
						<tr>
							<td colspan="3">
								<div class="search-line-border"></div>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<table class="search_tools">
				<tbody><tr>
					<td></td>
					<td class="tools_right">
							<c:forEach items="${resultset.paging}" var="page" varStatus="pstatus">
								<c:choose>
									<c:when test="${page.name == currentPage}">
										<b><c:out value="${page.name}" /></b>
									</c:when>
									<c:otherwise>
										<a href="<c:out value="${baseUrl}" />?<c:out value="${page.query}" />"><c:out value="${page.name}" /></a>
									</c:otherwise>
								</c:choose>
							</c:forEach>	
						</td>
				</tr></tbody>
			</table>
		</td>
		<td style="width:260px;">
			<ul id="mymenu" style="width:260px;" class="yui-accordionview">
				<c:forEach items="${resultset.attributes}" var="attribute" varStatus="astatus">
					<c:if test="${attribute.display}">
						<li class="yui-accordion-panel">
							<a id="mymenu-1-label" href="#toggle" tabindex="0" class="yui-accordion-toggle" style="color:#FFF;"><c:out value="${attribute.name}" escapeXml="false"/> (<c:out value="${attribute.count}" />)<span class="indicator"></span></a>
							<div class="yui-accordion-content hidden">
								<ul class="submenu">
								<c:forEach items="${attribute.values}" var="value" varStatus="vstatus">
									<li><a href="<c:out value="${resultset.baseUrl}" />/<c:out value="${resultset.query}" escapeXml="false"/>/<c:out value="${value.query}" escapeXml="false"/>"><c:out value="${value.name}" escapeXml="false"/> (<c:out value="${value.count}" />)</a></li>
								</c:forEach>
								</ul>
							</div>
						</li>
					</c:if>
				</c:forEach>
			</ul>
		</td>
	</tr>
</table>
</div>
<script type="text/javascript" src="<%= themeDisplay.getPathThemeJavaScript()%>/accordionview/accordionview.js"></script>
<script type="text/javascript">
var menu1 = new YAHOO.widget.AccordionView('mymenu', {collapsible: true, expandable: false, width: '260px', animate: true, animationSpeed: '0.5'});		
</script>