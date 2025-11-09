<#-- CommonHelper.view 调用 --> 
<script type="text/javascript">
	$(function() {
		var $a = $('#${uuid}');
		var type = '${type!'win'}';
		$a.click(function(event){
			event.preventDefault();
			var $tabs = $('div[tabs=true][main=true]:first');
			if(type=='tab' && $tabs.size()>0){
				Ajax.tab($tabs, _cp+'/${key}.view',{
					title : '${title!'提示窗口'}' ,
					data : {
						_params : $('#${uuid}_params').val()
					}
				});
			}else{
				Ajax.win(_cp+'/${key}.view',{
					title : '${title!'提示窗口'}' ,
					minWidth : 1024 ,
					data : {
						_params : $('#${uuid}_params').val()
					}
				});
			}
		});
		
	});
</script>
<textarea id="${uuid}_params" style="display: none;">${params!''}</textarea>
<a href="javascript:void(0);" id="${uuid}">${value!''}</a>