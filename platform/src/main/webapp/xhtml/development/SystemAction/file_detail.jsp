<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<table class="ws-table">
    <table class="ws-table">
        <tr>
            <th style="width: 80px;">文件名</th>
            <input type="hidden" name="filename" value="" />
            <td><input type="text" name="newfilename" value="" class="{required:true}"/></td>
        </tr>
        <tr>
            <th class="ws-bar center">
                <button type="button" icon="disk" text="true" name="rename">重命名</button>
                <button type="button" icon="folder-open" text="true" name="unzip">解压缩</button>
            </th>
        </tr>
    </table>
</table>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>