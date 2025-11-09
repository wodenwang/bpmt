<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//刷新树
		Core.fn($zone, 'refresh', function() {
			Ajax.post('${_zone}_tree_zone', '${_acp}/tree.shtml');
		});

		//新增界面
		Core.fn($zone, 'createZone', function() {
			Ajax.win('${_acp}/createZone.shtml', {
				title : '创建资源',
				minWidth : 600,
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '提交',
					click : function() {
						var $win = $(this);
						Ajax.form('${_zone}_detail_zone', $('form', $win), {
							confirmMsg : '确认创建?',
							errorZone : '${_zone}_tree_msg_zone',
							callback : function(flag) {
								if (flag) {
									Core.fn($zone, 'refresh')();
									$win.dialog("close");
								}
							}
						});

					}
				} ]
			});
		});

		//展示明细
		Core.fn('${_zone}_tree_zone', 'detail', function(id) {
			Ajax.post('${_zone}_detail_zone', '${_acp}/detail.shtml', {
				data : {
					id : id
				}
			});
		});

		//删除
		Core.fn('${_zone}_tree_zone', 'del', function(id) {
			Ui.confirmPassword('确认删除?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/remove.shtml', {
					data : {
						id : id
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
							$('#${_zone}_detail_zone').children().remove();
						}
					}
				});
			});
		});

		//删除类别
		Core.fn('${_zone}_tree_zone', 'delCatelog', function(id) {
			Ui.confirm('确认删除?', function() {
				Ajax.post('${_zone}_tree_msg_zone', '${_acp}/deleteCatelog.shtml', {
					data : {
						id : id
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refresh')();
						}
					}
				});
			});
		});

		//刷新
		Core.fn('${_zone}_detail_zone', 'refresh', function() {
			Core.fn($zone, 'refresh')();
		});

		//导出
		Core.fn('${_zone}_detail_zone', 'export', function(id) {
			Ui.confirm('确认导出?', function() {
				Ajax.download('${_acp}/export.shtml?id=' + id);
			});
		});

		//设计
		Core.fn('${_zone}_detail_zone', 'edit', function(id) {
			//使用浏览器同步窗口
			//var ret = window.showModalDialog('${_cp}/service/editor?id=' + id, '', "status:no;dialogWidth: 1024px;");
			var $win = Ajax.win('${_acp}/editor.shtml?id=' + id, {
				outFlag : true,
				title : '流程图设计(加载控件较多,请耐心等待)',
				minWidth : 1024,
				minHeight : 600,
				closeFn : function() {
					Core.fn('${_zone}_tree_zone', 'detail')(id);
				}
			});

			$win.attr('flow_editor', 'true');//添加标识以方便回调关闭
		});

		//新建事件
		$('button[name=add]', $zone).on('click', function() {
			Core.fn($zone, 'createZone')();
		});

		//刷新事件
		$('button[name=refresh]', $zone).on('click', function() {
			Core.fn($zone, 'refresh')();
		});
		//初始化调用
		$('button[name=refresh]', $zone).click();

	});
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
	<div title="流程图设计">
		<div class="ws-bar">
			<div class="left ws-group">
				<button icon="refresh" type="button" name="refresh">刷新</button>
			</div>
			<div class="right ws-group">
				<button icon="plus" type="button" name="add">新建流程图</button>
			</div>
		</div>

		<div style="overflow: auto; zoom: 1;">
			<div style="float: left; width: 300px;">
				<div panel="流程图列表">
					<div id="${_zone}_tree_msg_zone"></div>
					<div id="${_zone}_tree_zone"></div>
				</div>
			</div>
			<div style="margin-left: 320px; overflow: auto; zoom: 1;" id="${_zone}_detail_zone"></div>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>