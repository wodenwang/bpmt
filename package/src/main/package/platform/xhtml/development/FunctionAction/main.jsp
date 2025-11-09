<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $treeZone = $('#${_zone}_tree_zone');

		//刷新树
		Core.fn($zone, 'refreshTree', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/catelogs.shtml');
			$('#${_zone}_function_main_zone').html('');
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
									Core.fn($zone, 'refreshTree')();
								}
							}
						};
						Ajax.form('${_zone}_tree_msg_zone', $form, option);
					}
				} ]
			});
		});

		//树区域方法定义
		Core.fn('${_zone}_tree_zone', 'showFunctionMainZone', function(cateKey) {
			Ajax.post('${_zone}_function_main_zone', '${_acp}/functionMainZone.shtml', {
				data : {
					cateKey : cateKey
				}
			});
		});

		//编辑事件
		Core.fn('${_zone}_tree_zone', 'editCatelog', function(node) {
			if (node == null) {
				return;
			}
			Core.fn($zone, 'winZone')('编辑类别[' + node.busiName + ']', '${_acp}/updateCatelogZone.shtml?cateKey=' + node.cateKey);
		});

		//删除事件
		Core.fn('${_zone}_tree_zone', 'delCatelog', function(node) {
			if (node == null) {
				return;
			}
			Ui.confirmPassword('确认删除[' + node.busiName + ']分类及其数据(包含所有子孙分类及其对应数据)?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/deleteCatelog.shtml', {
					data : {
						cateKey : node.cateKey
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refreshTree')();
						}
					}
				});
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
				$.each(nodes, function(i, node) {
					var obj = {};
					obj.cateKey = node.cateKey;
					obj.parentKey = node.getParentNode() ? node.getParentNode().cateKey : null;
					obj.sort = i;
					tree.catelogs.push(obj);
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
		$('button[name=add]', $zone).click(function() {
			Core.fn($zone, 'winZone')('新增类别', '${_acp}/createCatelogZone.shtml');
		});

		//刷新事件
		$('button[name=refresh]', $zone).on('click', function() {
			Core.fn($zone, 'refreshTree')();
		});

		//初始化树
		Core.fn($zone, 'refreshTree')();

	});
</script>
<div tabs="true">
	<div title="函数设置">
		<div style="overflow: auto; zoom: 1;">
			<div tabs="true" style="float: left; width: 320px;">
				<div title="函数类别">
					<div class="ws-bar ws-group">
						<button type="button" icon="plus" text="true" name="add">增类别</button>
						<button icon="refresh" type="button" text="true" name="refresh">刷新</button>
						<button icon="disk" type="button" text="true" name="saveSort">存位置</button>
					</div>
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
				<div title="查询">
					<form action="${_acp}/functionMainZone.shtml" zone="${_zone}_function_main_zone">
						<table class="ws-table">
							<tr>
								<th>名称</th>
								<td><input type="text" name="functionKey" style="width: 150px;" /></td>
							</tr>
							<tr>
								<th>描述</th>
								<td><input type="text" name="description" style="width: 150px;" /></td>
							</tr>
							<tr>
								<th>脚本</th>
								<td><textarea name="functionScript" style="width: 150px;"></textarea></td>
							</tr>
							<tr>
								<th class="ws-bar">
									<button type="submit" icon="search">查询</button>
								</th>
							</tr>
						</table>
					</form>
				</div>
			</div>
			<div style="margin-left: 330px; min-height: 500px; overflow: auto; zoom: 1;" id="${_zone}_function_main_zone"></div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>