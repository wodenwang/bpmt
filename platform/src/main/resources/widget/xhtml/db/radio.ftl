<#-- 单选框 --> 
<#setting number_format="#">

<script type="text/javascript">
	$(function(){
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		
		var oldValue = $('input:radio:checked',$zone).val();
		//初始化
		Widget._setInit($form,'${name}',function(){
			$(':radio[value="'+oldValue+'"]').iCheck('check'); 
		});
	});
</script>


<div id="${uuid}">
	<#list list as vo>
	  <input type="radio" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" value="${(vo.code?string)!''}" <#if vo.code?? && (''+value!'') == (''+vo.code?string)!''> checked="checked"</#if><#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>></input>
	  <label><#if codeFlag>[${vo.code}] </#if>${vo.showName}</label>
	</#list>
	
	<#if state?? && ( state!'') == 'readonly' >
		<input type="hidden" name="${name}" value="${value!''}" />
	</#if>
	
	<#-- 传递状态到后台 -->
	<input type="hidden" name="${name}$" value="${state}" />
</div>