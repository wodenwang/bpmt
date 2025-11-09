<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {

		var $zone = $('#${_zone}');

		//新增处理器
		$('button[name="addExec"]', $zone).click(function() {
			var $tabs = $('#${_zone}_exec_tabs');
			Ajax.tab($tabs, '${_acp}/extDelegate.shtml', {
				data : {
					pixel : 'detail.exec.B.' + Core.nextSeq(),
					name : 'detail',//控件类型
					method : 'addExec'//调用方法
				}
			});
		});

		//新增批量字段
		$('button[name="addBatchColumn"]', $zone).click(function() {
			var $tabs = $('#${_zone}_batch_column_tabs');
			Ajax.tab($tabs, '${_acp}/extDelegate.shtml', {
				data : {
					pixel : 'detail.batchColumn.B.' + Core.nextSeq(),
					name : 'detail',//控件类型
					method : 'addBatchColumn'//调用方法
				}
			});
		});

		$("[name$='.description']", $('#${_zone}_exec_tabs')).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$("[name$='.busiName']", $('#${_zone}_batch_column_tabs')).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});
	});
</script>

<input type="hidden" name="detail.flag" value="true" />
<div tabs="true">
	<div title="基础设置">
		<div accordion="true" multi="true">
			<div title="数据">
				<table class="ws-table">
					<tr>
						<th>唯一标识(脚本类型)</th>
						<td><wcm:widget name="detail.pkType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.pkType}" /></td>
					</tr>
					<tr>
						<th>唯一标识 (脚本)<br /> <font color="red" tip="true" title="vo:单条数据.">(提示)</font></th>
						<td><wcm:widget name="detail.pkScript" cmd="codemirror[groovy]{required:true}" value="${vo.pkScript}" /></td>
					</tr>
				</table>
			</div>
			<div title="展示">
				<table class="ws-table">
					<tr>
						<th>汇总展示(脚本类型)</th>
						<td><wcm:widget name="detail.sumarryType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.sumarryType}" /></td>
					</tr>
					<tr>
						<th>汇总展示 (脚本)<br /> <font color="red" tip="true" title="list:数据列表.">(提示)</font></th>
						<td><wcm:widget name="detail.sumarryScript" cmd="codemirror[groovy]" value="${vo.sumarryScript}" /></td>
					</tr>
					<tr>
						<th>是否分页</th>
						<td><wcm:widget name="detail.pageFlag" cmd="radio[YES_NO]{required:true}" value="${vo==null?'0':vo.pageFlag}" /></td>
					</tr>
				</table>
			</div>
			<div title="操作">
				<table class="ws-table">
					<tr>
						<th>允许新增</th>
						<td><wcm:widget name="detail.allowAdd" cmd="radio[YES_NO]{required:true}" value="${vo==null?'1':vo.allowAdd}" /></td>
					</tr>
					<tr>
						<th>允许删除</th>
						<td><wcm:widget name="detail.allowDelete" cmd="radio[YES_NO]{required:true}" value="${vo==null?'1':vo.allowDelete}" /></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div title="数据处理器">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="addExec">新增处理器</button>
			</div>
		</div>
		<div tabs="true" button="left" sort="y" id="${_zone}_exec_tabs">
			<c:forEach items="${vo.execs}" var="exec" varStatus="status">
				<div title="${exec.description}" close="true">
					<c:set var="pixel" value="detail.exec.A.${status.index}" />
					<input type="hidden" name="detail.exec" value="${pixel}" />
					<table class="ws-table">
						<tr>
							<th>执行处理器(脚本类型)</th>
							<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${exec.execType}"></wcm:widget></td>
						</tr>
						<tr>
							<th>执行处理器(脚本)<br /> <font color="red" tip="true" title="mode:1:普通编辑;2:批量编辑;list:待处理的数据列表.">(提示)</font></th>
							<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${exec.execScript}"></wcm:widget></td>
						</tr>
						<tr>
							<th>备注</th>
							<td><wcm:widget cmd="textarea{required:true}" name="${pixel}.description" value="${exec.description}" /></td>
						</tr>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
	<div title="批量处理">
		<table class="ws-table">
			<tr>
				<th>允许批处理</th>
				<td><wcm:widget name="detail.batchFlag" cmd="radio[YES_NO]{required:true}" value="${vo==null?'0':vo.batchFlag}" /></td>
			</tr>
			<tr>
				<th class="ws-bar left">
					<button icon="plus" type="button" name="addBatchColumn">新增字段</button>
				</th>
			</tr>
		</table>
		<div tabs="true" button="left" sort="y" id="${_zone}_batch_column_tabs">
			<c:forEach items="${vo.batchColumns}" var="column" varStatus="status">
				<div title="${column.busiName}" close="true">
					<c:set var="pixel" value="detail.batchColumn.A.${status.index}" />
					<input type="hidden" name="detail.batchColumns" value="${pixel}" />
					<table class="ws-table">
						<tr>
							<th>列名</th>
							<td><wcm:widget name="${pixel}.name" cmd="text{required:true}" value="${column.name}" /></td>
						</tr>
						<tr>
							<th>展示名</th>
							<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${column.busiName}"></wcm:widget></td>
						</tr>
						<tr>
							<th>示例</th>
							<td><wcm:widget name="${pixel}.example" cmd="text" value="${column.example}" /></td>
						</tr>
						<tr>
							<th>备注</th>
							<td><wcm:widget cmd="textarea" name="${pixel}.description" value="${column.description}" /></td>
						</tr>
					</table>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>