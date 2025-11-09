<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $treeZone = $('#${_zone}_tree_zone');

		//刷新树
		Core.fn($zone, 'refresh', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/typeTree.shtml');
			$('#${_zone}_data_main_zone').html('');
		});

		//弹出编辑框
		Core.fn($zone, 'winZone', function(title, url) {
			Ajax.win(url, {
				title : title,
				minWidth : 600,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '保存',
					click : function() {
						var $dialog = $(this);
						var $form = $('form', $dialog);
						var $msgZone = $('div[name=msgZone]', $dialog);
						var option = {
							confirmMsg : '确认保存数据?',
							errorZone : $msgZone.attr('id'),
							callback : function(flag) {
								if (flag) {
									$dialog.dialog("close");
									Core.fn($zone, 'refresh')();
								}
							}
						};
						Ajax.form('${_zone}_tree_msg_zone', $form, option);
					}
				} ]
			});
		});

		//树区域方法定义
		Core.fn('${_zone}_tree_zone', 'showDataMainZone', function(dataType) {
			Ajax.post('${_zone}_data_main_zone', '${_acp}/dataMainZone.shtml', {
				data : {
					dataType : dataType
				}
			});
		});

		//编辑事件
		Core.fn('${_zone}_tree_zone', 'editDataType', function(node) {
			if (node == null) {
				return;
			}
			Core.fn($zone, 'winZone')('编辑字典类别[' + node.busiName + ']', '${_acp}/updateTypeZone.shtml?dataType=' + node.dataType);
		});

		//删除事件
		Core.fn('${_zone}_tree_zone', 'delDataType', function(node) {
			if (node == null) {
				return;
			}

			Ui.confirmPassword('确认删除[' + node.busiName + ']分类及其数据?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/deleteType.shtml', {
					data : {
						dataType : node.dataType
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
						}
					}
				});
			});
		});

		//删除类别
		Core.fn('${_zone}_tree_zone', 'delCatelog', function(id) {
			Ui.confirm('确认删除?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/deleteCatelog.shtml', {
					data : {
						id : id
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
						}
					}
				});
			});
		});

		//类别窗口
		Core.fn('${_zone}_tree_zone', 'catelogWin', function(id) {
			Ajax.win('${_acp}/catelogWin.shtml', {
				title : '管理类别',
				minWidth : 600,
				data : {
					id : id
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '提交',
					click : function() {
						var $win = $(this);
						Ajax.form('${_zone}_tree_msg_zone', $('form', $win), {
							confirmMsg : '确认提交?',
							errorZone : $('div[name=errorZone]', $win).attr('id'),
							callback : function(flag) {
								if (flag) {
									Core.fn($zone, 'refresh')();
									$win.dialog("close");
								}
							}
						});
					}
				} ]
			});
		});

		//保存排序
		$('button[name=saveSort]', $zone).click(function() {
			Ui.confirm('是否保存位置?', function() {
				var $ul = $('ul', $('#${_zone}_tree_zone'));
				var zTree = $.fn.zTree.getZTreeObj($ul.attr('id'));
				var nodes = zTree.transformToArray(zTree.getNodes());
				var tree = {};
				tree.catelogs = new Array();//最终提交到服务端的数组
				tree.types = new Array();//最终提交到服务端的数组
				$.each(nodes, function(i, node) {
					if (node.isDataType) {
						var obj = {};
						obj.dataType = node.dataType;
						obj.catelog = node.getParentNode() ? node.getParentNode().catelogId : null;
						obj.sort = i;
						tree.types.push(obj);
					} else {
						var obj = {};
						obj.id = node.catelogId;
						obj.sort = i;
						tree.catelogs.push(obj);
					}
				});

				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/saveSort.shtml', {
					data : {
						tree : JSON.stringify(tree)
					},
					callback : function(flag) {
						if (flag) {
							$('button[name=refresh]', $zone).click();
						}
					}
				});
			});
		});

		//新增事件
		$('button[name=addDataType]', $zone).click(function() {
			Core.fn($zone, 'winZone')('新增字典类别', '${_acp}/createTypeZone.shtml');
		});

		//导出所有事件
		$('button[name=downloadAll]', $zone).click(function() {
			Ui.confirm('是否导出所有?', function() {
				Ajax.download('${_acp}/downloadData.shtml');
			});
		});

		//批量导出
		$('button[name=batchAdd]', $zone).click(function() {
			Ajax.win('${_acp}/batchZone.shtml', {
				title : '批量导入',
				minWidth : 600,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '导入',
					click : function() {
						var $dialog = $(this);
						var $form = $('form', $dialog);
						var $msgZone = $('div[name=msgZone]', $dialog);
						var option = {
							confirmMsg : '确认导入数据?',
							errorZone : $msgZone.attr('id'),
							callback : function(flag) {
								if (flag) {
									$dialog.dialog("close");
									Core.fn($zone, 'refresh')();
								}
							}
						};
						Ajax.form('${_zone}_tree_msg_zone', $form, option);
					}
				} ]
			});
		});

		//新建类别
		$('button[name=addCatelog]', $zone).on('click', function() {
			Core.fn('${_zone}_tree_zone', 'catelogWin')(null);
		});

		//刷新事件
		$('button[name=refresh]', $zone).on('click', function() {
			Core.fn($zone, 'refresh')();
		});

		//初始化树
		Core.fn($zone, 'refresh')();

	});
</script>
<div tabs="true">
	<div title="字典数据设置">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="refresh" type="button" name="refresh">刷新</button>
				<button icon="disk" type="button" name="saveSort">保存位置</button>
				<button icon="plus" type="button" name="addCatelog">新建类别</button>
			</div>
			<div class="ws-group right">
				<button icon="plus" type="button" name="addDataType">新建分组</button>
				<button type="button" icon="arrowthickstop-1-s" text="true" name="downloadAll">导出所有</button>
				<button type="button" icon="plusthick" text="true" name="batchAdd">批处理</button>
			</div>
		</div>
		<div style="overflow: auto; zoom: 1;">
			<div style="float: left; width: 250px;">
				<div panel="数据分组">
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
			</div>
			<div style="margin-left: 270px;" id="${_zone}_data_main_zone"></div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>