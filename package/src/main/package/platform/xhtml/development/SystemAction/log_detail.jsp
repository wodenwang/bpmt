<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$('a', $zone).click(function(event) {
			event.preventDefault();
			//触发事件
			Ui.confirm('确认下载[${param.fileName}]?', function() {
				Ajax.download('${_acp}/downloadLog.shtml?fileName=${param.fileName}');
			});
		});
	})
</script>

<div style="text-align: right;">
	<span style="color: red; float: left;">(注:[${param.fileName}]只显示最后500条记录.)</span> <a href="#">[完整日志下载]</a>
</div>
<div style="border: solid 1px #333; margin-top: 5px;">
	<textarea name="_tmp" width="100%" height="500" code="true" option="{mode:'htmlmixed',readOnly:true,firstLineNumber:${firstLine},showCursorWhenSelecting:true}" class="CodeMirror-normal" readonly="readonly">${logs}</textarea>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>