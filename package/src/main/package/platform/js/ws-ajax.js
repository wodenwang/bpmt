/**
 * 系统ajax交互相关类
 */
var Ajax = {

	/**
	 * json方式调用
	 * 
	 * @param {}
	 *            url
	 * @param {}
	 *            successFn
	 * @param {}
	 *            options
	 */
	json : function(url, successFn, options) {
		if (options == null) {
			options = {};
		}
		options = $.extend({}, _ajax.options, options);
		var zone;
		if (options.errorZone && options.errorZone != null) {
			zone = options.errorZone;
		} else {
			zone = 'auto_' + Core.nextSeq();
		}
		var zoneId = _ajax.getZoneId(zone);

		// 设置json类型
		options.successFn = successFn;
		options.dataType = 'json';
		$.ajax(_ajax.getOption(url, zoneId, options));
	},
	/**
	 * 在区域中使用iframe打开
	 * 
	 * @param {}
	 *            zone
	 * @param {}
	 *            url
	 * @param {}
	 *            options
	 */
	frame : function(zone, url, options) {
		if (typeof (zone) == 'string') {
			zone = $('#' + zone);
		}
		var $iframe = $('<iframe style="border: 0px; width: 100%;" heigh="500"></iframe>');
		zone.html('');
		$iframe.load(function() {
			var mainheight = $(this).contents().find("body").height() + 30;
			$(this).height(mainheight);
		});
		$iframe.attr("src", url);
		zone.append($iframe);
	},

	/**
	 * 页面跳转
	 * 
	 * @param url
	 * @param newFlag
	 */
	jump : function(url, newFlag) {
		if (true == newFlag) {
			window.open(url);
		} else {
			window.onbeforeunload = '';
			window.location.href = url;
		}
	},

	/**
	 * 文件下载
	 * 
	 * @param url
	 * @param options
	 */
	download : function(url, options) {
		var $form = $('<form method="post" target="_blank"></form>');
		var params = "";
		var json = {};
		// 装入表单内容
		if (options != undefined && options.data != undefined) {
			json = options.data;
		}
		json = _ajax.changeUrlParamToJson(url, json);

		$.each(json, function(k, v) {
			if ($.isArray(v)) {
				$.each(v, function(i, o) {
					params += ('&' + k + '=' + _ajax.urlEncode(o));
				});
			} else {
				params += ('&' + k + '=' + _ajax.urlEncode(v));
			}
		});
		url = _ajax.getRealUrl(url);
		if (params != '') {
			url = url + '?' + params;
		}

		$form.attr('action', url);
		$('body').append($form);
		$form.submit();
		$form.remove();
	},

	/**
	 * 普通调用
	 * 
	 * @param zone
	 *            需要刷新的区域id
	 * @param url
	 *            调用网址
	 * @param options
	 *            扩展参数
	 */
	post : function(zone, url, options) {
		var zoneId = _ajax.getZoneId(zone);
		$.ajax(_ajax.getOption(url, zoneId, options));
	},

	/**
	 * 弹出ajax窗口
	 * 
	 * @param url
	 *            调用网址
	 * @param options
	 *            扩展参数
	 */
	win : function(url, options) {
		var $container = $('<div id="win_' + Core.nextSeq() + '" win="true"></div>');
		var title;
		if (options != null && options.title != null) {
			title = options.title;
		} else {
			title = "提示窗口";
		}

		var minWidth;
		if (options != null && options.minWidth != null) {
			minWidth = options.minWidth;
		} else {
			minWidth = 300;
		}

		var minHeight;
		if (options != null && options.minHeight != null) {
			minHeight = options.minHeight;
		} else {
			minHeight = 300;
		}

		var buttons;
		if (options != null && options.buttons != null) {
			buttons = options.buttons;
		} else {
			buttons = {};
		}

		var closeFn = null;
		if (options != null && $.isFunction(options.closeFn)) {
			closeFn = options.closeFn;
		}

		$('body').append($container);
		$container.dialog({
			modal : true,
			title : title,
			minWidth : minWidth,
			minHeight : minHeight,
			buttons : buttons,
			close : closeFn
		}).dialogExtend({
			"closable" : true,
			"maximizable" : true,
			"minimizable" : false,
			"minimizeLocation" : 'left',
			"collapsable" : false,
			"dblclick" : 'maximize'
		});
		var outFlag;
		if (options != null && options.outFlag != null) {
			outFlag = options.outFlag;
		} else {
			outFlag = false;
		}
		if (outFlag == false) {// 内部窗口
			Ajax.post($container, url, options);
		} else {// 外部窗口

			var params = "";
			var json = {};
			// 装入表单内容
			if (options != undefined && options.data != undefined) {
				json = options.data;
			}
			json = _ajax.changeUrlParamToJson(url, json);

			$.each(json, function(k, v) {
				params += ('&' + k + '=' + _ajax.urlEncode(v));
			});
			url = _ajax.getRealUrl(url);
			if (params != '') {
				url = url + '?' + params;
			}

			var $iframe = $('<iframe id="iframe_' + Core.nextSeq() + '" style="border: 0px; width: 100%;height:' + minHeight + 'px;" frameborder="0"></iframe>');
			$iframe.attr('src', url);
			$container.append($iframe);
			// resize回调
			$container.dialog({
				resize : function(event, ui) {
					$iframe.height(ui.size.height);
				}
			}).dialogExtend({
				"maximize" : function(evt, dlg) {
					$iframe.height($(this).height());
				},
				"restore" : function(evt, dlg) {
					$iframe.height($(this).height());
				}
			});
		}

		return $container;
	},

	/**
	 * 表单验证
	 * 
	 * @param form
	 */
	validateForm : function(form) {
		form = $('#' + _ajax.getZoneId(form));
		var valiForm = form.validate({
			errorPlacement : function(error, element) { // 错误信息位置设置方法
				var $parent = element.parents(':not(.ui-spinner):first');
				error.appendTo($parent); // 这里的element是录入数据的对象
			},
			ignore : ':hidden:not(.needValid)'
		});
		return valiForm;
	},

	/**
	 * 提交ajax表单
	 * 
	 * @param zone
	 * @param form
	 * @param options
	 */
	form : function(zone, form, options) {
		form = $('#' + _ajax.getZoneId(form));
		// 初始化验证框架
		var valiForm = Ajax.validateForm(form);

		if (valiForm.form()) {
			var url = form.attr('action');
			var zoneId = _ajax.getZoneId(zone);
			if (options == undefined) {
				options = {};
			}
			if (options.data == undefined) {
				options.data = {};
			}
			options.data._form = form.attr('id');
			if (options && options.confirmMsg != null && options.confirmMsg != '') {
				Ui.confirm(options.confirmMsg, function() {
					form.ajaxSubmit(_ajax.getOption(url, zoneId, options));
				});
			} else {
				form.ajaxSubmit(_ajax.getOption(url, zoneId, options));
			}
		} else {
			if (options.errorZone && options.errorZone != null) {
				var $errorZone = $('#' + options.errorZone);
				if ($errorZone.size() > 0) {
					var $error = $('<div>表单验证不通过,请检查所有项是否都已按规定填写.</div>');
					if (typeof (_lan) != "undefined" && _lan == 'en') {
						$error = $('<div>Form validation is not through, please check if all items are in accordance with the provisions.</div>');
					}
					$errorZone.children().remove();
					$errorZone.append($error);
					$error.addClass('ws-msg');
					$error.styleMsg({
						type : 'error'
					});

					if ($errorZone.prev().size() > 0) {
						var $prev = $errorZone.prev();
						if ($prev.attr('id') == undefined) {
							$prev.attr('id', 'zone_ajax_' + Core.nextSeq());
						}
						$.scrollTo('#' + $prev.attr('id'), 500);
					} else {
						$.scrollTo('#' + options.errorZone, 500);
					}
				}

			}
		}
	},

	/**
	 * 增加动态tab
	 * 
	 * @param tabs
	 * @param url
	 * @param options
	 */
	tab : function(tabs, url, options) {
		if (typeof (tabs) == 'string') {
			tabs = $('#' + tabs);
		}
		var currentSize = $('li[aria-selected]', $('ul:first', tabs)).size();
		var maxSize = tabs.attr('max');
		if (maxSize == null || maxSize == '') {
			maxSize = 100;
		}
		if (currentSize >= maxSize) {
			alert('当天标签数已超过最大值[' + maxSize + ']，请先关闭一些标签再操作。');
			return;
		}

		var id = 'tab_' + Core.nextSeq();
		var title;
		if (options != null && options.title != null) {
			title = options.title;
		} else {
			title = '新标签-' + id;
		}
		var $li = $('<li><a href="#' + id + '">' + title + '</a></li>');
		var $span = $('<span class="ui-icon ui-icon-close" role="presentation" style=" float: right; margin: 0.4em 0.2em 0 0; cursor: pointer;">关闭</span>');
		$span.click(function() {
			var panelId = $(this).closest("li").remove().attr("aria-controls");
			$("#" + panelId).remove();
			tabs.tabs("refresh");
		});
		$li.append($span);
		$("ul.ui-tabs-nav:first", tabs).append($li);
		var $tab = $("<div id='" + id + "' tab='true'></div>");
		tabs.append($tab);
		tabs.tabs("refresh");
		tabs.tabs("option", "active", currentSize);
		Ajax.post(id, url, options);
		return $tab;
	}
};

/**
 * ajax内部类
 */
var _ajax = {

	/**
	 * ajax入参
	 */
	options : {
		/**
		 * 错误提示区域ID
		 */
		errorZone : null,
		/**
		 * 调用成功/失败后的回调函数<br>
		 * flag:true调用成功，false调用失败
		 */
		callback : function(flag) {
			// empty
		},
		/**
		 * 触发按钮，自动diable并恢复，避免重复提交
		 */
		btn : null,
		/**
		 * 额外的参数
		 */
		data : {},
		/**
		 * tab和window窗口的标题(tab,win专用)
		 */
		title : '',
		/**
		 * 彈出确认框内容(form专用)
		 */
		confirmMsg : '',
		/**
		 * ajax调用类型
		 * 
		 * @type String
		 */
		dataType : 'html',
		/**
		 * 成功后调用函数
		 * 
		 * @type
		 */
		successFn : null,
		/**
		 * 是否异步模式
		 */
		async : true,
		/**
		 * 是否使用自定义loading<br>
		 * 需要借助ws-ajax-ext.js库
		 * 
		 * @type Boolean
		 */
		loading : false,

		/**
		 * 是否初始化打开区域(偷偷加载的情况应把此标识设成false)
		 * 
		 * @type Boolean
		 */
		showFlag : true,

		/**
		 * loading时滚动到此区域
		 * 
		 * @type Boolean
		 */
		focusLoading : false
	},

	/**
	 * 保存对应区域当前的唯一码
	 * 
	 * @type
	 */
	_seq : {},

	/**
	 * 校验区域唯一码是否依然有效
	 * 
	 * @param {}
	 *            $zone
	 * @param {}
	 *            seq
	 */
	checkSeq : function($zone, seq) {
		var id = $zone.attr('id');
		var num = _ajax._seq[id];
		if (num == undefined) {
			return false;
		} else {
			return seq == num;
		}
	},

	/**
	 * 获取分配的区域唯一码
	 * 
	 * @param {}
	 *            $zone
	 */
	getSeq : function($zone) {
		var id = $zone.attr('id');
		var num = _ajax._seq[id];
		if (num == undefined) {
			num = 1;
		} else {
			num++;
		}
		_ajax._seq[id] = num;
		return num;
	},

	/**
	 * 获取传入zone的Id，若没有ID则创建
	 * 
	 * @param zone
	 */
	getZoneId : function(zone) {
		if (typeof (zone) == 'string') {
			return zone;
		} else {
			var id = zone.attr('id');
			if (id != undefined && id != '') {
				return id;
			} else {
				zone.attr("id", "auto_" + Core.nextSeq());
				return zone.attr("id");
			}
		}
	},

	/**
	 * 获取真實网址
	 * 
	 * @param url
	 * @returns
	 */
	getRealUrl : function(url) {

		if (!url || url.indexOf('?') < 0) {
			return url;
		}

		return url.substring(0, url.indexOf('?'));
	},

	/**
	 * 将url参数转换成json
	 * 
	 * @param url
	 * @param data
	 * @returns
	 */
	changeUrlParamToJson : function(url, data) {
		if (!data) {
			data = {};
		}

		if (!url || url.indexOf('?') < 0) {
			return data;
		}

		var paraString = url.substring(url.indexOf("?") + 1, url.length).split("&");
		for (var i = 0; i < paraString.length; i++) {

			var key = paraString[i].substring(0, paraString[i].indexOf("="));
			var val = paraString[i].substring(paraString[i].indexOf("=") + 1, paraString[i].length);
			if (data[key] == null) {
				data[key] = val;
			} else {
				if (!data[key].push) {
					data[key] = [ data[key] ];
				}
				data[key].push(val);
			}
		}

		return data;
	},

	/**
	 * 获取当前请求随机数
	 */
	getRandom : function() {
		var now = new Date();
		return "" + now.getFullYear() + now.getMonth() + now.getDay() + now.getHours() + now.getMinutes() + now.getSeconds() + now.getMilliseconds() + Math.random();
	},

	/**
	 * 获取ajax入参
	 * 
	 * @param url
	 * @param zoneId
	 * @param options
	 */
	getOption : function(url, zone, options) {
		// 继承默认参数
		if (options == null) {
			options = {};
		}
		options = $.extend({}, _ajax.options, options);

		// 错误提示区域
		var errorZone;
		var errorZoneId = options.errorZone;
		if (errorZoneId && typeof (errorZoneId) == 'string') {
			errorZone = $('#' + errorZoneId);
		} else {
			errorZone = $('#' + zone);
			errorZoneId = zone;
		}

		// 按钮
		var btn = options.btn;
		var data = _ajax.changeUrlParamToJson(url);
		data = $.extend({}, options.data, data);
		data['_zone'] = zone;
		data['_action_mode'] = 'xhtml';// xhtml模式
		if (errorZoneId != zone) {
			data['_error_zone'] = errorZoneId;
		}
		// 设置唯一随机数
		var random = _ajax.getRandom();
		data['_random'] = random;

		// 把隱藏的zone喚起
		if (options.showFlag) {
			$("#" + zone).show();
			$("#" + errorZoneId).show();
		}

		// 获取唯一码
		var seq = _ajax.getSeq($("#" + zone));

		// json调用模式判断
		var dataType = 'html';
		var successFn = function(result) {
			if (options.loading && $.isFunction(Ajax.stopLoading)) {
				Ajax.stopLoading(random);
			}

			errorZone.html('');

			// 校验唯一码出错,则不覆盖区域
			if (!_ajax.checkSeq($("#" + zone), seq)) {
				return;
			}

			if (btn != null && btn.size() > 0) {
				btn.removeAttr("disabled");
				try {
					btn.button("option", "disabled", false);
				} catch (e) {
					// nothing
				}
			}
			var $result = $('<div>' + result + '</div>');
			if ($result.find('div.ws-msg.error').size() > 0) {// 查找得到错误区域，证明调用失败
				errorZone.html(result);
				// 失败调用
				options.callback(false);
			} else {
				$("#" + zone).attr("loaded", url);// load成功，则把成功的网址标入
				$("#" + zone).html(result);
				// 成功调用
				options.callback(true);
			}
		};

		if (options.dataType) {
			dataType = options.dataType;
			if ($.isFunction(options.successFn)) {
				if (dataType == 'json') {
					successFn = function(result) {
						if (options.loading && $.isFunction(Ajax.stopLoading)) {
							Ajax.stopLoading(random);
						}
						errorZone.html('');

						if (btn != null && btn.size() > 0) {
							btn.removeAttr("disabled");
							try {
								btn.button("option", "disabled", false);
							} catch (e) {
								// nothing
							}
						}

						if (result != undefined && result._error != undefined && result._error == true) {// 调用失败,详见ActionServlet对异常处理的逻辑
							if (result.html != undefined && $.isFunction(Ajax.html)) {
								Ajax.html(errorZone, result.html);
							} else {
								var $div = $('<div class="ws-msg error">' + result.msg + '</div>');
								$div.styleMsg({
									type : 'error'
								});
								errorZone.append($div);
							}
							// 失败调用
							options.callback(false);
						} else {
							options.successFn(result);
							options.callback(true);
						}
					};
				} else {
					successFn = options.successFn;
				}
			}
		}

		// 调用方式
		data['_data_type'] = dataType;

		return {
			url : _ajax.getRealUrl(url),
			type : 'post',
			async : options.async,
			dataType : dataType,
			data : data,
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			error : function(jqXHR, textStatus, errorThrown) {
				errorZone.html('');

				if (btn != null && btn.size() > 0) {
					btn.removeAttr("disabled");
					try {
						btn.button("option", "disabled", false);
					} catch (e) {
						// nothing
					}
				}
				if (jqXHR.status == 0 || errorThrown == '') {
					return;
				}
				if (options.loading && $.isFunction(Ajax.stopLoading)) {
					Ajax.stopLoading(random);
				}
				var $errorMsg = $('<div class="ws-msg"></div>');
				$errorMsg.html('服务器报错.错误码[' + jqXHR.status + '],错误信息:[' + errorThrown + '].');
				if (jqXHR.status == 300) {// json出错
					try {
						var message = JSON.parse(jqXHR.responseText).msg;
						$errorMsg.html(message);
					} catch (e) {
						// do nothing
						console.log(e);
					}
				}
				errorZone.append($errorMsg);
				$errorMsg.styleMsg({
					type : 'error'
				});
				options.callback(false);
			},
			success : successFn,
			beforeSend : function(XMLHttpRequest) {
				if (btn != null && btn.size() > 0) {
					btn.attr("disabled", "disabled");
					try {
						btn.button("option", "disabled", true);
					} catch (e) {
						// nothing
					}
				}
				if (errorZone && errorZone.size() > 0) {
					// 调用ajax扩展来完成
					if (options.loading && $.isFunction(Ajax.startLoading)) {
						Ajax.startLoading(errorZone, random);
					} else {
						errorZone.html('');
						var $frame = $('<div style="max-width:300px;margin-bottom:10px;margin-top:10px;"></div>');
						var $label = $('<div class="ui-state-default" style="position: absolute;font-weight: bold;top:2px;left:45%;border-width:0px;">加载中...</div>');
						var $bar = $('<div style="width:100%;position: relative;"></div>');
						$bar.append($label);
						$frame.append($bar);
						$bar.progressbar({
							value : false
						});
						errorZone.append($frame);
						$frame.position({
							of : errorZone,
							my : "center middle"
						});
					}

					if (options.focusLoading) {// 指定loading位置
						if (errorZone.prev().size() > 0) {
							var $prev = errorZone.prev();
							if ($prev.attr('id') == undefined) {
								$prev.attr('id', 'zone_ajax_' + Core.nextSeq());
							}
							$.scrollTo('#' + $prev.attr('id'), 500);
						} else {
							$.scrollTo('#' + errorZone.attr('id'), 500);
						}
					}
				}
			}
		};
	},

	urlEncode : function(str) {
		str = (str + '').toString();
		return encodeURIComponent(str).replace(/!/g, '%21').replace(/'/g, '%27').replace(/\(/g, '%28').replace(/\)/g, '%29').replace(/\*/g, '%2A').replace(/%20/g, '+');
	}

};