<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		//左导航伸展
		$('[left-nav]', $zone).on('click', function() {
			$(this).toggleClass('on').next().slideToggle(300);
		});
	});
</script>

<ul>
	<c:forEach items="${menus}" var="p">
		<c:if test="${p.parentId==null||p.parentId==''}">
			<li><a href="javascript:void(0);" class="tc-nav" left-nav=""><i class="tc-icon tc-icon-index"></i>${p.name}<i class="tc-arrow"></i></a>
				<div class="tc-sub-nav-wrap">
					<c:forEach items="${menus}" var="m">
						<c:if test="${m.parentId==p.id}">
							<a href="javascript:void(0);" class="tc-sub-nav">${m.name}</a>
						</c:if>
					</c:forEach>
				</div></li>
		</c:if>
	</c:forEach>
</ul>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>