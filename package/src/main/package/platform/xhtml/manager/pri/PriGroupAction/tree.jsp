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
					name : "name"
				},
				simpleData : {
					enable : true,
					idKey : "groupId",
					pIdKey : "parentId"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : true,
					inner : function(treeId, nodes, targetNode) {
						return !(targetNode && targetNode.leafFlag === 1);
					}
				},
				showRemoveBtn : function(treeId, treeNode) {
					return treeNode.sysFlag != 1;
				},
				showRenameBtn : false,
				removeTitle : '删除'
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					Ui.confirm('确认删除[' + treeNode.name + ']?', function() {
						Core.fn($zone, 'delNode')(treeNode.groupId);
					});
					return false;
				},
				onClick : function(event, treeId, treeNode) {
					Core.fn($zone, 'editZone')(treeNode.groupId);
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
<ul id="${_zone}_tree">
	<data>${tree}</data>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>