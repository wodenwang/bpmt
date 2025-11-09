<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
/*重写edit按钮的图标样式*/
.pdTreeStyle li span.button.edit {
	margin-right: 2px;
	background-image: url("${_cp}/css/icon/pause_blue.png");
	background-position: 0px 0px;
	vertical-align: top;
	*vertical-align: middle;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
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
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : false,
					inner : false,
					next : false,
					prev : false
				},
				showRemoveBtn : function(treeId, treeNode) {
					return treeNode.isPd == 2;
				},
				showRenameBtn : function(treeId, treeNode) {
					return treeNode.isPd == 2;
				},
				renameTitle : '暂停/启动',
				removeTitle : '删除'
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					Core.fn($zone, 'remove')(treeNode.pdId);
					return false;
				},
				beforeEditName : function(treeId, treeNode) {
					Core.fn($zone, 'suspend')(treeNode.pdId, !treeNode.suspended);
					return false;
				},
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					if (treeNode.isPd == 1) {//编辑pd key
						Core.fn($zone, 'editCategory')(treeNode.key);
					} else if (treeNode.isPd == 2) {//编辑pd实例
						Core.fn($zone, 'config')(treeNode.pdId);
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

<ul id="${_zone}_tree" class="pdTreeStyle">
	<data>${list}</data>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>