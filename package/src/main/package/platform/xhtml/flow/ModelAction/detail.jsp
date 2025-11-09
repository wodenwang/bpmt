<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		$('button[name=edit]', $zone).click(function() {
			Core.fn($zone, 'edit')($(this).val());
		});

		$('button[name=export]', $zone).click(function() {
			Core.fn($zone, 'export')($(this).val());
		});

		$('button[name=upgrade]', $zone).click(function() {
			var id = $(this).val();
			Ajax.win('${_acp}/upgradeWin.shtml', {
				title : '版本升级',
				minWidth : 600,
				maxHeight : 500,
				data : {
					id : id
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog('close');
					}
				}, {
					text : '确定',
					click : function() {
						var $this = $(this);
						var $errZone = $('div[name=errorZone]', $this);
						var $form = $('form', $this);
						Ajax.form('${_zone}_msg', $form, {
							errorZone : $errZone.attr('id'),
							confirmMsg : '确认升级版本?',
							callback : function(flag) {
								if (flag) {
									$this.dialog('close');
								}
							}
						});
					}
				} ]
			});
		});

		$('button[name=deploy]', $zone).click(function() {
			var id = $(this).val();
			Ajax.win('${_acp}/deployWin.shtml', {
				title : '新的部署',
				minWidth : 1024,
				maxHeight : 500,
				data : {
					id : id
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog('close');
					}
				}, {
					text : '确定',
					click : function() {
						var $this = $(this);
						var $errZone = $('div[name=errorZone]', $this);
						var $form = $('form', $this);
						Ajax.form('${_zone}_msg', $form, {
							errorZone : $errZone.attr('id'),
							confirmMsg : '确认新部署?',
							callback : function(flag) {
								if (flag) {
									$this.dialog('close');
								}
							}
						});
					}
				} ]
			});
		});

		$('form', $zone).submit(function(event) {
			event.preventDefault();
			var $form = $(this);
			Ajax.form('${_zone}_msg', $form, {
				confirmMsg : '确认提交?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
					}
				}
			});

			return false;
		});

		//图片网址
		if ($('img', $zone).size() > 0) {
			$('img', $zone).attr('src', '${_acp}/picture.shtml?id=${vo.id}&random=' + Math.random());
		}

	});
</script>

<div id="${_zone}_msg"></div>

<div tabs="true">
	<div title="流程图设计">
		<div class="ws-bar">
			<div class="left ws-group">
				<button icon="pencil" name="edit" value="${vo.id}" type="button">设计</button>
				<button icon="arrowthickstop-1-s" name="export" type="button" value="${vo.id}">导出</button>
			</div>
			<div class="right ws-group">
				<button icon="play" name="deploy" value="${vo.id}" type="button">新的部署</button>
				<button icon="arrowthickstop-1-n" name="upgrade" value="${vo.id}" type="button">版本升级</button>
			</div>
		</div>
		<c:choose>
			<c:when test="${xml!=null&&xml!=''}">
				<div style="width: 100%; overflow: auto;">
					<img alt="预览" />
				</div>
			</c:when>
			<c:otherwise>
				<div class="ws-msg info">未有流程设计数据,请点击[设计]按钮设计流程,并在设计完成之后点击[保存],系统将自动生成流程设计图.</div>
			</c:otherwise>
		</c:choose>
	</div>
	<div title="基础信息">
		<form action="${_acp}/submit.shtml" sync="true">
			<input type="hidden" name="id" value="${vo.id}" />
			<table class="ws-table">
				<tr>
					<th>流程图名称</th>
					<td><wcm:widget name="name" cmd="text{required:true}" value="${vo.name}"></wcm:widget></td>
				</tr>
				<tr>
					<th>归属分类</th>
					<td><wcm:widget name="category" cmd="text" value="${vo.category}"></wcm:widget></td>
				</tr>
				<tr>
					<th>流程唯一KEY<br /> <font color="red" tip="true" title="标识该流程的唯一键值,在设计工具中指定和修改.">(提示)</font></th>
					<td>${model.mainProcess.id}</td>
				</tr>
				<tr>
					<th>流程名称<br /> <font color="red" tip="true" title="流程部署后使用的名称,在设计工具中修改.">(提示)</font></th>
					<td>${model.mainProcess.name}</td>
				</tr>
				<tr>
					<th class="ws-bar">
						<div class="ws-group">
							<button icon="disk" type="submit">保存</button>
						</div>
					</th>
				</tr>
			</table>
		</form>
	</div>

</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>