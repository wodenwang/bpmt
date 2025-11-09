<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		$('button[name=expand]', $zone).click(function() {
			var $this = $(this);
			var $table = $this.parents('table:first');
			if ($('tr:hidden:not(.last-child)', $table).size() < 1) {
				$('tr:not(.last-child)', $table).hide('fast');
			} else {
				$('tr:not(.last-child)', $table).show('fast');
			}
		});

		//写邮件的窗口
		Core.fn('${_zone}_list', 'sendMailForm', function(type, id) {
			var $win = Ajax.win('${_acp}/outboxForm.shtml', {
				title : '*新邮件',
				minWidth : 1024,
				data : {
					type : type,
					id : id
				}
			});
			Core.fn($win, 'submitForm', function(sendFlag) {
				var $form = $('form', $win);
				var $errorZone = $('div[name=errorZone]', $win);
				Ajax.form('${_zone}_msg', $form, {
					errorZone : $errorZone.attr('id'),
					data : {
						sendFlag : sendFlag
					},
					callback : function(flag) {
						if (flag) {
							$win.dialog("close");
							$('#${_zone}_list_form', $zone).submit();
						}
					}
				});
			});
		});

		//写新邮件
		$('button[name=send]', $zone).click(function() {
			Core.fn('${_zone}_list', 'sendMailForm')(0, null);
		});

		//删除邮件
		Core.fn('${_zone}_list', 'delMail', function() {
			var $checkbox = $('#${_zone}_delete_form input:checked[name=_keys]');
			if ($checkbox.size() < 1) {
				Ui.alert("请选择至少一项。");
				return;
			}

			Ui.confirm('确认删除邮件?', function() {
				var $form = $('#${_zone}_delete_form');
				//滚动到提示区域
				Ajax.form('${_zone}_msg', $form, {
					callback : function(flag) {
						if (flag) {//调用成功
							$('#${_zone}_list_form', $zone).submit();
						}
					},
					btn : $('button', $form)
				});
			});
		});

		$('button[name=expand]', $zone).click();
		$('#${_zone}_list_form', $zone).submit();
	});
</script>

<form zone="${_zone}_list" action="${_acp}/listOutbox.shtml" query="true" id="${_zone}_list_form" method="get">
	<input name="_field" type="hidden" value="CREATE_DATE" /> <input name="_dir" type="hidden" value="desc" />
	<table class="ws-table">
		<tr>
			<th>标题</th>
			<td><wcm:widget name="_sl_SUBJECT" cmd="text" /></td>
			<th>内容</th>
			<td><wcm:widget name="_sl_CONTENT" cmd="text" /></td>
		</tr>
		<tr>
			<th>收件人</th>
			<td><wcm:widget name="_sl_TO_ADDRS" cmd="text" /></td>
			<th>抄送人</th>
			<td><wcm:widget name="_sl_CC_ADDRS" cmd="text" /></td>
		</tr>
		<tr>
			<th>创建时间(>=)</th>
			<td><wcm:widget name="_dnl_CREATE_DATE" cmd="date[datetime]" /></td>
			<th>创建时间(<=)</th>
			<td><wcm:widget name="_dnm_CREATE_DATE" cmd="date[datetime]" /></td>
		</tr>
		<tr>
			<th>发送时间(>=)</th>
			<td><wcm:widget name="_dnl_SENT_DATE" cmd="date[datetime]" /></td>
			<th>发送时间(<=)</th>
			<td><wcm:widget name="_dnm_SENT_DATE" cmd="date[datetime]" /></td>
		</tr>
		<tr>
			<th class="ws-bar">
				<div class="left ws-group">
					<button type="button" icon="mail-open" name="send">写邮件</button>
				</div>
				<div class="ws-group right">
					<button type="button" icon="arrowthick-2-n-s" name="expand">展开/收缩查询框</button>
					<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
					<button type="submit" icon="search" text="true">查询</button>
				</div>
			</th>
		</tr>
	</table>
</form>

<%--错误提示区域 --%>
<div id="${_zone}_msg"></div>

<form id="${_zone}_delete_form" action="${_acp}/removeSendMail.shtml" sync="true">
	<%--查询结果 --%>
	<div id="${_zone}_list"></div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>