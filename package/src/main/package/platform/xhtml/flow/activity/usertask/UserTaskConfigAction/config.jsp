<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {

		Ui.changeCurrentTitle('${_zone}', '${node.nodeType.name}[${node.name}]设置');

		var $zone = $('#${_zone}');
		var $assigneeZone = $('#${_zone}_assignee_tabs');
		$("[name$='.description']", $assigneeZone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var $tab = $this.parents('div.ui-tabs-panel:first');
				var $a = $('a', $("li[aria-controls='" + $tab.attr("id") + "']", $tab.parent()));
				var html = $a.html();
				var batch = html.substring(0, html.indexOf('-'));
				$a.html(batch + "-" + val);
			}
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

		$("[name$='.batchNum']", $assigneeZone).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				var $tab = $this.parents('div.ui-tabs-panel:first');
				var $a = $('a', $("li[aria-controls='" + $tab.attr("id") + "']", $tab.parent()));
				var html = $a.html();
				var description = html.substring(html.indexOf('-') + 1);
				$a.html(val + "-" + description);
			}
		});

		$('button[name="addAssigneeRule"]', $zone).click(function() {
			var $tabs = $('#${_zone}_assignee_tabs');
			Ajax.tab($tabs, '${_acp}/addAssigneeRuleForm.shtml', {
				data : {
					pixel : 'B' + Core.nextSeq()
				}
			});
		});

		//新增处理器
		$('button[name="addExec"]', $zone).click(function() {
			var $tabs = $(this).parents('div.ws-bar:first').next();
			var type = $(this).attr('configType');
			var flowId = $(this).attr('flowId');
			Ajax.tab($tabs, '${_acp}/addExecTab.shtml', {
				data : {
					pixel : flowId + '-' + Core.nextSeq(),
					type : type,
					flowId : flowId
				}
			});
		});

		//新增转发按钮
		$('button[name="addForward"]', $zone).click(function() {
			var $tabs = $(this).parents('div.ws-bar:first').next();
			Ajax.tab($tabs, '${_acp}/addForwardBtnTab.shtml', {
				data : {
					pixel : "B-" + Core.nextSeq()
				}
			});
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
				} else if (current == 7) {
					$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 1, 2, 3, 4, 5, 6 ]);
				} else {
					$("#${_zone}_tabs", $zone).tabs("option", "disabled", [ 7 ]);
				}
			}
		});

	});
</script>
<div id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitForm.shtml" sync="true" option="{confirmMsg:'是否保存?',errorZone:'${_zone}_msg_zone'}">
	<div tabs="true" id="${_zone}_tabs">
		<div title="${node.nodeType.name}[${node.name}]设置">
			<input type="hidden" name="pdId" value="${param.pdId}" /> <input type="hidden" name="activityId" value="${param.activityId}" />
			<table class="ws-table">
				<tr>
					<th>节点类型</th>
					<td>${node.nodeType.name}</td>
				</tr>
				<tr>
					<th>节点ID</th>
					<td>${activityId}</td>
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
		<div title="字段设置" init="${_acp}/columnFrom.shtml?pdId=${pdId}&activityId=${activityId}"></div>
		<div title="页面脚本(JS)">
			<table class="ws-table">
				<tr>
					<th>脚本类型</th>
					<td><wcm:widget name="jsType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.jsType}" /></td>
				</tr>
				<tr>
					<th>脚本<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程实体;$zone:当前区域;$form:目标表单.">(提示)</font></th>
					<td><wcm:widget name="jsScript" cmd="codemirror[javascript]" value="${vo.jsScript}" /></td>
				</tr>
			</table>
		</div>
		<div title="人员分配">
			<table class="ws-table">
				<tr>
					<th>消息通知<font color="red" tip="true" title="此设置当基础视图中[消息类型]选中至少一种通知方式时有效.">(提示)</font></th>
					<td><wcm:widget name="notifyType" cmd="checkbox[@com.riversoft.platform.translate.TaskNotifyType]" value="${vo!=null?vo.notifyType:'1;2'}" /></td>
				</tr>
			</table>
			<div class="ws-bar">
				<div class="left">
					<div class="ws-group">
						<button icon="plus" type="button" name="addAssigneeRule">高级分配规则</button>
					</div>
				</div>
			</div>
			<div tabs="true" button="left" sort="y" id="${_zone}_assignee_tabs">
				<c:forEach items="${assignees}" var="item" varStatus="state">
					<c:set var="pixel" value="assignees.A${state.index}" />
					<div title="${item.batchNum}-${item.description}" close="true">
						<input type="hidden" name="assignees" value="${pixel}" />
						<table class="ws-table">
							<tr>
								<th>规则分组标号</th>
								<td><wcm:widget name="${pixel}.batchNum" cmd="text{required:true,digits:true}" value="${item.batchNum}"></wcm:widget></td>
							</tr>
							<tr>
								<th>成立逻辑(脚本类型)</th>
								<td><wcm:widget name="${pixel}.decideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.decideType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>成立逻辑(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
								<td><wcm:widget name="${pixel}.decideScript" cmd="codemirror[groovy]{required:true}" value="${item.decideScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>分配类型</th>
								<td><wcm:widget name="${pixel}.allocateType" cmd="select[@com.riversoft.module.flow.activity.usertask.AllocateType]{required:true}" value="${item.allocateType}" state="readonly" /></td>
							</tr>
							<c:choose>
								<c:when test="${item.allocateType==0}">
									<tr>
										<th>目标用户(脚本类型)</th>
										<td><wcm:widget name="${pixel}.uidType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.uidType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>目标用户(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;返回待分配的用户ID.">(提示)</font></th>
										<td><wcm:widget name="${pixel}.uidScript" cmd="codemirror[groovy]{required:true}" value="${item.uidScript}"></wcm:widget></td>
									</tr>
									<tr>
										<th>是否独占</th>
										<td><wcm:widget name="${pixel}.uniqueFlag" cmd="radio[YES_NO]" value="${item.uniqueFlag}"></wcm:widget></td>
									</tr>
								</c:when>
								<c:otherwise>
									<%-- 组织 --%>
									<c:if test="${item.allocateType==1||item.allocateType==3}">
										<tr>
											<th>目标组织(脚本类型)</th>
											<td><wcm:widget name="${pixel}.groupType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.groupType}"></wcm:widget></td>
										</tr>
										<tr>
											<th>目标组织(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;返回待分配的组织Key.">(提示)</font></th>
											<td><wcm:widget name="${pixel}.groupScript" cmd="codemirror[groovy]{required:true}" value="${item.groupScript}"></wcm:widget></td>
										</tr>
									</c:if>
									<%--角色 --%>
									<c:if test="${item.allocateType==2||item.allocateType==3}">
										<tr>
											<th>目标角色(脚本类型)</th>
											<td><wcm:widget name="${pixel}.roleType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.roleType}"></wcm:widget></td>
										</tr>
										<tr>
											<th>目标角色(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;返回待分配的角色Key.">(提示)</font></th>
											<td><wcm:widget name="${pixel}.roleScript" cmd="codemirror[groovy]{required:true}" value="${item.roleScript}"></wcm:widget></td>
										</tr>
									</c:if>
									<tr>
										<th>是否独占</th>
										<td><wcm:widget name="${pixel}.uniqueFlag" cmd="radio[YES_NO]" value="0" state="readonly"></wcm:widget></td>
									</tr>
								</c:otherwise>
							</c:choose>
							<tr>
								<th>描述</th>
								<td><wcm:widget name="${pixel}.description" cmd="textarea{required:true}" value="${item.description}" /></td>
							</tr>
						</table>
					</div>
				</c:forEach>
			</div>
		</div>
		<div title="按钮设置">
			<div class="ws-bar">
				<div class="left">
					<button icon="plus" name="addForward" type="button">添加转发</button>
				</div>
			</div>
			<div tabs="true" button="left" sort="y" id="${_zone}_btn_tabs">
				<c:forEach items="${btns}" var="item" varStatus="state">
					<c:set var="pixel" value="btns.A${state.index}" />
					<c:choose>
						<c:when test="${item.name=='save'}">
							<div title="${item.busiName}">
								<input type="hidden" name="btns" value="${pixel}" />
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
							</div>
						</c:when>
						<c:when test="${item.name=='forward'}">
							<div title="${item.busiName}" close="true">
								<input type="hidden" name="btns" value="${pixel}" />
								<div accordion="true" multi="true">
									<div title="基础信息">
										<input type="hidden" name="${pixel}.name" value="${item.name}" />
										<table class="ws-table">
											<tr>
												<th>按钮类型</th>
												<td><font color="blue">转发按钮</font></td>
											</tr>
											<tr>
												<th>按钮主键</th>
												<td><c:choose>
														<c:when test="${item.btnKey!=null}">
															<input type="hidden" name="${pixel}.btnKey" value="${item.btnKey}" />
															${item.btnKey}
														</c:when>
														<c:otherwise>
															<span style="font-style: italic;">(自动生成)</span>
														</c:otherwise>
													</c:choose></td>
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
									<div title="接收人设置">
										<table class="ws-table">
											<tr>
												<th>绑定控件</th>
												<td><wcm:widget name="${pixel}.widget" cmd="widget{required:true}" value="${item.widget}" state="readonly" /></td>
											</tr>
											<tr>
												<th>控件动态入参(脚本类型)</th>
												<td><wcm:widget name="${pixel}.widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.widgetParamType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>控件动态入参(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程对象">(提示)</font></th>
												<td><wcm:widget name="${pixel}.widgetParamScript" cmd="codemirror[groovy]" value="${item.widgetParamScript}"></wcm:widget></td>
											</tr>
											<tr>
												<th>可编辑条件(脚本类型)</th>
												<td><wcm:widget name="${pixel}.widgetEnableType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${item.widgetEnableType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>可编辑条件(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程对象">(提示)</font></th>
												<td><wcm:widget name="${pixel}.widgetEnableScript" cmd="codemirror[groovy]{required:true}" value="${item.widgetEnableScript}" /></td>
											</tr>
											<tr>
												<th>表单内容(脚本类型)</th>
												<td><wcm:widget name="${pixel}.widgetValType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.widgetValType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>表单内容(脚本内容)<br /> <font color="red" tip="true" title="vo:订单实体;fo:流程对象">(提示)</font></th>
												<td><wcm:widget name="${pixel}.widgetValScript" cmd="codemirror[groovy]" value="${item.widgetValScript}"></wcm:widget></td>
											</tr>
										</table>
									</div>
									<div title="弹出框信息">
										<table class="ws-table">
											<tr>
												<th>快速审批意见(脚本类型)</th>
												<td><wcm:widget name="${pixel}.quickOpinionType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.quickOpinionType}" /></td>
											</tr>
											<tr>
												<th>快速审批意见(脚本)<br /> <font color="red" tip="true" title="返回字符数组或以分号分隔的字符;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.quickOpinionScript" cmd="codemirror[groovy]" value="${item.quickOpinionScript}"></wcm:widget></td>
											</tr>
										</table>
									</div>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div title="${item.busiName}">
								<input type="hidden" name="btns" value="${pixel}" /> <input type="hidden" name="${pixel}.flowId" value="${item.flowId}" />
								<div accordion="true" multi="true">
									<div title="基础信息">
										<table class="ws-table">
											<tr>
												<th>按钮ID</th>
												<td>${item.flowId}</td>
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
											<tr>
												<th>可用时提示(脚本类型)</th>
												<td><wcm:widget name="${pixel}.enabledTipType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.enabledTipType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>可用时提示(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.enabledTipScript" cmd="codemirror[groovy]" value="${item.enabledTipScript}"></wcm:widget></td>
											</tr>
											<tr>
												<th>不可用时提示(脚本类型)</th>
												<td><wcm:widget name="${pixel}.disabledTipType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${item.disabledTipType}"></wcm:widget></td>
											</tr>
											<tr>
												<th>不可用时提示(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
												<td><wcm:widget name="${pixel}.disabledTipScript" cmd="codemirror[groovy]" value="${item.disabledTipScript}"></wcm:widget></td>
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
							</div>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</div>
		</div>
		<div title="处理器">
			<div class="ws-msg info">
				此设置会与[工作流基础视图]中的处理器协同使用.<br />实际提交后执行顺序依次为:<b>本配置前置处理器</b>->基础视图前置处理器->订单保存逻辑->基础视图后置处理器-><b>本配置后置处理器</b>->工作流引擎调用逻辑.
			</div>
			<div tabs="true" id="${_zone}_exec_tabs_zone">
				<c:forEach items="${sequenceFlows}" var="sequenceFlow">
					<div title="${sequenceFlow.name}[前置]">
						<div class="ws-bar">
							<div class="ws-group left">
								<button icon="plus" type="button" name="addExec" flowId="${sequenceFlow.id}" configType="beforeExecs">新增处理器</button>
							</div>
						</div>
						<div tabs="true" button="left" sort="y">
							<c:forEach items="${beforeExecMap[sequenceFlow.id]}" var="exec" varStatus="status">
								<div title="${exec.description}" close="true">
									<c:set var="pixel" value="beforeExecs.${sequenceFlow.id}-${status.index}" />
									<input type="hidden" name="beforeExecs" value="${pixel}" /><input type="hidden" name="${pixel}.flowId" value="${exec.flowId}" />
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
					<div title="${sequenceFlow.name}[后置]">
						<div class="ws-bar">
							<div class="ws-group left">
								<button icon="plus" type="button" name="addExec" flowId="${sequenceFlow.id}" configType="afterExecs">新增处理器</button>
							</div>
						</div>
						<div tabs="true" button="left" sort="y">
							<c:forEach items="${afterExecMap[sequenceFlow.id]}" var="exec" varStatus="status">
								<div title="${exec.description}" close="true">
									<c:set var="pixel" value="afterExecs.${sequenceFlow.id}-${status.index}" />
									<input type="hidden" name="afterExecs" value="${pixel}" /><input type="hidden" name="${pixel}.flowId" value="${exec.flowId}" />
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
				</c:forEach>
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
		<div title="界面布局" init="${_acp}/frameSetting.shtml?pdId=${pdId}&activityId=${activityId}"></div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>