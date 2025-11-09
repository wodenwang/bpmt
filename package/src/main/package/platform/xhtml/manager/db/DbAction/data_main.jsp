<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//重新查询
		Core.fn($zone, 'query', function() {
			$('#${_zone}_data_list_zone_form', $zone).submit();
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
						Ajax.form("${_zone}_data_msg_zone", $form, option);
					}
				} ]
			});
		});

		//新增按钮
		Core.fn('${_zone}_data_list_zone', 'add', function() {
			Core.fn($zone, 'winZone')('新增字典数据',
					'${_acp}/createDataZone.shtml', {
						dataType : '${vo.dataType}'
					});
		});

		//编辑按钮
		Core.fn('${_zone}_data_list_zone', 'edit', function(dataCode) {
			Core.fn($zone, 'winZone')('编辑字典数据',
					'${_acp}/editDataZone.shtml', {
						dataType : '${vo.dataType}',
						dataCode : dataCode
					});
		});

		//删除按钮
		Core.fn('${_zone}_data_list_zone', 'delete', function() {
			var $form = $('#${_zone}_data_delete_form');
			if ($('[name=dataCode]:checked', $form).size() < 1) {
				Ui.alert('请至少选中一项.');
				return;
			}

			Ui.confirm('是否确认删除?', function() {
				Ajax.form('${_zone}_data_msg_zone', $form, {
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'query')();
						}
					}
				});
			});
		});

		Core.fn($zone, 'query')();
	});
</script>

<div panel="[${vo.dataType}]${vo.busiName}">
	<form action="${_acp}/dataList.shtml" zone="${_zone}_data_list_zone"
		id="${_zone}_data_list_zone_form">
		<input type="hidden" name="_se_dataType" value="${vo.dataType}" /> <input
			type="hidden" name="_field" value="sort" /> <input type="hidden"
			name="_dir" value="asc" />
		<table class="ws-table">
			<tr>
				<th>代码</th>
				<td><wcm:widget name="_sl_dataCode" cmd="text"></wcm:widget></td>
				<th>翻译值</th>
				<td><wcm:widget name="_sl_showName" cmd="text"></wcm:widget></td>
			</tr>
			<tr>
				<th>父代码</th>
				<td><wcm:widget name="_sl_parentCode" cmd="text"></wcm:widget></td>
				<th>扩展字段</th>
				<td><wcm:widget name="_sl_description" cmd="text"></wcm:widget></td>
			</tr>
			<tr>
				<th class="ws-bar ">
					<div class="right">
						<button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
						<button type="submit" icon="search" text="true">查询</button>
					</div>
				</th>
			</tr>
		</table>
	</form>

	<div id="${_zone}_data_msg_zone"></div>
	<form action="${_acp}/dataDelete.shtml" aync="true"
		id="${_zone}_data_delete_form">
		<input type="hidden" name="dataType" value="${vo.dataType}" />
		<div id="${_zone}_data_list_zone"></div>
	</form>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>