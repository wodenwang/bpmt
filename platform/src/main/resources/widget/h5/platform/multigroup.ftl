<#setting number_format="#">

<select name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" 
multiple="multiple"
<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>
<#-- 判断必填 -->
<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> >
<#list list as vo>
	<#if vo.code!=''>
		<option value="${vo.code}" <#if (';'+(value!'')+';')?index_of(';'+vo.code?string+';') != -1> selected="selected"</#if>><#if codeFlag>[${vo.code}] </#if>${vo.showName}</option>
	</#if>
</#list>
</select>
<#if state?? && ( state!'') == 'readonly' >
	<input type="hidden" name="${name}" value="${value!''}">
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />