/**
 * 系统ajax交互类扩展
 */
if (Ajax == undefined) {
	Ajax = {};
}

var _ajaxExt = {
	/**
	 * 标识那些random已经装载过
	 * 
	 * @type
	 */
	loaded : {},

	/**
	 * 调用ajax获取log信息
	 * 
	 * @param {}
	 *            $zone
	 * @param {}
	 *            random
	 * @param {}
	 *            callback 回调函数
	 */
	checkLog : function($zone, random, callback) {
		var $bar = $('[zonetype=bar]', $zone);
		var $msg = $('[zonetype=msg]', $zone);

		if (_ajaxExt.loaded[random] == undefined || !_ajaxExt.loaded[random]) {
			Ajax.json(_cp + '/frame/AjaxExtAction/sessionLog.shtml', function(result) {
				// 返回LogVO
				$msg.text(result.msg);

				if (result.complete) {// 已完成
					$bar.progressbar("value", false);
					$bar.progressbar("destroy");
					$zone.html(result.msg);
					if (result.success) {
						$zone.styleMsg({
							type : 'info'
						});
					} else {
						$zone.styleMsg({
							type : 'error'
						});
					}

					if ($.isFunction(callback)) {
						callback(result.success);
					}
					return;
				}

				if (result.max > 0) {
					$bar.progressbar("option", "max", result.max);
					$bar.progressbar("value", result.current);
				} else {
					$bar.progressbar("value", false);
				}
				// 延时再调用一次
				setTimeout(function() {
					_ajaxExt.checkLog($zone, random, callback);
				}, 500);
			}, {
				data : {
					random : random
				}
			});

		}
	}
};

Ajax.stopLoading = function(random) {
	_ajaxExt.loaded[random] = true;
}

/**
 * 交互loading
 * 
 * @param {}
 *            $zone
 * @param {}
 *            random
 * @param {}
 *            callback 回调函数
 */
Ajax.startLoading = function($zone, random, callback) {
	$zone.html('');
	var $frame = $('<div style="width:300px;margin-bottom:20px;"></div>');
	var $msg = $('<div style="text-align:center;font-weight: bold;margin-bottom:5px;" zonetype="msg">加载中...</div>');
	var $bar = $('<div style="width:100%;position: relative;" zonetype="bar"></div>');
	var $label = $('<div style="position: absolute;font-weight: bold;top:2px;left:45%;text-shadow: 1px 1px 0 #fff;"></div>');

	$bar.append($label);
	$frame.append($msg);
	$frame.append($bar);

	$bar.progressbar({
		value : false,
		change : function() {
			if ($bar.progressbar("value")) {
				$label.text($bar.progressbar("value") + "/" + $bar.progressbar("option", "max"));
			} else {
				$label.text('');
			}
		},
		complete : function() {
			$msg.text("数据处理完成,正在下载文件.");
			$label.text('');
		}
	});
	$zone.append($frame);
	$frame.position({
		of : $zone,
		my : "center middle"
	});

	setTimeout(function() {
		_ajaxExt.checkLog($zone, random, callback);
	}, 1000);

};

/**
 * 调出loading窗口
 * 
 * @param {}
 *            random
 */
Ajax.loadingWin = function(random) {
	var $win = $('<div></div>');
	$('body').append($win);
	$win.dialog({
		title : '加载中...',
		modal : true,
		minWidth : 400,
		minHeight : 200
	}).dialogExtend({
		"closable" : false
	});

	Ajax.startLoading($win, random, function(success) {
		$win.dialog("close");
	});

};

/**
 * 展示html
 */
Ajax.html = function(zone, html) {
	Ajax.post(zone, _cp + '/frame/AjaxExtAction/html.shtml', {
		async : false,// 同步
		data : {
			html : html
		}
	});
};

/**
 * 自定义函数调用
 * 
 * @param {}
 *            fn
 * @param {}
 *            arg
 */
Ajax.invoke = function(fn, arg) {
	var result = {};
	if (arg == undefined || arg == null || arg == '') {
		arg = {};
	}
	Ajax.json(_cp + '/frame/AjaxExtAction/function.shtml', function(json) {
		result.json = json;
	}, {
		async : false,// 同步
		data : {
			fn : fn,
			arg : JSON.stringify(arg)
		}
	});
	return result.json;
};