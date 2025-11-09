/**
 * 
 * 控件使用脚本库<br>
 * 用于客户端联动<br>
 * 开放API:<br>
 * init:控件初始化<br>
 * enabled/disabled:控件生效/失效<br>
 * val:设值/取值
 * 
 * @type
 */
var Widget = {

	/**
	 * 函数集
	 * 
	 * @type
	 */
	fn : {},

	// ----初始化
	/**
	 * 设置控件初始化函数
	 * 
	 * @param {}
	 *            form 所在表单
	 * @param {}
	 *            widgetName 控件名
	 * @param {}
	 *            fn 函数
	 */
	_setInit : function(form, widgetName, fn) {
		Widget.setFn(form, widgetName, '_init', fn);
	},

	/**
	 * 调用初始化函数
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 * @param {}
	 *            params 动态参数
	 */
	init : function(form, widgetName, params) {
		if (params != undefined) {
			if (typeof (params) != 'string') {
				params = JSON.stringify(params);
			}
			Widget.params(form, widgetName, params);
		}

		return Widget.getFn(form, widgetName, '_init')();
	},

	// ---生效/失效
	/**
	 * 设置控件生效/失效函数
	 * 
	 * @param {}
	 *            form 所在表单
	 * @param {}
	 *            widgetName 控件名
	 * @param {}
	 *            fn 函数
	 */
	_setEnabled : function(form, widgetName, fn) {
		Widget.setFn(form, widgetName, '_enabled', fn);
	},

	/**
	 * 生效
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 */
	enabled : function(form, widgetName, flag) {
		if (flag == undefined) {
			flag = true;
		}
		return Widget.getFn(form, widgetName, '_enabled')(flag);
	},

	/**
	 * 失效
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 * @return {}
	 */
	disabled : function(form, widgetName) {
		return Widget.getFn(form, widgetName, '_enabled')(false);
	},

	// ---设值
	/**
	 * 设置控件设值函数
	 * 
	 * @param {}
	 *            form 所在表单
	 * @param {}
	 *            widgetName 控件名
	 * @param {}
	 *            fn 函数
	 */
	_setVal : function(form, widgetName, fn) {
		Widget.setFn(form, widgetName, '_val', fn);
	},

	/**
	 * 设值/取值
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 */
	val : function(form, widgetName, val) {
		return Widget.getFn(form, widgetName, '_val')(val);
	},

	// ---回调函数
	/**
	 * 获取控件事件函数
	 * 
	 * @param {}
	 *            form 所在表单
	 * @param {}
	 *            widgetName 控件名
	 */
	_getChange : function(form, widgetName) {
		return Widget.getFn(form, widgetName, '_change');
	},

	/**
	 * 设置事件
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 */
	change : function(form, widgetName, fn) {
		Widget.setFn(form, widgetName, '_change', fn);
	},

	// ---设置动态参数
	/**
	 * 设置控件初始化函数
	 * 
	 * @param {}
	 *            form 所在表单
	 * @param {}
	 *            widgetName 控件名
	 * @param {}
	 *            fn 函数
	 */
	_setParams : function(form, widgetName, fn) {
		Widget.setFn(form, widgetName, '_params', fn);
	},

	/**
	 * 调用初始化函数
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 */
	params : function(form, widgetName, params) {
		return Widget.getFn(form, widgetName, '_params')(params);
	},

	// ---通用
	/**
	 * 调用所有函数
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            name
	 */
	invokeAll : function(form, name) {
		if (typeof (form) != 'string') {
			if (form.size() > 0) {
				var id = form.attr('id');
				if (id == undefined || id == null) {
					id = Widget._nextSeq();
					form.attr('id', id);
				}
				form = id;
			} else {
				form = '_unknow';
			}
		}

		var fn = Widget.fn[form];
		if (fn != undefined) {
			$.each(fn, function(k, v) {
				v[name]();
			});
		}
	},

	/**
	 * 调用所有初始化函数
	 * 
	 * @param {}
	 *            form
	 */
	initAll : function(form) {
		Widget.invokeAll(form, '_init');
	},

	clean : function(form) {
		if (typeof (form) != 'string') {
			if (form.size() > 0) {
				var id = form.attr('id');
				if (id == undefined || id == null) {
					id = Widget._nextSeq();
					form.attr('id', id);
				}
				form = id;
			} else {
				form = '_unknow';
			}
		}

		Widget.fn[form] = {};// 清空
	},

	/**
	 * 设置函数
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 * @param {}
	 *            name
	 * @param {}
	 *            fn
	 */
	setFn : function(form, widgetName, name, fn) {

		if (typeof (form) != 'string') {
			if (form.size() > 0) {
				var id = form.attr('id');
				if (id == undefined || id == null) {
					id = Widget._nextSeq();
					form.attr('id', id);
				}
				form = id;
			} else {
				form = '_unknow';
			}
		}

		var _fn = Widget.fn[form];
		if (_fn == undefined) {
			_fn = {};
			Widget.fn[form] = _fn;
		}
		var _widgetFn = _fn[widgetName];
		if (_widgetFn == undefined) {
			_widgetFn = {};
			_fn[widgetName] = _widgetFn;
		}

		_widgetFn[name] = fn;
	},

	/**
	 * 调用函数
	 * 
	 * @param {}
	 *            form
	 * @param {}
	 *            widgetName
	 * @param {}
	 *            name
	 * @return {}
	 */
	getFn : function(form, widgetName, name) {

		if (typeof (form) != 'string') {
			if (form.size() > 0) {
				var id = form.attr('id');
				if (id == undefined || id == null) {
					id = Widget._nextSeq();
					form.attr('id', id);
				}
				form = id;
			} else {
				form = '_unknow';
			}
		}

		var _fn = Widget.fn[form];
		if (_fn == undefined) {
			_fn = {};
			Widget.fn[form] = _fn;
		}
		var _widgetFn = _fn[widgetName];
		if (_widgetFn == undefined) {
			_widgetFn = {};
			_fn[widgetName] = _widgetFn;
		}

		var _result = _widgetFn[name];
		if (!$.isFunction(_result)) {
			// 返回空白函数
			return function() {
			};
		} else {
			return _result;
		}
	},

	// 自定义排序
	_seq : 80000,
	_nextSeq : function() {
		if (typeof (Core) != "undefined") {
			return Core.nextSeq();
		}
		return Widget._seq++;
	}

};