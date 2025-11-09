<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		Ui.changeCurrentTitle('${_zone}', '${node.nodeType.name}[${node.name}]设置');
		var $zone = $('#${_zone}');

		//新增处理器
		$('button[name="addExec"]', $zone).click(function() {
			var $tabs = $(this).parents('div.ws-bar:first').next();
			var type = $(this).attr('configType');
			Ajax.tab($tabs, '${_acp}/addExecTab.shtml', {
				data : {
					pixel : 'execs.' + Core.nextSeq(),
					type : type
				}
			});
		});

		$("[name$='.description']", $('#${_zone}_exec_tabs_zone')).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var $tab = $this.parents('div.ui-tabs-panel:first');
				var $a = $('a', $("li[aria-controls='" + $tab.attr("id") + "']", $tab.parent()));
				$a.html(val);
			}
		});

		//继承高亮
		var subHightlight = function($this) {
			var $tab = $this.parents('div.ui-tabs-panel:first');
			var $a = $('a', $("li[aria-controls='" + $tab.attr("id") + "']", $tab.parent()));
			if ($this.val() == '1') {
				$a.css('font-weight', 'bold').css('color', 'blue');
			} else {
				$a.css('font-weight', 'normal').css('color', 'gray');
			}
		};
		$(":radio[name$='.showFlag']", $('#${_zone}_subs_tabs_zone')).on('ifChecked', function(evnet) {
			var $this = $(this);
			subHightlight($this);
		});
		$.each($(":radio[name$='.showFlag']:checked", $('#${_zone}_subs_tabs_zone')), function() {
			var $this = $(this);
			subHightlight($this);
		});

		$("#${_zone}_tabs", $zone).tabs({
			activate : function(event, ui) {
				$('textarea', ui.newPanel).blur();
				var current = $("#${_zone}_tabs", $zone).tabs("option", "active");
				if (current == 0) {
					//do nothing
				} else if (current == 6) {
					$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 1, 2, 3, 4, 5 ]);
				} else {
					$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 6 ]);
				}
			}
		});

	});
</script>
<div id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitForm.shtml" sync="true" option="{confirmMsg:'是否保存?',errorZone:'${_zone}_msg_zone'}">
	<div tabs="true" id="${_zone}_tabs">
		<div title="${node.nodeType.name}[${node.name}]设置">
			<input type="hidden" name="pdId" value="${param.pdId}" />
			<table class="ws-table">
				<tr>
					<th>节点类型</th>
					<td>${node.nodeType.name}</td>
				</tr>
				<tr>
					<th>节点名</th>
					<td>${node.name}</td>
				</tr>
				<tr>
					<th>备注</th>
					<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
				</tr>
			</table>
		</div>

		<div title="字段设置" init="${_acp}/columnFrom.shtml?pdId=${pdId}"></div>

		<div title="页面脚本(JS)">
			<table class="ws-table">
				<tr>
					<th>脚本类型</th>
					<td><wcm:widget name="jsType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.jsType}" /></td>
				</tr>
				<tr>
					<th>脚本<br /> <font color="red" tip="true" title="$zone:当前区域;$form:目标表单.">(提示)</font></th>
					<td><wcm:widget name="jsScript" cmd="codemirror[javascript]" value="${vo.jsScript}" /></td>
				</tr>
			</table>
		</div>

		<div title="按钮设置">
			<div tabs="true" button="left" sort="y" id="${_zone}_btn_tabs">
				<c:forEach items="${btns}" var="item" varStatus="state">
					<c:set var="pixel" value="btns.A${state.index}" />
					<div title="${item.busiName}">
						<input type="hidden" name="btns" value="${pixel}" />
						<c:choose>
							<c:when test="${item.name=='save'}">
								<div accordion="true" multi="true">
									<div title="基础信息">
										<input type="hidden" name="${pixel}.name" value="${item.name}" />
										<table class="ws-table">
											<tr>
												<th>按钮类型</th>
												<td><font color="blue">保存按钮</font></td>
											</tr>
											<tr>
												<th>按钮名</th>
												<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${item.busiName}"></wcm:widget></td>
											</tr>
											<tr>
												<th>图标</th>
												<td><wcm:widget name="${pixel}.icon" cmd="icon{required:true}" value="${item.icon}"></wcm:widget></td>
											</tr>
											<tr>
												<th>按钮位置</th>
												<td><wcm:widget cmd="radio[@com.riversoft.platform.translate.BtnStyleClass]{required:true}" name="${pixel}.styleClass" value="${item.styleClass}" /></td>
											</tr>
											<tr>
												<th>高级进度条</th>
												<td><wcm:widget cmd="radio[YES_NO]{required:true}" name="${pixel}.loading" value="${item.loading}" /></td>
											</tr>
											<tr>
												<th>描述</th>
												<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${item.description}" /></td>
											</tr>
										</table>
									</div>
									<div title="按钮展示">
										<table class="ws-table">
											<tr>
												<th>展示条件(脚本类型)</th>
												<td><wcm:widget name="${pixel}.checkType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.checkType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>展示条件(脚本)<br /> <font color="red" tip="true" title="返回boolean类型;vo:订单实体;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.checkScript" cmd="codemirror[groovy]{required:true}" value="${item.checkScript}"></wcm:widget></td>
											</tr>
										</table>
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<div accordion="true" multi="true">
									<div title="基础信息">
										<input type="hidden" name="${pixel}.name" value="${item.name}" />
										<table class="ws-table">
											<tr>
												<th>按钮类型</th>
												<td><font color="blue">开始按钮</font></td>
											</tr>
											<tr>
												<th>按钮名</th>
												<td><wcm:widget name="${pixel}.busiName" cmd="text{required:true}" value="${item.busiName}"></wcm:widget></td>
											</tr>
											<tr>
												<th>图标</th>
												<td><wcm:widget name="${pixel}.icon" cmd="icon{required:true}" value="${item.icon}"></wcm:widget></td>
											</tr>
											<tr>
												<th>按钮位置</th>
												<td><wcm:widget cmd="radio[@com.riversoft.platform.translate.BtnStyleClass]{required:true}" name="${pixel}.styleClass" value="${item.styleClass}" /></td>
											</tr>
											<tr>
												<th>高级进度条</th>
												<td><wcm:widget cmd="radio[YES_NO]{required:true}" name="${pixel}.loading" value="${item.loading}" /></td>
											</tr>
											<tr>
												<th>描述</th>
												<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${item.description}" /></td>
											</tr>
										</table>
									</div>
									<div title="按钮展示">
										<table class="ws-table">
											<tr>
												<th>展示条件(脚本类型)</th>
												<td><wcm:widget name="${pixel}.checkType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.checkType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>展示条件(脚本)<br /> <font color="red" tip="true" title="返回boolean类型;vo:订单实体;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.checkScript" cmd="codemirror[groovy]{required:true}" value="${item.checkScript}"></wcm:widget></td>
											</tr>
										</table>
									</div>
									<div title="弹出框信息">
										<table class="ws-table">
											<tr>
												<th>弹出审批意见</th>
												<td><wcm:widget name="${pixel}.opinionFlag" cmd="radio[YES_NO]" value="${item.opinionFlag}" /></td>
											</tr>
											<tr>
												<th>快速审批意见(脚本类型)</th>
												<td><wcm:widget name="${pixel}.quickOpinionType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.quickOpinionType}" /></td>
											</tr>
											<tr>
												<th>快速审批意见(脚本)<br /> <font color="red" tip="true" title="返回字符数组或以分号分隔的字符;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.quickOpinionScript" cmd="codemirror[groovy]" value="${item.quickOpinionScript}"></wcm:widget></td>
											</tr>
											<tr>
												<th>弹出确认框(脚本类型)</th>
												<td><wcm:widget name="${pixel}.confirmType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.confirmType}" /></td>
											</tr>
											<tr>
												<th>弹出确认框(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.confirmScript" cmd="codemirror[groovy]" value="${item.confirmScript}"></wcm:widget></td>
											</tr>
										</table>
									</div>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</c:forEach>
			</div>
		</div>

		<div title="处理器">
			<div class="ws-msg info">
				此设置会与[工作流基础视图]中的处理器协同使用.<br />实际提交后执行顺序依次为:<b>本配置前置处理器</b>->基础视图前置处理器->订单保存逻辑->工作流引擎调用逻辑->基础视图后置处理器-><b>本配置后置处理器</b>.
			</div>
			<div tabs="true" id="${_zone}_exec_tabs_zone">
				<div title="启动[前置]">
					<div class="ws-bar">
						<div class="ws-group left">
							<button icon="plus" type="button" name="addExec" configType="beforeExecs">新增处理器</button>
						</div>
					</div>
					<div tabs="true" button="left" sort="y">
						<c:forEach items="${beforeExecList}" var="exec" varStatus="status">
							<div title="${exec.description}" close="true">
								<c:set var="pixel" value="beforeExecs.${status.index}" />
								<input type="hidden" name="beforeExecs" value="${pixel}" />
								<table class="ws-table">
									<tr>
										<th>执行处理器(脚本类型)</th>
										<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${exec.execType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>执行处理器(脚本)<br /> <font color="red" tip="true" title="mode:1:新增时,2:修改时;vo:实体;fo:节点工作流信息;">(提示)</font></th>
										<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${exec.execScript}"></wcm:widget></td>
									</tr>
									<tr>
										<th>备注</th>
										<td><wcm:widget cmd="textarea{required:true}" name="${pixel}.description" value="${exec.description}" /></td>
									</tr>
								</table>
							</div>
						</c:forEach>
					</div>
				</div>
				<div title="启动[后置]">
					<div class="ws-bar">
						<div class="ws-group left">
							<button icon="plus" type="button" name="addExec" configType="afterExecs">新增处理器</button>
						</div>
					</div>
					<div tabs="true" button="left" sort="y">
						<c:forEach items="${afterExecList}" var="exec" varStatus="status">
							<div title="${exec.description}" close="true">
								<c:set var="pixel" value="afterExecs.${status.index}" />
								<input type="hidden" name="afterExecs" value="${pixel}" />
								<table class="ws-table">
									<tr>
										<th>执行处理器(脚本类型)</th>
										<td><wcm:widget name="${pixel}.execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${exec.execType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>执行处理器(脚本)<br /> <font color="red" tip="true" title="mode:1:新增时,2:修改时;vo:实体;fo:节点工作流信息;">(提示)</font></th>
										<td><wcm:widget name="${pixel}.execScript" cmd="codemirror[groovy]{required:true}" value="${exec.execScript}"></wcm:widget></td>
									</tr>
									<tr>
										<th>备注</th>
										<td><wcm:widget cmd="textarea{required:true}" name="${pixel}.description" value="${exec.description}" /></td>
									</tr>
								</table>
							</div>
						</c:forEach>
					</div>
				</div>
			</div>
		</div>
		<div title="子表设置">
			<div tabs="true" button="left" sort="true" id="${_zone}_subs_tabs_zone">
				<c:forEach items="${subs}" var="sub" varStatus="status">
					<div title="${sub.busiName}">
						<c:set var="pixel" value="subs.A.${status.index}" />
						<input type="hidden" name="subs" value="${pixel}" /> <input type="hidden" name="${pixel}.subKey" value="${sub.subKey}" />
						<table class="ws-table">
							<tr>
								<th>继承</th>
								<td><wcm:widget name="${pixel}.showFlag" cmd="radio[YES_NO]" value="${sub.showFlag}" /></td>
							</tr>
							<tr>
								<th>描述</th>
								<td><wcm:widget name="${pixel}.description" cmd="textarea" value="${sub.description}" /></td>
							</tr>
						</table>
					</div>
				</c:forEach>
			</div>
		</div>
		<div title="界面布局" init="${_acp}/frameSetting.shtml?pdId=${pdId}"></div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>