<#setting number_format="#">

<select 
name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" 
<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>
<#-- 判断必填 -->
<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> >
<option value="">请选择</option>
<#list list as vo>
	<option value="${(vo.code?string)!''}" <#if vo.code?? && (''+value!'') == (''+vo.code?string)!''> selected="selected"</#if>>${vo.showName}</option>
</#list>
</select>

<#if state?? && ( state!'') == 'readonly' >
	<input type="hidden" name="${name}" value="${value!''}" />
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />