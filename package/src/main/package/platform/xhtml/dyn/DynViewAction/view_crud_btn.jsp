<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<c:set var="isCopy" value="${param.copy==1}" />
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

		$('button[name=itemAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_item_tabs');
			Ajax.tab($tabs, '${_acp}/itemBtnForm.shtml', {
				data : {
					type : 'itemBtns',
					pixel : 'itemBtns.B' + Core.nextSeq()
				}
			});
		});

		$('button[name=summaryAdd]', $zone).click(function() {
			var $tabs = $('#${_zone}_summary_tabs');
			Ajax.tab($tabs, '${_acp}/summaryBtnForm.shtml', {
				data : {
					type : 'summaryBtns',
					pixel : 'summaryBtns.B' + Core.nextSeq()
				}
			});
		});
	});
</script>

<input type="hidden" name="hasBtns" value="true" />
<div tabs="true">
	<div title="明细按钮(表格循环)">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="itemAdd">新增按钮</button>
			</div>
		</div>

		<div tabs="true" button="left" sort="y" id="${_zone}_item_tabs">
			<c:if test="${itemBtns!=null&&fn:length(itemBtns)>0}">
				<c:forEach items="${itemBtns}" var="vo" varStatus="status">
					<div title="${vo.busiName}" close="${vo.name==null}">
						<c:set var="pixel" value="itemBtns.A${status.index}" />
						<input type="hidden" name="itemBtns" value="${pixel}" />
						<c:choose>
							<%-- 系统内置按钮 --%>
							<c:when test="${vo.name!=null}">
								<input type="hidden" name="${pixel}.name" value="${vo.name}" />
								<input type="hidden" name="${pixel}.icon" value="${vo.icon}" />
								<input type="hidden" name="${pixel}.styleClass" value="${vo.styleClass}" />
								<table class="ws-table">
									<tr>
										<th>按钮名称/图标预览</th>
										<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}" />
											<button type="button" icon="${vo.icon}" text="false">${vo.busiName}</button></td>
									</tr>
									<tr>
										<th>备注</th>
										<td><wcm:widget cmd="textarea" name="${pixel}.description" value="${vo.description}" /></td>
									</tr>
									<tr>
										<th>功能点</th>
										<td><wcm:widget name="${pixel}.pri" cmd="pri[vo:实体]{required:true}" value="${isCopy?null:vo.pri}"></wcm:widget></td>
									</tr>
								</table>
							</c:when>
							<%-- 自定义按钮 --%>
							<c:otherwise>
								<table class="ws-table">
									<tr>
										<th>展示名</th>
										<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
									</tr>
									<tr>
										<th>图标</th>
										<td><wcm:widget name="${pixel}.icon" cmd="icon{required:true}" value="${vo.icon}"></wcm:widget></td>
									</tr>
									<tr>
										<th>打开类型</th>
										<td><wcm:widget name="${pixel}.openType" cmd="radio[@com.riversoft.platform.translate.BtnOpenType]" value="${vo.openType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>绑定视图</th>
										<td><wcm:widget name="${pixel}.action" cmd="view[BTN]{required:true}" value="${vo.action}"></wcm:widget></td>
									</tr>
									<tr>
										<th>动态参数(脚本类型)</th>
										<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.paramType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>动态参数(脚本)<font color="red" tip="true" title="vo:实体;在request中通过[_params]命令字获取.">(提示)</font></th>
										<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]" value="${vo.paramScript}"></wcm:widget></td>
									</tr>
									<tr>
										<th>提示确认信息<br /> <font color="red" tip="true" title="无需确认框则留空.">(提示)</font></th>
										<td><wcm:widget name="${pixel}.confirmMsg" cmd="textarea" value="${vo.confirmMsg}"></wcm:widget></td>
									</tr>
									<tr>
										<th>备注</th>
										<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description}"></wcm:widget></td>
									</tr>
									<tr>
										<th>功能点</th>
										<td><wcm:widget name="${pixel}.pri" cmd="pri[vo:实体]{required:true}" value="${!isCopy?vo.pri:null}"></wcm:widget></td>
									</tr>
								</table>
							</c:otherwise>
						</c:choose>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
	<div title="汇总按钮(底部)">
		<div class="ws-bar">
			<div class="ws-group left">
				<button icon="plus" type="button" name="summaryAdd">新增按钮</button>
			</div>
		</div>
		<div tabs="true" button="left" sort="y" id="${_zone}_summary_tabs">
			<c:if test="${summaryBtns!=null&&fn:length(summaryBtns)>0}">
				<c:forEach items="${summaryBtns}" var="vo" varStatus="status">
					<div title="${vo.busiName}" close="${vo.name==null}">
						<c:set var="pixel" value="summaryBtns.A${status.index}" />
						<input type="hidden" name="summaryBtns" value="${pixel}" />
						<c:choose>
							<%-- 系统内置按钮 --%>
							<c:when test="${vo.name!=null}">
								<input type="hidden" name="${pixel}.name" value="${vo.name}" />
								<input type="hidden" name="${pixel}.icon" value="${vo.icon}" />

								<table class="ws-table">
									<tr>
										<th>按钮名称/图标预览</th>
										<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}" />
											<button type="button" icon="${vo.icon}" text="false">${vo.busiName}</button></td>
									</tr>
									<tr>
										<th>按钮位置</th>
										<td><wcm:widget cmd="radio[@com.riversoft.platform.translate.BtnStyleClass]{required:true}" name="${pixel}.styleClass" value="${vo.styleClass}" /></td>
									</tr>
									<tr>
										<th>备注</th>
										<td><wcm:widget cmd="textarea" name="${pixel}.description" value="${vo.description}" /></td>
									</tr>
									<tr>
										<th>功能点</th>
										<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}" value="${isCopy?null:vo.pri}"></wcm:widget></td>
									</tr>
								</table>
							</c:when>
							<%-- 自定义按钮 --%>
							<c:otherwise>
								<table class="ws-table">
									<tr>
										<th>展示名</th>
										<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${vo.busiName}"></wcm:widget></td>
									</tr>
									<tr>
										<th>图标</th>
										<td><wcm:widget name="${pixel}.icon" cmd="icon{required:true}" value="${vo.icon}"></wcm:widget></td>
									</tr>
									<tr>
										<th>按钮位置</th>
										<td><wcm:widget cmd="select[@com.riversoft.platform.translate.BtnStyleClass]{required:true}" name="${pixel}.styleClass" value="${vo.styleClass}" /></td>
									</tr>
									<tr>
										<th>打开类型</th>
										<td><wcm:widget name="${pixel}.openType" cmd="radio[@com.riversoft.platform.translate.BtnOpenType]" value="${vo.openType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>绑定视图</th>
										<td><wcm:widget name="${pixel}.action" cmd="view[BTN]{required:true}" value="${vo.action}"></wcm:widget></td>
									</tr>
									<tr>
										<th>动态参数(脚本类型)</th>
										<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.paramType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>动态参数(脚本)<font color="red" tip="true" title="在request中通过[_params]命令字获取.">(提示)</font></th>
										<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]" value="${vo.paramScript}"></wcm:widget></td>
									</tr>
									<tr>
										<th>提示确认信息<br /> <font color="red" tip="true" title="无需确认框则留空.">(提示)</font></th>
										<td><wcm:widget name="${pixel}.confirmMsg" cmd="textarea" value="${vo.confirmMsg}"></wcm:widget></td>
									</tr>
									<tr>
										<th>备注</th>
										<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${vo.description}"></wcm:widget></td>
									</tr>
									<tr>
										<th>功能点</th>
										<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}" value="${isCopy?null:vo.pri}"></wcm:widget></td>
									</tr>
								</table>
							</c:otherwise>
						</c:choose>
					</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>