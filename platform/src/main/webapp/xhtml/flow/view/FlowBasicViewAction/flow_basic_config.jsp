<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var urls = {};//构建map
		{
			urls['${_zone}_column'] = '${_acp}/columnConfigForm.shtml';
			urls['${_zone}_query'] = '${_acp}/queryConfigForm.shtml';
			urls['${_zone}_subs'] = '${_acp}/subsConfigForm.shtml';
			urls['${_zone}_execs'] = '${_acp}/execConfigForm.shtml';
			urls['${_zone}_vars'] = '${_acp}/varConfigForm.shtml';
			urls['${_zone}_limit'] = '${_acp}/limitConfigForm.shtml';
			urls['${_zone}_btn'] = '${_acp}/btnConfigForm.shtml';
			urls['${_zone}_frame'] = '${_acp}/frameSetting.shtml';
			urls['${_zone}_notify'] = '${_acp}/notifySetting.shtml';
			urls['${_zone}_weixin'] = '${_acp}/weixinSetting.shtml';
		}

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
					Ui.alert('请先选择关联订单表.');
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
				busiName = busiName.substring(busiName.indexOf(']') + 1);
				$("input[name='table.busiName']", $zone).val(busiName);
			}
		});

		$('select[name=quickOrdIdRule]', $zone).change(function() {
			var val = $(this).val();
			var script;
			if (val == '1') {//uuid
				script = "return seq.uuid();";
			} else if (val == '2') {//字符串
				script = "return seq.pattern('AUTO-{now}{seq}','yyyyMMdd','${table!=null?table.tableName:"
				"}','ORD_ID',3);";
			} else {
				return;
			}

			$("select[name$='ordIdType']").val('1').trigger('liszt:updated');
			$("textarea[name$='ordIdScript']", $zone).val(script);
			$("textarea[name$='ordIdScript']", $zone).blur();

		});

	});
</script>

<div tabs="true" id="${_zone}_tabs">
	<div title="订单表关联">
		<table class="ws-table">
			<tr>
				<th>绑定流程KEY</th>
				<td><c:choose>
						<c:when test="${table!=null}">
							<wcm:widget name="table.pdKey" cmd="text" value="${table.pdKey}" state="readonly"></wcm:widget>
						</c:when>
						<c:otherwise>
							<wcm:widget name="table.pdKey" cmd="text{required:true}"></wcm:widget>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>关联订单表</th>
				<td><c:choose>
						<c:when test="${table!=null}">
							<wcm:widget name="table.tableName" cmd="select[$com.riversoft.platform.po.TbTable;name;description;null;true]" value="${table.tableName}" state="readonly"></wcm:widget>
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
				<th>关联订单历史表</th>
				<td><c:choose>
						<c:when test="${table!=null&&table.historyTableName!=null}">
							<wcm:widget name="table.historyTableName" cmd="select[$com.riversoft.platform.po.TbTable;name;description;null;true]" value="${table.historyTableName}" state="readonly" />
						</c:when>
						<c:otherwise>
							<font color="red">(不支持配置)</font>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>关联审批意见表</th>
				<td><c:choose>
						<c:when test="${table!=null&&table.opinionTableName!=null}">
							<wcm:widget name="table.opinionTableName" cmd="select[$com.riversoft.platform.po.TbTable;name;description;null;true]" value="${table.opinionTableName}" state="readonly" />
						</c:when>
						<c:otherwise>
							<font color="red">(不支持配置)</font>
						</c:otherwise>
					</c:choose></td>
			</tr>
			<tr>
				<th>展示名</th>
				<td><wcm:widget name="table.busiName" cmd="text{required:true}" value="${table!=null?table.busiName:''}"></wcm:widget></td>
			</tr>
		</table>
		<div accordion="true" multi="true">
			<div title="订单号生成规则">
				<table class="ws-table">
					<tr>
						<th>快速选择</th>
						<td><select class="chosen" name="quickOrdIdRule">
								<option value="">自定义</option>
								<option value="1">UUID</option>
								<option value="2">使用模板</option>
						</select></td>
					</tr>
					<tr>
						<th>规则(脚本类型)</th>
						<td><wcm:widget name="table.ordIdType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${table.ordIdType}"></wcm:widget></td>
					</tr>
					<tr>
						<th>规则(脚本内容)</th>
						<td><wcm:widget name="table.ordIdScript" cmd="codemirror[groovy]{required:true}" value="${table.ordIdScript}"></wcm:widget></td>
					</tr>
				</table>
			</div>
			<div title="展示">
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
						<td><select name="table.sortName" class="chosen {required:true}">
								<c:forEach items="${mainColumns}" var="column">
									<c:choose>
										<c:when test="${column.name eq table.sortName}">
											<option value="${column.name}" selected="selected">[${column.name}]${column.description}</option>
										</c:when>
										<c:otherwise>
											<option value="${column.name}">[${column.name}]${column.description}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
						</select></td>
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
	<div title="数据筛选" id="${_zone}_limit"></div>
	<div title="查询条件" id="${_zone}_query"></div>
	<div title="展示变量" id="${_zone}_vars"></div>
	<div title="数据处理器" id="${_zone}_execs"></div>
	<div title="子表设置" id="${_zone}_subs"></div>
	<div title="消息通知" id="${_zone}_notify"></div>
	<div title="微信设置" id="${_zone}_weixin"></div>
	<div title="界面排版" id="${_zone}_frame"></div>

</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>