<%@ page language="java" pageEncoding="UTF-8" %>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp" %>
<%@ include file="/include/html_head.jsp" %>

<div id="${_zone}_msg_zone" name="msgZone"></div>

<div>
    <div>
        <table class="ws-table">
            <tr>
                <th style="width: 200px;">所属行业<font color="red" tip="true" title="所属行业可以到微信公众号管理端设置，每月只能修改一次">(提示)</font></th>
                <td><span style="color:blue">主业</span>[${industry.primary}]  |  <span style="color:blue">副业</span>[${industry.primary}]</td>
            </tr>
        </table>
    </div>
    <div>
        <table class="ws-table">
            <thead>
            <tr>
                <th style="width: 200px;">模板ID<font color="red" tip="true" title="消息模板可以到微信公众号管理端进行新增或者删除">(提示)</font></th>
                <th>标题</th>
                <th>详情和例子</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${templates}" var="template" varStatus="loop">
                <tr>
                    <td class="center">${template.templateId}</td>
                    <td class="center">${template.title}</td>
                    <td class="left"><span style="color:blue">详情:</span>${template.content} <br> <span style="color:blue">例子:</span>${template.example}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp" %>