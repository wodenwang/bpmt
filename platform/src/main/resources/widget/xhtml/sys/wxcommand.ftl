<#setting number_format="#.##">
<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $input = $('textarea', $zone);
		var $span = $('span:first',$zone);
		
		$('button', $zone).click(function(){
			var type = $(this).val();
			var title = type == 'self' ? '选择自定义处理器' : '选择系统内置处理器';
			var $win = Ajax.win(_cp + '/widget/WxCommandAction/index.shtml', {
				title : title,
				minWidth : 1024,
				minHeight : 400,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				},{
					text : '清空',
					click : function() {
						$input.val('');
						$span.html('');
						$(this).dialog("close");
					}
				}],
				data : {
					val : $input.val(),
					type : type,
					mpFlag : '${mpFlag}',
					menuType : '${menuType}',
				}
			});
			
			//设置回写函数
			Core.setFn($win,'select',function(json){
				$input.val(json.commandKey);
				var $s = $('<span>'+json.busiName+'</span>');
				$span.html('');
				$span.append($s);
				$win.dialog("close");
			});
		});
		
	});
</script>
<div id="${uuid}">
	<textarea style="display:none;" name="${name}" class="needValid ${validate!''}" <#if state?? && (state!'') == 'readonly'> readonly="readonly"</#if>>${value!''}</textarea>
	<span style="color:blue;margin-right: 10px;font-weight:bold;">${showName!''}</span>
	<#if (state!'') != 'readonly' && (state!'') != 'disabled'>
		<span class="ws-group">
			<button type="button" icon="note" value="self" text="false">自定义处理器</button>
			<button type="button" icon="gear" value="sys" text="false">内置处理器</button>
		</span>
	</#if>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />