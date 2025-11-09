<%@ page language="java" pageEncoding="UTF-8" %>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp" %>
<%@ include file="/include/html_head.jsp" %>

<script type="text/javascript">
    $(function () {
        var $zone = $('#${_zone}');
        $('button[name=submitForm]', $zone).click(function () {
            var $form = $('form', $zone);
            Core.fn($zone, 'submitForm')($form, $zone, {
                confirmMsg: '确认提交?',
                errorZone: '${_zone}_err_zone'
            });
        });
    });
</script>

<c:set var="editFlag" value="${vo!=null}"/>
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitForm.shtml" method="post">
    <input type="hidden" name="edit" value="${editFlag?1:0}" />
    <table class="ws-table">
        <tr>
            <th>队列KEY</th>
            <td><wcm:widget name="queueKey" cmd="text{required:true}" value="${editFlag?vo.queueKey:''}"
                            state="${editFlag?'readonly':''}"></wcm:widget>
            <c:if test="editFlag">
                <input type="hidden" name="queueKey" value="${vo.queueKey}" />
            </c:if>
            </td>
        </tr>
        <tr>
            <th>描述</th>
            <td><wcm:widget name="description" cmd="textarea" value="${vo.description}"></wcm:widget></td>
        </tr>

        <tr>
            <th>脚本类型</th>
            <td><wcm:widget name="execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}"
                            value="${vo.execType}"></wcm:widget></td>
        </tr>
        <tr>
            <th>执行脚本<br /> <font color="red" tip="true" title="可在脚本中调用任意内置函数；vo为一条待处理记录，保存脚本执行所需的信息，具体参考该队列动态表结构定义。">(提示)</font></th>
            <td><wcm:widget name="execScript" cmd="codemirror[groovy]{required:true}"
                            value="${vo.execScript}"></wcm:widget></td>
        </tr>
        <tr>
            <th>队列表</th>
            <td><select name="tableName" class="chosen needValid {required:true}">
                <option value="">无</option>
                <c:forEach items="${queueTables}" var="model">
                    <c:choose>
                        <c:when test="${editFlag&&model.name==vo.tableName}">
                            <option value="${model.name}" selected="selected">
                                [${model.name}]${model.description}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${model.name}">[${model.name}]${model.description}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select></td>
        </tr>
        <tr>
            <th>外挂日志表</th>
            <td><select name="logTableName" class="chosen">
                <option value="">无</option>
                <c:forEach items="${hisTables}" var="model">
                    <c:choose>
                        <c:when test="${editFlag&&model.name==vo.logTableName}">
                            <option value="${model.name}" selected="selected">
                                [${model.name}]${model.description}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${model.name}">[${model.name}]${model.description}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select></td>
        </tr>
        <c:if test="${editFlag}">
            <tr>
                <th>创建时间</th>
                <td>${wcm:widget('date[datetime]',vo.createDate)}</td>
            </tr>
            <tr>
                <th>更新时间</th>
                <td>${wcm:widget('date[datetime]',vo.updateDate)}</td>
            </tr>
        </c:if>
    </table>
</form>

<div class="ws-bar">
    <div class="ws-group">
        <button type="button" icon="check" text="true" name="submitForm">保存</button>
    </div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp" %>