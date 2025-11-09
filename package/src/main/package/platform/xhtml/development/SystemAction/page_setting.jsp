<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<style type="text/css">
.themecontainer ul {
	list-style-type: none;
	margin: 10px 0 0;
	padding: 0;
}

.themecontainer ul li {
	border-radius: 8px;
	display: inline-block;
	margin: 0 10px 10px;
	border: solid 2px #ccc;
	padding: 5px;
}

.themecontainer ul li .title {
	text-align: center;
	color: #666;
	font-size: 13px;
	font-weight: bold;
}

.themecontainer ul li.hover, .themecontainer ul li.active {
	border: solid 2px blue;
}

.themecontainer ul li.hover .title, .themecontainer ul li.active .title {
	color: blue;
}

.themecontainer ul li.click, .themecontainer ul li.click {
	border: solid 2px blue;
	background-color: #f2f5f7;
}

.themecontainer ul li.click .title {
	color: blue;
}

.themecontainer ul li img {
	width: 125px;
	border-radius: 3px;
}

.themecontainer .grouptitle {
	color: #ccc;
	font-size: 16px;
	border-bottom: solid 1px #333;
	padding: 5px;
	margin: 10px;
}

.themecontainer {
	background-color: #fff;
	border: solid 1px #333;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var $themeInput = $("input:hidden[name='page.theme']", $zone);
		$('.themecontainer li', $zone).hover(function() {
			$(this).addClass('hover');
		}, function() {
			$(this).removeClass('hover');
		}).click(function() {
			var $img = $(this).find('img');
			$('.themecontainer li', $zone).removeClass('click');
			$(this).addClass('click');
			$themeInput.val($img.attr('data-value'));
		});

		$('.themecontainer', $zone).find("img[data-value='" + $themeInput.val() + "']").parents('li:first').click();

		$('button[name=reload]', $zone).click(function() {
			Ajax.post($zone, '${_acp}/setPage.shtml');
		});
	});
</script>


<form zone="${_zone}_msg" action="${_acp}/submitPageSetting.shtml" option="{confirmMsg:'确认保存?'}">
	<div class="ws-bar">
		<div class="left"></div>
		<div class="right">
			<button type="button" icon="refresh" name="reload">刷新</button>
			<button type="submit" icon="disk">保存</button>
		</div>
	</div>
	<div id="${_zone}_msg"></div>
	<div tabs="true" button="left">
		<div title="页面信息">
			<table class="ws-table">
				<tr>
					<th>标题</th>
					<td><wcm:widget name="page.title" cmd="textarea[90%]" value="${config['page.title']}" /></td>
				</tr>
				<tr whole="true">
					<th>LOGO图片地址<font color="red" tip="true" title="留空则使用BPMT产品默认LOGO.">(提示)</font></th>
					<td><wcm:widget name="page.logo.url" cmd="textarea[90%]" value="${config['page.logo.url']}" /></td>
				</tr>
				<tr whole="true">
					<th>favicon图标地址<font color="red" tip="true" title="留空则使用BPMT产品默认图标.">(提示)</font></th>
					<td><wcm:widget name="page.ico.url" cmd="textarea[90%]" value="${config['page.ico.url']}" /></td>
				</tr>
				<tr>
					<th>登录页提示</th>
					<td><wcm:widget name="page.tips" cmd="editor" value="${config['page.tips']}" /></td>
				</tr>
				<tr whole="true">
					<th>底部版权信息</th>
					<td><wcm:widget name="page.copyright" cmd="editor" value="${config['page.copyright']}" /></td>
				</tr>
				<tr>
					<th>登录验证码<font color="red" tip="true" title="需要验证码的次数阈值.如:3表示输错3次账号或密码后出现验证码;0表示验证码必须.">(提示)</font></th>
					<td><wcm:widget name="page.randomcode" cmd="text{digits:true,min:0,max:999}" value="${config['page.randomcode']}" /></td>
				</tr>
				<tr>
					<th>任务控制台开关</th>
					<td><wcm:widget name="page.taskpanel" cmd="radio[OPEN_CLOSE]" value="${config['page.taskpanel']}" /></td>
				</tr>
			</table>
		</div>
		<div title="样式设置">
			<table class="ws-table">
				<tr>
					<th>皮肤样式</th>
					<td><input name="page.theme" value="${config['page.theme']}" type="hidden" />
						<div class="themecontainer">
							<ul>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/base.png" alt="Base" data-value="base"></a>
									<div class="title">Base</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/black-tie.png" alt="Black Tie" data-value="black-tie"></a>
									<div class="title">Black Tie</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/blitzer.png" alt="Blitzer" data-value="blitzer"></a>
									<div class="title">Blitzer</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/cupertino.png" alt="Cupertino" data-value="cupertino"></a>
									<div class="title">Cupertino</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/dark-hive.png" alt="Dark Hive" data-value="dark-hive"></a>
									<div class="title">Dark Hive</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/dot-luv.png" alt="Dot Luv" data-value="dot-luv"></a>
									<div class="title">Dot Luv</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/eggplant.png" alt="Eggplant" data-value="eggplant"></a>
									<div class="title">Eggplant</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/excite-bike.png" alt="Excite Bike" data-value="excite-bike"></a>
									<div class="title">Excite Bike</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/flick.png" alt="Flick" data-value="flick"></a>
									<div class="title">Flick</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/hot-sneaks.png" alt="Hot Sneaks" data-value="hot-sneaks"></a>
									<div class="title">Hot Sneaks</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/humanity.png" alt="Humanity" data-value="humanity"></a>
									<div class="title">Humanity</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/le-frog.png" alt="Le Frog" data-value="le-frog"></a>
									<div class="title">Le Frog</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/mint-choc.png" alt="Mint Choc" data-value="mint-choc"></a>
									<div class="title">Mint Choc</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/overcast.png" alt="Overcast" data-value="overcast"></a>
									<div class="title">Overcast</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/pepper-grinder.png" alt="Pepper Grinder" data-value="pepper-grinder"></a>
									<div class="title">Pepper Grinder</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/redmond.png" alt="Redmond" data-value="redmond"></a>
									<div class="title">Redmond</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/smoothness.png" alt="Smoothness" data-value="smoothness"></a>
									<div class="title">Smoothness</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/south-street.png" alt="South Street" data-value="south-street"></a>
									<div class="title">South Street</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/start.png" alt="Start" data-value="start"></a>
									<div class="title">Start</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/sunny.png" alt="Sunny" data-value="sunny"></a>
									<div class="title">Sunny</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/swanky-purse.png" alt="Swanky Purse" data-value="swanky-purse"></a>
									<div class="title">Swanky Purse</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/trontastic.png" alt="Trontastic" data-value="trontastic"></a>
									<div class="title">Trontastic</div></li>
								<li><a href="javascript:;"> <img src="${_cp}/css/themes/index/ui-darkness.png" alt="UI Darkness" data-value="ui-darkness"></a>
									<div class="title">UI Darkness</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/ui-lightness.png" alt="UI Lightness" data-value="ui-lightness"></a>
									<div class="title">UI Lightness</div></li>
								<li class=""><a href="javascript:;"> <img src="${_cp}/css/themes/index/vader.png" alt="Vader" data-value="vader"></a>
									<div class="title">Vader</div></li>
							</ul>
						</div></td>
				</tr>
				<tr>
					<th>底色风格</th>
					<td><wcm:widget name="page.theme.backgroud" cmd="radio[THEME_BACKGROUD]" value="${config['page.theme.backgroud']}" /></td>
				</tr>
				<tr>
					<th>扩展样式地址<font color="red" tip="true" title="设置了扩展样式则系统样式不再生效.">(提示)</font></th>
					<td><wcm:widget name="page.theme.ext" cmd="textarea[90%]" value="${config['page.theme.ext']}" /></td>
				</tr>
			</table>
		</div>
		<div title="浏览器提示">
			<table class="ws-table">
				<tr>
					<th>不支持时提示</th>
					<td><wcm:widget name="page.browser.msg" cmd="textarea[90%]" value="${config['page.browser.msg']}" /></td>
				</tr>
				<tr>
					<th>引导地址</th>
					<td><wcm:widget name="page.browser.url" cmd="textarea[90%]" value="${config['page.browser.url']}" /></td>
				</tr>
			</table>
		</div>
	</div>
</form>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>