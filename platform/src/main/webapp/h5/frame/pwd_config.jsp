<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<div data-am-widget="intro" class="am-intro am-cf am-intro-default">
	<div class="am-g am-intro-bd">
		<div class="am-intro-left am-u-sm-4">
			<img src="${user.wxAvatar}" />
		</div>
		<div class="am-intro-right am-u-sm-8">
			<h2 style="font-weight: bold;">${user.busiName}</h2>
			<p>
				<span class="am-article-meta">系统账号:${user.uid}</span>
			</p>
			<p>
				<span class="am-badge am-badge-secondary">${group.busiName}</span> <span class="am-badge am-badge-warning">${role.busiName}</span>
			</p>
		</div>
	</div>
</div>

<hr data-am-widget="divider" style="" class="am-divider am-divider-default" />

<%--表单 --%>
<form action="${_acp}/submitChangePwd.shtml" method="post" class="am-form am-form-horizontal" data-am-validator>
	<fieldset>
		<div class="am-form-group">
			<label for="oldPassword">原密码</label> <input type="password" id="oldPassword" placeholder="输入原密码" name="oldPassword" required="required" />
		</div>

		<div class="am-form-group">
			<label for="newPassword">新密码</label> <input type="password" id="newPassword" placeholder="输入新密码" name="newPassword" required="required" />
		</div>

		<div class="am-form-group">
			<label for="newPassword2">确认新密码</label> <input type="password" id="newPassword2" placeholder="请与上面输入的值一致" name="newPassword2" data-equal-to="#newPassword" required="required" />
		</div>

		<button type="submit" class="am-btn am-btn-success am-radius am-btn-block">提交</button>
		<button type="button" class="am-btn am-btn-default am-radius am-btn-block" onclick="wx.closeWindow();">关闭</button>

	</fieldset>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>