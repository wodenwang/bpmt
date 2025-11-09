<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="pixel" value="${menuKey}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('input[name$=".busiName"]', $zone).blur(function() {
			var val = $(this).val();
			if (val != null && val != '') {
				var ztree = $.fn.zTree.getZTreeObj("${param.treeId}");
				var node = ztree.getSelectedNodes();
				if (node.length > 0) {
					node[0].busiName = val;
					ztree.refresh();
					ztree.selectNode(node[0]);
				}
			}
		});

		//一级菜单
		$('button[name=addSubMenu]', $zone).click(function() {
			var parentKey = $(this).val();
			Core.fn($zone.parent(), 'addSubMenu')(parentKey);
		});

		//功能菜单
		$("input:radio[name$='.menuType']", $zone).on('ifChecked', function(event) {
			var val = $(this).val();
			$("tr[menuType='" + val + "']", $zone).show();
			$("tr[menuType][menuType!='" + val + "']", $zone).hide();
		});

		var menuType = '${menuType}';
		if (menuType == '1') {//菜单
			$("input:radio[name$='.menuType'][value=1]", $zone).iCheck('check');
		} else if (menuType == '2') {//自定义网址
			$("input:radio[name$='.menuType'][value=2]", $zone).iCheck('check');
		} else if (menuType == '3') {//小程序页面
			$("input:radio[name$='.menuType'][value=3]", $zone).iCheck('check');
		} else {//事件
			$("input:radio[name$='.menuType'][value=99]", $zone).iCheck('check');
			$("input:radio[name$='.commandType'][value=${menuType}]", $zone).iCheck('check');
		}
	});
</script>

<input type="hidden" name="${pixel}.flag" value="true" />

<c:if test="${menuType==0}">
	<input type="hidden" name="${pixel}.menuType" value="0" />
	<div class="ws-bar">
		<div class="left ws-group">
			<button type="button" icon="plus" name="addSubMenu" value="${menuKey}">二级功能菜单</button>
		</div>
	</div>
</c:if>

<table class="ws-table">
	<tr>
		<th>菜单KEY</th>
		<td><font color="red">${vo.menuKey}</font></td>
	</tr>
	<tr>
		<th>菜单名称</th>
		<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
	</tr>

	<c:if test="${menuType!=0}">
		<tr>
			<th>菜单类型</th>
			<td><input name="${pixel}.menuType" value="99" type="radio" /><label>事件绑定</label><input name="${pixel}.menuType" value="1" type="radio" />
			<label>视图绑定</label><input name="${pixel}.menuType" value="2" type="radio" /><label>自定义网址</label><input name="${pixel}.menuType" value="3" type="radio" /><label>小程序页面</label></td>
		</tr>
		<tr menuType="1">
			<th>视图URL</th>
			<td><font color="red">${menuUrl}</font></td>
		</tr>
		<tr menuType="1">
			<th>视图</th>
			<td><wcm:widget name="${pixel}.action" cmd="view[WX]" value="${vo.menuType==1?vo.action:''}" /></td>
		</tr>
		<tr menuType="1">
			<th>动态入参(脚本类型)</th>
			<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.paramType}"></wcm:widget></td>
		</tr>
		<tr menuType="1">
			<th>动态入参(脚本)</th>
			<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]" value="${vo.paramScript}" /></td>
		</tr>

		<tr menuType="99">
			<th>事件类型</th>
			<td><wcm:widget name="${pixel}.commandType" cmd="radio[@com.riversoft.platform.translate.WxMenuCommandType]" value="${vo.commandType}" /></td>
		</tr>
		<tr menuType="99">
			<th>事件处理器</th>
			<td><wcm:widget name="${pixel}.commandKey" cmd="wxcommand[mp;MENU]" value="${vo.commandKey}" /></td>
		</tr>
		<tr menuType="99">
			<th>动态入参(脚本类型)</th>
			<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.paramType}"></wcm:widget></td>
		</tr>
		<tr menuType="99">
			<th>动态入参(脚本)</th>
			<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]" value="${vo.paramScript}" /></td>
		</tr>

		<tr menuType="2">
			<th>网址</th>
			<td><wcm:widget name="${pixel}.defUrl" cmd="textarea{url:true}" value="${vo.menuType==2?vo.action:''}" /></td>
		</tr>
		
		<tr menuType='3'>
		    <th>跳转页面<font color="red" tip="true" title="小程序直接跳转的页面.">(提示)</font></th>
		    <td><wcm:widget name="${pixel}.defPath" cmd="textarea" value="${vo.menuType==3?vo.pagepath:''}" /></td>
		</tr>
		<tr menuType='3'>
		    <th>网址<font color="red" tip="true" title="若不支持小程序则跳转该网址.">(提示)</font></th>
			<td><wcm:widget name="${pixel}.appUrl" cmd="textarea{url:true}" value="${vo.menuType==3?vo.action:''}" /></td>
		</tr>
	</c:if>

	<tr>
		<th>描述</th>
		<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description}" /></td>
	</tr>
</table>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>