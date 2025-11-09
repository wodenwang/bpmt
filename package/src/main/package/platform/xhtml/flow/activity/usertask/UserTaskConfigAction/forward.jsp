<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.busiName']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='${_zone}']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});
	});
</script>

<c:set var="pixel" value="${param.pixel}" />

<input type="hidden" name="btns" value="${pixel}" />
<div accordion="true" multi="true">
	<div title="基础信息">
		<input type="hidden" name="${pixel}.name" value="forward" />
		<table class="ws-table">
			<tr>
				<th>按钮类型</th>
				<td><font color="blue">转发按钮</font></td>
			</tr>
			<tr>
				<th>按钮主键</th>
				<td><span style="font-style: italic;">(自动生成)</span></td>
			</tr>
			<tr>
				<th>按钮名</th>
				<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" /></td>
			</tr>
			<tr>
				<th>图标</th>
				<td><wcm:widget name="${pixel}.icon" cmd="icon{required:true}" value="arrowreturnthick-1-w" /></td>
			</tr>
			<tr>
				<th>按钮位置</th>
				<td><wcm:widget cmd="radio[@com.riversoft.platform.translate.BtnStyleClass]{required:true}" name="${pixel}.styleClass" /></td>
			</tr>
			<tr>
				<th>描述</th>
				<td><wcm:widget name="${pixel}.description" cmd="textarea" /></td>
			</tr>
		</table>
	</div>
	<div title="按钮展示">
		<table class="ws-table">
			<tr>
				<th>展示条件(脚本类型)</th>
				<td><wcm:widget name="${pixel}.checkType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="1" /></td>
			</tr>
			<tr>
				<th>展示条件(脚本)<br /> <font color="red" tip="true" title="返回boolean类型;vo:订单实体;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.checkScript" cmd="codemirror[groovy]{required:true}" value="return true;" /></td>
			</tr>
		</table>
	</div>
	<div title="接收人设置">
		<table class="ws-table">
			<tr>
				<th>绑定控件</th>
				<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="user" state="readonly" /></td>
			</tr>
			<tr>
				<th>控件动态入参(脚本类型)</th>
				<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" /></td>
			</tr>
			<tr>
				<th>控件动态入参(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程对象">(提示)</font></th>
				<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" /></td>
			</tr>
			<tr>
				<th>可编辑条件(脚本类型)</th>
				<td><wcm:widget name="${pixel}.widgetEnableType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="1" /></td>
			</tr>
			<tr>
				<th>可编辑条件(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程对象">(提示)</font></th>
				<td><wcm:widget name="${pixel}.widgetEnableScript" cmd="codemirror[groovy]{required:true}" value="return true;" /></td>
			</tr>
			<tr>
				<th>表单内容(脚本类型)</th>
				<td><wcm:widget name="${pixel}.widgetValType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" /></td>
			</tr>
			<tr>
				<th>表单内容(脚本内容)<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程对象">(提示)</font></th>
				<td><wcm:widget name="${pixel}.widgetValScript" cmd="codemirror[groovy]" /></td>
			</tr>
		</table>
	</div>
	<div title="弹出框信息">
		<table class="ws-table">
			<tr>
				<th>快速审批意见(脚本类型)</th>
				<td><wcm:widget name="${pixel}.quickOpinionType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" /></td>
			</tr>
			<tr>
				<th>快速审批意见(脚本)<br /> <font color="red" tip="true" title="返回字符数组或以分号分隔的字符;">(提示)</font></th>
				<td><wcm:widget name="${pixel}.quickOpinionScript" cmd="codemirror[groovy]" /></td>
			</tr>
		</table>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>