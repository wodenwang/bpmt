<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$(":radio[name='type']", $zone).on('ifChanged', function() {
			if ($(this).val() == '1') {//只功能点
				$('tr[name=special]', $zone).hide();
			} else {
				$('tr[name=special]', $zone).show();
			}
		});

		$(":radio[name='type']:checked", $zone).prop('checked', false).iCheck('check');

		if ('${disabledAll}' == '1') {
			$(":radio[name]", $zone).iCheck('disable')
			$('textarea', $zone).prop("disabled", true);
		}
	});

	function buildPriJson($zone) {
		var pri = {};
		pri.priKey = $('[name=priKey]', $zone).val();
		pri.busiName = $('[name=busiName]', $zone).val();
		pri.type = $('[name=type]:checked', $zone).val();
		pri.checkType = $('[name=checkType]:checked', $zone).val();
		pri.checkScript = $('[name=checkScript]', $zone).val();
		pri.description = $('[name=description]', $zone).val();
		return pri;
	};
</script>

<div tabs="true">
	<div title="基本信息">
		<table class="ws-table">
			<tr>
				<th>系统主键(自动生成)</th>
				<td>${pri.priKey}<input type="hidden" name="priKey" value="${pri.priKey}" /></td>
			</tr>
			<tr>
				<th>名称(模块自动生成)</th>
				<td><c:choose>
						<c:when test="${pri.busiName!=null&&pri.busiName!=''}">${pri.busiName}</c:when>
						<c:otherwise>
							<font color="red">(保存后由系统自动生成)</font>
						</c:otherwise>
					</c:choose><input type="hidden" name="busiName" value="${pri.busiName}" /></td>
			</tr>
			<tr>
				<th>模式</th>
				<td><wcm:widget name="type" cmd="radio[@com.riversoft.platform.po.CmPri$Types]" value="${pri.type}"></wcm:widget></td>
			</tr>
			<tr name="special">
				<th>脚本类型</th>
				<td><wcm:widget name="checkType" cmd="radio[@com.riversoft.platform.script.ScriptTypes]" value="${pri.checkType}" /></td>
			</tr>
			<tr name="special">
				<th>脚本<br /> <font color="red" title="返回布尔类型." tip="true">(提示)</font></th>
				<td><wcm:widget name="checkScript" cmd="codemirror[groovy]" value="${pri.checkScript}" /></td>
			</tr>
			<tr>
				<th>描述</th>
				<td><wcm:widget name="description" cmd="textarea" value="${pri.description}" /></td>
			</tr>
		</table>
	</div>
	<div title="关联权限组" init="${_acp}/quickRelate.shtml?priKey=${pri.priKey}"></div>
</div>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>