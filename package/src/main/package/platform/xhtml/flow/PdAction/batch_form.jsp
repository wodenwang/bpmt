<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<div name="errorZone" id="${_zone}_msg_zone"></div>
<form action="${_acp}/submitBatch.shtml" id="${_zone}_edit_form" sync="true">
	<c:choose>
		<c:when test="${hbm=='WfStartEventExecBefore'||hbm=='WfUserTaskExecBefore'}">
			<c:set var="hbmType" value="前置处理器" />
		</c:when>
		<c:when test="${hbm=='WfStartEventExecAfter'||hbm=='WfUserTaskExecAfter'}">
			<c:set var="hbmType" value="后置处理器" />
		</c:when>
		<c:when test="${hbm=='WfServiceTaskLogic'}">
			<c:set var="hbmType" value="逻辑节点处理器" />
		</c:when>
		<c:when test="${hbm=='WfUserTaskAssignee'}">
			<c:set var="hbmType" value="人员分配逻辑" />
		</c:when>
		<c:when test="${hbm=='WfStartEventColumnForm'||hbm=='WfUserTaskColumnForm'}">
			<c:set var="hbmType" value="节点字段" />
		</c:when>
		<c:when test="${hbm=='WfStartEventColumnLine'||hbm=='WfUserTaskColumnLine'}">
			<c:set var="hbmType" value="分割线" />
		</c:when>
		<c:when test="${hbm=='WfStartEventColumnExtend'||hbm=='WfUserTaskColumnExtend'}">
			<c:set var="hbmType" value="分割线(继承)" />
		</c:when>
		<c:when test="${hbm=='WfStartEventBtnStart'}">
			<c:set var="hbmType" value="启动流程按钮" />
		</c:when>
		<c:when test="${hbm=='WfStartEventBtnSave'||hbm=='WfUserTaskBtnSave'}">
			<c:set var="hbmType" value="保存按钮" />
		</c:when>
		<c:when test="${hbm=='WfUserTaskBtnForward'}">
			<c:set var="hbmType" value="转办按钮" />
		</c:when>
		<c:when test="${hbm=='WfUserTaskBtn'}">
			<c:set var="hbmType" value="流程按钮" />
		</c:when>
		<c:otherwise>
			<c:set var="hbmType" value="" />
		</c:otherwise>
	</c:choose>
	<div accordion="true" multi="true">
		<div title="流程信息">
			<input type="hidden" name="hbm" value="${hbm}" /> <input type="hidden" name="id" value="${vo.id}" /> <input type="hidden" name="pdId" value="${vo.pdId}" /> <input type="hidden" name="activityId"
				value="${vo.activityId}" /> <input type="hidden" name="flowId" value="${vo.flowId}" />
			<table class="ws-table">

				<tr>
					<th>流程Key</th>
					<td>${pd.key}</td>
				</tr>
				<tr>
					<th>流程版本</th>
					<td>${pd.version}</td>
				</tr>
				<tr>
					<th>流程名称</th>
					<td>${pd.name}</td>
				</tr>
				<tr>
					<th>节点</th>
					<td><c:if test="${node.nodeType.code=='START_EVENT'}">[${node.nodeType.showName}]</c:if>${node.name}</td>
				</tr>
			</table>
		</div>
		<div title="配置数据">
			<table class="ws-table">
				<tr>
					<th>配置类型</th>
					<td colspan="3">${hbmType}</td>
				</tr>

				<c:choose>

					<%-- 处理器 --%>
					<c:when test="${hbm=='WfStartEventExecBefore'||hbm=='WfUserTaskExecBefore'||hbm=='WfStartEventExecAfter'||hbm=='WfUserTaskExecAfter'}">
						<tr>
							<th>连线(按钮)</th>
							<td>${sequenceFlow.name}</td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
						</tr>
						<tr>
							<th>处理器(类型)</th>
							<td><wcm:widget name="execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.execType}"></wcm:widget></td>
						</tr>
						<tr>
							<th>处理器(脚本)<br /> <font color="red" tip="true" title="mode:1:新增时,2:修改时;vo:实体;fo:节点工作流信息;">(提示)</font></th>
							<td><wcm:widget name="execScript" cmd="codemirror[groovy]{required:true}" value="${vo.execScript}"></wcm:widget></td>
						</tr>
					</c:when>

					<%-- 逻辑处理器 --%>
					<c:when test="${hbm=='WfServiceTaskLogic'}">
						<tr>
							<th>描述</th>
							<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
						</tr>
						<tr>
							<th>处理逻辑(类型)</th>
							<td><wcm:widget name="logicType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.logicType}"></wcm:widget></td>
						</tr>
						<tr>
							<th>处理逻辑(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
							<td><wcm:widget name="logicScript" cmd="codemirror[groovy]{required:true}" value="${vo.logicScript}"></wcm:widget></td>
						</tr>
					</c:when>

					<%-- 人员分配 --%>
					<c:when test="${hbm=='WfUserTaskAssignee'}">
						<tr>
							<th>描述</th>
							<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
						</tr>
						<tr>
							<th>成立逻辑(脚本类型)</th>
							<td><wcm:widget name="decideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.decideType}"></wcm:widget></td>
						</tr>
						<tr>
							<th>成立逻辑(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;">(提示)</font></th>
							<td><wcm:widget name="decideScript" cmd="codemirror[groovy]{required:true}" value="${vo.decideScript}"></wcm:widget></td>
						</tr>
						<tr>
							<th>分配类型</th>
							<td><wcm:widget name="allocateType" cmd="select[@com.riversoft.module.flow.activity.usertask.AllocateType]{required:true}" value="${vo.allocateType}" state="readonly" /></td>
						</tr>
						<c:choose>
							<c:when test="${vo.allocateType==0}">
								<tr>
									<th>目标用户(脚本类型)</th>
									<td><wcm:widget name="uidType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.uidType}" /></td>
								</tr>
								<tr>
									<th>目标用户(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;返回待分配的用户ID.">(提示)</font></th>
									<td><wcm:widget name="uidScript" cmd="codemirror[groovy]{required:true}" value="${vo.uidScript}" /></td>
								</tr>
								<tr>
									<th>是否独占</th>
									<td><wcm:widget name="uniqueFlag" cmd="radio[YES_NO]" value="${vo.uniqueFlag}" /></td>
								</tr>
							</c:when>
							<c:otherwise>
								<%-- 组织 --%>
								<c:if test="${vo.allocateType==1||vo.allocateType==3}">
									<tr>
										<th>目标组织(脚本类型)</th>
										<td><wcm:widget name="groupType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.groupType}" /></td>
									</tr>
									<tr>
										<th>目标组织(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;返回待分配的组织Key.">(提示)</font></th>
										<td><wcm:widget name="groupScript" cmd="codemirror[groovy]{required:true}" value="${vo.groupScript}" /></td>
									</tr>
								</c:if>
								<%--角色 --%>
								<c:if test="${vo.allocateType==2||vo.allocateType==3}">
									<tr>
										<th>目标角色(脚本类型)</th>
										<td><wcm:widget name="roleType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.roleType}"></wcm:widget></td>
									</tr>
									<tr>
										<th>目标角色(脚本)<br /> <font color="red" tip="true" title="vo:订单实体;返回待分配的角色Key.">(提示)</font></th>
										<td><wcm:widget name="roleScript" cmd="codemirror[groovy]{required:true}" value="${vo.roleScript}"></wcm:widget></td>
									</tr>
								</c:if>
							</c:otherwise>
						</c:choose>
					</c:when>

					<%-- 字段 --%>
					<c:when
						test="${hbm=='WfStartEventColumnForm'||hbm=='WfUserTaskColumnForm'||hbm=='WfStartEventColumnLine'||hbm=='WfUserTaskColumnLine'||hbm=='WfStartEventColumnExtend'||hbm=='WfUserTaskColumnExtend'}">
						<c:if test="${hbm=='WfStartEventColumnForm'||hbm=='WfUserTaskColumnForm'}">
							<tr>
								<th>字段NAME</th>
								<td><wcm:widget name="name" cmd="text{required:true}" value="${vo.name}" /></td>
							</tr>
						</c:if>
						<c:if test="${hbm=='WfStartEventColumnForm'||hbm=='WfUserTaskColumnForm'||hbm=='WfStartEventColumnLine'||hbm=='WfUserTaskColumnLine'}">
							<tr>
								<th>展示名</th>
								<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
							</tr>
						</c:if>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
						</tr>
						<c:if test="${hbm=='WfStartEventColumnForm'||hbm=='WfUserTaskColumnForm'}">
							<tr>
								<th>绑定控件</th>
								<td><wcm:widget name="widget" cmd="widget{required:true}" value="${vo!=null?vo.widget:'text'}"></wcm:widget>
							</tr>
							<tr>
								<th>控件动态入参(脚本类型)</th>
								<td><wcm:widget name="widgetParamType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.widgetParamType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>控件动态入参(脚本)<br /> <font color="red" tip="true" title="vo:实体;返回字符串,在自定义控件中在request中通过[_params]命令字获取.">(提示)</font></th>
								<td><wcm:widget name="widgetParamScript" cmd="codemirror[groovy]" value="${vo.widgetParamScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>表单内容(脚本类型)</th>
								<td><wcm:widget name="contentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.contentType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>表单内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
								<td><wcm:widget name="contentScript" cmd="codemirror[groovy]" value="${vo.contentScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>数据处理器(脚本类型)</th>
								<td><wcm:widget name="execType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.execType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>数据处理器(脚本)<br /> <font color="red" tip="true" title="返回当前字段的期望值.mode:1:新增时,2:修改时;vo:实体;fo:节点工作流信息;">(提示)</font></th>
								<td><wcm:widget name="execScript" cmd="codemirror[groovy]" value="${vo.execScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>展示内容(脚本类型)</th>
								<td><wcm:widget name="showContentType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${vo.showContentType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>展示内容(脚本内容)<br /> <font color="red" tip="true" title="vo:实体;">(提示)</font></th>
								<td><wcm:widget name="showContentScript" cmd="codemirror[groovy]" value="${vo.showContentScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>展示条件(脚本类型)</th>
								<td><wcm:widget name="decideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.decideType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>展示条件(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程对象;">(提示)</font></th>
								<td><wcm:widget name="decideScript" cmd="codemirror[groovy]{required:true}" value="${vo.decideScript}"></wcm:widget></td>
							</tr>
							<tr>
								<th>可编辑条件(脚本类型)</th>
								<td><wcm:widget name="editDecideType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.editDecideType}"></wcm:widget></td>
							</tr>
							<tr>
								<th>可编辑条件(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程对象;">(提示)</font></th>
								<td><wcm:widget name="editDecideScript" cmd="codemirror[groovy]{required:true}" value="${vo.editDecideScript}"></wcm:widget></td>
							</tr>
						</c:if>
					</c:when>

					<%-- 按钮 --%>
					<c:when test="${hbm=='WfStartEventBtnStart'||hbm=='WfStartEventBtnSave'||hbm=='WfUserTaskBtnSave'||hbm=='WfUserTaskBtnForward'||hbm=='WfUserTaskBtn'}">
						<c:if test="${sequenceFlow!=null}">
							<tr>
								<th>连线(按钮)</th>
								<td>${sequenceFlow.name}</td>
							</tr>
						</c:if>
						<tr>
							<th>展示名</th>
							<td><wcm:widget name="busiName" cmd="text{required:true}" value="${vo.busiName}" /></td>
						</tr>
						<tr>
							<th>描述</th>
							<td><wcm:widget name="description" cmd="textarea" value="${vo.description}" /></td>
						</tr>
						<tr>
							<th>展示条件(脚本类型)</th>
							<td><wcm:widget name="checkType" cmd="select[@com.riversoft.platform.script.ScriptTypes]{required:true}" value="${vo.checkType}"></wcm:widget></td>
						</tr>
						<tr>
							<th>展示条件(脚本)<br /> <font color="red" tip="true" title="vo:实体;fo:流程对象;">(提示)</font></th>
							<td><wcm:widget name="checkScript" cmd="codemirror[groovy]{required:true}" value="${vo.checkScript}"></wcm:widget></td>
						</tr>
					</c:when>
				</c:choose>
			</table>
		</div>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>