<#-- 多行文本框 --> 
<#setting number_format="#">

<#assign style = "">
<#if (params[0].name)??&&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
</#if>
<#if (params[1].name)??&&(params[1].name)!='null'>
	<#assign style = style+"height:"+params[1].name+";">
</#if>

<script type="text/javascript">
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
				$('#hidden_${uuid}').remove();
				$text.prop('disabled',false);
			}else{//失效
				if($('#hidden_${uuid}').size()<1){
					$text.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$text.val()+'" />'));
				}
				$text.prop('disabled',true);
			}
		});
		
		//设值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $text.val();
			}else{
				$text.val(val);
			}
		});
		
		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
		
		//回调函数
		$text.blur(function(){
			var $this = $(this);
			Widget._getChange($form,'${name}')($this);
		});
	});
</script>

<textarea id="${uuid}" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>__</#if>" style="${style}" class="needValid ${validate!''}"
<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>>${value!''}</textarea>
<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
<#if state?? && (state!'') == 'readonly'>
	<textarea name="${name}" style="display:none;" class="needValid ${validate!''}">${value!''}</textarea>
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />