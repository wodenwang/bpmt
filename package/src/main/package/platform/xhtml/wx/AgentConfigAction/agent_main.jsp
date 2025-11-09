<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//企业号参数配置
		$('button[name=config]', $zone).click(function() {
			var $win = Ajax.win('${_acp}/config.shtml', {
				title : '企业号配置',
				minWidth : 1024,
				buttons : [ {
					text : '取消',
					icons : {
						primary : "ui-icon-cancel"
					},
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '保存',
					icons : {
						primary : "ui-icon-disk"
					},
					click : function() {
						var $this = $(this);
						var $form = $('form', $this);
						var $msgZone = $('div[name=msgZone]', $this);
						Ajax.form('${_zone}_msg_zone', $form, {
							errorZone : $msgZone.attr('id'),
							confirmMsg : '确认保存?',
							callback : function(flag) {
								if (flag) {
									$this.dialog("close");
								}
							}
						});
					}
				} ]
			})
		});

		//用户管理
		$('button[name=user]', $zone).click(function() {
			var $win = Ajax.win('${_acp}/userSetting.shtml', {
				title : '企业号用户管理',
				minWidth : 1024,
				buttons : [ {
					text : '关闭',
					icons : {
						primary : "ui-icon-cancel"
					},
					click : function() {
						$(this).dialog("close");
					}
				} ]
			})
		});

		//新增
		Core.fn('${_zone}_list_zone', 'create', function() {
			var $tab = Ui.openTab('新建应用', '${_acp}/createZone.shtml');
			//表单页
			Core.fn($tab, 'submitCreate', function($form) {
				Ajax.form('${_zone}_msg_zone', $form, {
					confirmMsg : '确认新建企业号应用?',
					errorZone : $('div[name=msgZone]', $tab).attr('id'),
					callback : function(flag) {
						if (flag) {
							Ui.closeTab($tab);
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//删除
		Core.fn('${_zone}_list_zone', 'remove', function(agentKey) {
			Ui.confirmPassword('确认删除应用[' + agentKey + ']?', function() {
				Ajax.post('${_zone}_msg_zone', '${_acp}/removeAgent.shtml', {
					data : {
						agentKey : agentKey
					},
					callback : function(flag) {
						if (flag) {
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//设置
		Core.fn('${_zone}_list_zone', 'edit', function(agentKey) {
			var $tab = Ui.openTab('应用[' + agentKey + ']设置', '${_acp}/editZone.shtml?agentKey=' + agentKey);
			//表单页
			Core.fn($tab, 'submitEdit', function($form) {
				Ajax.form('${_zone}_msg_zone', $form, {
					confirmMsg : '保存所做的修改?',
					errorZone : $('div[name=msgZone]', $tab).attr('id'),
					callback : function(flag) {
						if (flag) {
							Ui.closeTab($tab);
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//同步
		Core.fn('${_zone}_list_zone', 'sync', function() {
			Ui.confirmPassword("确认同步所选项？", function() {
				var $form = $('#${_zone}_sync_form');
				Ajax.form('${_zone}_msg_zone', $form, {
					callback : function(flag) {
						if (flag) {//调用成功
							$("#${_zone}_list_form").submit();
						}
					}
				});
			});
		});

		//资源管理
		Core.fn('${_zone}_list_zone', 'resource', function(agentKey) {
			var $tab = Ui.openTab('应用[' + agentKey + ']资源', '${_acp}/resource.shtml?agentKey=' + agentKey);
		});

		//初始查询
		$("#${_zone}_list_form").submit();
	});
</script>

<div tabs="true" main="true" id="${_zone}_tabs">
	<div title="企业号开发">
		<form action="${_acp}/list.shtml" query="true" zone="${_zone}_list_zone" id="${_zone}_list_form">
			<input type="hidden" name="_field" value="updateDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>逻辑主键(模糊)</th>
					<td><input type="text" name="_sl_agentKey" /></td>
					<th>Agent ID</th>
					<td><input type="text" name="_ne_agentId" /></td>
				</tr>
				<tr>
					<th>应用名称</th>
					<td><input type="text" name="_sl_title" /></td>
					<th>应用描述</th>
					<td><input type="text" name="_sl_description" /></td>
				</tr>
				<tr>
					<th>应用状态</th>
					<td><wcm:widget name="_ne_status" cmd="select[@com.riversoft.platform.translate.AppStatus(请选择)]" /></td>
					<th>是否禁用</th>
					<td><wcm:widget name="_ne_closeFlag" cmd="select[YES_NO(请选择)]" /></td>
				</tr>
				<tr>
					<th class="ws-bar ">
						<div class="ws-group left">
							<button type="button" icon="wrench" name="config">参数配置</button>
							<button type="button" icon="person" name="user">用户管理</button>
						</div>
						<div class="ws-group right">
							<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
							<button type="submit" icon="search" text="true">查询</button>
						</div>
					</th>
				</tr>
			</table>
		</form>

		<div id="${_zone}_msg_zone"></div>
		<form action="${_acp}/submitSync.shtml" sync="true" id="${_zone}_sync_form">
			<div id="${_zone}_list_zone"></div>
		</form>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>