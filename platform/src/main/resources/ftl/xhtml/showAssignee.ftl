<#-- FlowHelper.showAssignee 调用 --> 

<#-- 独占任务 -->
<#if user??>
	<span style="color:blue;font-weight:bold;margin-right:5px;" title="用户独占任务" tip="true">${user}</span>
<#-- 共享任务 -->
<#elseif (list?size>0) >
	<#list list as vo>
		<#if vo.user??>
			<span style="color:blue;margin-right:5px;" tip="true" title="用户">${vo.user}</span>
		<#elseif vo.role?? && vo.group??>
			<span style="margin-right:5px;" tip="true" title="组织+角色">${vo.group}+${vo.role}</span>
		<#elseif vo.role??>
			<span style="margin-right:5px;" tip="true" title="角色">${vo.role}</span>
		<#elseif vo.group??>
			<span style="margin-right:5px;" tip="true" title="组织">${vo.group}</span>
		</#if>
	</#list>
<#-- 无主任务 -->
<#else>
	<span style="color:gray;font-style:italic;margin-right:5px;">(无主任务)</span>
</#if>