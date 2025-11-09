function ueditorTextarea(editorId) {
	UE.registerUI('textarea', function(editor, uiName) {
		var BASE_URL = "/js/ueditor/dialog/textarea/";
		// 创建dialog
		var dialog = new UE.ui.Dialog({
			// 指定弹出层中页面的路径，这里只能支持页面,因为跟addCustomizeDialog.js相同目录，所以无需加路径
			iframeUrl : BASE_URL + 'textarea.html?id=090909',
			// 需要指定当前的编辑器实例
			editor : editor,
			// 指定dialog的名字
			name : "textareaDialog",
			// dialog的标题
			title : "文本编辑框设置",

			// 指定dialog的外围样式
			cssRules : "width:300px;height:130px;",

			// 如果给出了buttons就代表dialog有确定和取消
			buttons : [ {
				className : 'edui-okbutton',
				label : '确定',
				onclick : function() {
					dialog.close(true);
				}
			}, {
				className : 'edui-cancelbutton',
				label : '取消',
				onclick : function() {
					dialog.close(false);
				}
			} ]
		});

		// 参考addCustomizeButton.js
		var btn = new UE.ui.Button({
			name : 'textarea',
			title : '文本编辑框',
			// 需要添加的额外样式，指定icon图标，这里默认使用一个重复的icon
			cssRules : "background: url('" + BASE_URL + "textarea.png') no-repeat !important;cursor: auto;height: 16px;width: 16px;",
			onclick : function() {
				// 渲染dialog
				dialog.render();
				dialog.open();
			}
		});

		// 浮动编辑
		var popup = new baidu.editor.ui.Popup({
			editor : editor,
			content : '',
			className : 'edui-bubble',
			_onEditButtonClick : function() {
				this.hide();
				dialog.open();
			},
			_onRemoveButtonClick : function(cmdName) {
				baidu.editor.dom.domUtils.remove(this.anchorEl);
				this.hide();
			}
		});
		popup.render();

		// 当点到编辑内容上时，按钮要做的状态反射
		editor.addListener('selectionchange', function() {
			var current = editor.selection.getRange().startContainer;
			if (current && current.tagName == 'SPAN' && current.className.indexOf("textarea") != -1) {
				var textarea = current.getElementsByTagName("TEXTAREA")[0];
				var str = '<nobr>字段名: ' + '<span style="color:green;">' + textarea.getAttribute("name") + '</span>&nbsp;&nbsp;' + '<span onclick=$$._onEditButtonClick() class="edui-clickable">'
						+ editor.getLang("modify") + '</span>&nbsp;&nbsp;' + '<span onclick="$$._onRemoveButtonClick(\'textareaDialog\');" class="edui-clickable">' + editor.getLang("delete")
						+ '</span></nobr>';
				var html = popup.formatHtml(str);
				popup.getDom('content').innerHTML = html;
				popup.anchorEl = current;
				popup.showAnchor(current);

				btn.setDisabled(true);
				btn.setChecked(false);
			} else {
				btn.setDisabled(false);
				btn.setChecked(false);
			}
		});

		return btn;
	}, 14, editorId);
}