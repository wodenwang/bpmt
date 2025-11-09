<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
			data : {
				key : {
					name : "busiName"
				},
				simpleData : {
					enable : true,
					idKey : "cateKey",
					pIdKey : "parentKey"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : true
				},
				showRemoveBtn : true,
				showRenameBtn : true,
				removeTitle : '删除',
				renameTitle : '编辑'
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					Core.fn($zone, 'showFunctionMainZone')(treeNode.cateKey);
				},
				beforeRemove : function(treeId, treeNode) {
					Core.fn($zone, 'delCatelog')(treeNode);
					return false;
				},
				beforeEditName : function(treeId, treeNode) {
					Core.fn($zone, 'editCatelog')(treeNode);
					return false;
				}
			}
		};

		var strData = $('data', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		$.each(zTree.getNodesByParam('parentKey', null), function(index, node) {
			zTree.expandNode(node);
		});
	});
</script>

<ul id="${_zone}_tree">
	<data>${wcm:json(tree)}</data>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>