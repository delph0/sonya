<%@ include file="/common/taglibs.jsp"%>
<%@ page import="com.sonya.user.model.User"%>

<%
	//User user = (User) session.getAttribute("currentUser");
	//System.out.println("username == " + user.getUsername());
	
%>
<div id="logo">
	<h1><a href="<fmt:message key="company.url"/>"><fmt:message key="webapp.name" /></a></h1>
	<h2><a href="<fmt:message key="company.url"/>">Open Source Project by Louie</a></h2>
</div>
<div id="menu">
	<ul>
		<li class="first"><a href="#" accesskey="1" title="">Home</a></li>
		<li><a href="#" accesskey="2" title="">Services</a></li>
		<li><a href="#" accesskey="3" title="">About</a></li>
		<c:if test="${pageContext.request.locale.language != 'en'}">
			<li><a href="<c:url value='/?locale=en'/>">English</a></li>
		</c:if>
		<c:if test="${pageContext.request.locale.language != 'ko'}">
			<li><a href="<c:url value='/?locale=ko'/>">Korean</a></li>
		</c:if>
		<li><div id="user"><a href="<c:url value='/login.jsp'/>">Sign in</a></div></li>			
	</ul>
</div>