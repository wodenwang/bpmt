<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script src="http://res.wx.qq.com/connect/zh_CN/htmledition/js/wxLogin.js"></script>
<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		$('button[name=wxbinding]', $zone).click(function() {
			var $this = $(this);
			Ui.confirmPassword('请输验证登陆密码并打开微信扫一扫进行微信号验证.', function() {
				$this.button('option', 'disabled', true);
				window.onbeforeunload = '';
				new WxLogin({
					id : '${_zone}_wx_qrcode',
					appid : '${wxWebAppId}',
					scope : 'snsapi_login',
					redirect_uri : 'http://${wxDomain}/frame/FrameAction/submitBinding.shtml',
					state : new Date().getTime()
				});

				var $div = $('<div>若成功绑定微信,系统会要求重新登录系统.</div>').styleMsg({
					type : 'normal'
				});
				$("#${_zone}_wx_qrcode").before($div);
			});
		});
	});
</script>
<table class="ws-table">
	<c:if test="${user.openId!=null}">
		<tr>
			<th>绑定状态</th>
			<td><span style="color: green;">已绑定</span></td>
		</tr>
	</c:if>
	<tr>
		<th class="ws-bar left">
			<button type="button" icon="wx.png" name="wxbinding">绑定</button>
		</th>
	</tr>
</table>

<div id="${_zone}_wx_qrcode" style="text-align: center;"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>