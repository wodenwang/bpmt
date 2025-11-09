<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//表单页
		Core.fn($zone, 'submitForm', function(form, tabid, option) {
			option = $.extend({}, option, {
				callback : function(flag) {
					if (flag) {
						Ui.closeTab(tabid);
						$('#${_zone}_list_form').submit();
					}
				}
			});
			Ajax.form('${_zone}_msg', form, option);
		});

		//列表页
		Core.fn('${_zone}_list', 'create', function() {
			var $tab = Ui.openTab('创建动态表', '${_acp}/createZone.shtml');
			//表单页
			Core.fn($tab, 'submitForm', function(form, option) {
				Core.fn($zone, 'submitForm')(form, $tab.attr('id'), option);
			});
		});
		Core.fn('${_zone}_list', 'del', function(name) {
			Ui.confirmPassword('确认删除动态表?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/delete.shtml?name=' + name, {
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});
		Core.fn('${_zone}_list', 'lock', function(name) {
			Ajax.post('${_zone}_msg', '${_acp}/lock.shtml', {
				data : {
					name : name,
					lockFlag : '1'
				},
				callback : function(flag) {
					if (flag) {
						$('#${_zone}_list_form').submit();
					}
				}
			});
		});
		Core.fn('${_zone}_list', 'unlock', function(name) {
			Ui.confirmPassword('确认解锁?', function() {
				Ajax.post('${_zone}_msg', '${_acp}/lock.shtml', {
					data : {
						name : name,
						lockFlag : '0'
					},
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});
		});
		Core.fn('${_zone}_list', 'edit', function(name) {
			var $tab = Ui.openTab('编辑表[' + name + ']结构', '${_acp}/editZone.shtml?name=' + name);
			//表单页
			Core.fn($tab, 'submitForm', function(form, option) {
				Core.fn($zone, 'submitForm')(form, $tab.attr('id'), option);
			});
		});

		Core.fn('${_zone}_list', 'index', function(name) {
			var $tab = Ui.openTab('管理表[' + name + ']索引', '${_acp}/editIndex.shtml?tableName=' + name);
			//表单页
			Core.fn($tab, 'submitForm', function(form, option) {
				Core.fn($zone, 'submitForm')(form, $tab.attr('id'), option);
			});
		});

		Core.fn('${_zone}_list', 'preview', function(name) {
			Ajax.win('${_acp}/preview.shtml?name=' + name, {
				title : '表[' + name + ']数据预览',
				minWidth : 800,
				minHeight : 500
			});
		});
		Core.fn('${_zone}_list', 'show', function(name) {
			Ui.openTab('查看表[' + name + ']结构', '${_acp}/detailZone.shtml?name=' + name);
		});
		Core.fn('${_zone}_list', 'exportType', function() {
			var $checkbox = $('#${_zone}_download_form input:checked[name=_keys]');
			if ($checkbox.size() < 1) {
				Ui.alert("请选择至少一项。");
				return;
			}
			Ui.confirm('确认导出表结构?', function() {
				var random = Math.random();
				Ajax.download('${_acp}/exportType.shtml?_random=' + random + '&' + $('#${_zone}_download_form').serialize());
				Ajax.loadingWin(random);
			});
		});
		Core.fn('${_zone}_list', 'exportData', function() {
			var $checkbox = $('#${_zone}_download_form input:checked[name=_keys]');
			if ($checkbox.size() < 1) {
				Ui.alert("请选择至少一项。");
				return;
			}
			Ui.confirm('确认导出表数据?', function() {
				var random = Math.random();
				Ajax.download('${_acp}/exportData.shtml?_random=' + random + '&' + $('#${_zone}_download_form').serialize());
				Ajax.loadingWin(random);
			});
		});

		Core.fn('${_zone}_list', 'exportDataExt', function() {
			var $checkbox = $('#${_zone}_download_form input:checked[name=_keys]');
			if ($checkbox.size() < 1) {
				Ui.alert("请选择至少一项。");
				return;
			}
			Ui.confirm('确认导出表数据?', function() {
				var random = Math.random();
				Ajax.download('${_acp}/exportDataExt.shtml?_random=' + random + '&' + $('#${_zone}_download_form').serialize());
				Ajax.loadingWin(random);
			});
		});

		Core.fn('${_zone}_list', 'batchZone', function() {
			Ajax.win('${_acp}/batchZone.shtml', {
				title : '表结构导入',
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
							loading : true,
							callback : function(flag) {
								if (flag) {
									$dialog.dialog("close");
									$('#${_zone}_list_form').submit();
								}
							}
						};
						Ajax.form('${_zone}_msg', $form, option);
					}
				} ]
			});
		});
		Core.fn('${_zone}_list', 'importDataZone', function() {
			Ajax.win('${_acp}/importDataZone.shtml', {
				title : '批量数据导入',
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
							loading : true,
							confirmMsg : '确认导入数据?',
							errorZone : $msgZone.attr('id'),
							callback : function(flag) {
								if (flag) {
									$dialog.dialog("close");
								}
							}
						};
						Ajax.form('${_zone}_msg', $form, option);
					}
				} ]
			});
		});
		Core.fn('${_zone}_list', 'link', function() {
			Ajax.win('${_acp}/linkZone.shtml', {
				title : '关联游离表',
				minWidth : 600,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '确认',
					click : function() {
						var $dialog = $(this);
						var $form = $('form', $dialog);
						var $msgZone = $('div[name=msgZone]', $dialog);
						var option = {
							confirmMsg : '确认提交?',
							errorZone : $msgZone.attr('id'),
							callback : function(flag) {
								if (flag) {
									$dialog.dialog("close");
									$('#${_zone}_list_form').submit();
								}
							}
						};
						Ajax.form('${_zone}_msg', $form, option);
					}
				} ]
			});
		});

		//同步结构
		Core.fn('${_zone}_list', 'syncType', function() {
			var $checkbox = $('#${_zone}_download_form input:checked[name=_keys]');
			if ($checkbox.size() < 1) {
				Ui.alert("请选择至少一项。");
				return;
			}

			Ui.confirmPassword('确认同步选中的动态表?', function() {
				var array = [];
				$checkbox.each(function() {
					array.push($(this).val());
				});

				Ajax.post('${_zone}_msg', '${_acp}/syncType.shtml', {
					data : {
						_keys : array
					},
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_list_form').submit();
						}
					}
				});
			});

		});

		//初始化查询
		$('#${_zone}_list_form').submit();
	});
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<div title="动态表汇总">
		<form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
			<input type="hidden" name="_field" value="updateDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>表名(精确)</th>
					<td><wcm:widget name="_se_name" cmd="text">不支持命令</wcm:widget></td>
					<th>表名(模糊)</th>
					<td><wcm:widget name="_sl_name" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th>展示名(精确)</th>
					<td><wcm:widget name="_se_description" cmd="text">不支持命令</wcm:widget></td>
					<th>展示名(模糊)</th>
					<td><wcm:widget name="_sl_description" cmd="text">不支持命令</wcm:widget></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<%--错误提示区域 --%>
		<div id="${_zone}_msg"></div>

		<form id="${_zone}_download_form">
			<%--查询结果 --%>
			<div id="${_zone}_list"></div>
		</form>

	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>