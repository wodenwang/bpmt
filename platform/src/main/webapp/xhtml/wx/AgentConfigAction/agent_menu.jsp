<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $mainZone = $('#${_zone}_main_zone');
		var $deleteZone = $('#${_zone}_delete_zone');
		var $waitZone = $('#${_zone}_wait_zone');

		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
			view : {
				fontCss : function(treeId, node) {
					var fontCss = {};
					if (node.color) {
						fontCss.color = node.color;
						fontCss['font-weight'] = 'bold';
					}
					return fontCss;
				},
				nameIsHTML : true,
				selectedMulti : false
			},
			data : {
				key : {
					name : "busiName",
					title : "title"
				},
				simpleData : {
					enable : true,
					idKey : "menuKey",
					pIdKey : "parentKey"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : true,
					inner : function(treeId, treeNodes, targetNode) {
						if (treeNodes[0].menuType == 0) {
							return false;
						} else if (targetNode.menuType == 0) {
							return true;
						} else {
							return false;
						}
					}
				},
				showRemoveBtn : true,
				showRenameBtn : false,
				removeTitle : '删除',
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					Ui.confirm('确认删除[' + treeNode.busiName + ']?', function() {
						Core.fn($zone, 'deleteMenu')(treeNode);
					});
					return false;
				},
				onClick : function(event, treeId, treeNode) {
					Core.fn($zone, 'editMenu')(treeNode);
				},
				onDrop : function(event, treeId, treeNodes, targetNode, moveType) {
					Core.fn($zone, 'storeTree')();
				}
			}
		};

		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		zTree.expandAll(true);

		//保存树数据
		Core.fn($zone, 'storeTree', function() {
			$waitZone.children().remove();
			var nodes = zTree.transformToArray(zTree.getNodes());
			$.each(nodes, function(i, node) {
				var obj = {};
				obj.sort = i;
				obj.menuKey = node.menuKey;
				obj.parentKey = node.parentKey;
				var $textarea = $('<textarea name="waitMenu"></textarea>');
				$textarea.val(JSON.stringify(obj));
				$waitZone.append($textarea);
			});
		});

		//删除字段
		Core.fn($zone, 'deleteMenu', function(treeNode) {
			if (!treeNode.isCreate) {// 编辑的情况下才需要
				var $textarea = $('<textarea name="deleteMenu"></textarea>');
				var json = {};
				json.menuKey = treeNode.menuKey;
				$textarea.val(JSON.stringify(json));
				$deleteZone.append($textarea);
			}

			$('div[name="' + treeNode.menuKey + '"]', $mainZone).remove();
			zTree.removeNode(treeNode);
			Core.fn($zone, 'storeTree')();
		});

		//编辑字段
		Core.fn($zone, 'editMenu', function(treeNode) {
			//先隐藏
			$mainZone.children().hide();

			//查找在不在
			var $target = $('div[name="' + treeNode.menuKey + '"]', $mainZone);
			if ($target.size() > 0) {
				//存在
				$target.show();
			} else {
				//不存在
				$target = $('<div name="'+treeNode.menuKey+'"></div>');
				$mainZone.append($target);
				Ajax.post($target, '${_acp}/menuForm.shtml', {
					data : {
						menuKey : treeNode.menuKey,
						treeId : '${_zone}_tree',
						agentKey : '${agent.agentKey}'
					}
				});
			}
		});

		Core.fn('${_zone}_main_zone', 'addSubMenu', function(parentKey) {
			Ajax.json('${_acp}/getMenuKey.shtml', function(result) {
				//先隐藏
				$mainZone.children().hide();
				var treeNode = {};
				treeNode.icon = _cp + "/css/icon/tab_add.png";
				treeNode.color = "red";
				treeNode.title = "一级菜单";
				treeNode.menuKey = result.menuKey;
				treeNode.busiName = "(未命名)";
				treeNode.parentKey = parentKey;
				treeNode.isCreate = true;

				//增加节点
				var parentNode = zTree.getNodeByParam('menuKey', parentKey);
				var nodes = zTree.addNodes(parentNode, treeNode);
				zTree.selectNode(nodes[0]);

				//目标区域
				var $target = $('<div name="'+treeNode.menuKey+'"></div>');
				$mainZone.append($target);
				Ajax.post($target, '${_acp}/menuForm.shtml', {
					data : {
						isCreate : 1,
						menuKey : treeNode.menuKey,
						treeId : '${_zone}_tree',
						agentKey : '${agent.agentKey}',
						menuType : 99
					}
				});
				Core.fn($zone, 'storeTree')();
			});
		});

		//新增菜单
		$('button[name=addMenu]', $zone).click(function() {
			var menuType = $(this).val();
			//获取menuKey,uuid
			Ajax.json('${_acp}/getMenuKey.shtml', function(result) {
				//先隐藏
				$mainZone.children().hide();
				var treeNode = {};
				treeNode.icon = menuType == '0' ? _cp + "/css/icon/folder_add.png" : _cp + "/css/icon/tab_add.png";
				treeNode.color = "red";
				treeNode.title = "一级菜单";
				treeNode.menuKey = result.menuKey;
				treeNode.busiName = "(未命名)";
				treeNode.menuType = menuType;
				treeNode.isCreate = true;

				//增加节点
				var nodes = zTree.addNodes(null, treeNode);
				zTree.selectNode(nodes[0]);

				//目标区域
				var $target = $('<div name="'+treeNode.menuKey+'"></div>');
				$mainZone.append($target);
				Ajax.post($target, '${_acp}/menuForm.shtml', {
					data : {
						isCreate : 1,
						menuKey : treeNode.menuKey,
						treeId : '${_zone}_tree',
						agentKey : '${agent.agentKey}',
						menuType : menuType
					}
				});
				Core.fn($zone, 'storeTree')();
			});
		});

		//初始化树
		Core.fn($zone, 'storeTree')();
	});
</script>

<input type="hidden" name="hasMenus" value="true" />

<div class="ws-bar">
	<div class="left ws-group">
		<button icon="plus" type="button" name="addMenu" value="0">一级菜单(下挂二级菜单)</button>
		<button icon="plus" type="button" name="addMenu" value="99">功能菜单</button>
	</div>
</div>

<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="菜单管理">
			<ul id="${_zone}_tree" class="agentmenu">
				<textarea>${wcm:json(menus)}</textarea>
			</ul>
		</div>
	</div>
	<div style="margin-left: 310px; overflow: auto; zoom: 1;" id="${_zone}_main_zone"></div>
</div>

<%-- 待删除的记录 --%>
<div id="${_zone}_delete_zone" style="display: none;"></div>

<%-- 待提交的树数据 --%>
<div id="${_zone}_wait_zone" style="display: none;"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>