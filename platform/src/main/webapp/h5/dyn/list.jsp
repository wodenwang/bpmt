<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<script>
	$(function() {
		//增加loading
		window.onbeforeunload = function() {
			Wxui.showLoading();
		};
	});
</script>

<%--顶部--%>
<header data-am-widget="header" class="am-header am-header-default">
	<div class="am-header-left am-header-nav">
		<c:if test="${wpf:check(config.addBtn.pri)}">
			<a href="${_acp}/createZone.shtml?_params=${wcm:urlEncode(param._params)}" class="" id="${_zone}_btn_add"><i class="am-header-icon am-icon-plus-circle"></i></a>
		</c:if>
	</div>
	<h1 class="am-header-title">${_title}</h1>
	<div class="am-header-right am-header-nav">
		<a href="javascript:void(0);" class="" data-am-modal="{target: '#my-query'}"><i class="am-header-icon am-icon-search"></i> </a>
	</div>
</header>

<%--查询条件 --%>
<div class="am-modal-actions" id="my-query">
	<form action="${_acp}/list.shtml" method="get" class="am-form am-form-horizontal" id="${_zone}_query_form">
		<textarea style="display: none;" name="_params">${param._params}</textarea>
		<div class="am-modal-actions-group">
			<div class="am-panel am-panel-primary">
				<div class="am-panel-hd">查询条件</div>
				<div class="am-panel-bd am-scrollable-vertical">

					<c:if test="${config.querys !=null && fn:length(config.querys)>0}">
						<fieldset style="text-align: left;">
							<c:forEach items="${config.querys}" var="vo">
								<div class="am-form-group">
									<c:choose>
										<c:when test="${vo.name!=null}">
											<label for="${vo.name}">${vo.busiName}</label>
											<wcm:widget name="${vo.name}" cmd="${vo.widget}" value="${wcm:param(vo.name)}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}"
												actionMode="h5" />
										</c:when>
										<c:otherwise>
											<c:set var="widgetFormName" value="querys.${vo.id}" />
											<label for="${widgetFormName}">${vo.busiName}</label>
											<wcm:widget name="${widgetFormName}" cmd="${vo.widget}" value="${wcm:param(widgetFormName)}"
												params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}" actionMode="h5" />
										</c:otherwise>
									</c:choose>
								</div>
							</c:forEach>
						</fieldset>
					</c:if>
				</div>
			</div>
		</div>
		<div class="am-modal-actions-group">
			<button type="submit" class="am-btn am-btn-primary am-radius am-btn-block">查询</button>
		</div>
		<div class="am-modal-actions-group">
			<button type="reset" class="am-btn am-btn-default am-btn-block am-radius" data-am-modal-close>取消</button>
		</div>
	</form>
</div>

<%-- 点击url连接 --%>
<c:set var="clickUrl" value="detail" />
<c:if test="${config.table.weixin.urlMode==1}">
	<c:set var="clickUrl" value="updateZone" />
</c:if>

<%--列表内容--%>
<div data-am-widget="list_news" class="am-list-news am-list-news-default">
	<textarea style="display: none;" name="_params" id="${_zone}_params">${param._params}</textarea>
	<div class="am-list-news-bd">
		<ul class="am-list">
			<c:forEach items="${dp.list}" var="vo">
				<%-- 数据准备 --%>
				<c:set var="context" value="${wcm:map(wcm:map(null,'mode',1),'vo',vo)}" />
				<%-- 获取left join绑定上下文 --%>
				<c:forEach items="${config.table.parents}" var="parent">
					<c:set var="context" value="${wcm:map(context,parent.var,wpf:pixelVO(parent.var,vo))}" />
				</c:forEach>
				<!-- 数据展示准备处理器 -->
				<c:forEach items="${config.table.prepareExecs}" var="exec">
					<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
				</c:forEach>
				<c:choose>
					<%-- 图文模式 --%>
					<c:when test="${config.table.weixin.listMode==1}">
						<li class="am-g am-list-item-desced am-list-item-thumbed am-list-item-thumb-left">
							<div class="am-u-sm-3 am-list-thumb">
								<a href="${_acp}/${clickUrl}.shtml?_key=${wcm:urlEncode(wcm:jsonKey(vo,config.keysArray))}&_params=${wcm:urlEncode(param._params)}" class=""><wpf:image
										value="${wpf:script(config.table.weixin.imgType,config.table.weixin.imgScript,context)}" /></a>
							</div>
							<div class=" am-u-sm-9 am-list-main">
								<h3 class="am-list-item-hd">
									<a href="${_acp}/${clickUrl}.shtml?_key=${wcm:urlEncode(wcm:jsonKey(vo,config.keysArray))}&_params=${wcm:urlEncode(param._params)}" class=""><wpf:script
											script="${config.table.weixin.titleScript}" type="${config.table.weixin.titleType}" context="${context}" /></a>
								</h3>
								<div class="am-list-item-text">
									<wpf:script script="${config.table.weixin.desScript}" type="${config.table.weixin.desType}" context="${context}" />
								</div>
							</div>
						</li>
					</c:when>
					<%-- 纯文模式 --%>
					<c:otherwise>
						<li class="am-g am-list-item-dated"><a href="${_acp}/${clickUrl}.shtml?_key=${wcm:urlEncode(wcm:jsonKey(vo,config.keysArray))}&_params=${wcm:urlEncode(param._params)}"
							class="am-list-item-hd"><wpf:script script="${config.table.weixin.titleScript}" type="${config.table.weixin.titleType}" context="${context}" /></a><span class="am-list-date"><wpf:script
									script="${config.table.weixin.dateScript}" type="${config.table.weixin.dateType}" context="${context}" /></span>
							<div class="am-list-item-text">
								<wpf:script script="${config.table.weixin.desScript}" type="${config.table.weixin.desType}" context="${context}" />
							</div></li>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</ul>
	</div>
</div>

<%--分页 --%>
<wcm:page dp="${dp}" actionMode="h5" form="${_zone}_query_form" defLimit="${pageLimit}" />

<footer data-am-widget="footer" class="am-footer am-footer-default"></footer>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>