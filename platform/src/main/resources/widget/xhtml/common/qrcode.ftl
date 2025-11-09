<#-- 二维码控件 -->
<#assign style = "">
<#if (params[0].name)??&&(params[0].name)!='null'>
	<#assign style = style+"width:"+params[0].name+";">
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
				try{
					$text.spinner("enable");
				}catch(e){
				
				}
				$text.prop('disabled',false);
			}else{//失效
				if($('#hidden_${uuid}').size()<1){
					$text.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$text.val()+'" />'));
				}
				try{
					$text.spinner("disable");
				}catch(e){
				
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
<input id="${uuid}" type="text" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" value="${value!''}" class="needValid ${validate!''}" 
	style="${style}" <#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>/>
<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>

<#if state?? && ( state!'') == 'readonly' >
	<input id="${uuid}_real" type="hidden" name="${name}" value="${value!''}" />
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />