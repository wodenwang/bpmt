<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<c:choose>
	<c:when test="${fn:length(homes)>0}">
		<div class="tc-g tc-column-2">
			<div class="tc-flex-2">
				<c:forEach items="${homes}" var="home">
					<div class="tc-panel">
						<div class="tc-control-wrap">
							<!-- <i class="icon tc-icon-notice"></i> -->
							<h3 class="title">${wpf:lan(home.name)}</h3>
							<span class="icon tc-icon-reduce" tc-collapse=""></span>
						</div>
						<div class="tc-panel-collapse tc-collapse" id="${_zone}_home_${home.columnIndex}_${home.sort}">
							<textarea style="display: none;">${home.params}</textarea>
						</div>
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
					</div>
				</c:forEach>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div style="text-align:center;line-height:100px;font-size:14px;color:#1d95d4;">管理员暂无配置面板</div>
	</c:otherwise>
</c:choose>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");
		//伸缩
		var $collapse = $('[tc-collapse]', $zone);
		$collapse.on('click', function() {
			var _this = $(this);
			_this.toggleClass('on').parent().next().slideToggle(300);
		})
	});
</script>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>