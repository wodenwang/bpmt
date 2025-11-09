<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//查看新闻
		$.each($('table.note a', $zone), function() {
			var $this = $(this);
			var id = $this.attr("noteId");
			var title = $this.attr('noteTitle');
			$this.click(function() {
				Ajax.win('${_acp}/detail.shtml', {
					data : {
						viewKey : '${viewKey}',
						id : id
					},
					title : title,
					minWidth : 800,
					minHeight : 400,
					buttons : [ {
						text : '${wpf:lan("#:zh[关闭]:en[Close]#")}',
						click : function() {
							$(this).dialog("close");
						}
					} ]
				});
			});
		});
	});
</script>

<style type="text/css">
/* ul.note {
	padding-left: 15px;
	list-style: none;
}

ul.note li {
	margin-bottom: 10px;
}

ul.note a {
	font-weight: bold;
	text-decoration: none;
	color: blue;
}

ul.note a span {
	text-decoration: underline !important;
	margin-right: 5px;
}

ul.note a:visited {
	text-decoration: none;
	color: #7d7d7d;
}

ul.note a:hover {
	text-decoration: underline;
	color: red;
} */
</style>
<table class="tc-table note">
	<tbody>
		<c:forEach items="${list}" var="vo">	
			<tr>
				<th><span>[${wcm:widget('date',vo.PUBLISH_DATE)}]</span></th>
				<th style="text-align: left;">
					<a href="javascript:void(0);" noteId="${vo.ID}" noteTitle="${vo.TITLE}"><c:if test="${vo.TOP_FLAG==1}">
					<span style="color: red; font-weight: bold;">[${wpf:lan("#:zh[置顶]:en[Top]#")}]</span>
				</c:if><font color="${vo.COLOR}">${vo.TITLE}</font></a>
				</th>
			</tr>
		</c:forEach>
	</tbody>
</table>
<%-- <ul class="note">
	<c:forEach items="${list}" var="vo">
		<li><a href="javascript:void(0);" noteId="${vo.ID}" noteTitle="${vo.TITLE}"> <span>[${wcm:widget('date',vo.PUBLISH_DATE)}]</span> <c:if test="${vo.TOP_FLAG==1}">
					<span style="color: red; font-weight: bold;">[${wpf:lan("#:zh[置顶]:en[Top]#")}]</span>
				</c:if><font color="${vo.COLOR}">${vo.TITLE}</font></a></li>
	</c:forEach>
</ul> --%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>