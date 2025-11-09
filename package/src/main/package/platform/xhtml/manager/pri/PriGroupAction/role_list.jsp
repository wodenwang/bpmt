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
					name : "busiName",
					title : "title"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parentId"
				}
			},
			edit : {
				enable : false,
				showRemoveBtn : false,
				showRenameBtn : false
			}
		};

		var strData = $('textarea', $tree).html();
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