<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(null,'vo',vo)}" />
<c:set var="context" value="${wcm:map(context,'fo',fo)}" />
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		//流程图子标签
		$('form[name=sub_form][sysName=picture]', $zone).attr('action', '${_acp}/pictureMain.shtml');
		//历史信息子标签
		$('form[name=sub_form][sysName=history]', $zone).attr('action', '${_acp}/history.shtml');

	});
</script>
<c:if test="${subs!=null&&fn:length(subs)>0}">
	<div tabs="true">
		<c:forEach items="${subs}" var="sub" varStatus="status">
			<%-- 权限空白,直接可看 --%>
			<c:if test="${sub.pri==null||wpf:checkExt(sub.pri,context)}">
				<c:choose>
					<%-- 视图子标签 --%>
					<c:when test="${sub.action!=null}">
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
							<form action="${_cp}${sub.action}" id="${_zone}_${sub.subKey}_list_form" name="sub_form" zone="${_zone}_${sub.subKey}_list">
								<c:if test="${sub.paramScript!=null&&sub.paramScript!=''}">
									<textarea style="display: none;" name="_params">${wpf:script(sub.paramType,sub.paramScript,context)}</textarea>
								</c:if>
								<textarea style="display: none;" name="_main">${param._main}</textarea>
							</form>
							<div id="${_zone}_${sub.subKey}_list"></div>
						</div>
					</c:when>
					<%-- 系统子标签 --%>
					<c:otherwise>
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
							<form sysName="${sub.name}" name="sub_form" id="${_zone}_${sub.subKey}_list_form" zone="${_zone}_${sub.subKey}_list">
								<textarea style="display: none;" name="_FO">${wcm:json(fo)}</textarea>
							</form>
							<div id="${_zone}_${sub.subKey}_list"></div>
						</div>
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</div>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>