<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(wcm:map(null,'mode',2),'vo',vo)}" />

<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<script>
	$(function() {

		//增加loading
		window.onbeforeunload = function() {
			Wxui.showLoading();
		};

		$("#${_zone}_btn_home").on('click', function(event) {
			window.location.href = "${_acp}/list.shtml?_params=${wcm:urlEncode(param._params)}";
		});
	});
</script>

<div data-am-widget="gotop" class="am-gotop am-gotop-fixed">
	<a href="#top" title="回到顶部"> <span class="am-gotop-title">回到顶部</span> <i class="am-gotop-icon am-icon-chevron-up"></i>
	</a>
</div>

<%--顶部--%>
<header data-am-widget="header" class="am-header am-header-default">
	<div class="am-header-left am-header-nav">
		<a href="javascript:void(0);" class="" id="${_zone}_btn_home"><i class="am-header-icon am-icon-home"></i></a>
	</div>
	<h1 class="am-header-title">${title}[查看]</h1>
</header>

<div class="am-panel-group" id="${_zone}_accordion">
	<c:forEach items="${config.h5DetailList}" var="line" varStatus="status">
		<c:if test="${line.whole==null}">
			<%--分割线 --%>
			<c:if test="${line.name==null&&wpf:checkExt(line.pri,context)}">
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
								<c:forEach items="${config.h5DetailList}" var="field" varStatus="fieldStatus" begin="${line.detail_begin}" end="${line.detail_end}">
									<c:if test="${wpf:checkExt(field.pri,context)}">
										<dt>${field.busiName}</dt>
										<dd>
											<wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" />
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