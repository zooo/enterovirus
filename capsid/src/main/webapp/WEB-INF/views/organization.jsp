<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

    <nav>
      <a href="<s:url value="/" />">Home</a> &rarr; 
      <span class="nav-current">${organization.displayName}</span>
    </nav>
    <article>
      <div class="left-wide">
        <h2>
          Repositories
          <s:url var="create_repo_url" value="/organizations/${organization.id}/repositories/create" />
          <sf:form method="GET" action="${create_repo_url}">
            <input class="delete" type="submit" value="+" />
          </sf:form>
        </h2>
        <c:forEach var="repository" items="${organization.repositories}">
          <h3>
            <a href="<s:url value="/organizations/${organization.id}/repositories/${repository.id}" />">${repository.displayName}</a>
            <c:if test="${isManager == true}">
              <s:url var="collaborators_url" value="/organizations/${organization.id}/repositories/${repository.id}/collaborators" />
              <sf:form method="GET" action="${collaborators_url}">
                <input class="delete" type="submit" value="Collaborators" />
              </sf:form>
            </c:if>
          </h3>
        </c:forEach>
      </div>
      <div class="right-narrow">
        <h2>
          Managers
          <c:if test="${isManager == true}">
            <s:url var="manager_url" value="/organizations/${organization.id}/managers" />
            <sf:form method="GET" action="${manager_url}">
              <input class="delete" type="submit" value="Settings" />
            </sf:form>
          </c:if>
        </h2>
        <ul class="user-list">
          <c:forEach var="member" items="${organization.managers}">
            <li><span class="user">${member.displayName}</span></li>
          </c:forEach>
        </ul>
      </div>
      <div style="clear:both"></div>
    </article>