<#-- 普通文本框 -->
<#assign type = "">
<#if ((validateObj.number)??&&(validateObj.number)) || ((validateObj.digits)??&&(validateObj.digits)) || ((validateObj.integer)??&&(validateObj.integer))>
	<#assign type = "number">
<#elseif ((validateObj.email)??&&(validateObj.email))>
	<#assign type = "email">
<#elseif ((validateObj.url)??&&(validateObj.url))>
	<#assign type = "url">
<#else>
    <#assign type = "text">
</#if>


<#assign style = "">
<#if (params[0].name)??&&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
</#if>

<script>
	$(function(){
		var $text = $('#${uuid}');
		var $form = $text.parents('form:first');
		var oldValue = $text.val();
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			var params = $('#${uuid}_params').val();
			if(params==''){//无参数
				$text.val(oldValue);
			}else{
				try{
					var json = JSON.parse(params);
					if(json.val!=undefined){
						$text.val(json.val);
					}
				}catch(e){
					//do nothing
				}
			}
		});
		
		//可用/不可用
		Widget._setEnabled($form,'${name}',function(flag){
			if(flag){//生效
				$text.prop('disabled',false);
			}else{//失效
				if($('#hidden_${uuid}').size()<1){
					$text.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$text.val()+'" />'));
				}
				$text.prop('disabled',true);
			}
		});
		
		//设值/取值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $text.val();
			}else{
				$text.val(val);
				return val;
			}
		});
		
		//回调函数
		$text.blur(function(){
			var $this = $(this);
			Widget._getChange($form,'${name}')($this);
		});
		
		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
		
	});
</script>

<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
<input id="${uuid}" type="${type}" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" 
value="${value!''}" 
<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if> 
<#-- 判断必填 -->
<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> 
<#-- 最大,最小长度 -->
<#if (validateObj.maxlength)??> maxlength="${validateObj.maxlength!''}"</#if> 
<#if (validateObj.minlength)??> minlength="${validateObj.minlength!''}"</#if> 
/>

<#if state?? && ( state!'') == 'readonly' >
	<input id="${uuid}_real" type="hidden" name="${name}" value="${value!''}" />
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />