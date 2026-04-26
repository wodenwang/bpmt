<#-- 多行文本框 --> 
<#if params[0]?? >
	<#assign mode = params[0].name>
<#else>
	<#assign mode = "">
</#if>

<#if params[1]?? && params[1].name == 'true'>
	<#assign width = "">
<#else>
	<#assign width = "400">
</#if>

<#if params[2]?? && params[2].name == 'true'>
	<#assign height = "300">
<#else>
	<#assign height = "100">
</#if>

<#if state?? && (state!'') == 'readonly'>
	<#assign readOnlyFlag = "true">
<#else>
	<#assign readOnlyFlag = "false">
</#if>

<textarea id="${uuid}" name="${name}" width="${width}" height="${height}" code="true" mode="${mode!''}" option="{readOnly:${readOnlyFlag}}" class="CodeMirror-normal needValid ${validate!''}" <#if state?? && (state!'') == 'readonly'> readonly="readonly"</#if>>${value!''}</textarea>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />