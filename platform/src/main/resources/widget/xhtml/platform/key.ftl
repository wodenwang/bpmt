<#-- 带自动主键的文本框 --> 

<script type="text/javascript">
	function createAutoKey(btn){
		var $input = $(btn).prev();
		var val = $input.val();
		if(val==null||val==''){
			Ajax.json(_cp+'/widget/KeyAction/create.shtml', function(obj) {
				$input.val(obj.result);
			});
		}
	}
</script>

<#setting number_format="#">
<input type="text" name="${name}" value="${value!''}" class="needValid ${validate!''}" <#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>/>
<button icon="key" type="button"<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if> onclick="createAutoKey(this);">生成</button>
<#if state?? && ( state!'') == 'readonly' >
	<input type="hidden" name="${name}" value="${value!''}" />
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />