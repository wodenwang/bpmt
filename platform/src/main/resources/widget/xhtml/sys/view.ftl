<#setting number_format="#.##">
<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $input = $('textarea', $zone);
		var $span = $('span:first',$zone);
		
		Core.setFn($zone,'refresh',function(){
			Ajax.post('${uuid}_doc',_cp+'/widget/ViewAction/doc.shtml',{
				data : {
					action : $input.val()
				}
			});
		});
		
		$('button', $zone).click(function(){
			var type = $(this).val();
			var title = type == 'view' ? '选择自定义视图' : '选择系统内置视图';
			var $win = Ajax.win(_cp + '/widget/ViewAction/index.shtml', {
				title : title,
				minWidth : 400,
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
					target : '${target}',
				}
			});
			
			//设置回写函数
			Core.setFn($win,'select',function(treeNode){
				$input.val(treeNode.action);
				var $s = $('<span>'+treeNode.name+'</span>');
				$span.html('');
				$span.append($s);
				Core.getFn($zone,'refresh')();
				$win.dialog("close");
			});
		});
		
		//初始化
		Core.getFn($zone,'refresh')();
	});
</script>
<div id="${uuid}">
	<textarea style="display:none;" name="${name}" class="needValid ${validate!''}" <#if state?? && (state!'') == 'readonly'> readonly="readonly"</#if>>${value!''}</textarea>
	<span style="color:blue;margin-right: 10px;font-weight:bold;">${showName!''}</span>
	<#if (state!'') != 'readonly' && (state!'') != 'disabled'>
		<span class="ws-group">
			<button type="button" icon="note" value="view" text="false">自定义视图</button>
			<button type="button" icon="gear" value="sys" text="false">系统内置视图</button>
		</span>
	</#if>
</div>
<div id="${uuid}_doc"></div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />