<#-- 多行文本框 --> 
<#setting number_format="#">
<script type="text/javascript">

	$(function(){
		var $zone = $('#${uuid}');
		var $selectBtn = $('button[name=select]',$zone);
		var $checkBtn = $('button[name=check]',$zone);
		var $previewBtn = $('button[name=preview]',$zone);
		var $input = $('textarea.needValid',$zone);
		var $span = $('span[name=cmd]',$zone);
		var $codeBtn = $('button[name=code]',$zone);
		
		//验证
		$checkBtn.click(function(){
			Ajax.win(_cp + '/widget/WidgetAction/designValidate.shtml', {
				title : '验证器设计',
				minWidth : 800,
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
						var json = {};
						if ($(':radio[name=required]:checked', $this).val() == '1') {
							json['required'] = true;
						}
				
						var types = [ 'digits', 'number', 'url', 'email', 'creditcard' ,'integer'];
						for (var i = 0; i < types.length; i++) {
							var type = types[i];
							if ($('input:radio[name=type][value=' + type + ']', $this).attr("checked") == "checked") {
								json[type] = true;
							}
						}
				
						if ($('input[name=min]', $this).val() != '') {
							json.min = $('input[name=min]', $this).val();
						}
						if ($('input[name=max]', $this).val() != '') {
							json.max = $('input[name=max]', $this).val();
						}
						if ($('input[name=minlength]', $this).val() != '') {
							json.minlength = $('input[name=minlength]', $this).val();
						}
						if ($('input[name=maxlength]', $this).val() != '') {
							json.maxlength = $('input[name=maxlength]', $this).val();
						}
				
						if ($('textarea[name=extension]', $this).val() != '') {
							json.extension = $('textarea[name=extension]', $this).val();
						}
				
						if ($('textarea[name=pattern2]', $this).val() != '') {
							var arry = [ $('textarea[name=pattern2]', $this).val(), $('textarea[name=pattern2_msg]', $this).val() ];
							json.pattern2 = arry;
						}
						var validator = JSON.stringify(json).replace(/"([^"]*)":/g, "$1:").replace(/"([^"]*)"/g, "'$1'");
						var cmd = $input.val();
						if(cmd.indexOf("{")>0){
							$input.val(cmd.substring(0,cmd.indexOf("{"))+validator);
						}else{
							$input.val(cmd+validator);
						}
						$(this).dialog("close");
						$span.html($input.val());
					}
				} ],
				data : {
					cmd : $input.val()
				}
			});
		});
		
		//设计
		$selectBtn.click(function(){
			var $win = Ajax.win(_cp + '/widget/WidgetAction/designWidget.shtml', {
				title : '控件设计',
				minWidth : 800,
				minHeight : 400,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}],
				data : {
					cmd : $input.val()
				}
			});
			
			//选中回调
			Core.setFn($win,'callback',function(selectCmd){
					if(selectCmd==null){
						Ui.alert('请先选择控件.');
						return;
					}
						
					var cmd = $input.val();
					if(cmd.indexOf("{")>0){
						$input.val(selectCmd+cmd.substring(cmd.indexOf("{")));
					}else{
						$input.val(selectCmd);
					}
						
					$previewBtn.click();
					$win.dialog("close");
			});
		});
		
		//预览
		$previewBtn.click(function(){
			Ajax.post('${uuid}_preview',_cp + '/widget/WidgetAction/preview.shtml',{
				data : {
					cmd : $input.val()
				}
			});
			$span.html($input.val());
		});
		
		//切换模式
		$codeBtn.click(function(){
			$span.toggle();
			$input.toggle();
		});
		
		$previewBtn.click();//初始化操作
	});
</script>

<div id="${uuid}">
	<span style="color: blue; font-weight: bold;" name="cmd">${value!''}</span>
	<textarea name="${name}" style="display:none;" class="${validate!''} needValid" <#if state?? && (state!'') == 'readonly'> readonly="readonly"</#if>>${value!''}</textarea>
	<span class="ws-group">
		<button type="button" icon="wrench" text="false" name="select" <#if state?? && (state!'') == 'readonly'> disabled="disabled"</#if>>控件选择</button>
		<button type="button" icon="check" text="false" name="check" <#if state?? && (state!'') == 'readonly'> disabled="disabled"</#if>>验证器设计</button>
		<button type="button" icon="document" text="false" name="code">手动模式切换</button>
		<button type="button" icon="calculator" text="false" name="preview">预览</button>
	</span>
	<#-- 传递状态到后台 -->
	<input type="hidden" name="${name}$" value="${state}" />
</div>

<div id="${uuid}_preview"></div>

