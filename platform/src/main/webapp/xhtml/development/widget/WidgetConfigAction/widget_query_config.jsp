<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="detailFlag" value="${param.detailFlag!=null&&param.detailFlag=='true'}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.busiName']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('button[name=add]', $zone).click(function() {
			var $tabs = $('#${_zone}_querys_tabs');
			Ajax.tab($tabs, '${_acp}/queryForm.shtml', {
				data : {
					tableName : '${param.tableName}',
					type : 'querys',
					pixel : 'querys.B' + Core.nextSeq()
				}
			});
		});

	});
</script>

<input type="hidden" name="hasQuerys" value="true" />
<div class="ws-bar">
	<div class="ws-group left">
		<button icon="plus" type="button" name="add">新增查询</button>
	</div>
</div>

<div tabs="true" button="left" sort="y" id="${_zone}_querys_tabs">
	<c:if test="${fn:length(config.querys)>0}">
		<c:forEach items="${config.querys}" var="vo" varStatus="status">
			<div title="${vo.busiName}" close="true">
				<c:set var="pixel" value="querys.A${status.index}" />
				<input type="hidden" name="querys" value="${pixel}" />
				<table class="ws-table">
					<tr>
						<th>展示名</th>
						<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
					</tr>
					<tr>
						<th>表单字段名<br /> <font color="red" tip="true" title="指定字段名后在脚本中可使用request.getString('name')获取查询框填写值.若不指定则由系统随机分配.">(提示)</font></th>
						<td><wcm:widget name="${pixel}.name" cmd="text"  value="${vo.name}"></wcm:widget></td>
					</tr>
					<tr>
						<th>控件</th>
						<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${vo.widget}"></wcm:widget></td>
					</tr>
					<tr>
						<th>控件动态入参(脚本类型)</th>
						<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.widgetParamType}"></wcm:widget></td>
					</tr>
					<tr>
						<th>控件动态入参(脚本)</th>
						<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" value="${vo.widgetParamScript}"></wcm:widget></td>
					</tr>
					<tr>
						<th>默认值</th>
						<td><wcm:widget name="${pixel}.defVal" cmd="textarea" value="${vo.defVal}"></wcm:widget></td>
					</tr>
					<tr>
						<th>SQL片段(脚本类型)</th>
						<td><wcm:widget name="${pixel}.sqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.sqlType}"></wcm:widget></td>
					</tr>
					<tr>
						<th>SQL片段(脚本)<br /> <font color="red" tip="true" title="value:提交后表单值;values:多选框表单值">(提示)</font></th>
						<td><wcm:widget name="${pixel}.sqlScript" cmd="codemirror[groovy]{required:true}" value="${vo.sqlScript}"></wcm:widget></td>
					</tr>
					<tr>
						<th>备注</th>
						<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description!='null'?vo.description:''}"></wcm:widget></td>
					</tr>
				</table>
			</div>
		</c:forEach>
	</c:if>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>