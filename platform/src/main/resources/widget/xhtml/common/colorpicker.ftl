<#-- 普通文本框 --> 
<#setting number_format="#.##">
<input type="text" name="${name}" value="${value!''}" class="colorpicker ${validate!''}" <#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>/>
<#if state?? && ( state!'') == 'readonly' >
	<input type="hidden" name="${name}" value="needValid ${value!''}" />
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />