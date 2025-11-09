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

<div data-am-widget="gotop" class="am-gotop am-gotop-fixed">
	<a href="#top" title="回到顶部"> <span class="am-gotop-title">回到顶部</span> <i class="am-gotop-icon am-icon-chevron-up"></i>
	</a>
</div>

<div class="am-container am-margin-top">
	<h2>
		<c:if test="${vo.topFlag}">
			<span class="am-badge am-badge-danger">置顶</span>
		</c:if>
		<span style="color:${vo.COLOR}">${vo.TITLE}</span>
	</h2>
	<div class="am-fr">
		<span class="am-badge">${wcm:widget('date',vo.PUBLISH_DATE)}</span> <span class="am-badge">${vo.AUTHOR}</span>
	</div>
</div>

<hr data-am-widget="divider" class="am-divider am-divider-default" />

<div class="am-container">${vo.CONTENT}</div>

<c:if test="${not empty vo.ATTACHMENT}">
	<wcm:widget name="attachments" cmd="multifilemanager" state="readonly" value="${vo.ATTACHMENT}" actionMode="h5" />
</c:if>

<div class="am-container am-margin-top am-margin-bottom">
	<script type="text/javascript">
		$(function() {
			$("#${_zone}_btn_back").on('click', function(event) {
				window.location.href = "${_acp}/index.shtml?_view_key=${param.viewKey}&_params=${wcm:urlEncode(param._params)}";
			});
		});
	</script>
	<button id="${_zone}_btn_back" type="button" class="am-btn am-btn-primary am-radius am-btn-block">返回</button>
</div>

<footer data-am-widget="footer" class="am-footer am-footer-default"></footer>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>