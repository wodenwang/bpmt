<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		// 删除
		Core.fn('${_zone}_tree_zone', 'remove', function(id) {
			Ui.confirmPassword('确认删除?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/remove.shtml', {
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

		// 删除
		Core.fn('${_zone}_tree_zone', 'suspend', function(id, flag) {
			Ui.confirm('确认' + (flag ? '暂停' : '激活') + '?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/suspend.shtml', {
					data : {
						id : id,
						flag : flag ? 1 : 0
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
						}
					}
				});
			});
		});

		// 配置
		Core.fn('${_zone}_tree_zone', 'config', function(id) {
			Ajax.post('${_zone}_detail_zone', '${_acp}/config.shtml', {
				data : {
					id : id
				},
				callback : function(flag) {
					if (flag) {
						Core.fn('${_zone}_detail_zone', 'refresh', function() {
							Core.fn('${_zone}_tree_zone', 'config')(id);
						});
					}
				}
			});
		});
		Core.fn('${_zone}_detail_zone', 'config', function(id) {
			Core.fn('${_zone}_tree_zone', 'config')(id);
		});

		//修改流程类型
		Core.fn('${_zone}_tree_zone', 'editCategory', function(id) {
			Ajax.post('${_zone}_detail_zone', '${_acp}/editCategory.shtml', {
				data : {
					pdKey : id
				},
				callback : function(flag) {
					if (flag) {
						Core.fn('${_zone}_detail_zone', 'refresh', function() {
							Core.fn($zone, 'refresh')();
							Core.fn('${_zone}_tree_zone', 'editCategory')(id);
						});
					}
				}
			});
		});

		//刷新树
		Core.fn($zone, 'refresh', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/tree.shtml');
		});

		//刷新事件
		$('button[name=refresh]', $zone).on('click', function() {
			Core.fn($zone, 'refresh')();
		});
		//初始化调用
		$('button[name=refresh]', $zone).click();

		//批量查询展开按钮
		$('button[name=expand]', $zone).click(function() {
			var $this = $(this);
			var $table = $this.parents('table:first');
			if ($('tr:hidden:not(.last-child)', $table).size() < 1) {
				$('tr:not(.last-child)', $table).hide('fast');
			} else {
				$('tr:not(.last-child)', $table).show('fast');
			}
		});

		//编辑
		Core.fn('${_zone}_batch_result_zone', 'edit', function(title, json) {
			var $win = Ajax.win('${_acp}/editBatch.shtml', {
				title : title,
				minWidth : 1024,
				data : {
					hbm : json.hbm,
					id : json.id,
					pdId : json.pdId,
					activityId : json.activityId,
					flowId : json.flowId
				},
				buttons : [ {
					icons : {
						primary : "ui-icon-close"
					},
					text : '取消',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					icons : {
						primary : "ui-icon-check"
					},
					text : '确认',
					click : function() {
						var $this = $(this);
						var $form = $('form', $this);
						var $errorZone = $('div[name=errorZone]', $this);
						Ajax.form('${_zone}_batch_msg_zone', $form, {
							errorZone : $errorZone.attr('id'),
							confirmMsg : '确认提交本次修改?',
							callback : function(flag) {
								if (flag) {
									$this.dialog("close");
									$("#${_zone}_batch_form").submit();
								}
							}
						});

					}
				} ]
			});

		});

	});
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<div title="流程定义配置">
		<div class="ws-bar">
			<div class="left ws-group">
				<button icon="refresh" type="button" name="refresh">刷新</button>
			</div>
		</div>
		<div style="overflow: auto; zoom: 1;">
			<div style="float: left; width: 300px;">
				<div panel="流程分类列表">
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
			</div>
			<div style="margin-left: 320px; overflow: auto; zoom: 1;" id="${_zone}_detail_zone"></div>
		</div>
	</div>
	<div title="快捷设置">
		<form action="${_acp}/batchList.shtml" zone="${_zone}_batch_result_zone" id="${_zone}_batch_form" method="get" query="true">
			<table class="ws-table">
				<tr>
					<th>流程Key</th>
					<td><wcm:widget name="pdKey" cmd="text" /></td>
					<th>版本</th>
					<td><input type="radio" name="version" value="last" checked="checked" /><label tip="true" title="配合[流程Key]使用,未填写[流程Key]则默认搜索所有版本">最新</label> <input type="radio" name="version" value="all" /><label>所有</label></td>
				</tr>
				<tr>
					<th>流程名称</th>
					<td><wcm:widget name="pdName" cmd="text" /></td>
					<th>节点名称</th>
					<td><wcm:widget name="flowNodeName" cmd="text" /></td>
				</tr>
				<tr>
					<th>设置类型</th>
					<td colspan="3"><input type="radio" name="type" value="column" checked="checked" /><label>字段</label><input type="radio" name="type" value="column_all" /><label tip="true"
						title="查询时需要耐心等待.">字段(含基础视图)</label> <input type="radio" name="type" value="button" /><label>按钮</label> <input type="radio" name="type" value="exec" /><label>处理器</label> <input type="radio"
						name="type" value="person" /><label>人员分配</label></td>
				</tr>
				<tr>
					<th>描述<font color="red" tip="true" title="字段,按钮等的名称或配置描述,多个关键字用空格分隔.">(提示)</font></th>
					<td colspan="3"><wcm:widget name="description" cmd="text[90%]" /></td>
				</tr>
				<tr>
					<th>脚本<font color="red" tip="true" title="输入脚本关键字,多个关键字用空格分隔.">(提示)</font></th>
					<td colspan="3"><wcm:widget name="script" cmd="textarea[90%]" /></td>
				</tr>
				<tr>
					<th class="ws-bar">
						<div class="ws-group left">
							<button type="button" icon="arrowthick-2-n-s" name="expand">展开/收缩查询框</button>
						</div>
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>
		<div id="${_zone}_batch_msg_zone"></div>
		<div id="${_zone}_batch_result_zone">
			<div class="ws-msg normal">请输入查询条件并点击"查询"按钮.</div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>