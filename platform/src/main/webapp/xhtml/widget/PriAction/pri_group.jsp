<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		//外部回调
		Core.fn($zone, 'buildJson', function() {
			var vo = {};
			vo.checkType = $('[name=checkType]:checked', $zone).val();
			vo.checkScript = $('[name=checkScript]', $zone).val();
			vo.description = $('[name=description]', $zone).val();
			vo.priKey = '${pri.priKey}';
			vo.groupId = '${group.groupId}';
			return vo;
		});
	});
</script>
<div tabs="true">
	<div title="权限关联">
		<table class="ws-table">
			<tr>
				<th>权限点主键</th>
				<td>${pri.priKey}</td>
			</tr>
			<tr>
				<th>权限详情</th>
				<td>[${wcm:widget('select[@com.riversoft.platform.po.CmPri$Catelog]',pri.catelogType)}]${pri.busiName}</td>
			</tr>
			<tr>
				<th>归属权限组</th>
				<td>[${group.groupId}]${group.name}</td>
			</tr>
			<tr>
				<th>脚本类型</th>
				<td><wcm:widget name="checkType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${(vo!=null&&vo.checkType!=0)?vo.checkType:2}" /></td>
			</tr>
			<tr>
				<th>脚本<br /> <font color="red" tip="true" title="返回布尔类型."></font></th>
				<td><wcm:widget name="checkScript" cmd="codemirror[groovy]" value="${vo.checkScript}" /></td>
			</tr>
			<tr>
				<th>描述</th>
				<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
			</tr>
		</table>
	</div>
	<div title="关联权限组" init="${_acp}/quickRelate.shtml?priKey=${pri.priKey}&groupId=${group.groupId}"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>