<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		var width = '${config.width}';
		if (width == '') {
			width = 1024;
		}

		//编辑
		$('button[name=edit]', $zone).click(function() {
			Ajax.win('${_acp}/editWin.shtml', {
				title : '${wpf:lan("#:zh[明细数据编辑]:en[Detail data editing]#")}',
				minWidth : new Number(width),
				minHeight : 450 ,
				data : {
					_params : $('[name=_detail_params]', $zone).val(),
					widgetKey : '${param.widgetKey}',
					list : $('[name=${param._name}]', $zone).val()
				},
				buttons : [ {
					icons : {
						primary : "ui-icon-close"
					},
					text : '${wpf:lan("#:zh[取消]:en[Cancel]#")}',
					click : function() {
						var $this = $(this);
						Ui.confirm('${wpf:lan("#:zh[是否关闭?]:en[Is it closed?]#")}', function() {
							$this.dialog('close');
						});
					}
				}, {
					icons : {
						primary : "ui-icon-check"
					},
					text : '${wpf:lan("#:zh[确认]:en[Confirm]#")}',
					click : function() {
						var $this = $(this);
						var $form = $('form[name=editForm]', $this);
						var $error = $('div[name=errorZone]', $this);
						console.log($form);
						Ajax.form($zone, $form, {
							errorZone : $error.attr('id'),
							data : {
								_params : $('[name=_detail_params]', $zone).val(),
								state : '${param.state}',
								_name : '${param._name}',
								widgetKey : '${param.widgetKey}'
							},
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

		//编辑
		$('button[name=batch]', $zone).click(function() {
			Ajax.win('${_acp}/batchWin.shtml', {
				title : '${wpf:lan("#:zh[明细数据批处理]:en[Detailed data batch processing]#")}',
				minWidth : 600,
				data : {
					_params : $('[name=_detail_params]', $zone).val(),
					widgetKey : '${param.widgetKey}',
					list : $('[name=${param._name}]', $zone).val()
				},
				buttons : [ {
					icons : {
						primary : "ui-icon-close"
					},
					text : '${wpf:lan("#:zh[取消]:en[Cancel]#")}',
					click : function() {
						$(this).dialog('close');
					}
				}, {
					icons : {
						primary : "ui-icon-check"
					},
					text : '${wpf:lan("#:zh[确认]:en[Confirm]#")}',
					click : function() {
						var $this = $(this);
						var $form = $('form[name=editForm]', $this);
						var $error = $('div[name=errorZone]', $this);
						Ajax.form($zone, $form, {
							confirmMsg : '${wpf:lan("#:zh[确认提交批量数据?]:en[Confirm the submission of batch data?]#")}',
							errorZone : $error.attr('id'),
							data : {
								_params : $('[name=_detail_params]', $zone).val(),
								state : '${param.state}',
								_name : '${param._name}',
								widgetKey : '${param.widgetKey}'
							},
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

		//分页表单
		$('div.ws-bar.page button', $zone).unbind("click"); //移除分页按钮原有事件
		$('div.ws-bar.page button', $zone).click(function() {
			var page = $(this).val();
			Ajax.post($zone, '${_acp}/index.shtml', {
				data : {
					_params : $('[name=_detail_params]', $zone).val(),
					_name : '${param._name}',
					widgetKey : '${config.widgetKey}',
					state : '${param.state}',
					list : $('[name=${param._name}]', $zone).val(),
					_page : page
				}
			});
		});

	});
</script>

<textarea style="display: none;" name="${param._name}">${wcm:json(list)}</textarea>
<textarea style="display: none;" name="_detail_params">${param._params}</textarea>

<table class="ws-table">
	<tr>
		<th style="width: 40px; min-width: 40px;">${wpf:lan("#:zh[序号]:en[No.]#")}</th>
		<c:forEach items="${fields}" var="field">
			<c:if test="${wpf:check(field.pri)}">
				<th>${wpf:lan(field.busiName)}</th>
			</c:if>
		</c:forEach>
	</tr>
	<c:forEach items="${dp.list}" var="vo" varStatus="status">
		<c:set var="context" value="${wcm:map(null,'vo',vo)}" />
		<tr>
			<td class="center" style="font-weight: bold; min-width: 30px;">${status.index+1+dp.start}</td>
			<c:forEach items="${fields}" var="field">
				<c:if test="${wpf:check(field.pri)}">
					<td class="center" style="${wcm:widget('style[min-height;min-width(60)]',field.style)}"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
				</c:if>
			</c:forEach>
		</tr>
	</c:forEach>
	<tr>
		<th class="ws-bar"><c:if test="${param.state=='normal'}">
				<div class="left ws-group">
					<button icon="pencil" type="button" name="edit">${wpf:lan("#:zh[编辑]:en[Edit]#")}</button>
					<%-- 批量方式 --%>
					<c:if test="${detail.batchFlag==1}">
						<button icon="arrowthickstop-1-n" type="button" name="batch">${wpf:lan("#:zh[批处理]:en[Batch]#")}</button>
					</c:if>
				</div>
			</c:if>
			<div class="right" style="margin-right: 5px;">
				<wpf:script type="${detail.sumarryType}" script="${detail.sumarryScript}" context="${wcm:map(null,'list',list)}" />
			</div></th>
	</tr>
</table>

<c:if test="${detail.pageFlag==1}">
	<wcm:page dp="${dp}" />
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>