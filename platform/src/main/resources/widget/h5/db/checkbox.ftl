<#-- 多选框 --> 
<#setting number_format="#">

<div class="am-input-group">
	<#list list as vo>
	     <label class="am-checkbox-inline am-secondary">
	        <input id="${uuid}_${vo_index}" type="checkbox" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" value="${(vo.code?string)!''}" <#if (';'+(value!'')+';')?index_of(';'+vo.code?string+';') != -1> checked="checked"</#if><#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>></input> <#if codeFlag>[${vo.code}] </#if>${vo.showName}
	      </label>
	</#list>
	
	<#if state?? && ( state!'') == 'readonly' >
		<input type="hidden" name="${name}" value="${value!''}" />
	</#if>
</div>
<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />
