<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $("#${_zone}");

		$('button[name=edit]', $zone).click(function() {
			var $this = $(this);
			var title = $this.val();
			var json = {};
			json.id = $this.attr("vo_id");
			json.hbm = $this.attr("vo_hbm");
			json.pdId = $this.attr("vo_pd_id");
			json.activityId = $this.attr("vo_activity_id");
			json.flowId = $this.attr("vo_flow_id");
			Core.fn($zone, 'edit')(title, json);
		});
	})
</script>

<div class="ws-scroll">
	<table class="ws-table" form="${_form}">
		<tr>
			<th style="min-width: 50px; width: 50px;">操作</th>
			<th style="min-width: 100px;">流程Key</th>
			<th style="min-width: 100px;">流程名称</th>
			<th style="min-width: 60px; width: 60px;">流程版本</th>
			<th style="min-width: 100px;">节点</th>
			<c:if test="${param.type=='exec'}">
				<th style="min-width: 100px;">按钮</th>
			</c:if>
			<th style="min-width: 100px;">类型</th>
			<c:if test="${param.type!='button'}">
				<th style="min-width: 60px; width: 60px;">ID</th>
			</c:if>
			<c:if test="${param.type=='column'||param.type=='column_all'}">
				<th style="min-width: 100px;">NAME</th>
			</c:if>
			<c:if test="${param.type=='column'||param.type=='column_all'||param.type=='button'}">
				<th style="min-width: 100px;">展示名称</th>
			</c:if>
			<c:if test="${param.type=='person'}">
				<th style="min-width: 100px;">分配方式</th>
			</c:if>
			<th style="min-width: 200px;">描述</th>
		</tr>
		<c:forEach items="${dp.list}" var="vo">
			<c:choose>
				<c:when test="${vo.hbm=='WfStartEventExecBefore'||vo.hbm=='WfUserTaskExecBefore'}">
					<c:set var="hbmType" value="前置处理器" />
				</c:when>
				<c:when test="${vo.hbm=='WfStartEventExecAfter'||vo.hbm=='WfUserTaskExecAfter'}">
					<c:set var="hbmType" value="后置处理器" />
				</c:when>
				<c:when test="${vo.hbm=='WfServiceTaskLogic'}">
					<c:set var="hbmType" value="逻辑节点处理器" />
				</c:when>
				<c:when test="${vo.hbm=='WfUserTaskAssignee'}">
					<c:set var="hbmType" value="人员分配逻辑" />
				</c:when>
				<c:when test="${vo.hbm=='WfStartEventColumnForm'||vo.hbm=='WfUserTaskColumnForm'}">
					<c:set var="hbmType" value="节点字段" />
				</c:when>
				<c:when test="${vo.hbm=='WfStartEventColumnLine'||vo.hbm=='WfUserTaskColumnLine'}">
					<c:set var="hbmType" value="分割线" />
				</c:when>
				<c:when test="${(vo.hbm=='WfStartEventColumnExtend'||vo.hbm=='WfUserTaskColumnExtend')&&vo.CONTENT_SCRIPT==null}">
					<c:set var="hbmType" value="分割线(继承)" />
				</c:when>
				<c:when test="${(vo.hbm=='WfStartEventColumnExtend'||vo.hbm=='WfUserTaskColumnExtend')&&vo.CONTENT_SCRIPT!=null&&vo.CONTENT_SCRIPT!=''}">
					<c:set var="hbmType" value="展示字段(继承)" />
				</c:when>
				<c:when test="${(vo.hbm=='WfStartEventColumnExtend'||vo.hbm=='WfUserTaskColumnExtend')&&vo.CONTENT_SCRIPT==''}">
					<c:set var="hbmType" value="字段继承" />
				</c:when>
				<c:when test="${vo.hbm=='WfStartEventBtnStart'}">
					<c:set var="hbmType" value="启动流程按钮" />
				</c:when>
				<c:when test="${vo.hbm=='WfStartEventBtnSave'||vo.hbm=='WfUserTaskBtnSave'}">
					<c:set var="hbmType" value="保存按钮" />
				</c:when>
				<c:when test="${vo.hbm=='WfUserTaskBtnForward'}">
					<c:set var="hbmType" value="转办按钮" />
				</c:when>
				<c:when test="${vo.hbm=='WfUserTaskBtn'}">
					<c:set var="hbmType" value="流程按钮" />
				</c:when>
				<c:otherwise>
					<c:set var="hbmType" value="" />
				</c:otherwise>
			</c:choose>

			<c:set var="title" value="${hbmType}" />
			<c:choose>
				<c:when test="${vo.BUSI_NAME!=null&&vo.BUSI_NAME!=''}">
					<c:set var="title" value="${title} - ${vo.BUSI_NAME}" />
				</c:when>
				<c:when test="${vo.DESCRIPTION!=null&&vo.DESCRIPTION!=''}">
					<c:set var="title" value="${title} - ${vo.DESCRIPTION}" />
				</c:when>
			</c:choose>
			<tr>
				<td class="center">
					<button type="button" name="edit" icon="pencil" text="false" value="${title}" vo_hbm="${vo.hbm}" vo_id="${vo.ID}" vo_pd_id="${vo.PD_ID}" vo_activity_id="${vo.ACTIVITY_ID}"
						vo_flow_id="${vo.FLOW_ID}">编辑</button>
				</td>
				<td class="center">${vo.pd.key}</td>
				<td class="center">${vo.pd.name}</td>
				<td class="center">${vo.pd.version}</td>
				<td class="center"><c:choose>
						<c:when test="${vo.node!=null}">
							<c:if test="${vo.node.nodeType.code=='START_EVENT'}">[${vo.node.nodeType.showName}]</c:if>${vo.node.name}</c:when>
						<c:otherwise>无效节点(${vo.ACTIVITY_ID})</c:otherwise>
					</c:choose></td>
				<c:if test="${param.type=='exec'}">
					<td class="center">${vo.sequenceFlow.name}</td>
				</c:if>
				<td class="center">${hbmType}</td>
				<c:if test="${param.type!='button'}">
					<td class="center">${vo.ID}</td>
				</c:if>
				<c:if test="${param.type=='column'||param.type=='column_all'}">
					<td class="left">${vo.NAME}</td>
				</c:if>
				<c:if test="${param.type=='column'||param.type=='column_all'||param.type=='button'}">
					<td class="left">${vo.BUSI_NAME}</td>
				</c:if>
				<c:if test="${param.type=='person'}">
					<td class="left">${wcm:widget('select[@com.riversoft.module.flow.activity.usertask.AllocateType]',vo.ALLOCATE_TYPE)}<c:if test="${vo.ALLOCATE_TYPE==0&&vo.UNIQUE_FLAG==1}">(独占)</c:if>
					</td>
				</c:if>
				<td class="left">${vo.DESCRIPTION}</td>
			</tr>
		</c:forEach>
	</table>
</div>

<wcm:page dp="${dp}" form="${_form}" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>