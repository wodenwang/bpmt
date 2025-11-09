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
					name : "name"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "parentId"
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					if (!treeNode.isRole) {
						return;
					}
					if (treeNode.currentRole) {
						return;
					}
					Ui.confirm('${wpf:lan("#:zh[确认切换角色?]:en[Confirm the switch roles?]#")}', function() {
						Ajax.post('${_zone}_msg', '${_acp}/submitChangeGroup.shtml', {
							data : {
								groupKey : treeNode.groupKey,
								roleKey : treeNode.roleKey
							},
							callback : function(flag) {
								if (flag) {
									Ajax.jump('${_cp}/index.jsp');
								}
							}
						});
					});
				}
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		zTree.expandAll(true);
	});
</script>

<div id="${_zone}_msg"></div>

<ul id="${_zone}_tree">
	<textarea>${wcm:json(tree)}</textarea>
</ul>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>