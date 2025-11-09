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

		var $zone = $('#${_zone}');
		var $tree = $("#${_zone}_tree", $zone);
		var treeSetting = {
			view : {
				fontCss : function(treeId, node) {
					var fontCss = {};
					if (node.color) {
						fontCss = {
							'font-weight' : 'bold'
						};
						fontCss.color = node.color;
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
					return treeNode._type != undefined && treeNode._type != 'sys';
				},
				showRenameBtn : false,
				removeTitle : '删除'
			},
			callback : {
				beforeRemove : function(treeId, treeNode) {
					Ui.confirm('确认删除[' + treeNode.busiName + ']?', function() {
						Core.fn($zone, 'deleteColumn')(treeNode);
					});
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
				var data = {
					pixel : treeNode._id,
					treeId : '${_zone}_tree',
					id : treeNode.id
				};
				if (treeNode._type == 'show') {
					url = '${_acp}/columnShowConfigForm.shtml';
				} else if (treeNode._type == 'form') {
					url = '${_acp}/columnFormConfigForm.shtml';
				}
				Ajax.post($target, url, {
					data : data
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
			if (treeNode._type == 'show') {
				url = '${_acp}/columnShowConfigForm.shtml';
			} else if (treeNode._type == 'form') {
				url = '${_acp}/columnFormConfigForm.shtml';
			}
			Ajax.post($target, url, {
				data : {
					pixel : treeNode._id,
					treeId : '${_zone}_tree'
				}
			});

			Core.fn($zone, 'storeTree')();
		});

		// 展示字段
		$('button[name=addShow]', $zone).click(function() {
			var treeNode = {};
			treeNode.icon = _cp + "/css/icon/application_add.png";
			treeNode.color = "red";
			treeNode._type = "show";
			treeNode.title = "展示字段";
			treeNode._id = "show_auto_" + Core.nextSeq();
			treeNode.busiName = "(未命名)";
			Core.fn($zone, 'addColumn')(treeNode);
		});

		//表单字段
		$('button[name=addForm]', $zone).click(function() {
			var treeNode = {};
			treeNode.icon = _cp + "/css/icon/application_form_add.png";
			treeNode.color = "red";
			treeNode._type = "form";
			treeNode.title = "表单字段";
			treeNode._id = "form_auto_" + Core.nextSeq();
			treeNode.busiName = "(未命名)";
			Core.fn($zone, 'addColumn')(treeNode);
		});

		//重新加载
		$('button[name=refresh]', $zone).click(function() {
			Ui.confirm('确认重新加载?', function() {
				Ajax.post($zone, '${_acp}/columnConfigForm.shtml', {
					data : {
						widgetKey : '${param.widgetKey}'
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
	<div class="left">
		<span class="ws-group">
			<button icon="plus" type="button" name="addShow">展示字段</button>
			<button icon="plus" type="button" name="addForm">表单字段</button>
		</span>
	</div>
	<div class="right">
		<button type="button" icon="refresh" name="refresh">重新加载</button>
	</div>
</div>

<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 300px;">
		<div panel="字段列表(拖拽排序)">
			<ul id="${_zone}_tree">
				<textarea>${wcm:json(list)}</textarea>
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