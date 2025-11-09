<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $logTableSelects = $('select[name="table.logTable"]', $zone);

		var urls = {};//构建map
		{
			urls['${_zone}_column'] = '${_acp}/columnConfigForm.shtml';
			urls['${_zone}_query'] = '${_acp}/queryConfigForm.shtml';
			urls['${_zone}_subs'] = '${_acp}/subsConfigForm.shtml';
			urls['${_zone}_vars'] = '${_acp}/varConfigForm.shtml';
			urls['${_zone}_limit'] = '${_acp}/limitConfigForm.shtml';
			urls['${_zone}_btn'] = '${_acp}/btnConfigForm.shtml';
			urls['${_zone}_execs'] = '${_acp}/execConfigForm.shtml';
			urls['${_zone}_js'] = '${_acp}/jsConfigForm.shtml';
			urls['${_zone}_frame'] = '${_acp}/frameSetting.shtml';
			urls['${_zone}_weixin'] = '${_acp}/weixinSetting.shtml';
		}

		//tabs事件
		$('#${_zone}_tabs', $zone).on("tabsactivate", function(event, ui) {
			var $panel = ui.newPanel;
			var id = $panel.attr("id");

			$('textarea', ui.newPanel).blur();
			var current = $("#${_zone}_tabs", $zone).tabs("option", "active");
			if (current == 0) {
				//do nothing
			} else if (current == 10) {
				$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ]);
			} else {
				$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 10 ]);
			}

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
							tableName : manTable
						}
					});
					$panel.attr("oldMan", manTable);
				}
			}
		});

		//新建时
		if ('${table!=null?1:0}' == '0') {
			$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 10 ]);
		}

		$("select[name='table.tableName']", $zone).change(function() {
			var name = $(this).val();
			if (name != null && name != '') {
				var busiName = $('option[value=' + name + ']', $(this)).html();
				busiName = $.trim(busiName.substring(busiName.indexOf(']') + 1));
				$("input[name='table.busiName']", $zone).val(busiName);

				$('option[value!=""]', $logTableSelects).remove();
				if (!$logTableSelects.prop('disabled') && name != '') {
					Ajax.json('${_acp}/fetchLogTables.shtml', function(list) {
						if (list != null && $.isArray(list)) {
							$.each(list, function(i, o) {
								var $option = $('<option></option>');
								$option.html('[' + o.description + ']' + o.name);
								$option.val(o.name);
								$logTableSelects.append($option);
							});
							$logTableSelects.trigger("liszt:updated");
						}
					}, {
						data : {
							tableName : name
						}
					});
				}

				Ajax.post('${_zone}_sortName_zone', '${_acp}/sortNameSelect.shtml', {
					data : {
						tableName : name
					}
				});
			}
		});

		if ('${table!=null}' == 'true') {//编辑状态
			Ajax.post('${_zone}_sortName_zone', '${_acp}/sortNameSelect.shtml', {
				data : {
					tableName : '${table.name}',
					sortName : '${table.sortName}'
				}
			});
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
							<input type="hidden" value="${table.name}" name="table.tableName" />
							<select class="chosen {required:true}" name="_table.tableName" disabled="disabled">
								<option value="">请选择绑定表</option>
								<c:forEach items="${tables}" var="vo">
									<c:choose>
										<c:when test="${vo.name eq table.name}">
											<option value="${vo.name}" selected="selected">[${vo.name}]${vo.description}</option>
										</c:when>
										<c:otherwise>
											<option value="${vo.name}">[${vo.name}]${vo.description}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</c:when>
						<c:otherwise>
							<select class="chosen {required:true}" name="table.tableName">
								<option value="">请选择绑定表</option>
								<c:forEach items="${tables}" var="vo">
									<c:choose>
										<c:when test="${vo.name eq table.name}">
											<option value="${vo.name}" selected="selected">[${vo.name}]${vo.description}</option>
										</c:when>
										<c:otherwise>
											<option value="${vo.name}">[${vo.name}]${vo.description}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
							</select>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>展示名</th>
				<td><wcm:widget name="table.busiName" cmd="text{required:true}" value="${table!=null?table.busiName:''}"></wcm:widget></td>
			</tr>
			<tr>
				<th>关联日志表</th>
				<td><select class="chosen" name="table.logTable">
						<option value="">请选择日志表</option>
						<c:forEach items="${logTables}" var="vo">
							<c:choose>
								<c:when test="${vo.name eq table.logTable}">
									<option value="${vo.name}" selected="selected">[${vo.name}]${vo.description}</option>
								</c:when>
								<c:otherwise>
									<option value="${vo.name}">[${vo.name}]${vo.description}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
				</select></td>
			</tr>
		</table>
		<div accordion="true" multi="true">
			<div title="展示与排序">
				<table class="ws-table">
					<tr>
						<th>展示分列数量</th>
						<td><wcm:widget name="table.col" cmd="text{required:true,digits:true,max:5,min:1}" value="${table!=null?table.col:2}"></wcm:widget></td>
					</tr>
					<tr>
						<th>自动查询 <font color="red" tip="true" title="打开视图时无需手动点击'查询'即可浏览数据.">(提示)</font></th>
						<td><wcm:widget name="table.initQuery" cmd="radio[@com.riversoft.platform.translate.InitQueryType]{required:true}" value="${table!=null?table.initQuery:1}"></wcm:widget></td>
					</tr>
					<tr>
						<th>每页条数</th>
						<td><wcm:widget name="table.pageLimit" cmd="text{digits:true}" value="${table!=null?table.pageLimit:''}" /></td>
					</tr>
					<tr>
						<th>排序字段</th>
						<td>
							<div id="${_zone}_sortName_zone"></div>
						</td>
					</tr>
					<tr>
						<th>排序方向</th>
						<td><wcm:widget name="table.dir" cmd="radio[SORT_DIR]{required:true}" value="${table!=null?table.dir:'asc'}"></wcm:widget></td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	<div title="视图字段" id="${_zone}_column"></div>
	<div title="按钮设置" id="${_zone}_btn"></div>
	<div title="页面脚本(JS)" id="${_zone}_js"></div>
	<div title="数据筛选" id="${_zone}_limit"></div>
	<div title="查询条件" id="${_zone}_query"></div>
	<div title="展示变量" id="${_zone}_vars"></div>
	<div title="数据处理器" id="${_zone}_execs"></div>
	<div title="子表设置" id="${_zone}_subs"></div>
	<div title="微信设置" id="${_zone}_weixin"></div>
	<div title="界面排版" id="${_zone}_frame"></div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>