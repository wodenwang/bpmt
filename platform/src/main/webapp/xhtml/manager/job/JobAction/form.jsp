<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('button[name=submitForm]', $zone).click(function() {
			var $form = $('form', $zone);
			Core.fn($zone, 'submitForm')($form, $zone, {
				confirmMsg : '确认提交?',
				errorZone : '${_zone}_err_zone'
			});
		});
	});
</script>

<c:set var="editFlag" value="${vo!=null}" />
<div name="msgZone" id="${_zone}_err_zone"></div>
<form aync="true" action="${_acp}/submitForm.shtml" method="post">
	<table class="ws-table">
		<tr>
			<th>主键</th>
			<td><c:choose>
					<c:when test="${editFlag}">
						<c:out value="${vo.jobKey}" />
						<input type="hidden" name="jobKey" value="${vo.jobKey}" />
					</c:when>
					<c:otherwise>
						<font color="red">(系统自动生成)</font>
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea" value="${vo.description}"></wcm:widget></td>
		</tr>
		<tr>
			<th>CRON表达式</th>
			<td><wcm:widget name="cronExpression" cmd="textarea{required:true}" value="${vo.cronExpression}"></wcm:widget></td>
		</tr>

		<tr>
			<th>脚本类型</th>
			<td><wcm:widget name="execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
		</tr>
		<tr>
			<th>执行脚本</th>
			<td><wcm:widget name="execScript" cmd="codemirror[groovy]{required:true}" value="${vo.execScript}"></wcm:widget></td>
		</tr>
		<tr>
			<th>事务<font color="red" tip="true" title="选[是]则整端脚本逻辑被定义为一个事务;选[否]则依赖各函数或原子脚本来保证事务.">(提示)</font></th>
			<td><wcm:widget name="isTransaction" cmd="radio[YES_NO]" value="${vo.isTransaction}"></wcm:widget></td>
		</tr>
		<tr>
			<th>外挂日志表</th>
			<td><select name="logTableName" class="chosen">
					<option value="">无</option>
					<c:forEach items="${tables}" var="model">
						<c:choose>
							<c:when test="${editFlag&&model.name==vo.logTableName}">
								<option value="${model.name}" selected="selected">[${model.name}]${model.description}</option>
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
		<c:choose>
			<c:when test="${editFlag&&vo.activeFlag==1}">
				<button type="button" icon="check" text="true" disabled="disabled" title="活动中的任务不允许修改.">提交</button>
			</c:when>
			<c:otherwise>
				<button type="button" icon="check" text="true" name="submitForm" tip="true" title="提交后任务会自动处于活跃状态.">提交</button>
			</c:otherwise>
		</c:choose>

	</div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>