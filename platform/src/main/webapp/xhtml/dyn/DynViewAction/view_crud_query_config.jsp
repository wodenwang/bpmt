<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="detailFlag" value="${param.detailFlag!=null&&param.detailFlag=='true'}" />

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		$("[name$='.busiName']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('select[name=tmpName]', $zone).on("change", function() {
			var $this = $(this);
			var value = this.value;
			var $condition = $('[name=tmpCondition]', $this.parents('table'));
			if (value == '') {
				$('optgroup', $condition).attr("disabled", "disabled").trigger('liszt:updated');
			} else {
				var type = $('option[value=' + value + ']', $this).attr('type');
				type = 0 + type;
				if (type == 91 || type == 92 || type == 93) {
					type = 'date';
				} else if (type == 12 || type == 2005) {
					type = 'string';
				} else {
					type = 'number';
				}
				$('optgroup', $condition).attr("disabled", "disabled");
				$('optgroup[name=' + type + ']', $condition).removeAttr("disabled");
				$condition.val('').trigger('liszt:updated');
			}
		});

		$('select[name=tmpCondition]', $zone).on("change", function() {
			var $this = $(this);
			var value = this.value;
			var $column = $('select[name=tmpName]', $this.parents('table'));
			var $name = $("input[name$='.name']", $this.parents('table'));
			$name.val(value + '_' + $column.val())
		});

		$.each($("input:disabled[name$='.name']", $zone), function() {
			var $this = $(this);
			var value = $this.val();
			var column = value.substring(value.lastIndexOf('_') + 1);
			var condition = value.substring(0, value.lastIndexOf('_'));
			var $columnSelector = $('select[name=tmpName]', $this.parents('table'));
			var $conditionSelector = $('[name=tmpCondition]', $this.parents('table'));
			$columnSelector.val(column).trigger('liszt:updated');
			$columnSelector.click();
			$conditionSelector.val(condition).trigger('liszt:updated');
		});

		$('button[name=normalAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_querys_tabs');
			Ajax.tab($tabs, '${_acp}/normalQueryForm.shtml', {
				data : {
					tableName : '${param.tableName}',
					type : 'querys',
					pixel : 'querys.B' + Core.nextSeq()
				}
			});
		});

		$('button[name=extAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_querys_tabs');
			Ajax.tab($tabs, '${_acp}/extQueryForm.shtml', {
				data : {
					type : 'querys',
					pixel : 'querys.B' + Core.nextSeq()
				}
			});
		});
	});
</script>

<input type="hidden" name="hasQuerys" value="true" />
<div class="ws-bar">
	<div class="ws-group left">
		<button icon="plus" type="button" name="normalAdd">新增普通查询</button>
		<button icon="plus" type="button" name="extAdd">新增高级查询</button>
	</div>
</div>

<div tabs="true" button="left" sort="y" id="${_zone}_querys_tabs">
	<c:if test="${querys!=null&&fn:length(querys)>0}">
		<c:forEach items="${querys}" var="vo" varStatus="status">

			<div title="${vo.busiName}" close="true">
				<c:set var="pixel" value="querys.A${status.index}" />
				<input type="hidden" name="querys" value="${pixel}" />
				<c:choose>
					<%-- 简单查询 --%>
					<c:when test="${vo.sqlType==null}">
						<table class="ws-table">
							<tr>
								<th>查询字段</th>
								<td><select name="tmpName" class="chosen">
										<option value="">请选择</option>
										<c:forEach items="${tbTable.tbColumns}" var="tb">
											<option value="${tb.name}" type="${tb.mappedTypeCode}">[${tb.name}]${tb.description}</option>
										</c:forEach>
								</select></td>
							</tr>
							<tr>
								<th>条件</th>
								<td><select name="tmpCondition" class="chosen">
										<option value="">请选择</option>
										<optgroup label="字符串判断" name="string" disabled="disabled">
											<option value="_se">等于</option>
											<option value="_sne">不等于</option>
											<option value="_sl">模糊匹配</option>
											<option value="_snl">不匹配</option>
											<option value="_sin">包含(多选框)</option>
											<option value="_snin">不包含(多选框)</option>
										</optgroup>
										<optgroup label="数字判断" name="number" disabled="disabled">
											<option value="_ne">等于</option>
											<option value="_nne">不等于</option>
											<option value="_nb">大于</option>
											<option value="_nbe">大于或等于</option>
											<option value="_ns">小于</option>
											<option value="_nse">小于或等于</option>
											<option value="_nin">包含(多选框)</option>
											<option value="_nnin">不包含(多选框)</option>
										</optgroup>
										<optgroup label="日期判断" name="date" disabled="disabled">
											<option value="_de">等于</option>
											<option value="_dne">不等于</option>
											<option value="_dnm">小于等于</option>
											<option value="_dnl">大于等于</option>
										</optgroup>
								</select></td>
							</tr>
							<tr>
								<th>生成命令</th>
								<td><input type="text" name="${pixel}.name" class="{required:true}" readonly="readonly" value="${vo.name}" /></td>
							</tr>
							<tr>
								<th>展示名</th>
								<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件</th>
								<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${vo.widget}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件动态入参(脚本类型)</th>
								<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.widgetParamType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件动态入参(脚本)</th>
								<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" value="${vo.widgetParamScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>默认值</th>
								<td><wcm:widget name="${pixel}.defVal" cmd="textarea" value="${vo.defVal}"></wcm:widget></td>
							</tr>
						</table>
					</c:when>
					<%-- 复杂查询 --%>
					<c:otherwise>
						<table class="ws-table">
							<tr>
								<th>展示名</th>
								<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
							</tr>
							<tr>
								<th>字段名</th>
								<td><wcm:widget name="${pixel}.name" cmd="text{required:false}" value="${vo.name}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件</th>
								<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${vo.widget}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件动态入参(脚本类型)</th>
								<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.widgetParamType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件动态入参(脚本)</th>
								<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" value="${vo.widgetParamScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>默认值</th>
								<td><wcm:widget name="${pixel}.defVal" cmd="textarea" value="${vo.defVal}"></wcm:widget></td>
							</tr>
							<tr>
								<th>SQL片段(脚本类型)</th>
								<td><wcm:widget name="${pixel}.sqlType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.sqlType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>SQL片段(脚本)<font color="red" tip="true" title="value:提交后表单值;values:多选框表单值">(提示)</font></th>
								<td><wcm:widget name="${pixel}.sqlScript" cmd="codemirror[groovy]{required:true}" value="${vo.sqlScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>备注</th>
								<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description!='null'?vo.description:''}"></wcm:widget></td>
							</tr>
						</table>
					</c:otherwise>
				</c:choose>
			</div>
		</c:forEach>
	</c:if>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>