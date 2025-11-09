<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//新增
		Core.fn('${_zone}_list_zone', 'create', function() {
			var $tab = Ui.openTab('新建公众号', '${_acp}/createZone.shtml');
			//表单页
			Core.fn($tab, 'submitCreate', function($form) {
				Ajax.form('${_zone}_msg_zone', $form, {
					confirmMsg : '确认新建公众号应用?',
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
		Core.fn('${_zone}_list_zone', 'remove', function(mpKey) {
			Ui.confirmPassword('确认删除公众号[' + mpKey + ']?', function() {
				Ajax.post('${_zone}_msg_zone', '${_acp}/removeMp.shtml', {
					data : {
						mpKey : mpKey
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
		Core.fn('${_zone}_list_zone', 'edit', function(mpKey) {
			var $tab = Ui.openTab('公众号[' + mpKey + ']设置', '${_acp}/editZone.shtml?mpKey=' + mpKey);
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

		//人员管理
		Core.fn('${_zone}_list_zone', 'visitor', function(mpKey) {
			var $tab = Ui.openTab('公众号[' + mpKey + ']人员管理', '${_acp}/visitorMain.shtml?mpKey=' + mpKey);
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
		Core.fn('${_zone}_list_zone', 'resource', function(mpKey) {
			var $tab = Ui.openTab('公众号[' + mpKey + ']资源', '${_acp}/resource.shtml?mpKey=' + mpKey);
		});

		//模板消息管理
		Core.fn('${_zone}_list_zone', 'templateMsg', function(mpKey) {
			var $tab = Ui.openTab('公众号[' + mpKey + ']模板消息', '${_acp}/templateMsg.shtml?mpKey=' + mpKey);
		});

		//初始查询
		$("#${_zone}_list_form").submit();
	});
</script>

<div tabs="true" main="true" id="${_zone}_tabs">
	<div title="公众号开发">
		<form action="${_acp}/list.shtml" query="true" zone="${_zone}_list_zone" id="${_zone}_list_form">
			<input type="hidden" name="_field" value="updateDate" /> <input type="hidden" name="_dir" value="desc" />
			<table class="ws-table">
				<tr>
					<th>逻辑主键(模糊)</th>
					<td><input type="text" name="_sl_mpKey" /></td>
					<th>AppID</th>
					<td><input type="text" name="_sl_appId" /></td>
				</tr>
				<tr>
					<th>名称</th>
					<td><input type="text" name="_sl_title" /></td>
					<th>描述</th>
					<td><input type="text" name="_sl_description" /></td>
				</tr>
				<tr>
					<th>状态</th>
					<td><wcm:widget name="_ne_status" cmd="select[@com.riversoft.platform.translate.AppStatus(请选择)]" /></td>
					<th>是否禁用</th>
					<td><wcm:widget name="_ne_closeFlag" cmd="select[YES_NO(请选择)]" /></td>
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

		<div id="${_zone}_msg_zone"></div>
		<form action="${_acp}/submitSync.shtml" sync="true" id="${_zone}_sync_form">
			<div id="${_zone}_list_zone"></div>
		</form>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>