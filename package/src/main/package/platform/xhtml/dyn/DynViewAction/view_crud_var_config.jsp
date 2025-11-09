<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="isCopy" value="${param.copy==1}" />
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$("[name$='.var']", $zone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $this.parents('[tabs=true]:first'))).html(val);
			}
		});

		$('button[name=prepareExecAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_prepareExecs_tabs');
			Ajax.tab($tabs, '${_acp}/prepareExecsForm.shtml', {
				data : {
					type : 'prepareExecs',
					pixel : 'prepareExecs.B' + Core.nextSeq()
				}
			});
		});

		$('button[name=parentAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_parents_tabs');
			Ajax.tab($tabs, '${_acp}/parentViewConfig.shtml', {
				data : {
					tableName : '${param.tableName}',
					type : 'parents',
					pixel : 'parents.B' + Core.nextSeq()
				}
			});
		});

		$('button[name=foreignAdd]', $zone).click(function() {
			var $tabs = $(this).parents('div.ws-bar:first').next();
			var $div = $tabs.parents('div:first');
			var parentTableName = $("[name$='.tableName']", $div).val();
			var pixel = $("input[name='parents']", $div).val();
			Ajax.tab($tabs, '${_acp}/parentViewForeignOneConfig.shtml', {
				data : {
					tableName : '${tbTable.name}',
					parentTableName : parentTableName,
					type : pixel + '.foreigns',
					pixel : pixel + '.foreigns.B' + Core.nextSeq()
				}
			});
		});

		$("[name$='.parentColumn']", $zone).change(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var description = $('option[value=' + val + ']', $this).html();
				var id = $this.parents('div.ui-tabs-panel:first').attr('id');
				$('a', $("li[aria-controls='" + id + "']", $this.parents('[tabs=true]:first'))).html(description);
			}
		});
		$("[name$='.parentColumn']", $zone).change();

	});
</script>

<input type="hidden" name="hasVars" value="true" />
<div tabs="true">
	<div title="关联表变量">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="parentAdd" tip="true" title="关联变量不仅在展示界面中可以使用,而且作为SQL别名在高级查询中也可使用.">新增关联</button>
			</div>
		</div>
		<div tabs="true" button="left" sort="y" id="${_zone}_parents_tabs">
			<c:if test="${parents!=null&&fn:length(parents)>0}">
				<c:forEach items="${parents}" var="vo" varStatus="status">
					<div title="${vo.var}" close="true">
						<c:set var="pixel" value="parents.A${status.index}" />
						<input type="hidden" name="parents" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>变量名</th>
								<td><wcm:widget name="${pixel}.var" cmd="text{required:true}" value="${vo.var}" /></td>
							</tr>
							<tr>
								<th>关联表</th>
								<td><wcm:widget name="${pixel}.tableName" cmd="select[$com.riversoft.platform.po.TbTable(请选择);name;description;null;true]" value="${vo.tableName}" state="readonly"></wcm:widget></td>
							</tr>
							<tr>
								<th>描述</th>
								<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description}" /></td>
							</tr>
						</table>

						<div class="ws-bar">
							<div class="ws-group left">
								<button icon="plus" type="button" name="foreignAdd">新增外键</button>
							</div>
						</div>

						<div tabs="true" button="left">
							<c:forEach items="${vo.foreigns}" var="foreign" varStatus="foreignStatus">
								<c:set var="parentTable" value="${wpf:po('com.riversoft.platform.po.TbTable',vo.tableName)}" />

								<div title="${foreign.parentColumn}">
									<c:set var="foreignPixel" value="${pixel}.foreigns.A${foreignStatus.index}" />
									<input type="hidden" name="${pixel}.foreigns" value="${foreignPixel}" />
									<table class="ws-table">
										<tr>
											<th>关联表字段</th>
											<td><select name="${foreignPixel}.parentColumn" class="chosen needValid {required:true}">
													<c:forEach items="${parentTable.tbColumns}" var="column">
														<c:choose>
															<c:when test="${foreign.parentColumn==column.name}">
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
											<th>本表字段</th>
											<td><select name="${foreignPixel}.mainColumn" class="chosen needValid {required:true}">
													<c:forEach items="${tbTable.tbColumns}" var="column">
														<c:choose>
															<c:when test="${foreign.mainColumn==column.name}">
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
											<th>描述</th>
											<td><wcm:widget name="${foreignPixel}.description" cmd="textarea" value="${foreign.description}"></wcm:widget></td>
										</tr>
									</table>
								</div>
							</c:forEach>
						</div>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
	<div title="普通变量">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="prepareExecAdd" title="展示变量允许在界面展示中使用,包括展示字段,表单字段等." tip="true">新增变量</button>
			</div>
		</div>
		<div tabs="true" button="left" sort="y" id="${_zone}_prepareExecs_tabs">
			<c:if test="${prepareExecs!=null&&fn:length(prepareExecs)>0}">
				<c:forEach items="${prepareExecs}" var="vo" varStatus="status">
					<div title="${vo.var}" close="true">
						<c:set var="pixel" value="prepareExecs.A${status.index}" />
						<input type="hidden" name="prepareExecs" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>变量名</th>
								<td><wcm:widget name="${pixel}.var" cmd="text{required:true}" value="${vo.var}"></wcm:widget></td>
							</tr>
							<tr>
								<th>脚本类型</th>
								<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>脚本<br /> <font color="red" tip="true" title="vo:实体;mode:列表页=1;明细页=2;表单页=3;导出时=4;">(提示)</font></th>
								<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${vo.execScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>备注</th>
								<td><wcm:widget cmd="textarea" name="${pixel}.description" value="${vo.description}" /></td>
							</tr>
						</table>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
</div>
<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>