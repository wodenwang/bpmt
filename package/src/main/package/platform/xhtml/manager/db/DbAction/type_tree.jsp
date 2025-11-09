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
					idKey : "id",
					pIdKey : "parentId"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : true,
					prev : function(treeId, nodes, targetNode) {
						if (nodes.length > 1) {
							Ui.alert("不允许同时拖拽多个节点.");
							return false;
						}
						var sourceNode = nodes[0];
						if (sourceNode.isDataType == targetNode.isDataType) {//同类允许排序
							return true;
						}
						return false;
					},
					inner : function(treeId, nodes, targetNode) {

						if (nodes.length > 1) {
							Ui.alert("不允许同时拖拽多个节点.");
							return false;
						}

						if (targetNode == null || targetNode.isDataType) {//图形不允许拖入
							return false;
						}

						var sourceNode = nodes[0];
						if (!sourceNode.isDataType) {//只有图形才允许拖入
							return false;
						}

						return true;
					},
					next : function(treeId, nodes, targetNode) {
						if (nodes.length > 1) {
							Ui.alert("不允许同时拖拽多个节点.");
							return false;
						}
						var sourceNode = nodes[0];
						if (sourceNode.isDataType == targetNode.isDataType) {//同类允许排序
							return true;
						}
						return false;
					}
				},
				showRemoveBtn : true,
				showRenameBtn : true,
				removeTitle : '删除',
				renameTitle : '编辑'
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					var dataType = treeNode.dataType;
					if (dataType == undefined || dataType == null) {
						return;
					}
					Core.fn($zone, 'showDataMainZone')(dataType);
				},
				beforeRemove : function(treeId, treeNode) {
					if (!treeNode.isDataType) {
						Core.fn($zone, 'delCatelog')(treeNode.catelogId);
					} else {
						Core.fn($zone, 'delDataType')(treeNode);
					}
					return false;
				},
				beforeEditName : function(treeId, treeNode) {
					if (!treeNode.isDataType) {
						Core.fn($zone, 'catelogWin')(treeNode.catelogId);
					} else {
						Core.fn($zone, 'editDataType')(treeNode);
					}
					return false;
				}
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		zTree.expandNode(zTree.getNodes()[0]);
	});
</script>

<ul id="${_zone}_tree">
	<textarea>${wcm:json(tree)}</textarea>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>