<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//表单动作
		$('form', $zone).submit(function() {
			var $form = $(this);
			Ajax.form('${_zone}', $form, {
				errorZone : '${_zone}_msg',
				confirmMsg : '是否保存?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
					}
				}
			});
			return false;
		});

	});
</script>

<div tabs="true">
	<c:choose>
		<c:when test="${isCreate}">
			<c:set var="title" value="添加用户" />
		</c:when>
		<c:otherwise>
			<c:set var="title" value="用户[${vo.busiName}]编辑" />
		</c:otherwise>
	</c:choose>
	<div title="${title}">
		<%--错误提示区域 --%>
		<div id="${_zone}_msg"></div>
		<%--表单 --%>
		<form action="${_acp}/submitUserForm.shtml" method="post" sync="true">
			<input type="hidden" name="isCreate" value="${isCreate?1:0}" />
			<table class="ws-table" group="true" col="1">
				<tr whole="true" group="true">
					<th colspan="2">基础设置</th>
				</tr>
				<tr>
					<th>用户名</th>
					<td><wcm:widget name="uid" cmd="text{required:true}" value="${vo.uid}" state="${isCreate?'normal':'readonly'}" /></td>
				</tr>
				<tr>
					<th>展示名</th>
					<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
				</tr>
				<tr>
					<th>标签</th>
					<td><wcm:widget name="tags" cmd="multiselect[$com.riversoft.platform.po.UsTag;tagKey;busiName]" value="${tags}" /></td>
				</tr>
				<tr>
					<th>是否生效</th>
					<td><wcm:widget name="activeFlag" cmd="radio[YES_NO]" value="${vo.activeFlag}" /></td>
				</tr>
				<tr>
					<th>是否控件可选</th>
					<td><wcm:widget name="selectFlag" cmd="radio[YES_NO]" value="${vo.selectFlag}" /></td>
				</tr>
				<tr>
					<th>邮箱</th>
					<td><wcm:widget name="mail" cmd="text{email:true}" value="${vo.mail}" /></td>
				</tr>
				<tr>
					<th>手机号</th>
					<td><wcm:widget name="mobile" cmd="text" value="${vo.mobile}" /></td>
				</tr>
				<tr whole="true" group="true">
					<th colspan="2">微信相关</th>
				</tr>
				<tr>
					<th>是否企业号用户</th>
					<td><wcm:widget name="wxEnable" cmd="radio[YES_NO]" value="${vo.wxEnable}" /></td>
				</tr>

				<tr>
					<th>绑定微信</th>
					<td><wcm:widget name="wxid" cmd="text" value="${vo.wxid}" state="${vo.wxStatus==1||vo.wxStatus==2?'readonly':'normal'}" /></td>
				</tr>
				<tr>
					<th>关注状态</th>
					<td>${wcm:widget('select[@com.riversoft.platform.translate.WxStatus]',vo.wxStatus)}</td>
				</tr>

				<tr whole="true" group="true">
					<th colspan="2">消息设置</th>
				</tr>
				<tr>
					<th>通知方式</th>
					<td><wcm:widget name="msgType" cmd="checkbox[@com.riversoft.platform.translate.NotifyMsgType]" value="${vo.msgType}" /></td>
				</tr>
				<tr>
					<th>接收范围</th>
					<td><wcm:widget name="receiveType" cmd="checkbox[@com.riversoft.platform.translate.NotifyReceiveType]" value="${vo.receiveType}" /></td>
				</tr>
				<tr whole="true" group="true">
					<th colspan="2">安全设置</th>
				</tr>
				<tr>
					<th>IP白名单<br /> <font color="red" tip="true" title="多个以逗号分隔,支持*表达式.例:192.168.*.*">(提示)</font>
					</th>
					<td><wcm:widget name="allowIp" cmd="textarea" value="${vo.allowIp}" /></td>
				</tr>
				<c:choose>
					<c:when test="${isCreate}">
						<tr>
							<th>密码</th>
							<td><input type="password" name="password" class="{required:true,maxlength:20}" /></td>
						</tr>
						<tr>
							<th>再输一次密码</th>
							<td><input type="password" name="password_configm" class="{equalTo:'#${_zone} :password[name=password]',maxlength:20}" /></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<th>密码(不填代表不修改)</th>
							<td><input type="password" name="password" class="{maxlength:20}" /></td>
						</tr>
						<tr>
							<th>再输一次密码</th>
							<td><input type="password" name="password_configm" class="{equalTo:'#${_zone} :password[name=password]',maxlength:20}" /></td>
						</tr>
					</c:otherwise>
				</c:choose>

			</table>
			<div class="ws-bar">
				<button type="submit" icon="disk">保存</button>
			</div>
		</form>
	</div>
	<c:if test="${!isCreate}">
		<div title="用户角色管理" init="${_acp}/userRoleGroupSetting.shtml?uid=${vo.uid}"></div>
	</c:if>
</div>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>