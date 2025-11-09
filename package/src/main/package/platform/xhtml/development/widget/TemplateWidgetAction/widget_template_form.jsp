<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${config==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//关闭
		$('button[name=close]', $zone).click(function() {
			Ui.confirm('当前操作未保存,确认关闭?', function() {
				Ui.closeCurrent('${_zone}');
			});
		});

		//提交
		$('button[name=submit]', $zone).click(function() {
			Core.fn($zone, 'submit')($zone, $('#${_zone}_form'));
		});
	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submit.shtml" method="post" id="${_zone}_form" option="{errorZone:'${_zone}_error',confirmMsg:'确认提交？'}">
	<input type="hidden" name="isCreate" value="${isCreate?1:0}">
	<table class="ws-table">
		<tr>
			<th>控件主键<font color="red" tip="true" title="做为模板控件的key">(提示)</font></th>
			<td><c:choose>
					<c:when test="${isCreate}">
						<font color="red">(自动生成)</font>
					</c:when>
					<c:otherwise>
						<wcm:widget name="widgetKey" cmd="text" value="${config.widgetKey}" state="readonly" />
					</c:otherwise>
				</c:choose></td>
		</tr>
		<tr>
			<th>描述</th>
			<td><wcm:widget name="description" cmd="textarea" value="${config.description}"></wcm:widget></td>
		</tr>
	</table>

<div tabs="true" id="${_zone}_tabs">
	<div title="基础设置">
		<table class="ws-table" id="${_zone}_table">
			<tr>
				<th>展示名</th>
				<td><wcm:widget name="busiName" cmd="text{required:true}" value="${config.busiName}" /></td>
			</tr>
			<tr name="template" class="last-child">
				<th>模板文件</th>
				<td><wcm:widget name="templateFile" cmd="filemanager" value="${config.templateFile}" /></td>
			</tr>
		</table>
	</div>
	<div title="模板变量">
		<div class="ws-msg info">
			1. 可以定义多个模板变量供模板文件里面使用。<br />2. 可以在模板变量里面定义除赋值外其他业务逻辑。<br />
		</div>
		<div id="${_zone}_vars" init="${_acp}/varConfigForm.shtml?widgetKey=${config.widgetKey}"></div>
	</div>
</div>
	<div class="ws-bar">
		<div class="ws-group">
			<button type="button" icon="closethick" text="true" name="close">关闭</button>
			<button type="button" icon="check" text="true" name="submit">提交</button>
		</div>
   </div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>