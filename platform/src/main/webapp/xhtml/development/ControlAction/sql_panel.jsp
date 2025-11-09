<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		//沙箱执行
		$('button[name=sandExec]', $zone).click(function() {
			var type = $(this).val();
			$('input[name=type]', $zone).val(type);
			$('form', $zone).submit();
		});

		//真实执行
		$('button[name=exec]', $zone).click(function() {
			Ui.confirmPassword("此操作会改变当前系统的真实数据,请否继续?", function() {
				Ajax.post('${_zone}_msg', '${_acp}/submitSqlPanelReal.shtml', {
					data : {
						cmd : $('[name=cmd]', $zone).val()
					}
				});
			});
		});
	});
</script>

<form zone="${_zone}_msg" action="${_acp}/submitSqlPanel.shtml">
	<input type="hidden" name="type" />
	<div id="${_zone}_textarea" style="margin-left: 5px; margin-bottom: 5px;">
		<wcm:widget name="cmd" cmd="codemirror[sql;true]"></wcm:widget>
	</div>

	<div class="ws-bar">
		<div class="ws-group left">
			<button icon="search" type="button" value="1" name="sandExec">查询</button>
			<button icon="wrench" type="button" value="2" name="sandExec" tip="true" title="在一个模拟当前系统环境,但完全隔离的沙箱内运行,不会对现有系统的数据造成影响.">虚拟执行</button>
		</div>
		<div class="ws-group right">
			<button type="button" icon="wrench" name="exec">真实执行</button>
			<button icon="arrowreturnthick-1-w" type="reset">清空</button>
		</div>
	</div>
</form>

<div id="${_zone}_msg">
	<div class="ws-msg info">请执行SQL语句.</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>