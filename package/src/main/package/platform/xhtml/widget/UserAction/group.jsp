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
					idKey : "groupKey",
					pIdKey : "parentKey"
				}
			},
			check : {
				enable : true,
				chkboxType : {
					"Y" : "",
					"N" : ""
				},
				chkStyle : '${param.checkType}',
				radioType : "all"
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					if (treeNode.checked) {
						$.fn.zTree.getZTreeObj(treeId).checkNode(treeNode, false, true);
					} else {
						$.fn.zTree.getZTreeObj(treeId).checkNode(treeNode, true, true);
					}
				},
				onDblClick : function(event, treeId, treeNode) {
					$.fn.zTree.getZTreeObj(treeId).checkAllNodes(false);
					$.fn.zTree.getZTreeObj(treeId).checkNode(treeNode, true, true);
					Core.fn($zone, 'confirmFn')($zone);
				}
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		var $selectedZone = $("#${_zone}_selected_zone", $zone);

		$.each(datas, function(i, o) {
			//增加icon
			o.icon = "${_cp}/css/icon/group.png";
			//已选中
			o.checked = $('li[name=' + o.groupKey + ']', $selectedZone).size() > 0;
		});

		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		zTree.expandAll(true);
	});
</script>

<ul style="display: none;" id="${_zone}_selected_zone">
	<c:forEach items="${values}" var="v">
		<li name="${v}">${v}</li>
	</c:forEach>
</ul>

<ul id="${_zone}_tree">
	<textarea>${wcm:json(tree)}</textarea>
</ul>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>