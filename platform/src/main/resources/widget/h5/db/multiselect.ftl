<#-- 多选下拉框模板 --> 
<#setting number_format="#">

<script>
	$(function(){
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		var $select = $('#select_${uuid}');
		var oldValue = $('select',$zone).val();

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
					$select.prop('disabled',false).trigger("liszt:updated");
				}else{//失效
					$select.prop('disabled',true).trigger("liszt:updated");
				}
			}
		});
		
		//设值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $select.val();
			}else{
				$select.val(val).trigger("liszt:updated");
			}
		});
		
		//回调函数
		$select.change(function(){
			var $this = $(this);
			Widget._getChange($form,'${name}')($this);
		});
	});
</script>

<div id="${uuid}">
<select id="select_${uuid}" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" class="chosen needValid ${validate!''}"
multiple="multiple"
<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if>
<#-- 判断必填 -->
<#if (validateObj.required)??&&(validateObj.required)> required placeholder="必填"</#if> 
>
<#list list as vo>
	<#if vo.code!=''>
		<option value="${vo.code}" <#if (';'+(value!'')+';')?index_of(';'+vo.code?string+';') != -1> selected="selected"</#if>><#if codeFlag>[${vo.code}] </#if>${vo.showName}</option>
	</#if>
</#list>
</select>
<#if state?? && ( state!'') == 'readonly' >
	<input type="hidden" name="${name}" value="${value!''}">
</#if>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />