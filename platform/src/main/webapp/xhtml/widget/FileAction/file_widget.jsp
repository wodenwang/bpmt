<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $text = $('textarea.fileManager', $zone);
		var $span = $("#${_zone}_span", $zone);
		var agent = '${agent}';

		var mode = "${param.mode}";
		var maxSize = new Number("${maxSize}");
		if (maxSize <= 0) {//无设置
			maxSize = 10 * 1024 * 1024;//最大允许10M
			if (mode == 'disk') {//disk模式允许100M
				maxSize = 100 * 1024 * 1024;
			}
		}

		//校验文件大小
		var checkSize = function(items) {
			var total = 0;
			for (var i = 0; i < items.length; i++) {
				total += items[i].size;
			}
			if (total > maxSize) {
				alert('${wpf:lan("#:zh[文件总大小]:en[Total file size]#")}[' + (total / 1024 / 1024).toFixed(2) + ' mb]${wpf:lan("#:zh[超过系统限制大小]:en[Exceed system limit size]#")}['
						+ (maxSize / 1024 / 1024) + ' mb].');
				return false;
			} else {
				return true;
			}
		};

		//删除
		Core.fn($zone, 'delFile', function(fileName) {
			var allFiles = $text.val();
			var items = eval('(' + allFiles + ')');

			var remainedNames = new Array();
			$.each(items, function(index, val) {
				if (val.name != fileName) {
					remainedNames.push(val);
				}
			});

			if (remainedNames.length > 0) {
				$text.val(JSON.stringify(remainedNames));
			} else {
				$text.val('');
			}

			//删除完显示
			Core.fn($zone, 'showFile')();
		});

		//展示
		Core.fn($zone, 'showFile', function() {
			var val = $text.val();
			$span.children().remove();

			if (val != "") {
				var jsonValue = val;
				var items = eval('(' + jsonValue + ')');
				for (var i = 0; i < items.length; i++) {
					var obj = items[i];
					var $tmp = $('<span style="margin-right:10px;"></span>');
					if (obj.type == 'temp') {//不能下载预览
						$tmp.css('color', 'blue');
						$tmp.html(obj.name);
					} else {
						$tmp.css('color', 'green');
						var $a = $('<a href="javascript:void(0);" style="color:blue;"></a>');
						$a.html(obj.name);
						$a.attr('name', obj.name);
						$a.attr('sysName', obj.sysName);
						$a.attr('type', obj.type);
						$a.attr('pixel', /[^\.]+$/.exec(obj.name).toString());
						$a.click(function() {
							var $thisA = $(this);
							var $div = $('<div style="display:none;">请选择对文件[' + $thisA.attr('name') + ']的处理.</div>');
							$('body').append($div);
							$div.styleMsg({
								type : 'warning'
							});
							var buttons = [ {
								icons : {
									primary : "ui-icon-arrowthickstop-1-s"
								},
								text : '下发到微信',
								click : function() {
									var $this = $(this);
									Ajax.json('${_acp}/send2wx.shtml?name=' + $thisA.attr('sysName') + '&fileName=' + $thisA.attr('name') + '&type=' + $thisA.attr('type'), function(json) {
										$this.dialog("close");
										Ui.alert(json.msg);
									}, {
										errorZone : $div
									});
								}
							}, {
								icons : {
									primary : "ui-icon-arrowthickstop-1-s"
								},
								text : '${wpf:lan("#:zh[下载]:en[Download]#")}',
								click : function() {
									$(this).dialog("close");
									Ajax.download('${_acp}/download.shtml?download=true&name=' + $thisA.attr('sysName') + '&fileName=' + $thisA.attr('name') + '&type=' + $thisA.attr('type'));
								}
							}, {
								icons : {
									primary : "ui-icon-cancel"
								},
								text : '${wpf:lan("#:zh[取消]:en[Cancel]#")}',
								click : function() {
									$(this).dialog("close");
								}
							} ];

							//是否微信下发到微信
							var arr = [ "jpg", "gif", "png", "jpeg", "mp3", "wma", "wmv", "amr", "mp4", "avi", "rmvb", "pdf", "txt", "zip", , "xml", "doc", "docx", "xls", "xlsx", "ppt", "pptx" ];
							if (!agent || Number(agent) <= 0) {
								buttons.shift();
							} else if ($.inArray($thisA.attr('pixel'), arr) < 0) {
								buttons.shift();
							}

							$div.dialog({
								title : "提示窗口",
								width : 400,
								modal : true,
								buttons : buttons
							});
						});
						$tmp.append($a);
					}

					//图标
					var pixel = /[^\.]+$/.exec(obj.name).toString();
					var $img = $('<img width="16" height="16" border="0"/>');
					$img.attr('src', _cp + '/css/icon/filetype/' + pixel + '.png');
					$tmp.prepend($img);

					//删除按钮
					if ('${param.state}' == 'normal') {
						var $del = $('<a href="javascript:void(0);" style="color:red;margin-left:5px;">[${wpf:lan("#:zh[删]:en[DEL]#")}]</a>');
						$del.attr('name', obj.name);
						$del.attr('type', obj.type);
						$del.click(function() {
							var fileName = $(this).attr('name');//唯一标识
							Core.fn($zone, 'delFile')(fileName);
						});
						$tmp.append($del);
					}

					$span.append($tmp);
				}
			}
		});

		$('button[name=selectFile]', $zone).click(
				function() {
					var $btn = $(this);
					var checkType = '${param.checkType}';
					var $win = Ajax.win('${_acp}/selectFile.shtml', {
						title : '${wpf:lan("#:zh[个人文件中转区(中转区文件在服务器保留1天)]:en[Personal file transfer area (transfer area file in server 1 days)]#")}',
						minWidth : 800,
						minHeight : 450,
						data : {
							checkType : checkType
						},
						buttons : [
								{
									text : '${wpf:lan("#:zh[关闭]:en[Close]#")}',
									click : function() {
										$(this).dialog("close");
									}
								},
								{
									text : '${wpf:lan("#:zh[确定]:en[Confirm]#")}',
									click : function() {
										var $this = $(this);
										var array = Core.fn($this, 'getCheckNode')();
										if (array != null) {
											var allFiles = $text.val();
											var items;
											//获取已选文件
											if (allFiles != '' && checkType == 'checkbox') {
												items = eval('(' + allFiles + ')');//去掉字符前面files:字符
											} else {
												items = [];
											}
											//加入新选
											for (var i = 0; i < array.length; i++) {
												var obj = {};
												obj.type = 'temp';
												obj.mode = mode;//控件决定保存模式
												obj.name = array[i].name;
												obj.size = array[i].size;
												for (var j = 0; j < items.length; j++) {
													if (items[j].name == obj.name) {
														alert('${wpf:lan("#:zh[已添加文件中已存在]:en[Added file already exists]#")}[' + obj.name
																+ ']${wpf:lan("#:zh[重名的文件,无法重复添加.]:en[Duplicate files, not repeat to add.]#")}');
														return;
													}
												}
												items.push(obj);
											}
											if (items.length > 0) {
												//校验大小
												if (!checkSize(items)) {
													return;
												}
												$text.val(JSON.stringify(items));
											} else {
												$text.val('');
											}
											$this.dialog("close");
											//展示内容
											Core.fn($zone, 'showFile')();
										} else {
											Ui.alert("${wpf:lan('#:zh[没有选中任何项.]:en[No items selected.]#')}");
										}
									}
								} ]
					});
				});

		$('button[name=uploadFile]', $zone).click(function() {
			var $btn = $(this);
			var checkType = '${param.checkType}';
			var $win = Ajax.win('${_acp}/uploadPage.shtml', {
				title : '${wpf:lan("#:zh[文件上传(可在个人文件中转区查看上传的文件)]:en[Upload files(in the personal file transfer area to view the uploaded file)]#")}',
				minWidth : 800,
				closeFn : function(event, ui) {
					event.preventDefault();
					return true;
				},
				data : {
					checkType : checkType
				}
			});
			$win.dialog("option", "closeOnEscape", false);
			Core.fn($win, 'appendFiles', function(list) {
				$win.dialog("close");
				var allFiles = $text.val();
				var items;
				//获取已选文件
				if (allFiles != '' && checkType == 'checkbox') {
					items = eval('(' + allFiles + ')');
				} else {
					items = [];
				}
				//加入新选
				for (var i = 0; i < list.length; i++) {
					var obj = list[i];
					for (var j = 0; j < items.length; j++) {
						if (items[j].name == obj.name) {
							alert('${wpf:lan("#:zh[已添加文件中已存在]:en[Added file already exists]#")}[' + obj.name + ']${wpf:lan("#:zh[重名的文件,无法重复添加.]:en[Duplicate files, not repeat to add.]#")}');
							return;
						}
					}
					obj.mode = mode;
					items.push(obj);
				}
				if (items.length > 0) {
					//校验大小
					if (!checkSize(items)) {
						return;
					}
					$text.val(JSON.stringify(items));
				} else {
					$text.val('');
				}
				//展示内容
				Core.fn($zone, 'showFile')();
			});
		});

		//初始显示
		Core.fn($zone, 'showFile')();

	});
</script>

<span id="${_zone}_span"></span>
<c:if test="${param.state=='normal'}">
	<span class="ws-group">
		<button type="button" icon="circle-arrow-n" text="true" name="uploadFile">${wpf:lan("#:zh[上传]:en[Upload]#")}</button>
		<c:if test="${login}">
			<button type="button" icon="folder-open" text="true" name="selectFile">${wpf:lan("#:zh[中转区]:en[Transit zone]#")}</button>
		</c:if>
	</span>
</c:if>
<textarea name="${param.name}" class="${param.validate} needValid fileManager" style="display: none;">${param.value}</textarea>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>