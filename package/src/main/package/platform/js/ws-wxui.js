/**
 * 微信核心UI
 * 
 * @author wodenwang
 */
var Wxui = {
	/**
	 * 展示loading
	 */
	showLoading : function() {
		if (typeof ($.AMUI) != "undefined") {// 优先amaze ui
			var $loading = $("#_body_loading");
			if ($loading.size() < 1) {
				$loading = $('<div class="am-modal am-modal-loading am-modal-no-btn" tabindex="-1" id="_body_loading"></div>');
				$loading.append('<div class="am-modal-dialog"><div class="am-modal-hd">数据加载中</div><div class="am-modal-bd"><span class="am-icon-spinner am-icon-spin"></span></div></div>');
				$('body').append($loading);
			}
			$loading.modal({
				closeViaDimmer : false
			});
		} else {// weui
			$.showLoading();
		}
	},

	/**
	 * 隐藏loading
	 */
	hideLoading : function() {
		if (typeof ($.AMUI) != "undefined") {// 优先amaze ui
			var $loading = $("#_body_loading");
			try {
				$loading.modal('close');
			} catch (e) {
			}
		} else {// weui
			$.hideLoading();
		}
	},

	/**
	 * alert提示
	 */
	alert : function(msg, fn) {
		if (!$.isFunction(fn)) {
			fn = function() {
			};
		}

		if (typeof ($.AMUI) != "undefined") {// 优先amaze ui
			var $alert = $('#_body_alert');
			if ($alert.size() < 1) {
				$alert = $('<div class="am-modal am-modal-alert" tabindex="-1" id="_body_alert"></div>');
				$alert
						.append('<div class="am-modal-dialog"><div class="am-modal-hd">消息提示</div><div class="am-modal-bd"></div><div class="am-modal-footer"><span class="am-modal-btn" data-am-modal-confirm>确定</span></div></div>');
				$('body').append($alert);
			}
			$('.am-modal-bd', $alert).html(msg);

			$alert.modal({
				closeViaDimmer : false,
				onConfirm : function() {
					fn();
				}
			});
		} else {// weui
			$.alert(msg, '提示', fn);
		}
	},

	/**
	 * confirm提示
	 */
	confirm : function(msg, fn, cancelFn) {
		if (!$.isFunction(fn)) {
			fn = function() {
			};
		}
		if (!$.isFunction(cancelFn)) {
			cancelFn = function() {
			};
		}
		if (typeof ($.AMUI) != "undefined") {// 优先amaze ui
			var $confirm = $('#_body_confirm');
			if ($confirm.size() < 1) {
				$confirm = $('<div class="am-modal am-modal-confirm" tabindex="-1" id="_body_confirm"></div>');
				$confirm
						.append('<div class="am-modal-dialog"><div class="am-modal-hd">消息提示</div><div class="am-modal-bd"></div><div class="am-modal-footer"><span class="am-modal-btn" data-am-modal-cancel>取消</span><span class="am-modal-btn" data-am-modal-confirm>确定</span></div></div>');
				$('body').append($confirm);
			}
			$('.am-modal-bd', $confirm).html(msg);
			$confirm.modal({
				closeViaDimmer : false,
				onConfirm : function() {
					fn();
				},
				onCancel : function() {
					cancelFn();
				}
			});
		} else {// weui
			$.confirm(msg, '消息确认', fn, cancelFn);
		}
	},

	/**
	 * 固定区域提示
	 */
	toast : function(msg, style) {
		if (typeof ($.AMUI) != "undefined") {// 优先amaze ui
			var $toast = $('#_body_toast');
			if ($toast.size() < 1) {
				$toast = $('<div class="am-modal am-modal-no-btn" tabindex="-1" id="_body_toast"><div class="am-modal-dialog"></div></div>');
				$('body').append($toast);
			}
			if (!style) {
				style = 'info';
			}
			var $div = $('<div class="am-alert" style="margin-bottom:0px;" data-am-alert><button type="button" class="am-close" data-am-modal-close>&times;</button><p></p></div>')
			if (style == 'info') {
				$div.addClass('am-alert-success');
			} else if (style == 'warning') {
				$div.addClass('am-alert-warning');
			} else if (style == 'error') {
				$div.addClass('am-alert-danger');
			}
			$('p', $div).html(msg);
			$('.am-modal-dialog', $toast).html('').append($div);
			$toast.modal({
				closeViaDimmer : true
			});
		} else {// weui
			if (style == 'error') {
				style = 'cancel';
			} else if (style == 'warning') {
				style = 'cancel';
			}
			$.toast(msg, style);
		}
	},

	/**
	 * 初始化表单验证框架
	 */
	validateForm : function($form) {
		if (typeof ($.AMUI) != "undefined") {// 优先amaze ui
			$form.validator({
				onValid : function(validity) {
					$(validity.field).closest('dd').find('.am-alert').hide();
				},

				onInValid : function(validity) {
					var $field = $(validity.field);
					var $group = $field.closest('dd');
					var $alert = $group.find('.am-alert');
					// 使用自定义的提示信息 或 插件内置的提示信息
					var msg = $field.data('validationMessage') || this.getValidationMessage(validity);
					if (!$alert.length) {
						$alert = $('<div class="am-alert am-alert-danger"></div>').hide().appendTo($group);
					}
					$alert.html(msg).show();
				}
			});
		}
	},

	/**
	 * ajax提交表单,默认用json模式
	 */
	form : function($form, options, successFn, errorFn) {
		if ($.isFunction(options)) {// 是函数表示没有options
			errorFn = successFn;
			successFn = options;
			options = {};
		}
		if (!options.data) {
			options.data = {};
		}
		$.extend(options.data, {
			_data_type : 'json',
			_random : "" + Math.random()
		});

		// 出错提示
		if (errorFn == undefined || !$.isFunction(errorFn)) {
			errorFn = function(res) {
				var json;
				try {
					json = JSON.parse(res.responseText);
				} catch (e) {
					json = {
						msg : '系统出错.'
					};
				}
				Wxui.toast(json.msg, 'error');
			};
		}

		if ($('input[name=_data_type]', $form).size() < 1) {
			$form.append('<input type="hidden" name="_data_type" value="json" />');
		}

		// amaze才需要验证表单
		var valid = true;
		if (typeof ($.AMUI) != "undefined") {
			valid = $form.validator('isFormValid');
		}

		if (valid) {
			//清空form中的type=file,否则会导致无法提交
			$(":file",$form).val('');
			
			$form.ajaxSubmit({
				dataType : 'json',
				data : options.data,
				beforeSend : function(XMLHttpRequest) {
					if (options.loading != false) {
						Wxui.showLoading();
					}
				},
				success : function(o) {
					Wxui.hideLoading();
					successFn(o);
				},
				error : function(o) {
					Wxui.hideLoading();
					errorFn(o);
				}
			});
			return false;
		}
	},

	/**
	 * json提交
	 */
	json : function(url, options, successFn, errorFn) {
		if ($.isFunction(options)) {// 是函数表示没有options
			errorFn = successFn;
			successFn = options;
			options = {};
		}

		if (!options.data) {
			options.data = {};
		}
		$.extend(options.data, {
			_data_type : 'json',
			_random : "" + Math.random()
		});

		// 出错提示
		if (errorFn == undefined || !$.isFunction(errorFn)) {
			errorFn = function(res) {
				var json = JSON.parse(res.responseText);
				Wxui.toast(json.msg, 'error');
			};
		}
		$.ajax(url, {
			dataType : 'json',
			data : options.data,
			beforeSend : function(XMLHttpRequest) {
				if (options.loading != false) {
					Wxui.showLoading();
				}
			},
			success : function(o) {
				Wxui.hideLoading();
				successFn(o);
			},
			error : function(o) {
				Wxui.hideLoading();
				errorFn(o);
			}
		});
	},

	/**
	 * 提交返回HTML
	 */
	html : function(url, options, successFn, errorFn) {
		if ($.isFunction(options)) {// 是函数表示没有options
			errorFn = successFn;
			successFn = options;
			options = {};
		}

		if (!options.data) {
			options.data = {};
		}
		$.extend(options.data, {
			_head : false,
			_data_type : 'json',
			_random : "" + Math.random()
		});

		// 出错提示
		if (errorFn == undefined || !$.isFunction(errorFn)) {
			errorFn = function(res) {
				var json = JSON.parse(res.responseText);
				Wxui.toast(json.msg, 'error');
			};
		}
		$.ajax(url, {
			dataType : 'html',
			data : options.data,
			beforeSend : function(XMLHttpRequest) {
				if (options.loading != false) {
					Wxui.showLoading();
				}
			},
			success : function(o) {
				Wxui.hideLoading();
				successFn(o);
			},
			error : function(o) {
				Wxui.hideLoading();
				errorFn(o);
			}
		});
	}

};
