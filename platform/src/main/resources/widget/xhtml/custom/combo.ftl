<#-- 多选下拉框模板 --> 
<#setting number_format="#.##">

<script type="text/javascript">
	$(function() {
		var $zone = $('#${uuid}');
		var $form = $zone.parents('form:first');
		var state = '${state!''}';
		var $select = $('select',$zone);
		var $span = $('span[name=show]',$zone);

		var setVal = function(val){
			$select.children().remove();
			$select.append('<option value="'+val+'" selected="selected">'+val+'</option>');
			Ajax.post('${uuid}_show_zone',_cp+'/widget/ComboAction/show.shtml',{
				data : {
					_params : $('textarea[paramName=true]',$zone).val(),
					widgetKey : '${config.widgetKey}'	,
					val : val		
				}
			});
		};
		
		//设值
		Widget._setVal($form,'${name}',function(val){
			if(val==undefined){
				return $select.val();
			}else{
				setVal(val);
			}
		});

		//初始化
		Widget._setInit($form,'${name}',function(){
			//先清空
			$('button[comboName=del]',$zone).click();
			
			var params = $('#${uuid}_params').val();
			if(params==''){//无参数
				if('${value!''}'!=''){
					setVal('${value!''}');
				}else{
					$('button[comboName=del]',$zone).click();
				}
			}else{
				try{
					var json = eval('('+params+')');
					if(json.val!=undefined){//默认值
						setVal(json.val);
					}
				}catch(e){
					//do nothing
					console.log(e);
					alert('${cm.lan("#:zh[控件]:en[control]#")}[${name}]${cm.lan("#:zh[参数]:en[parameter]#")}'+params+'${cm.lan("#:zh[出错,请联系管理员.]:en[Error, please contact the administrator.]#")}');
				}
			}
		});
		
		//可用/不可用
		Widget._setEnabled($form,'${name}',function(flag){
			if('${state}'=='normal'){
				if(flag){//生效
					$('button[comboName]',$zone).button( "enable" );
				}else{//失效
					$('button[comboName]',$zone).button( "disable" );
				}
			}
		});

		//设置参数
		Widget._setParams($form,'${name}', function(params){
			$('#${uuid}_params').val(params);
		});
								
		//删
		$('button[comboName=del]',$zone).click(function(){
			$select.children().remove();
			$span.html('');
			Widget._getChange($form,'${name}')($select);
		});
		
		var width = '${config.width!800}';
		if(width==''){
			width = 800;
		}
		
		//选择数据
		$('button[comboName=select]',$zone).click(function(event){
			event.preventDefault();
			Ajax.win(_cp+'/widget/ComboAction/index.shtml',{
				title : '${cm.lan("#:zh[选择数据]:en[Select data]#")}',
				minWidth : new Number(width),
				data : {
					_params : $('textarea[paramName=true]',$zone).val(),
					widgetKey : '${config.widgetKey}'
				},
				buttons : [
					{
						icons : {
							primary : "ui-icon-close"
						},
						text : '${cm.lan("#:zh[取消]:en[Cancel]#")}',
						click : function(){
							var $this = $(this);
							Ui.confirm('${cm.lan("#:zh[是否关闭]:en[Whether to close]#")}?', function() {
								$this.dialog('close');
							});
						}
					},
					{
						icons : {
							primary : "ui-icon-check"
						},
						text : '${cm.lan("#:zh[确认]:en[Confirm]#")}',
						click : function(){
							var $win = $(this);
							var $error = $('div[name=errorZone]',$win);
							var $checkbox = $(':checkbox[name=code]:checked',$win);
							if($checkbox==null||$checkbox.size()<1){
								var $div = $('<div>${cm.lan("#:zh[没有数据被选中]:en[No data is selected]#")}.</div>');
								$error.children().remove();
								$error.append($div);
								$div.addClass('ws-msg');
								$div.styleMsg({
									type : 'error'
								});
								return ;
							}
							var code = $checkbox.val();
							setVal(code);
							$win.dialog("close");
							//事件回调
							Widget._getChange($form,'${name}')($select);
						}
					}
				]
			});
		});
	});
</script>

<div id="${uuid}">
	<textarea style="display: none;" paramName="true" id="${uuid}_params">${dyncParams!''}</textarea>
	<select style="display: none;" name="${name}" class="needValid ${validate!''}">
		<#if value != null && value != ''>
			<option selected="selected" value="${value!''}">${showName!''}</option>
		</#if>
	</select>
	<span  style="color:blue ;" name="show" id="${uuid}_show_zone">${showName!''}</span>
	
	<#if (state!'') != 'readonly' && (state!'') != 'disabled'>
		<span class="ws-group">
			<button type="button" icon="search" text="true" comboName="select">${cm.lan("#:zh[选择]:en[Select]#")}</button>
			<button type="button" icon="trash" text="false" comboName="del">${cm.lan("#:zh[删除]:en[Delect]#")}</button>
		</span>
	</#if>
</div>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />