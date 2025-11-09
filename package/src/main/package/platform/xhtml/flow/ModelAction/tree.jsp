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
					idKey : "id",
					pIdKey : "category"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : false,
				},
				showRemoveBtn : function(treeId, treeNode) {
					if (treeNode.sysFlag == 1) {
						return false;
					}

					return treeNode.isModel;
				},
				showRenameBtn : false,
				removeTitle : '删除'
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					if (treeNode.isModel) {
						Core.fn($zone, 'del')(treeNode.id);
					}
					return false;
				},
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					if (treeNode.isModel) {
						Core.fn($zone, 'detail')(treeNode.id);
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

<ul id="${_zone}_tree">
	<data>${list}</data>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>