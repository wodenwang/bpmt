<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(document).ready(function() {
		var $zone = $('#${_zone}');
		$.each($('.popup-gallery img', $zone), function() {
			var $img = $(this);
			var $a = $img.parents('a:first');
			$img.attr('src', $a.attr("href"));
		});

		$('.popup-gallery', $zone).magnificPopup({
			delegate : 'a',
			type : 'image',
			tLoading : 'Loading image #%curr%...',
			mainClass : 'mfp-img-mobile',
			gallery : {
				enabled : true,
				navigateByImgClick : true,
				preload : [ 0, 1 ]
			// Will preload 0 - before current, and 1 after the current image
			},
			image : {
				tError : '<a href="%url%">#%curr%</a> 地址不正确.',
				titleSrc : function(item) {
					return item.el.attr('title');
				}
			}
		});
	});
</script>

<div class="popup-gallery" style="margin-top: 10px;">
	<c:forEach items="${list}" var="vo">
		<a href="${_cp}/widget/FileAction/download.shtml?name=${vo.sysName}&type=${vo.type}" title="${vo.name}"><img width="${param.width}" height="${param.height}" border="1" /></a>
	</c:forEach>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>