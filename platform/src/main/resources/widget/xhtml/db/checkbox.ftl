<#-- 多选框 --> 
<#setting number_format="#">
<script type="text/javascript">
	$(function(){
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		
		var oldValue = [];
		$.each($(':checkbox:checked',$zone),function(){
			oldValue.push($(this).val());
		});
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			$(':checkbox').iCheck('uncheck'); 
			$.each(oldValue, function(i,val){      
			      $(':checkbox[value="'+val+'"]').iCheck('check'); 
			  });   
		});
	});
</script>

<div id="${uuid}">
	<#list list as vo>
	  <input type="checkbox" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" value="${(vo.code?string)!''}" <#if (';'+(value!'')+';')?index_of(';'+vo.code?string+';') != -1> checked="checked"</#if><#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>></input>
	  <label><#if codeFlag>[${vo.code}] </#if>${vo.showName}</label>
	</#list>
	
	<#if state?? && ( state!'') == 'readonly' >
		<input type="hidden" name="${name}" value="${value!''}" />
	</#if>
	
	<#-- 传递状态到后台 -->
	<input type="hidden" name="${name}$" value="${state}" />
</div>