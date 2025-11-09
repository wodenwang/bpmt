<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var urls = {};//构建map
		{
			urls['${_zone}_limit'] = '${_acp}/limitConfigForm.shtml';
		}

		$('#${_zone}_tabs', $zone).on("tabsactivate", function(event, ui) {
			var $panel = ui.newPanel;
			var id = $panel.attr("id");
			var manTable = $("[name='table.tableName']", $zone).val();//主表
			var oldMan = $panel.attr("oldMan");
			var url = urls[id];
			if (url != undefined && url != null) {
				if (manTable == '') {
					Ui.alert('请先选择主表.');
					$(this).tabs("option", "active", 0);
				} else if (oldMan == undefined || oldMan != manTable) {
					Ajax.post(ui.newPanel, url, {
						data : {
							key : '${table.viewKey}',
							tableName : manTable,
							copy : '${isCopy?1:0}'
						}
					});
					$panel.attr("oldMan", manTable);
				}
			}
		});

		$("select[name='table.tableName']", $zone).change(function() {
			var name = $(this).val();
			if (name != null && name != '') {
				var busiName = $('option[value=' + name + ']', $(this)).html();
				busiName = $.trim(busiName.substring(busiName.indexOf(']') + 1));
				$("input[name='table.busiName']", $zone).val(busiName);
			}
		});

		if ('${table!=null}' == 'true') {//编辑状态
			//激活标签
			$('li a[tab]', $('#${_zone}_tabs', $zone)).click();
			$('li a[tab=0]', $('#${_zone}_tabs', $zone)).click();
		}
	});
</script>

<div tabs="true" id="${_zone}_tabs">
	<div title="数据表关联">
		<table class="ws-table">
			<tr>
				<th>关联表</th>
				<td><c:choose>
						<c:when test="${table!=null}">
							<wcm:widget name="table.tableName"
								cmd="select[$com.riversoft.platform.po.TbTable;name;description;null;true]"
								value="${table.tableName}" state="readonly"></wcm:widget>
						</c:when>
						<c:otherwise>
							<select name="table.tableName" class="chosen">
								<option value="">请选择</option>
								<c:forEach items="${tables}" var="model">
									<option value="${model.name}">[${model.name}]${model.description}</option>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>展示名</th>
				<td><wcm:widget name="table.busiName" cmd="text{required:true}"
						value="${table!=null?table.busiName:''}"></wcm:widget></td>
			</tr>
		</table>
	</div>
	<div title="数据筛选" id="${_zone}_limit"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>