<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var type = '${type}';
		var treeSetting = {
			data : {
				key : {
					name : "busiName"
				},
				simpleData : {
					enable : true,
					idKey : "uid",
					pIdKey : "parentId"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : type == '2',
					inner : function(treeId, nodes, targetNode) {
						return false;
					},
					next : function(treeId, nodes, targetNode) {
						return type == '2';
					},
					prev : function(treeId, nodes, targetNode) {
						return type == '2';
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

		//保存排序按钮
		$('button[name=saveSort]', $zone).click(function() {
			Core.fn($zone, 'saveSort')();
		});

		//搜索框
		$('input[name=search]', $zone).bind('keydown', function(e) {
			var key = e.which;
			var search = $(this).val();
			if (key == 13) {
				e.preventDefault();
				Core.fn($zone, 'loadTree')('1', search);
			}
		});
	});
</script>

<c:if test="${type==1}">
	<div>
		<span style="font-weight: bold;">快捷查询:</span> <input type="text" name="search" placeholder=" 输入账号ID或姓名" value="${param.search}" />
	</div>
</c:if>

<ul id="${_zone}_tree">
	<data>${tree}</data>
</ul>

<c:if test="${type==2}">
	<div class="ws-bar">
		<button icon="disk" type="button" name="saveSort">保存排序</button>
	</div>
</c:if>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>