<#setting number_format="#">
<#if value?? && dataMap[value]??>
	<#if codeFlag>
		<#assign showValue = "["+value+"]"+dataMap[value].showName>
	<#else>
		<#assign showValue = dataMap[value].showName>
	</#if>
</#if>

<script type="text/javascript">
	$(function() {
		var $tree = $("#${uuid}_tree");
		var $dialog = $('#${uuid}');
		var $span = $dialog.next().next();
		var $btn = $span.next();
		var $input = $btn.next();
		
		var oldValue = $input.val();
		var $form = $input.parents('form:first');
		
		var treeSetting = {
			data : {
				simpleData : {
					enable : true,
					idKey : "code",
					pIdKey : "parentCode"
				},
				key : {
					name : 'showName',
					title : 'showName'
				}
			},
			view : {
				fontCss : function(treeId, treeNode) {
					return (!!treeNode.highlight) ? {color:"red", "font-weight":"bold","background-color": "yellow"} : {color:"#333", "font-weight":"normal","background-color": "transparent"};
				}
			},
			check : {
				enable : true,
				chkStyle : 'radio',
				radioType : "all"
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					$.fn.zTree.getZTreeObj(treeId).checkNode(treeNode, true, true);
				}
			}
		};
		var strData = $('textarea', $tree).val();
		var datas = eval("(" + strData + ")");
		var ztree = $.fn.zTree.init($tree, treeSetting, datas);
		$tree.addClass("ztree");
		ztree.expandAll(true);

		$dialog.dialog({
			autoOpen : false,
			height : 300,
			width : 350,
			modal : true,
			buttons : [
					{
						icons : {
							primary : "ui-icon-close"
						},
						text : '取消',
						click : function() {
							$(this).dialog("close");
						}
					},
					{
						icons : {
							primary : "ui-icon-check"
						},
						text : '确认',
						click : function() {
							var array = ztree.getCheckedNodes(true);
							$(this).dialog("close");
							if (array != null && array.length > 0) {
								$input.val(array[0].code);
								$span.html('');
								var $font = $('font', $span.prev()).clone();
								var $a = $('a', $font);
								$a.click(function() {
									$input.val('');
									$font.remove();
								});
								<#if codeFlag>
									$('b', $font).html('[' + array[0].code + ']'+ array[0].showName);
								<#else>
									$('b', $font).html(array[0].showName);
								</#if>
								$span.append($font);
							}
						}
					} ]
		});
		
		//清除样式
		var clearNodes = function(obj){
			obj.highlight = false;
			ztree.updateNode(obj);
			
			var nodeList = obj.children;
			if(nodeList==undefined||nodeList==null||nodeList.length==0){
				return ;
			}
			for( var i=0; i<nodeList.length; i++) {
				nodeList[i].highlight = false;
				ztree.updateNode(nodeList[i]);
				clearNodes(nodeList[i]);
			}
		};
		
		//查找树
		var searchNode = function(e){
			var value = $(this).val();
			var rootList = ztree.getNodes();
			for( var i=0; i<rootList.length; i++) {
				clearNodes(rootList[i]);
			}
			if(value==''){
				return;
			}
			var nodeList = ztree.getNodesByParamFuzzy('showName', value);
			for( var i=0; i<nodeList.length; i++) {
				nodeList[i].highlight = true;
				ztree.updateNode(nodeList[i]);
			}
		};
		
		$('input[name=_search]',$dialog).bind("propertychange", searchNode).bind("input", searchNode)
			.bind('keydown', function (e) {
				var key = e.which;
				 if (key == 13) {
					$dialog.scrollTo("a[style*='bold']:first",500);
				}
		});
		
		var initValue = function(){
			var $font = $('font', $span.prev()).clone();
			var $a = $('a', $font);
			$a.click(function() {
				$input.val('');
				$font.remove();
			});
			$('b', $font).html($input.attr('showValue'));
			$span.append($font);		
		};
		
		if ($input.val() != '') {
			initValue();
		}

		//初始化
		Widget._setInit($form,'${name}',function(){
			$('font',$span).remove();
			$input.val(oldValue);
			if ($input.val() != '') {
				initValue();
			}
		});
		
	});
</script>

<div id="${uuid}" title="选择数据">
	<div class="ws-bar">
		<span style="font-weight: bold;margin-right:5px;">快速查找:</span><input type="text"
			name="_search" />
	</div>
	<ul id="${uuid}_tree">
		<textarea>${treeJson!''}</textarea>
	</ul>
</div>
<span style="display: none;">
	<font color="blue" style="margin-right: 10px;">
		<b></b>
		<#if (state!'') != 'readonly' && (state!'') != 'disabled'>
			<a href="javascript:void(0);" title="关闭" style="margin-left: 5px;">[删]</a>
		</#if>
	</font>
</span>
<span></span>
<button icon="arrowthick-1-nw" onclick="$('#${uuid}').dialog('open');" type="button"<#if state?? && (state!'') == 'readonly'> disabled="disabled"</#if>>选择</button>
<input type="hidden"  name="${name}" value="${value!''}" showValue="${showValue!''}" class="${validate!''} needValid "/>

<#-- 传递状态到后台 -->
<input type="hidden" name="${name}$" value="${state}" />