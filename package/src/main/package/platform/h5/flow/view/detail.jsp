<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<wwx:jssdk url="${_full_url}" />

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(wcm:map(null,'mode',2),'vo',vo)}" />
<c:set var="context" value="${wcm:map(context,'fo',fo)}" />
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<%-- 是否需要校验权限 --%>
<c:set var="ordFlag" value="${fo.activityId==null||ordFlag}" />

<script>
	$(function() {

		//增加loading
		window.onbeforeunload = function() {
			Wxui.showLoading();
		};

		$("#${_zone}_btn_home").on('click', function(event) {
			window.location.href = "${_acp}/list.shtml?_params=${wcm:urlEncode(param._params)}";
		});

		$("#${_zone}_flow_picture_btn").on('click', function() {
			$("#${_zone}_flow_picture").modal();
		});

		//删除第一个分割线
		$('li.am-divider:first', $('ul.am-dropdown-content')).remove();
		if ($('header .am-header-right.am-header-nav ul.am-dropdown-content li').size() < 1) {
			$('header .am-header-right.am-header-nav').hide();
		}
	});
</script>

<%-- 流程图 --%>
<div class="am-modal am-modal-no-btn" tabindex="-1" id="${_zone}_flow_picture">
	<div class="am-modal-dialog">
		<div class="am-modal-hd">
			流程图 <a href="javascript: void(0)" class="am-close am-close-spin" data-am-modal-close>&times;</a>
		</div>
		<div class="am-modal-bd">
			<figure data-am-widget="figure" class="am am-figure am-figure-default " data-am-figure="{pureview:true}">
				<img src="${_acp}/picture.shtml?_PD_ID=${fo.pdId}&_TASK_ID=${fo.taskId}&_ORD_ID=${fo.ordId}&_ACTIVITY_ID=${fo.activityId}&imageType=png" alt="点击看大图" />
			</figure>
		</div>
	</div>
</div>

<%-- 回到顶部 --%>
<div data-am-widget="gotop" class="am-gotop am-gotop-fixed">
	<a href="#top" title="回到顶部"> <span class="am-gotop-title">回到顶部</span> <i class="am-gotop-icon am-icon-chevron-up"></i>
	</a>
</div>

<%--顶部--%>
<header data-am-widget="header" class="am-header am-header-default">
	<div class="am-header-left am-header-nav">
		<a href="javascript:void(0);" class="" id="${_zone}_btn_home"><i class="am-header-icon am-icon-home"></i></a>
	</div>
	<h1 class="am-header-title">${_title}</h1>

	<div class="am-header-right am-header-nav " data-am-dropdown>
		<a href="###" class="am-dropdown-toggle"><i class="am-header-icon am-icon-bars"></i></a>
		<ul class="am-dropdown-content">
			<c:if test="${wflow:checkTask(vo)}">
				<li class="am-divider"></li>
				<li><a href="${_acp}/form.shtml?ordFlag=${ordFlag?1:0}&_params=${wcm:urlEncode(param._params)}&_FO=${wcm:urlEncode(wcm:json(fo))}"><i class="am-icon-pencil am-icon-fw am-margin-right-xs"></i>处理</a></li>
			</c:if>

			<c:forEach items="${subs}" var="sub" varStatus="status">
				<%-- 权限空白,直接可看 --%>
				<c:if test="${sub.pri==null||wpf:checkExt(sub.pri,context)}">
					<c:choose>
						<%-- 视图子 --%>
						<c:when test="${sub.action!=null}">
						</c:when>
						<%-- 流程图 --%>
						<c:when test="${sub.name=='picture'}">
							<li class="am-divider"></li>
							<li><a href="javascript:void(0);" id="${_zone}_flow_picture_btn"><i class="am-icon-object-group am-icon-fw am-margin-right-xs"></i>${sub.busiName}</a></li>
						</c:when>
						<c:when test="${sub.name=='history'}">
						</c:when>
					</c:choose>
				</c:if>
			</c:forEach>
		</ul>
	</div>

</header>

<div class="am-panel-group" id="${_zone}_accordion">
	<c:forEach items="${columns}" var="line" varStatus="status">
		<c:if test="${line.whole==null}">

			<%--分割线 --%>
			<c:set var="lineCheckResult" value="${true}" />
			<c:choose>
				<c:when test="${ordFlag}">
					<c:set var="lineCheckResult" value="${wpf:checkExt(line.pri,context)}" />
				</c:when>
				<%-- 节点字段,采用decide决定结果 --%>
				<c:when test="${line.decideScript!=null}">
					<c:set var="lineCheckResult" value="${wpf:script(line.decideType,line.decideScript,context)}" />
				</c:when>
				<%-- 继承字段,直接展示--%>
				<c:otherwise>
					<c:set var="lineCheckResult" value="${true}" />
				</c:otherwise>
			</c:choose>

			<c:if test="${lineCheckResult}">
				<div class="am-panel am-panel-secondary">
					<div class="am-panel-hd" data-am-collapse="{target: '#${_zone}_accordion_p${status.index}'}">
						<h4 class="am-panel-title">
							${line.busiName}
							<c:if test="${line.tipScript!=null&&line.tipScript!=''}">(${wpf:script(line.tipType,line.tipScript,context)})</c:if>
						</h4>
					</div>
					<div id="${_zone}_accordion_p${status.index}" class="am-panel-collapse am-collapse ${line.expandFlag==0?'':'am-in'}">
						<div class="am-panel-bd">
							<dl>
								<c:forEach items="${columns}" var="field" varStatus="fieldStatus" begin="${line.detail_begin}" end="${line.detail_end}">
									<c:set var="checkResult" value="${true}" />
									<%--展示字段 --%>
									<c:choose>
										<c:when test="${ordFlag}">
											<c:set var="checkResult" value="${wpf:checkExt(field.pri,context)}" />
										</c:when>
										<%-- 节点字段,采用decide决定结果 --%>
										<c:when test="${field.decideScript!=null}">
											<c:set var="checkResult" value="${wpf:script(field.decideType,field.decideScript,context)}" />
										</c:when>
										<%-- 继承字段,直接展示--%>
										<c:otherwise>
											<c:set var="checkResult" value="${true}" />
										</c:otherwise>
									</c:choose>

									<c:if test="${checkResult}">
										<c:if test="${field.whole!=2}">
											<dt>${field.busiName}</dt>
										</c:if>
										<dd>
											<c:choose>
												<c:when test="${field.name==null&&field.contentScript!=null}">
													<%-- 展示字段 --%>
													<wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" />
												</c:when>
												<c:when test="${field.name!=null&&field.showContentScript!=null&&field.showContentScript!=''}">
													<%-- 表单字段,设置了showContentScript --%>
													<wpf:script script="${field.showContentScript}" type="${field.showContentType}" context="${context}" />
												</c:when>
												<c:when test="${field.name!=null}">
													<%-- 表单字段,showContentScirpt为空 --%>
													<wcm:widget name="${field.name}" cmd="${field.widget}" state="readonly" value="${wpf:script(field.contentType,field.contentScript,context)}" />
												</c:when>
											</c:choose>
										</dd>
										<c:if test="${fieldStatus.index!=fieldStatus.end}">
											<hr data-am-widget="divider" style="" class="am-divider am-divider-dashed" />
										</c:if>
									</c:if>
								</c:forEach>
							</dl>
						</div>
					</div>
				</div>
			</c:if>
		</c:if>
	</c:forEach>
</div>

<footer data-am-widget="footer" class="am-footer am-footer-default"></footer>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>