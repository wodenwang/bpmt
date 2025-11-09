
<%
    /**
     * 跳转到首页模块
     */
%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%
    request.getRequestDispatcher("/frame/FrameAction/index.shtml").forward(request, response);
%>