<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		//同步通信录
		$('button[name=syncUser]', $zone).click(function() {
			Ui.confirm('是否同步通信录?', function() {
				Ajax.post('${_zone}_user_msg_zone', '${_acp}/syncUser.shtml', {
					callback : function(flag) {
						if (flag) {
							$('#${_zone}_user_list_form').submit();
						}
					}
				});
			});
		});

		$('#${_zone}_user_list_form').submit();
	});
</script>

<form action="${_acp}/listUser.shtml" query="true" zone="${_zone}_user_list_zone" id="${_zone}_user_list_form">
	<table class="ws-table">
		<tr>
			<th>所在组织</th>
			<td><wcm:widget name="groupKey" cmd="group" /></td>
			<th>角色</th>
			<td><wcm:widget name="roleKey" cmd="select[$com.riversoft.platform.po.UsRole(请选择);roleKey;busiName]" /></td>
		</tr>
		<tr>
			<th>登录ID</th>
			<td><input type="text" name="_sl_uid" /></td>
			<th>姓名</th>
			<td><input type="text" name="_sl_busiName" /></td>
		</tr>
		<tr>
			<th>邮箱</th>
			<td><input type="text" name="_sl_mail" /></td>
			<th>手机号码</th>
			<td><input type="text" name="_sl_mobile" /></td>
		</tr>
		<tr>
			<th>微信ID</th>
			<td><input type="text" name="_sl_wxid" /></td>
			<th>微信关注状态</th>
			<td><wcm:widget name="_ne_wxStatus" cmd="select[@com.riversoft.platform.translate.WxStatus(请选择)]" /></td>
		</tr>
		<tr>
			<th class="ws-bar ">
				<div class="ws-group left">
					<button type="button" icon="transferthick-e-w" text="true" name="syncUser">同步组织架构</button>
				</div>
				<div class="ws-group right">
					<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
					<button type="submit" icon="search" text="true">查询</button>
				</div>
			</th>
		</tr>
	</table>
</form>
<div id="${_zone}_user_msg_zone"></div>
<div id="${_zone}_user_list_zone"></div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>