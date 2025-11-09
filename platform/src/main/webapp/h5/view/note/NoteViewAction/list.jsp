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

<div data-am-widget="list_news" class="am-list-news am-list-news-default">
	<div class="am-list-news-hd am-cf">
		<a href="###" class="">
			<h2>列表展示</h2>
		</a>
	</div>

	<div class="am-list-news-bd">
		<ul class="am-list">
			<c:forEach items="${list}" var="vo">
				<li class="am-g am-list-item-dated"><a href="${_acp}/detail.shtml?id=${vo.ID}&viewKey=${viewKey}&_params=${wcm:urlEncode(param._params)}" class="am-list-item-hd "><c:if
							test="${vo.TOP_FLAG==1}">
							<span class="am-badge am-badge-danger">置顶</span>
						</c:if><span style="color: ${vo.COLOR};">${vo.TITLE}</span></a> <span class="am-list-date">${wcm:widget('date',vo.PUBLISH_DATE)}</span> <c:if test="${vo.REMARK!=null}">
						<div class="am-list-item-text">${vo.REMARK}</div>
					</c:if></li>
			</c:forEach>
		</ul>
	</div>
</div>

<footer data-am-widget="footer" class="am-footer am-footer-default"></footer>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>