<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var initConfig = function() {
			Ajax.post('${_zone}_config', '${_cp}${tableConfigAction}?name=${tableName}', {
				callback : function(flag) {
					if (flag) {
						$('button[name=closeTab]', $('#${_zone}_config')).hide();
					}
				}
			});
		};

		Core.fn('${_zone}_config', 'submitForm', function(form, option) {
			option = $.extend({}, option, {
				callback : function(flag) {
					if (flag) {
						initConfig();
					}
				}
			});
			Ajax.form('${_zone}_error', form, option);
		});

		initConfig();

	});
</script>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<div id="${_zone}_config"></div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>