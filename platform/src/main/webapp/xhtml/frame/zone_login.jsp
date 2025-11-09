<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>


<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('#${_zone}_random_code').click(
				function() {
					var now = new Date();
					var $img = $(this).find('img');
					$img.attr('src', '${_acp}/randomImg.shtml?_tmp=' + now.getFullYear() + now.getMonth() + now.getDay() + now.getHours() + now.getMinutes() + now.getSeconds() + now.getMilliseconds()
							+ Math.random());
				});

		var submitForm = function($this) {
			var $form = $('form', $this);
			Ajax.form('${_zone}_msg', $form, {
				callback : function(flag) {
					if (flag) {
						$this.dialog("close");
						$('#${_zone}_show_zone').html('${wpf:lan("#:zh[登录成功,请继续您的操作.]:en[Login is successful, please continue your operation.]#")}').styleMsg({
							type : 'info'
						});
					} else {
						//刷新验证码
						$('#${_zone}_random_code').click();
					}
				}
			});
		}

		var $win = $('#${_zone}_win').dialog({
			modal : true,
			title : '${wpf:lan("#:zh[用户登录]:en[User Login]#")}',
			minWidth : 400,
			buttons : [ {
				text : '${wpf:lan("#:zh[登录]:en[Login]#")}',
				icons : {
					primary : "ui-icon-person"
				},
				click : function() {
					var $this = $(this);
					submitForm($this);
				}
			} ]
		});

		$('input', $win).bind('keydown', function(e) {
			var key = e.which;
			if (key == 13) {
				e.preventDefault();
				submitForm($win);
			}
		});

		$('#${_zone}_show_win').click(function(event) {
			event.preventDefault();
			$win.dialog("open");
		});

		//初始化验证码
		$('#${_zone}_random_code').click();
	});
</script>

<div class="ws-msg error" id="${_zone}_show_zone">
	${wpf:lan("#:zh[登录超时,请重新登录]:en[Login timeout, please login again]#")}.<a href="javascript:void(0);" id="${_zone}_show_win">[${wpf:lan("#:zh[点击登录]:en[Click login]#")}]</a>
</div>

<div id="${_zone}_win">
	<div id="${_zone}_msg"></div>
	<%--表单 --%>
	<form action="${_cp}/frame/LoginAction/login.shtml" method="post" sync="true">
		<table class="ws-table">
			<tr>
				<th>${wpf:lan("#:zh[用户名]:en[User Name]#")}</th>
				<td style="background: #fff;"><input type="text" name="username" style="width: 200px;" /></td>
			</tr>
			<tr>
				<th>${wpf:lan("#:zh[密码]:en[Password]#")}</th>
				<td style="background: #fff;"><input type="password" name="password" style="width: 200px;" /></td>
			</tr>
			<c:if test="${randomFlag}">
				<tr>
					<th>${wpf:lan("#:zh[验证码]:en[Codes]#")}</th>
					<td style="background: #fff;"><div style="float: left;">
							<input type="text" name="randomcode" style="width: 110px;" />
						</div>
						<div style="margin-left: 120px;">
							<a style="cursor: pointer;" id="${_zone}_random_code" href="javascript:void(0);" alt="点击刷新"> <img alt="点击刷新" style="border-width: 1px; border-style: solid; width: 80px; height: 22px;" /></a>
						</div></td>
				</tr>
			</c:if>
		</table>
	</form>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>