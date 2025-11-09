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
				showRenameBtn : false,
				drag : false
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					if (treeNode.fileFlag) {
						Core.fn($zone, 'cmd')(treeNode.id);
						$.fn.zTree.getZTreeObj(treeId).checkNode(treeNode);
					}
				},
				beforeRemove : function(treeId, treeNode) {
					Ui.confirm('确认删除[' + treeNode.name + ']?', function() {
						Core.fn($zone, 'delete')(treeNode.id);
					});
					return false;
				}
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		console.log('这是一个测试：',datas.length);
		if (datas.length > 0) {
			var ztree = $.fn.zTree.init($tree, treeSetting, datas);
			$tree.addClass("ztree");
			ztree.expandAll(true);
		}else{
			$('#${_zone}_tree').html('<p style="padding:15px;">暂没有内容！</p>');
		}

	});
</script>

<ul id="${_zone}_tree"
	style="border: 1px solid #aaa;">
	<textarea>${trees}</textarea>
</ul>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>