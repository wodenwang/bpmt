<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(null,'vo',vo)}" />
<%-- 获取left join绑定上下文 --%>
<c:forEach items="${config.table.parents}" var="parent">
	<c:set var="context" value="${wcm:map(context,parent.var,wpf:pixelVO(parent.var,vo))}" />
</c:forEach>
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

	});
</script>

<div tabs="true">
	<c:forEach items="${config.subs}" var="sub" varStatus="status">
		<c:if test="${wpf:checkExt(sub.pri,context)}">
			<c:choose>
				<%-- 视图子标签 --%>
				<c:when test="${sub.action!=null}">
					<div title="${wpf:lan(sub.busiName)}" id="${_zone}_subTab_${status.index}" tabStyle="${sub.style}">
						<script type="text/javascript">
							$(function() {
								var $zone = $('#${_zone}');
								//刷新主表明细
								Core.fn('${_zone}_${sub.subKey}_list', 'callback', function() {
									if ($.isFunction(Core.fn($zone, 'callback'))) {
										Core.fn($zone, 'callback')();
									}
								});

								$('#${_zone}_${sub.subKey}_list_form').submit();
							});
						</script>
						<form action="${_cp}${sub.action}" id="${_zone}_${sub.subKey}_list_form" name="sub_form" zone="${_zone}_${sub.subKey}_list">
							<c:if test="${sub.paramScript!=null&&sub.paramScript!=''}">
								<textarea style="display: none;" name="_params">${wpf:script(sub.paramType,sub.paramScript,context)}</textarea>
							</c:if>
							<textarea style="display: none;" name="_main">${param._main}</textarea>
						</form>
						<div id="${_zone}_${sub.subKey}_list"></div>
					</div>
				</c:when>
				<%-- 操作日志 --%>
				<c:when test="${sub.name=='log'&&config.table.logTable!=null}">
					<div title="${wpf:lan(sub.busiName)}" id="${_zone}_subTab_${status.index}" tabStyle="${sub.style}">
						<script type="text/javascript">
							$(function() {
								var $zone = $('#${_zone}');
								//刷新主表明细
								Core.fn('${_zone}_${sub.subKey}_list', 'callback', function() {
									Core.fn('${_zone}', 'callback')();
								});

								$('#${_zone}_${sub.subKey}_list_form').submit();
							});
						</script>
						<form action="${_acp}/log.shtml" name="sub_form" id="${_zone}_${sub.subKey}_list_form" zone="${_zone}_${sub.subKey}_list">
							<textarea style="display: none;" name="_key">${fn:replace(wcm:jsonKey(vo,config.keysArray),'\\\\"','')}</textarea>
						</form>
						<div id="${_zone}_${sub.subKey}_list"></div>
					</div>
				</c:when>
			</c:choose>
		</c:if>
	</c:forEach>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>