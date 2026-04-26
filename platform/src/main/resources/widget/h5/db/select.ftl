<#-- 下拉框模板 --> 
<#setting number_format="#">

<script>
	$(function(){
		var $select = $('#select_${uuid}');
		var $form = $select.parents('form:first');
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			var params = $('#${uuid}_params').val();
			if (params) {
				try{
					var json = JSON.parse(params);
					if(json.val!=undefined){//默认值
						$select.val(json.val);
					}
				}catch(e){
					//do nothing
				}
			}
		});
				
		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
				
		//可用/不可用
		Widget._setEnabled($form,'${name}',function(flag){
			if('${state}'=='normal'){
				if(flag){//生效
					$('#hidden_${uuid}').remove();
					$select.prop('disabled',false);
				}else{//失效
					if($('#hidden_${uuid}').size()<1){
						$select.after($('<input id="hidden_${uuid}" type="hidden" name="${name}" value="'+$select.val()+'" />'));
					}
					$select.prop('disabled',true);
				}
			}
		});
		
		//设值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $select.val();
			}else{
				$select.val(val);
			}
		});
		
		//回调函数
		$select.change(function(){
			var $this = $(this);
			Widget._getChange($form,'${name}')($this);
		});		
		
	});
</script>

<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
<select id="select_${uuid}"
name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" 
<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>
<#-- 判断必填 -->
<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> 
>
<#list list as vo>
	<option value="${(vo.code?string)!''}" <#if vo.code?? && (''+value!'') == (''+vo.code?string)!''> selected="selected"</#if>>${vo.showName}</option>
</#list>
</select>

<#if state?? && ( state!'') == 'readonly' >
	<input type="hidden" name="${name}" value="${value!''}" />
</#if>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />