<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:set var="baseCp" value="${_cp}/xhtml/frame_new" />
<link rel="stylesheet" href="${baseCp}/css/main.css">

<style type="text/css">
/*重写edit按钮的图标样式*/
.tc-slider-bar .ztree li span.button.edit {
	margin-right: 2px;
	background-image: url("${_cp}/css/icon/monitor_add.png");
	background-position: 0px 0px;
	vertical-align: top;
	*vertical-align: middle;
}
</style>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//左侧导航栏高度
		$(window).resize(function() {
			var winH = $(window).height();
			$('[silder-bar]', $zone).css({
				'maxHeight' : winH - 100,
				'height' : winH - 100
			})
		}).trigger('resize');

		//伸缩
		var $collapse = $('[tc-collapse]');
		$collapse.on('click', function() {
			$(window).trigger('resize');
			var _this = $(this);
			_this.toggleClass('on').parent().next().slideToggle(300);
		});

		$('a[name=userSetting]', $zone).click(function() {
			Ajax.win('${_acp}/userSetting.shtml', {
				title : "${wpf:lan('#:zh[用户设置]:en[User settings]#')}",
				minWidth : 1024,
				minHeight : 200,
				buttons : [ {
					text : "${wpf:lan('#:zh[关闭]:en[Close]#')}",
					click : function() {
						$(this).dialog("close");
					}
				} ]
			});
		});

		$('a[name=changeGroup]', $zone).click(function() {
			Ajax.win('${_acp}/changeGroup.shtml', {
				title : "${wpf:lan('#:zh[切换组织]:en[Switching organization]#')}",
				minWidth : 400,
				buttons : [ {
					text : "${wpf:lan('#:zh[关闭]:en[Close]#')}",
					click : function() {
						$(this).dialog("close");
					}
				} ]
			});
		});

		$('a[name=logout]', $zone).click(function() {
			Ui.confirm('${wpf:lan("#:zh[确认退出?]:en[Confirm exit?]#")}', function() {
				Ajax.jump('${_cp}/frame/LoginAction/logout.shtml')
			});
		});

		$('a[name=taskPanel]', $zone).click(function() {
			Ajax.win('${_cp}/flow/CommonFlowAction/taskPanel.shtml', {
				title : "${wpf:lan('#:zh[待办事项(快捷处理)]:en[Todo(fast processing)]#')}",
				minWidth : 1024,
				data : {
					_params : "{quickMode:true}"
				},
				buttons : [ {
					text : '${wpf:lan("#:zh[关闭]:en[Close]#")}',
					click : function() {
						$(this).dialog("close");
					}
				} ]
			});
		});
	});
</script>
<div class="tc-clearfix tc-top-wrap">
	<p class="tc-pull-left tc-summary">
		用户<a href="javascript:void(0);">【${sessionScope.USER.busiName}】</a>所在组织<a href="javascript:void(0);">【${sessionScope.ROLE.busiName}】</a>,于【${wcm:widget('date[datetime]',sessionScope.DATE)}】登录.
	</p>

	<ul class="tc-pull-right tc-menu-wrap tc-box-sizing">
		<c:if test="${taskPanel}">
			<li><a href="javascript:void(0);" class="tc-menu" name="taskPanel"><i class="tc-icon tc-icon-clock"></i>待办</a></li>
		</c:if>
		<c:if test="${sessionScope.USER.entity}">
			<li><a href="javascript:void(0);" class="tc-menu" name="changeGroup"><i class="tc-icon tc-icon-tab"></i>切换</a></li>
			<li><a href="javascript:void(0);" class="tc-menu" name="userSetting"><i class="tc-icon tc-icon-setting"></i>设置</a></li>
		</c:if>
		<li><a href="javascript:void(0);" class="tc-menu" name="logout"><i class="tc-icon tc-icon-quit"></i>退出</a></li>
	</ul>
</div>

<div class="tc-clearfix tc-box-sizing ">
	<div class="tc-pull-left tc-logo-wrap" logo-wrap="">
		<div class="tc-logo">
			<img src="${logoUrl}" alt="" />
		</div>
	</div>
	<div class="tc-tab-bar">
		<c:choose>
			<c:when test="${domains!=null && fn:length(domains) > 0}">
				<c:forEach items="${domains}" var="vo">
					<a href="javascript:void(0);" class="tc-nav ${(vo.domainKey == domain.domainKey)?'active':''}" onclick="Ajax.jump('${_cp}/${vo.domainKey}.xhtml')">${wpf:lan(vo.busiName)}</a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<a href="javascript:void(0);" class="tc-nav active">无可用域</a>
			</c:otherwise>
		</c:choose>
	</div>
</div>


<div class="tc-wrapper tc-clearfix">

	<div class="tc-slider-bar" init="${_acp}/menu.shtml?domain=${domain.domainKey}&menu=${menuKey}" silder-bar></div>

	<div class="tc-container" main-zone></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>