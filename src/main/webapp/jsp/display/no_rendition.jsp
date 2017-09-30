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
			<td class="left">
				<table>
					<tbody>
						<c:if test="${displayName != 'false'}">
							<tr><td class="detail-name"><c:out value="${result.title}" escapeXml="false" /></td></tr>
						</c:if>
						<c:if test="${result.description != null && displayDescription != 'false'}">
							<tr><td class="detail-description-label">Description:</td></tr>
							<tr><td class="detail-description"><c:out value="${result.description}" escapeXml="false" /></td></tr>
						</c:if>
						<c:if test="${result.dateExpression != null && displayDateExpression != 'false'}">
							<tr><td class="detail-date-label">Date:</td></tr>
							<tr><td class="detail-date"><c:out value="${result.dateExpression}" /></td></tr>
						</c:if>
						<c:if test="${result.container != null && displayContainer != 'false'}">
							<tr><td class="detail-container-label">Location:</td></tr>
							<tr><td class="detail-container"><c:out value="${result.container}" /></td></tr>
						</c:if>
						
						<c:forEach items="${result.notes}" var="note" varStatus="nstatus">									
						<tr>
							<td class="note">
								<span style="font-weight:bold;font-size:13px;color:#395AC3;text-decoration:underline;"><c:out value="${note.type}" /></span>
								<span class="detail-text"> - <c:out value="${note.content}" escapeXml="false" /></span>			
							</td>
						</tr>
						</c:forEach>										
					</tbody>
				</table>
			</td>
			<td class="right">
				<table>
					<tbody>
					<c:if test="${not empty result.people}">
						<tr><td class="detail-title">Associated Personal Entities:</td></tr>
						<c:forEach items="${result.people}" var="name" varStatus="nstatus">	
							<tr><td class="relation person">
								<span class="detail-text">
									<c:out value="${name.title}" /> 
									<c:if test="${name.function != null}">
										(<b><c:out value="${name.function}" /></b>)
									</c:if>
								</span>
							</td></tr>
						</c:forEach>
					</c:if>
			
					<c:if test="${not empty result.corporations}">
						<tr><td class="detail-title">Associated Corporate Entities:</td></tr>
						<c:forEach items="${result.corporations}" var="name" varStatus="nstatus">	
							<tr><td class="relation corporation">
								<span class="detail-text"><c:out value="${name.title}" /> 
								<c:if test="${name.function != null}">
									(<b><c:out value="${name.function}" /></b>)
								</c:if>
								</span>
							</td></tr>
						</c:forEach>
					</c:if>
							
					<c:if test="${not empty result.subjects}">
						<tr><td class="detail-title">Associated Subjects:</td></tr>
						<c:forEach items="${result.subjects}" var="subject" varStatus="nstatus">
							<tr><td class="relation subject">
								<span class="detail-text"><c:out value="${subject.title}" /></span>
							</td></tr>
						</c:forEach>
					</c:if>
			
					<c:if test="${not empty result.path}">
						<tr><td class="detail-title">Keywords:</td></tr>
						<c:forEach items="${result.path}" var="node" varStatus="nstatus">
							<c:if test="${node.id != result.id}">
								<tr><td class="relation keyword">
									<span class="detail-text"><c:out value="${node.title}" escapeXml="false" /></span>
								</td></tr>
							</c:if>
						</c:forEach>
					</c:if>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
</table>