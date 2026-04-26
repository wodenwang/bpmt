<#-- FlowHelper.showAssignee 调用 --> 

<#-- 独占任务 -->
<#if user??>
	<span class="am-badge am-badge-primary am-radius am-margin-bottom-xs">${user}</span>
<#-- 共享任务 -->
<#elseif list?size>
	<#list list as vo>
		<#if vo.user??>
			<span class="am-badge am-badge-primary am-radius am-margin-bottom-xs">${vo.user}</span>
		<#elseif vo.role?? && vo.group??>
			<span class="am-badge am-badge-primary am-radius am-margin-bottom-xs">${vo.group} | ${vo.role}</span>
		<#elseif vo.role??>
			<span class="am-badge am-badge-primary am-radius am-margin-bottom-xs">${vo.role}</span>
		<#elseif vo.group??>
			<span class="am-badge am-badge-primary am-radius am-margin-bottom-xs">${vo.group}</span>
		</#if>
	</#list>
<#-- 无主任务 -->
<#else>
	<span class="am-badge am-radius am-margin-bottom-xs">(无主任务)</span>
</#if>
