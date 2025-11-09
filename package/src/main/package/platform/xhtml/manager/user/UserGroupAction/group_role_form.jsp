<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var treeSetting = {
			data : {
				key : {
					name : "name"
				},
				simpleData : {
					enable : true,
					idKey : "groupId",
					pIdKey : "parentId"
				}
			},
			check : {
				enable : true
			}
		};

		var $roleTree = $("#${_zone}_role_tree", $zone);
		{
			var strData = $('data', $roleTree).html();
			var datas = eval("(" + strData + ")");
			var roleTree = $.fn.zTree.init($roleTree, treeSetting, datas);
			$roleTree.addClass("ztree");
			roleTree.expandAll(true);
		}

		var $groupRoleTree = $("#${_zone}_group_role_tree", $zone);
		{
			var strData = $('data', $groupRoleTree).html();
			var datas = eval("(" + strData + ")");
			var groupRoleTree = $.fn.zTree.init($groupRoleTree, treeSetting, datas);
			$groupRoleTree.addClass("ztree");
			groupRoleTree.expandAll(true);

			//处理表单
			$('form[name=pri]', $zone).submit(function(event) {
				var $this = $(this);
				event.preventDefault();

				var $checkboxZone = $('div:hidden', $this);
				$(':checkbox', $checkboxZone).prop("checked", false);

				//提交之前把树勾选的项都勾上
				var array = groupRoleTree.getCheckedNodes(true);
				$.each(array, function(i, treeNode) {
					var $checkbox = $(':checkbox[value=' + treeNode.groupId + ']', $checkboxZone);
					if ($checkbox.size() < 1) {
						$checkbox = $('<input type="checkbox" name="groupId"/>');
						$checkbox.val(treeNode.groupId);
						$checkboxZone.append($checkbox);
					}
					$checkbox.prop("checked", true);
				});

				Ajax.form('${_zone}_pri_zone', $this, {
					confirmMsg : '确认保存设置?'
				});
			});
		}

	});
</script>

<div id="${_zone}_pri_zone"></div>

<div panel="角色固有权限组" style="width: 49%; float: left;">
	<ul id="${_zone}_role_tree">
		<data>${roleTree}</data>
	</ul>
</div>
<div panel="角色所在组织权限组" style="width: 49%; margin-left: 51%;">
	<ul id="${_zone}_group_role_tree">
		<data>${groupRoleTree}</data>
	</ul>
</div>
<form action="${_acp}/submitGroupRolePri.shtml" sync="true" name="pri">
	<input type="hidden" name="groupKey" value="${groupKey}" /> <input type="hidden" name="roleKey" value="${roleKey}" />
	<div style="display: none;"></div>
	<div class="ws-bar">
		<button icon="disk" type="submit">保存设置</button>
	</div>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>