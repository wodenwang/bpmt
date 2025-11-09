<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//重新查询
		Core.fn($zone, 'query', function() {
			$('#${_zone}_function_list_zone_form', $zone).submit();
		});

		//弹出编辑框
		Core.fn($zone, 'winZone', function(title, url, data) {
			Ajax.win(url, {
				title : title,
				minWidth : 600,
				data : data,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '保存',
					click : function() {
						var $dialog = $(this);
						var $form = $('form', $dialog);
						var $msgZone = $('div[name=msgZone]', $dialog);
						var option = {
							confirmMsg : '确认保存数据?',
							errorZone : $msgZone.attr('id'),
							callback : function(flag) {
								if (flag) {
									$dialog.dialog("close");
									Core.fn($zone, 'query')();
								}
							}
						};
						Ajax.form("${_zone}_function_msg_zone", $form, option);
					}
				} ]
			});
		});

		//新增按钮
		Core.fn('${_zone}_function_list_zone', 'add', function() {
			Core.fn($zone, 'winZone')('新增函数', '${_acp}/createFunctionZone.shtml', {
				catelog : '${param.cateKey}'
			});
		});

		//编辑按钮
		Core.fn('${_zone}_function_list_zone', 'edit', function(functionKey) {
			Core.fn($zone, 'winZone')('编辑函数', '${_acp}/editFunctionZone.shtml', {
				catelog : '${param.cateKey}',
				functionKey : functionKey
			});
		});

		//删除按钮
		Core.fn('${_zone}_function_list_zone', 'delete', function() {
			var $form = $('#${_zone}_function_delete_form');
			if ($('[name=functionKey]:checked', $form).size() < 1) {
				Ui.alert('请至少选中一项.');
				return;
			}

			Ui.confirmPassword('是否确认删除?', function() {
				Ajax.form('${_zone}_function_msg_zone', $form, {
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'query')();
						}
					}
				});
			});
		});

		//编辑按钮
		Core.fn('${_zone}_function_list_zone', 'detail', function(functionKey) {
			Ajax.post('${_zone}_detail_zone', '${_acp}/functionDetailTab.shtml?functionKey=' + functionKey);
		});

		Core.fn($zone, 'query')();
	});
</script>

<c:set var="title" value="查询结果" />
<c:if test="${vo!=null}">
	<c:set var="title" value="[${vo.busiName}]函数展示" />
</c:if>
<div panel="${title}">

	<div id="${_zone}_detail_zone"></div>

	<form action="${_acp}/functionList.shtml" zone="${_zone}_function_list_zone" id="${_zone}_function_list_zone_form">
		<input type="hidden" name="_se_catelog" value="${param.cateKey}" /> <input type="hidden" name="_sl_functionKey" value="${param.functionKey}" /> <input type="hidden" name="_sl_description"
			value="${param.description}" /><input type="hidden" name="_sl_functionScript" value="${param.functionScript}" /><input type="hidden" name="_field" value="functionKey" /> <input type="hidden"
			name="_dir" value="asc" />
	</form>

	<div id="${_zone}_function_msg_zone"></div>
	<form action="${_acp}/functionDelete.shtml" aync="true" id="${_zone}_function_delete_form">
		<div id="${_zone}_function_list_zone"></div>
	</form>
</div>



<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>