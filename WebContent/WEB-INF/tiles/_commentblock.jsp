<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="e" uri="https://www.owasp.org/index.php/OWASP_Java_Encoder_Project" %>
<c:if test="${empty comments}">
Peope haven't said anything
</c:if>
<c:forEach items="${comments}" var="comment">
<div class="comment">
<strong><e:forHtml value="${comment.leftby}"/></strong> says:
<blockquote>
<e:forHtml value="${comment.comment}"/>
</blockquote>
</div>
</c:forEach>
