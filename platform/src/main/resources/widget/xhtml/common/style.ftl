<#setting number_format="#.##">
<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		
		var $selectBtn = $('button[name=select]',$zone);
		var $previewBtn = $('button[name=preview]',$zone);
		var $input = $('textarea.needValid',$zone);
		var $codeBtn = $('button[name=code]',$zone);
		
		$selectBtn.click(function() {
			var style = $input.val();
			Ajax.win(_cp + '/widget/StyleAction/index.shtml', {
				title : '样式设计',
				minWidth : 600,
				minHeight : 400,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '确定',
					click : function() {
						var $this = $(this);
						var $form = $('form', $this);
						var style = '';

						$.each($("[name='_keys']:checked", $form), function() {
							var key = $(this).val();
							var $field = $("[name='" + key + "']", $form);
							style += (key + ':' + $field.val() + ';');
						});

						var url = _cp + '/widget/StyleAction/json.shtml';
						Ajax.json(url, function(result) {
							$input.val(result.result);
							$previewBtn.click();
							$this.dialog("close");
						}, {
							data : {
								style : style
							}
						});
					}
				} ],
				data : {
					style : style
				}
			});
		});
		
		
		//预览
		$previewBtn.click(function(){
			var $previewZone = $('#${uuid}_preview');
			if($input.val()!=''){
				$previewZone.show();
				$previewZone.html('<div style="'+$input.val()+'">预览区域</div>');
			}else{
				$previewZone.hide();
			}
		});
		
		//切换模式
		$codeBtn.click(function(){
			$input.toggle();
		});
		
		$previewBtn.click();//初始化操作
	});
</script>

<div id="${uuid}">
	<textarea name="${name}" style="display:none;" class="${validate!''} needValid" <#if state?? && (state!'') == 'readonly'> readonly="readonly"</#if>>${value!''}</textarea>
	<span class="ws-group">
		<button type="button" icon="pencil" text="false" name="select" <#if state?? && (state!'') == 'readonly'> disabled="disabled"</#if>>控件选择</button>
		<button type="button" icon="document" text="false" name="code">手动模式切换</button>
		<button type="button" icon="calculator" text="false" name="preview">预览</button>
	</span>
	<#-- 传递状态到后台 -->
	<input type="hidden" name="${name}$" value="${state}" />
</div>

<div id="${uuid}_preview" style="padding:5px 5px 5px 5px;border: 1px dashed #000;"></div>
