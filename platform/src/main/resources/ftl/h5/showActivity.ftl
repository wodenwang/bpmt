<#-- FlowHelper.showActivity 调用 --> 
<#assign style = "">
<#if color=='blue'>
	<#assign style = "am-badge-secondary">
<#elseif color=='red'>
	<#assign style = "am-badge-danger">
<#elseif color=='green'>
	<#assign style = "am-badge-success">
</#if>

<span class="am-badge ${style} am-margin-bottom-xs">${value!''}</span>
