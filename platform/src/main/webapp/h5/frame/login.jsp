<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<script>
	$(function() {
		$('#${_zone}_random_code').click(
				function() {
					var now = new Date();
					var $img = $(this).find('img');
					$img.attr('src', '${_acp}/randomImg.shtml?_tmp=' + now.getFullYear() + now.getMonth() + now.getDay() + now.getHours() + now.getMinutes() + now.getSeconds() + now.getMilliseconds()
							+ Math.random());
				});

		var errorCount = Number('${errorCount}');
		var allowErrorCount = Number('${allowErrorCount}');
		var initRandom = function() {
			if (errorCount >= allowErrorCount) {
				$('.random_code').show();
			}
			$('#${_zone}_random_code').click();
		};

		//登录
		$("#${_zone}_login_form").submit(function() {
			var $form = $(this);
			Wxui.form($form, function(json) {
				if (json.flag) {
					window.location.reload(true);
				} else {
					Wxui.toast(json.msg, 'error');
					errorCount = json.errorCount;
					initRandom();
				}
			}, function(res) {
				var json;
				try {
					json = JSON.parse(res.responseText);
				} catch (e) {
					json = {
						msg : '系统出错.'
					};
				}
				Wxui.toast(json.msg, 'error');
				initRandom();
			});
			return false;
		});

		//初始化验证码
		initRandom();
	});
</script>

<div class="am-container am-margin-top am-text-middle">
	<c:if test="${logoUrl!=null&&logoUrl!=''}">
		<img src="${logoUrl}" class="am-img-responsive am-center" />
	</c:if>
</div>

<hr data-am-widget="divider" style="" class="am-divider am-divider-default" />

<div class="am-g">
	<div class="am-u-lg-6 am-u-md-8 am-u-sm-centered">
		<%--表单 --%>
		<form action="${_cp}/frame/LoginAction/login.shtml" method="post" class="am-form am-form-horizontal" data-am-validator id="${_zone}_login_form">
			<fieldset>
				<div class="am-form-group">
					<label for="username">用户名</label> <input type="text" id="username" placeholder="输入登录ID" name="username" required="required" />
				</div>

				<div class="am-form-group">
					<label for="password">密码</label> <input type="password" id="password" placeholder="输入密码" name="password" required="required" />
				</div>

				<div class="am-form-group random_code" style="display: none;">
					<label>验证码</label>
					<div class="am-g  am-g-fixed">
						<div class="am-u-sm-8">
							<input type="text" id="randomcode" placeholder="验证码" name="randomcode" required="required" />
						</div>
						<div class="am-u-sm-4">
							<a style="cursor: pointer;" id="${_zone}_random_code" href="javascript:void(0);" alt="点击刷新"> <img alt="点击刷新" class="  am-img-thumbnail" width="200" /></a>
						</div>
					</div>
				</div>

				<div class="am-cf">
					<input type="submit" name="" value="登 录" class="am-btn am-btn-primary am-center" />
				</div>
			</fieldset>
		</form>
	</div>
</div>

<footer data-am-widget="footer" class="am-footer am-footer-default" data-am-footer="{  }">
	<div class="am-footer-miscs ">
		<p>${copyRight}</p>
	</div>
</footer>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>