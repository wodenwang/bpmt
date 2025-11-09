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
					inner : function(treeId, nodes, targetNode) {
						var srcNode = nodes[0];
						if (srcNode.type == 1) {//域节点
							return false;
						}

						if (targetNode == null || targetNode.unManageFlag) {//不允许拖到无主域
							return false;
						}

						if (targetNode.type == 1) {
							//已更换了域
							var _tree = $.fn.zTree.getZTreeObj('${_zone}_tree');
							$.each(_tree.getNodesByParam("domainKey", srcNode.domainKey, srcNode), function(i, node) {
								node.domainKey = targetNode.domainKey;
								_tree.updateNode(node);
							});
							srcNode.domainKey = targetNode.domainKey;
							_tree.updateNode(srcNode);
						}

						return true;
					},
					next : function(treeId, nodes, targetNode) {
						var srcNode = nodes[0];
						if (srcNode.unManageFlag) {// 无主域不允许拖拽
							return false;
						}
						if (targetNode.unManageFlag) {// 不允许拖拽到无主域后面
							return false;
						}

						if (srcNode.type == targetNode.type) { //同类型才允许拖拽
							if (srcNode.type != 1) {
								//已更换了域
								var _tree = $.fn.zTree.getZTreeObj('${_zone}_tree');
								$.each(_tree.getNodesByParam("domainKey", srcNode.domainKey, srcNode), function(i, node) {
									node.domainKey = targetNode.domainKey;
									_tree.updateNode(node);
								});
								srcNode.domainKey = targetNode.domainKey;
								_tree.updateNode(srcNode);
							}
							return true;
						} else {
							return false;
						}
					},
					prev : function(treeId, nodes, targetNode) {
						var srcNode = nodes[0];
						if (srcNode.unManageFlag) {// 无主域不允许拖拽
							return false;
						}

						if (srcNode.type == targetNode.type) { //同类型才允许拖拽
							if (srcNode.type != 1) {
								//已更换了域
								var _tree = $.fn.zTree.getZTreeObj('${_zone}_tree');
								$.each(_tree.getNodesByParam("domainKey", srcNode.domainKey, srcNode), function(i, node) {
									node.domainKey = targetNode.domainKey;
									_tree.updateNode(node);
								});
								srcNode.domainKey = targetNode.domainKey;
								_tree.updateNode(srcNode);
							}
							return true;
						} else {
							return false;
						}
					}
				},
				showRemoveBtn : function(treeId, treeNode) {
					if (treeNode.sysFlag) {// 不允许删除无主节点
						return false;
					}

					return true;
				},
				showRenameBtn : false,
				removeTitle : '删除'
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					Core.fn($zone, 'delNode')(treeNode);
					return false;
				},
				onClick : function(event, treeId, treeNode) {
					if (treeNode.unManageFlag) {
						return false;
					}
					Core.fn($zone, 'editZone')(treeNode);
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
	<data>${menu}</data>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>