<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
/*重写edit按钮的图标样式*/
.usertask.ztree li span.button.edit {
	margin-right: 2px;
	background-image: url("${_cp}/css/icon/plugin.png");
	background-position: 0px 0px;
	vertical-align: top;
	*vertical-align: middle;
}
</style>


<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $mainZone = $('#${_zone}_main_zone');
		var $deleteZone = $('#${_zone}_delete_zone');
		var $waitZone = $('#${_zone}_wait_zone');

		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
			view : {
				fontCss : function(treeId, node) {
					var fontCss = {};
					if (node.color) {
						fontCss.color = node.color;
						fontCss['font-weight'] = 'bold';
						if (node.color == 'gray') {//灰色加删除线
							fontCss['text-decoration'] = 'line-through';
						}
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
					idKey : "_id"
				}
			},
			edit : {
				enable : true,
				drag : {
					autoExpandTrigger : true,
					isCopy : false,
					isMove : true,
					inner : false
				},
				showRemoveBtn : function(treeId, treeNode) {
					if (treeNode.hasRemove) {
						return true;
					}
					return false;
				},
				showRenameBtn : function(treeId, treeNode) {
					if (treeNode.hasExtend) {
						return true;
					}
					return false;
				},
				removeTitle : '删除',
				renameTitle : '继承'
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					Ui.confirm('确认删除[' + treeNode.busiName + ']?', function() {
						Core.fn($zone, 'deleteColumn')(treeNode);
					});
					return false;
				},
				beforeEditName : function(treeId, treeNode) {
					Core.fn($zone, 'extendColumn')(treeNode);
					return false;
				},
				onClick : function(event, treeId, treeNode) {
					Core.fn($zone, 'editColumn')(treeNode);
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

		//继承/取消继承
		Core.fn($zone, 'extendColumn', function(treeNode) {
			if (treeNode.showFlag != 1) {
				treeNode.showFlag = 1;
				treeNode.color = 'blue';
			} else {
				treeNode.showFlag = 0;
				treeNode.color = 'gray';
			}
			zTree.refresh();
			Core.fn($zone, 'storeTree')();
		});

		//保存树数据
		Core.fn($zone, 'storeTree', function() {
			$waitZone.children().remove();
			var nodes = zTree.transformToArray(zTree.getNodes());
			$.each(nodes, function(i, node) {
				var obj = {};
				obj.type = node._type;
				obj.pixel = node._id;
				obj.sort = i;
				obj.id = node.id;
				obj.showFlag = node.showFlag;
				obj.pixelKey = node.pixelKey;
				var $textarea = $('<textarea name="waitColumn"></textarea>');
				$textarea.val(JSON.stringify(obj));
				$waitZone.append($textarea);
			});
		});

		//删除字段
		Core.fn($zone, 'deleteColumn', function(treeNode) {
			if (treeNode.id) {// 编辑的情况下才需要
				var $textarea = $('<textarea name="deleteColumn"></textarea>');
				var json = {};
				json.id = treeNode.id;
				json.type = treeNode._type;
				$textarea.val(JSON.stringify(json));
				$deleteZone.append($textarea);
			}

			$('div[name="' + treeNode._id + '"]', $mainZone).remove();
			zTree.removeNode(treeNode);
			Core.fn($zone, 'storeTree')();
		});

		//编辑字段
		Core.fn($zone, 'editColumn', function(treeNode) {
			//先隐藏
			$mainZone.children().hide();

			//查找在不在
			var $target = $('div[name="' + treeNode._id + '"]', $mainZone);
			if ($target.size() > 0) {
				//存在
				$target.show();
			} else {
				//不存在
				$target = $('<div name="'+treeNode._id+'"></div>');
				$mainZone.append($target);
				var url;
				if (treeNode._type == 'self_line') {
					url = '${_acp}/columnSelfLineConfigForm.shtml';
				} else if (treeNode._type == 'self_form') {
					url = '${_acp}/columnSelfFormConfigForm.shtml';
				} else {
					url = '${_acp}/columnExtendConfigForm.shtml';
				}
				Ajax.post($target, url, {
					data : {
						pdId : '${param.pdId}',
						pixel : treeNode._id,
						type : treeNode._type,
						treeId : '${_zone}_tree',
						id : treeNode.id,
						tableName : '${tableName}'
					}
				});
			}
		});

		//新增字段
		Core.fn($zone, 'addColumn', function(treeNode) {
			//先隐藏
			$mainZone.children().hide();

			//增加节点
			var nodes = zTree.addNodes(null, treeNode);
			zTree.selectNode(nodes[0]);

			//目标区域
			var $target = $('<div name="'+treeNode._id+'"></div>');
			$mainZone.append($target);
			var url;
			if (treeNode._type == 'self_line') {
				url = '${_acp}/columnSelfLineConfigForm.shtml';
			} else {
				url = '${_acp}/columnSelfFormConfigForm.shtml';
			}
			Ajax.post($target, url, {
				data : {
					pixel : treeNode._id,
					treeId : '${_zone}_tree',
					tableName : '${tableName}'
				}
			});

			Core.fn($zone, 'storeTree')();
		});

		//节点字段
		$('button[name=addForm]', $zone).click(function() {
			var treeNode = {};
			treeNode.icon = _cp + "/css/icon/application_form_add.png";
			treeNode.color = "red";
			treeNode._type = "self_form";
			treeNode.title = "节点字段";
			treeNode._id = "form_auto_" + Core.nextSeq();
			treeNode.busiName = "(未命名)";
			treeNode.hasRemove = true;
			Core.fn($zone, 'addColumn')(treeNode);
		});

		//分割线
		$('button[name=addLine]', $zone).click(function() {
			var treeNode = {};
			treeNode.icon = _cp + "/css/icon/bookmark_add.png";
			treeNode.color = "red";
			treeNode._type = "self_line";
			treeNode.title = "分割线";
			treeNode._id = "line_auto_" + Core.nextSeq();
			treeNode.busiName = "(未命名)";
			treeNode.hasRemove = true;
			Core.fn($zone, 'addColumn')(treeNode);
		});

		//继承所有
		$('button[name=extendAll]', $zone).click(function() {
			Ui.confirm('是否继承视图字段的展示?', function() {
				Ajax.post($zone, '${_acp}/columnFrom.shtml', {
					data : {
						pdId : '${param.pdId}',
						extendFlag : 'true',
						sortFlag : '${param.sortFlag}'
					}
				});
			});
		});

		//视图排序
		$('button[name=sortAll]', $zone).click(function() {
			Ui.confirm('是否采用基础视图的字段排序?', function() {
				Ajax.post($zone, '${_acp}/columnFrom.shtml', {
					data : {
						pdId : '${param.pdId}',
						extendFlag : '${param.extendFlag}',
						sortFlag : 'true'
					}
				});
			});
		});

		//取消继承
		$('button[name=restoreAll]', $zone).click(function() {
			Ui.confirm('是否取消视图字段的继承与排序操作?', function() {
				Ajax.post($zone, '${_acp}/columnFrom.shtml', {
					data : {
						pdId : '${param.pdId}',
						extendFlag : 'false'
					}
				});
			});
		});

		//根据初始化的树生成数据
		Core.fn($zone, 'storeTree')();

	});
</script>

<input type="hidden" name="hasColumns" value="true" />
<div class="ws-bar">
	<div class="left ws-group">
		<button icon="plus" type="button" name="addForm">节点字段</button>
		<button icon="plus" type="button" name="addLine">分割线</button>
	</div>
	<div class="right">
		<span class="ws-group">
			<button icon="arrowthickstop-1-s" type="button" name="extendAll">基础继承</button>
			<button icon="arrowthick-2-n-s" type="button" name="sortAll">基础排序</button>
		</span>

		<button icon="arrowreturnthick-1-w" type="button" name="restoreAll">恢复</button>
	</div>
</div>

<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="字段列表(拖拽排序)">
			<ul id="${_zone}_tree" class="usertask">
				<textarea>${wcm:json(columns)}</textarea>
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