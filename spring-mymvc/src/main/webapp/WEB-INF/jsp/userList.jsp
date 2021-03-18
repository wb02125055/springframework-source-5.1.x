<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<h2>This is SpringMVC demo page</h2>
<c:forEach items="${users}" var="user">
    　 <c:out value="${user.name}"/><br/>
    　 <c:out value="${user.age}"/><br/>
</c:forEach>