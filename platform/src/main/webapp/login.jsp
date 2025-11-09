
<%
    /**
     * 跳转到登录模块
     */
%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
    request.getRequestDispatcher("/frame/LoginAction/index.shtml").forward(request, response);
%>