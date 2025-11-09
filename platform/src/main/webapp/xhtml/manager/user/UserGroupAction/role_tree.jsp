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
					idKey : "roleKey"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : true,
					inner : function(treeId, nodes, targetNode) {
						return false;
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
					Core.fn($zone, 'delNode')(treeNode);
					return false;
				},
				onClick : function(event, treeId, treeNode) {
					Core.fn($zone, 'editZone')(treeNode);
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
<ul id="${_zone}_tree">
	<data>${tree}</data>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>