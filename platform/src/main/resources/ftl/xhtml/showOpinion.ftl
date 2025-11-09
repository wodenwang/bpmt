<#-- FlowHelper.showOpinion 调用 --> 
<table class="ws-table">
	<tr>
		<th style="max-width:30px;">${cm.lan("#:zh[序号]:en[NO.]#")}</th>
		<th>${cm.lan("#:zh[节点]:en[Point]#")}</th>
		<th>${cm.lan("#:zh[操作]:en[	Operation]#")}</th>
		<th>${cm.lan("#:zh[处理人]:en[Handler]#")}</th>
		<th style="min-width:140px;">${cm.lan("#:zh[审批时间]:en[Approval time]#")}</th>
		<th style="min-width:100px;">${cm.lan("#:zh[审批意见]:en[Approval opinions]#")}</th>
	</tr>
	<#list list as vo>
	    <tr>
	        <td class="center">${vo_index+1}</td>
	         <td class="center">${vo.ACTIVITY_NAME!''}</td>
	         <td class="center">${vo.SEQUENCE_FLOW_NAME!''}</td>
	         <td class="center">${showUser(vo.OPR_USER,vo.OPR_GROUP,vo.OPR_ROLE)}</td>
	         <td class="center">${vo.CREATE_DATE}</td>
	         <td style="word-wrap:break-word;">${vo.OPINION!''}</td>
	    </tr>
	</#list>
</table>