<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $tree = $("#${_zone}_tree");
		var treeSetting = {
			data : {
				simpleData : {
					enable : true,
					idKey : "code",
					pIdKey : "parentCode"
				},
				key : {
					name : 'showName',
					title : 'showName'
				}
			}
		};
		var strData = $('textarea', $tree).val();
		var datas = eval("(" + strData + ")");
		var ztree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		ztree.expandAll(true);
	});
</script>

<ul id="${_zone}_tree">
	<textarea>${wcm:json(list)}</textarea>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>