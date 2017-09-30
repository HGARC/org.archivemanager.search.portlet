<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/theme" prefix="liferay-theme" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<portlet:defineObjects />
<liferay-theme:defineObjects />	
<%
String themeImagePath = themeDisplay.getPathThemeImages();
%>	
	<section>
      <div id="search-details">
        <h1>Search Details</h1>
        <h3><c:out value="${result.title}" escapeXml="false" /><h3>
        <h4>Description:</h4>
        <p><c:out value="${result.description}" escapeXml="false" /></p> 
        <h4>Summary:</h4>
        <p><c:out value="${result.summary}" escapeXml="false" /></p>
        <h4>Date:</h4>
        <p><c:out value="${result.dateExpression}" escapeXml="false" /></p> 
        <h4>Location:</h4>
        <p><c:out value="${result.container}" /></p> 
        <h4>Existence and Location of Originals:</h4>
        <p><c:out value="${result.collectionName}" escapeXml="false" /></p>
        
        <c:if test="${not empty result.people || not empty result.corporations}"> 
        	<h4>Named Entities Links:</h4>
        	<c:forEach items="${result.people}" var="name" varStatus="nstatus">
        		<c:out value="${name.title}" /> 
        		<c:if test="${not empty name.function}">
        			(<b><c:out value="${name.function}" /></b>)
        		</c:if>
        		<br/>
        	</c:forEach>
        	<c:forEach items="${result.corporations}" var="name" varStatus="nstatus">
        		<c:out value="${name.title}" />
        		<c:if test="${not empty name.function}">
        			(<b><c:out value="${name.function}" /></b>)
        		</c:if>
        		<br/>        	
        	</c:forEach>
        </c:if>
        
        <c:if test="${not empty result.subjects}">
	        <h4>Subject Links:</h4>
	        <c:forEach items="${result.subjects}" var="subject" varStatus="nstatus">
	        	<c:out value="${subject.title}" /><br/>
	        </c:forEach>
	    </c:if>                         
      </div>
      <div id="flippingbook-container">
      	<c:set var="linked" scope="session" value="false"/>
      	<c:if test="${not empty result.weblinks}">
			<c:forEach items="${result.weblinks}" var="file" varStatus="nstatus">
				<c:if test="${file.type == 'rendition' || file.type == 'flippingbook'}">
					<c:set var="linked" scope="session" value="true"/>
					<iframe style="width:550px;height:700px;" src="<c:out value="${file.url}" />"></iframe>
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
      <div class="clearfix"></div>  
    </section>