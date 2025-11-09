<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $tree = $("#${_zone}_tree", $('#${_zone}'));
		var treeSetting = {
			data : {
				simpleData : {
					enable : true,
					idKey : "groupId",
					pIdKey : "parentId"
				},
				key : {
					title : "description"
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					var leafFlag = treeNode.leafFlag;
					if (leafFlag == 1) {
						var groupId = treeNode.groupId;
						var action = _cp + '${action}';
						Ajax.post('${_zone}_pri_list', action, {
							data : {
								groupId : groupId,
								viewKey : '${param.viewKey}'
							}
						});
					}
				}
			}
		};

		var strData = $('data', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		$.each(zTree.getNodesByParam('parentId', null), function(index, node) {
			zTree.expandNode(node);
		});
	});
</script>

<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 340px;">
		<div panel="权限组列表">
			<ul id="${_zone}_tree">
				<data>${wcm:json(groups)}</data>
			</ul>
		</div>
	</div>
	<div style="margin-left: 350px;">
		<form action="${_acp}/submitPris.shtml" zone="${_zone}_pri_msg" option="{errorZone:'${_zone}_pri_msg',confirmMsg:'确认保存设置?'}">
			<div panel="权限设置">
				<div id="${_zone}_pri_msg"></div>
				<div id="${_zone}_pri_list"></div>
			</div>
			<div class="ws-bar">
				<div class="ws-group">
					<button icon="disk">保存设置</button>
				</div>
			</div>
		</form>
	</div>
</div>



<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>