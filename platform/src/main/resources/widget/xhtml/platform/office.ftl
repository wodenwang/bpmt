<#if value??>
	<#assign value = (value?html)>
<#else>
	<#assign value = "">
</#if>

<script type="text/javascript">
	$(function() {
		var maxSize = ${maxSize};
		var $zone = $('#${uuid}');
		var random = '${uuid}';
		
		//office展开
		var officeClick = function(event){
			event.preventDefault();
			var $this = $(this);
			var size =  $('textarea[name=size]',$this).val();
			var random = Math.random();
			var isShow = $this.attr('isShow');
			if(isShow=='true'){//收缩
				$this.attr('isShow','false');
				$this.css('color','');
				$this.css('font-weight','');
				$('#${uuid}').hide();
				$('#${uuid}_loading_zone').hide();
			}else 	if(size<=1024*1024*maxSize || confirm('${cm.lan("#:zh[此文件较大]:en[This file is larger]#")}('+(size*1.0/1024/1024).toFixed(2)+'M),${cm.lan("#:zh[后台转换处理需要等待较长时间.是否继续并等待?]:en[Background conversion processing needs to wait for a long time. Do you want to continue and wait?]#")}')){
				$zone.hide();
				$('#${uuid}_loading_zone').html('').show();
				Ajax.post($zone,_cp+'/widget/FileAction/office.shtml',{
					//showFlag : false,
					data : {
						fileName : $('textarea[name=name]',$this).val(),
						name : $('textarea[name=sysName]',$this).val(),
						type : $('textarea[name=type]',$this).val(),
						random : random
					},
					callback : function(flag){
						$('a:not([title])',$('#${uuid}_index')).css('color','');
						$('a:not([title])',$('#${uuid}_index')).css('font-weight','');
						$('a:not([title])',$('#${uuid}_index')).attr('isShow','false');
						$this.css('color','red');
						$this.css('font-weight','bold');
						$this.attr('isShow','true');
						
						//进度条
						Ajax.startLoading($('#${uuid}_loading_zone'), random, function(success) {
							if (success) {
								$('#${uuid}_loading_zone').hide();
								$zone.show();
							}else{
								$('#${uuid}_loading_zone').show();
								$zone.hide();
							}
						});
					}
				});
			}
		};
		
		//图片展开
		var imageClick = function(event){
			event.preventDefault();
			var $this = $(this);
			var isShow = $this.attr('isShow');
			if(isShow=='true'){//收缩
				$this.attr('isShow','false');
				$this.css('color','');
				$this.css('font-weight','');
				$('#${uuid}').hide();
				$('#${uuid}_loading_zone').hide();
			}else{
				$('a:not([title])',$('#${uuid}_index')).css('color','');
				$('a:not([title])',$('#${uuid}_index')).css('font-weight','');
				$('a:not([title])',$('#${uuid}_index')).attr('isShow','false');
				$this.css('color','red');
				$this.css('font-weight','bold');
				$this.attr('isShow','true');
				$('#${uuid}_loading_zone').hide();
				var $img = $('<img style="max-width:800px;"/>');
				$img.attr('src',_cp+'/widget/FileAction/download.shtml?name=' + $('textarea[name=sysName]',$this).val() + '&fileName=' +  encodeURIComponent($('textarea[name=name]',$this).val()) + '&type=' +$('textarea[name=type]',$this).val());
				var $div = $('<div style="text-align:center;border: solid 1px #000;max-height:400px;overflow:auto;"></div>');
				$div.append($img);
				$('#${uuid}').html('').append($div);
				$('#${uuid}').show();
			}
		};
		
		//下载
		$('a.download',$('#${uuid}_index')).click(function(event){
			event.preventDefault();
			var $this = $(this);		
			Ui.confirm('${cm.lan("#:zh[确认下载文件]:en[Confirm download file]#")}[' + $this.attr('name') + ']?', function() {
				Ajax.download(_cp+'/widget/FileAction/download.shtml',{
					data : {
						download : 'true',
						name : $this.attr('sysName'),
						fileName : $this.attr('name'),
						type :  $this.attr('type')
					}
				});
			});	
		});
		
		//添加图片,下载连接
		$.each($('span',$('#${uuid}_index')),function(){
			var $this = $(this);
			var pixel =/[^\.]+$/.exec($('textarea[name=name]',$this).val());
			pixel = (''+pixel).toLowerCase();
			//图片
			var $img = $('<img width="16" height="16" border="0"/>');
			$img.attr('src',_cp+'/css/icon/filetype/'+pixel+'.png');
			$this.prepend($img);
			if(",gif,png,jpg,jpeg,bmp,".indexOf(","+pixel+",")>=0){//图片类型
				var $a = $('a.load',$this);
				$a.bind('click',imageClick);//绑定预览
			}else 	if(",pdf,doc,docx,xls,xlsx,ppt,pptx,".indexOf(","+pixel+",")>=0){//office类型
				var $a = $('a.load',$this);
				$a.bind('click',officeClick);//绑定预览
				var size = $('textarea[name=size]',$this).val();
				if(size>1024*1024*maxSize){
					$a.attr('title','${cm.lan("#:zh[此文件较大]:en[This file is larger]#")}('+(size*1.0/1024/1024).toFixed(2)+'M),${cm.lan("#:zh[后台转换处理需要等待较长时间,建议下载后打开.]:en[Background conversion processing need to wait for a long time, it is recommended to open after download.]#")}');
					$a.tooltip({
						track : true,
						content : $a.attr('title')
					});	
				}
			}else{
				var $a = $('a.load',$this);
				$a.unbind('click');
				$a.on('click',function(event){
					event.preventDefault();
				});
				$a.css('color','gray');
				$a.css('cursor','help');
				$a.attr('title','${cm.lan("#:zh[此文件不支持预览]:en[This file does not support the preview]#")}');
				$a.tooltip({
					track : true,
					content : $a.attr('title')
				});			
			}
		});
		
		//解决样式冲突
		var $parentTr = $zone.parents('.ui-styled-table>tbody>tr:first');
		if ($parentTr.size() > 0) {
			$parentTr.unbind('mouseover');
			$parentTr.unbind('mouseout');
			$parentTr.children('td').removeClass("ui-state-hover");
			$zone.parents('td:first').removeClass('left');
		}
		
	});
</script>

<div id="${uuid}_index">
	<#list list as vo>
		<span style="margin-right:5px;"><a href="#" class="load">${vo.name}
				<textarea name="name" style="display: none;">${vo.name}</textarea>
				<textarea name="sysName" style="display: none;">${vo.sysName}</textarea>
				<textarea name="type" style="display: none;">${vo.type}</textarea>
				<textarea name="size" style="display: none;">${vo.size}</textarea>
			</a><a href="#" class="download" style="margin-left:2px;" name="${vo.name}" sysName="${vo.sysName}" type="${vo.type}">[${cm.lan("#:zh[下载]:en[Download]#")}]</a></span>
	</#list>
</div>

<div id="${uuid}_loading_zone"></div>
<div id="${uuid}"></div>