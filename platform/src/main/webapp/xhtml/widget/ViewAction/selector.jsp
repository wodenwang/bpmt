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
					pIdKey : "parentId"
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					if (treeNode.isModule) {
						Core.fn($zone, 'select')(treeNode);
					}
				}
			}
		};

		var strData = $('textarea', $tree).val();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		zTree.expandAll(true);

	});
</script>

<ul id="${_zone}_tree">
	<textarea>${tree}</textarea>
</ul>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>