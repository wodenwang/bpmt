<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
			view : {
				fontCss : function(treeId, node) {
					return node.font ? eval('(' + node.font + ')') : {};
				},
				nameIsHTML : true
			},
			data : {
				key : {
					name : "name",
					title : "title"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parentId"
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					var action = treeNode.action;
					if (action != undefined) {
						Ajax.post('${_zone}_pris_detail', _cp + action, {
							data : {
								viewKey : treeNode.viewKey,
								widgetKey : treeNode.widgetKey,
								groupId : '${groupId}'
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
		zTree.expandAll(true);

	});
</script>
<form action="${_acp}/submitPris.shtml" zone="${_zone}_pris_msg" option="{confirmMsg:'确认保存?'}">
	<input type="hidden" name="groupId" value="${groupId}" />

	<div style="overflow: auto; zoom: 1;">
		<div style="float: left; width: 240px;">
			<ul id="${_zone}_tree">
				<data>${tree}</data>
			</ul>
		</div>
		<div style="margin-left: 250px; overflow: auto; zoom: 1;">

			<div id="${_zone}_pris_detail"></div>
		</div>
	</div>

	<div id="${_zone}_pris_msg" style="margin-top: 10px;"></div>

	<div class="ws-bar" style="margin-top: 10px;">
		<div class="center">
			<button icon="disk" type="submit">保存</button>
		</div>
	</div>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>