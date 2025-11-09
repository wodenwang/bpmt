<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:choose>
	<c:when test="${fn:length(homes)>0}">
		<c:forEach items="${fn:split(vo.columns,';')}" var="scale"
			varStatus="state">
			<div class="ws-column" scale="${scale}" style="margin-top: 5px;">
				<c:forEach items="${homes}" var="home">
					<c:if test="${home.columnIndex==state.index}">
						<div panel="${wpf:lan(home.name)}" expand="true" height="${home.height}">
							<script type="text/javascript">
								$(function() {
									var $div = $('#${_zone}_home_${home.columnIndex}_${home.sort}');
									var _params = $('textarea', $div).val();
									var data = {
										_frame_type : 2
									};//来源于首页
									if (_params != '') {
										data._params = _params;
									}
									Ajax.post($div, _cp + '${home.action}', {
										data : data
									});
								});
							</script>
							<div id="${_zone}_home_${home.columnIndex}_${home.sort}">
								<textarea style="display: none;">${home.params}</textarea>
							</div>
						</div>
					</c:if>
				</c:forEach>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div panel="${wpf:lan('#:zh[首页]:en[Home page]#')}">
			<div class="ws-msg info">${wpf:lan("#:zh[没有首页标签]:en[No front page tag]#")}.</div>
		</div>
	</c:otherwise>
</c:choose>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>