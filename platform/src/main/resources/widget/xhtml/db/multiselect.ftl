<#-- 多选下拉框模板 --> 
<#setting number_format="#">

<script type="text/javascript">
	$(function(){
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		var $select = $('#select_${uuid}');
		var oldValue = $('select',$zone).val();
		
		//初始化
		Widget._setInit($form,'${name}',function(){
			var params = $('#${uuid}_params').val();
			if(params==''){//无参数
				$('select',$zone).val(oldValue).trigger("liszt:updated");
			}else{
				try{
					var json = JSON.parse(params);
					if(json.type!=undefined){//重载列表
						$select.children().remove();
						$select.append('<option value="">加载中...</option>');
						<#if state?? && ( state!'') == 'normal' >
						$select.prop("disabled", true).trigger("liszt:updated");
						</#if>
						Ajax.json(_cp + '/widget/SelectAction/reload.shtml', function(list) {
									<#if state?? && ( state!'') == 'normal' >
									$select.prop("disabled", false);
									</#if>
									$select.children().remove();
									if (list != null && $.isArray(list)) {
										$.each(list, function(i, o) {
													var $option = $('<option></option>');
													$option.html(o.showName);
													$option.val(o.code);
													$select.append($option);
												});
									}
									$select.trigger("liszt:updated");
								}, {
									async : false,//同步
									data : json
								});
					}
					
					if(json.val!=undefined){//默认值
						$select.val(json.val).trigger("liszt:updated");
					}
					
				}catch(e){
					//do nothing
					console.log(e);
					alert('控件[${name}]参数'+params+'出错,请联系管理员.');
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
		
		//改尺寸
		if('${width!''}'!=''){
			$select.css('width','${width!''}');
		}else{
			$select.addClass('chosen');
		}
		
		$select.chosen({
				no_results_text : "没有匹配结果",
				placeholder_text : "请选择"
		});
		
		$.validator.addMethod("placeholder",function(){
                return true;
        });//为了改变默认文字的传参
		
	});
</script>

<div id="${uuid}">
	<textarea style="display: none;" id="${uuid}_params">${dyncParams!''}</textarea>
	<select id="select_${uuid}" name="${name}<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')>_</#if>" class="chosen needValid ${validate!''}" multiple="multiple"<#if state?? && ( (state!'') == 'readonly' || (state!'') == 'disabled')> disabled="disabled"</#if> <#if validateObj.placeholder?? && validateObj.placeholder != 'null' >data-placeholder="${validateObj.placeholder!''}"</#if>>
	<#list list as vo>
		<option value="${vo.code}" <#if (';'+(value!'')+';')?index_of(';'+vo.code?string+';') != -1> selected="selected"</#if>><#if codeFlag>[${vo.code}] </#if>${vo.showName}</option>
	</#list>
	</select>
	<#if state?? && ( state!'') == 'readonly' >
		<input type="hidden" name="${name}" value="${value!''}">
	</#if>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />