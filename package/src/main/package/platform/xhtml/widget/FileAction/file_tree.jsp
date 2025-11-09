<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}")
		var $tree = $("#${_zone}_tree");
		var treeSetting = {
			data : {
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parentId"
				}
			},
			edit : {
				enable : true,
				showRemoveBtn : true,
				showRenameBtn : false
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					if (treeNode.fileFlag) {
						Core.fn($zone, 'showDetail')(treeNode.name);
						$.fn.zTree.getZTreeObj(treeId).checkNode(treeNode);
					}
				},
				beforeRemove : function(treeId, treeNode) {
					if (treeNode.fileFlag) {
						Ui.confirm('确认删除[' + treeNode.name + ']?', function() {
							Core.fn($zone, 'delete')(treeNode.name);
						});
					}
					return false;
				}
			},
			check : {
				enable : true,
				chkStyle : $zone.attr("checkType"),
				radioType : "all"
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		//选中第一个节点
		if ('${param.checkfirst}' == 'true') {
			for (var i = 0; i < datas.length; i++) {
				if (datas[i].fileFlag) {
					datas[i].checked = true;
					break;
				}
			}
		}
		var ztree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		ztree.expandAll(true);

	});
</script>

<ul id="${_zone}_tree">
	<textarea>${trees}</textarea>
</ul>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>