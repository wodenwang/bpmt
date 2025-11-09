/**
 * 系统ui交互类扩展
 */
if (Ui == undefined) {
	Ui = {};
}

/**
 * 询问框(需输入密码)
 * 
 * @param {}
 *            msg
 * @param {}
 *            fn
 */
Ui.confirmPassword = function(msg, fn) {
	var title = "提示窗口(需要密码验证)";
	var $msg = $('<div>' + msg + '</div>');
	$msg.styleMsg({
		type : 'warning'
	});
	var $password = $('<p style="text-align:center;"><input type="password" /></p>');
	var $frame = $('<div></div>');
	$frame.append($msg).append($password);
	$('body').append($frame);

	/**
	 * 确定回调
	 */
	var func = function() {
		Ajax.json(_cp + '/frame/AjaxExtAction/checkPassword.shtml', function(result) {
			if (result.flag) {
				$frame.dialog("close");
				fn();
			} else {
				$msg.html('密码校验错误.');
				$msg.styleMsg({
					type : 'error'
				});
			}
		}, {
			data : {
				password : $('input', $password).val()
			}
		});
	};

	$('input', $password).bind('keydown', function(e) {
		var key = e.which;
		if (key == 13) {
			e.preventDefault();
			func();
		}
	});

	$frame.dialog({
		title : title,
		modal : true,
		buttons : [ {
			icons : {
				primary : "ui-icon-alert"
			},
			text : '确定',
			click : function() {
				func();
			}
		}, {
			icons : {
				primary : "ui-icon-cancel"
			},
			text : '取消',
			click : function() {
				$(this).dialog("close");
			}
		} ]
	});
};