<#-- FlowHelper.showActivity 调用 --> 
<script type="text/javascript">
	$(function() {
		var $a = $('#${uuid}');
		$a.click(function(event){
			event.preventDefault();
			Ajax.win(_cp+'/${url}',{
				title : '流程图' ,
				minWidth : 1024 ,
				data : {
					_params : $('#${uuid}_params').val()
				}
			});
		});
		
		$a.tooltip({
			track : true,
			content : $('#${uuid}_div').html()
		});
		
		//改变颜色
		var color = '${color!''}';
		if(color!=''){
			$a.css('color',color);
		}
		
	});
</script>

<div id="${uuid}_div" style="display: none;">
	<div style="width:230px;">
		<table class="ws-table">
			<tr>
				<th style="width:30%;">创建人</th>
				<td>${translateUser(vo.OWNER)}</td>
			</tr>
			<tr>
				<th style="width:30%;">创建时间</th>
				<td>${vo.CREATE_DATE}</td>
			</tr>
		</table>
	</div>
</div>

<textarea id="${uuid}_params" style="display: none;">{picture:true,taskId:'${taskId!''}',ordId:'${ordId!''}'}</textarea>
<a href="javascript:void(0);" id="${uuid}" title="${value!''}">${value!''}</a>
