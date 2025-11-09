<#-- 单选框 --> 
<#setting number_format="#">

<div class="am-input-group">
	<#list list as vo>
      <label class="am-radio-inline am-radio am-secondary">
        <input data-am-ucheck id="${uuid}_${vo_index}" type="radio" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" value="${(vo.code?string)!''}" <#if vo.code?? && (''+value!'') == (''+vo.code?string)!''> checked="checked"</#if><#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>></input> <#if codeFlag>[${vo.code}] </#if>${vo.showName}
      </label>
	</#list>
	
	<#if state?? && ( state!'') == 'readonly' >
		<input type="hidden" name="${name}" value="${value!''}" />
	</#if>
</div>
<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />
