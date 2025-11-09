<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<%--定义变量 --%>
<c:set var="isCreate" value="${vo==null}" />

<%-- 数据准备 --%>
<c:set var="context" value="${wcm:map(wcm:map(null,'mode',3),'vo',vo)}" />
<%-- 获取left join绑定上下文 --%>
<c:forEach items="${config.table.parents}" var="parent">
	<c:set var="context" value="${wcm:map(context,parent.var,wpf:pixelVO(parent.var,vo))}" />
</c:forEach>
<!-- 数据展示准备处理器 -->
<c:forEach items="${config.table.prepareExecs}" var="exec">
	<c:set var="context" value="${wcm:map(context,exec.var,(wpf:script(exec.execType,exec.execScript,context)))}" />
</c:forEach>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $main, $msg, $tabs;
		if ('${param._main}' != '') {
			$main = $('#${param._main}');
			$msg = $('[name=mainMsgZone]', $main);
			$tabs = $('div[tabs=true]:first', $main);
		} else {
			if ('${param._list}' != '') {
				$main = $('#${param._list}');
			} else {
				$main = null;
			}
			$msg = $('#${_zone}_msg');
			$tabs = null;
		}

		//定义为变量方便传递
		var params = $('#${_zone}_params').val();

		/**
		 * 表单提交
		 * mode=1:打开明细页
		 * mode=2:打开新增页
		 */
		Core.fn($zone, 'submitForm', function(mode) {
			var form = $('#${_zone}_form');
			var option = eval('(' + form.attr("option") + ')');

			option = $.extend({}, {
				dataType : 'json',
				loading : true,  //暂时写死, 以后改成可配置
				successFn : function(pk) {
					Ui.closeCurrent('${_zone}');
					//回调
					if ($.isFunction(Core.fn($zone, 'callback'))) {
						Core.fn($zone, 'callback')();
					}

					switch (mode) {
					case 1:
						Core.fn($zone, 'show')(JSON.stringify(pk));
						break;
					case 2:
						Core.fn($zone, 'create')();
						break;
					default:
						//do noting
						break;
					}
				},
				btn : $('button', form),
				callback : function(flag) {
					if (!flag) {
						$.scrollTo('#${_zone}_button_bar', 500);
					}
				}
			}, option);

			//$.scrollTo($("#" + zone));
			Ajax.form($msg, form, option);
		});
		/**
		 * 打开创建表单
		 */
		Core.fn($zone, 'create', function() {
			var data = {
				_params : params,
				_main : '${param._main}',
				_list : '${param._list}'
			};
			var $tab;
			if ($tabs != null) {
				$tab = Ajax.tab($tabs, '${_acp}/createZone.shtml', {
					title : '${wpf:lan(title)}',
					data : data
				});
			} else if ($zone.attr('win') == 'true') {
				$tab = Ajax.win('${_acp}/createZone.shtml', {
					title : '${wpf:lan(title)}',
					minWidth : 1024,
					data : data
				});
			} else {
				Ajax.post($zone, '${_acp}/createZone.shtml', {
					data : data
				});
				$tab = $zone;
			}
			Core.fn($tab, 'callback', function() {
				if ($.isFunction(Core.fn($zone, 'callback'))) {
					Core.fn($zone, 'callback')();
				}
			});
		});
		/**
		 * 展示
		 */
		Core.fn($zone, 'show', function(pk) {
			if (pk == undefined) {
				pk = $('#${_zone}_key').val();
			}
			var data = {
				_params : params,
				_main : '${param._main}',
				_list : '${param._list}',
				_key : pk
			};

			var $tab;
			if ($tabs != null) {
				$tab = Ajax.tab($tabs, '${_acp}/detail.shtml', {
					title : '${wpf:lan(title)}',
					data : data
				});
			} else if ($zone.attr('win') == 'true') {
				$tab = Ajax.win('${_acp}/detail.shtml', {
					title : '${wpf:lan(title)}',
					minWidth : 1024,
					data : data
				});
			} else {
				Ajax.post($zone, '${_acp}/detail.shtml', {
					data : data
				});
				$tab = $zone;
			}
			Core.fn($tab, 'callback', function() {
				if ($.isFunction(Core.fn($zone, 'callback'))) {
					Core.fn($zone, 'callback')();
				}
			});
		});

		//跳到子表
		$('button[name=scrollToSub]', $zone).click(function() {
			$.scrollTo('#${_zone}_subTab_main', 500);
		});

		//保存并关闭tab
        $('button[name=submitAndDone]', $zone).click(function() {
        	Core.fn($zone, 'submitForm')();
        });

		//保存并关闭tab，打开明细页面
		$('button[name=submitAndClose]', $zone).click(function() {
			Core.fn($zone, 'submitForm')(1);
		});

		//保存并新增下一条
		$('button[name=submitAndNext]', $zone).click(function() {
			Core.fn($zone, 'submitForm')(2);
		});

		//查看明细
		$('button[name=show]', $zone).click(function() {
			Ui.confirm('${wpf:lan("#:zh[(查看明细)操作将不会保存您当前未提交的内容,是否继续?]:en[(View detail)operation will not save your current uncommitted content, whether or not to continue?]#")}', function() {
				Core.fn($zone, 'show')();
				Ui.closeCurrent('${_zone}');
			});
		});

		//刷新按钮
		$('button[name=refresh]', $zone).click(function() {
			Ui.confirm('${wpf:lan("#:zh[(刷新)操作将不会保存您当前未提交的内容,是否继续?]:en[(Refresh)operation will not save your current uncommitted content, whether or not to continue?]#")}', function() {
				if ('${isCreate?1:0}' == '1') {
					Ajax.post('${_zone}', '${_acp}/createZone.shtml', {
						data : {
							_params : params,
							_main : '${param._main}',
							_list : '${param._list}'
						},
						showFlag : false
					});
				} else {
					Ajax.post('${_zone}', '${_acp}/updateZone.shtml', {
						data : {
							_params : params,
							_main : '${param._main}',
							_list : '${param._list}',
							_key : $('#${_zone}_key').val()
						},
						showFlag : false
					});
				}
			});

		});

		var $subZone = $('#${_zone}_subTab_main');
		if ($subZone.size() > 0) {//存在子表标签
			//回写函数
			Ajax.post($subZone, '${_acp}/sub.shtml', {
				data : {
					_params : params,
					_main : '${param._main}',
					_list : '${param._list}',
					_key : $('#${_zone}_key').val()
				}
			});

			//回写函数
			Core.fn($subZone, 'callback', function() {
				if ($.isFunction(Core.fn($zone, 'callback'))) {
					Core.fn($zone, 'callback')();
				}
				$('button[name=callback]', $zone).click();
			});
		}

	});
</script>

<%-- 客户端脚本 --%>
<wpf:javascript script="${config.table.formJsScript}" type="${config.table.formJsType}" context="${context}" form="${_zone}_form" />

<%-- 顶部按钮bar(功能性操作) --%>
<div class="ws-bar" id="${_zone}_button_bar">
	<div class="left ws-group">
		<c:if test="${!isCreate&&(wpf:checkExt(config.detailBtn.pri,context))}">
			<button type="button" icon="${config.detailBtn.icon}" name="${config.detailBtn.name}">${wpf:lan(config.detailBtn.busiName)}</button>
		</c:if>
	</div>
	<div class="right ws-group">
		<button icon="refresh" text="true" type="button" name="refresh">${wpf:lan("#:zh[刷新]:en[Refresh]#")}</button>
		<c:if test="${!isCreate&&config.subs!=null&&fn:length(config.subs)>0}">
			<button icon="arrowthick-1-s" text="true" type="button" name="scrollToSub">${wpf:lan("#:zh[底部]:en[Bottom]#")}</button>
		</c:if>
		<button type="button" icon="closethick" text="true" onclick="Ui.closeCurrent('${_zone}')">${wpf:lan("#:zh[关闭]:en[Close]#")}</button>
	</div>
</div>

<%--错误提示区域 --%>
<div id="${_zone}_error"></div>

<%--表单 --%>
<form action="${_acp}/submit.shtml" method="post" id="${_zone}_form" option="{errorZone:'${_zone}_error',confirmMsg:'${wpf:lan("#:zh[确认提交？]:en[Confirm to submit?]#")}'}" onsubmit="$('button[name=submitAndClose]').click();return false;"
	sync="true">
	<c:if test="${!isCreate}">
		<textarea name="_key" style="display: none;" id="${_zone}_key">${wcm:jsonKey(vo,config.keysArray)}</textarea>
	</c:if>
	<textarea style="display: none;" name="_params" id="${_zone}_params">${param._params}</textarea>

	<c:if test="${fn:length(config.formFields)>0}">
		<table class="ws-table" col="${config.table.col}" group="true">
			<c:forEach items="${config.formFields}" var="field">
				<c:choose>
					<c:when test="${field.whole==null}">
						<%--分割线 --%>
						<c:if test="${field.name==null&&wpf:checkExt(field.pri,context)}">
							<tr whole="true" group="true" show="${field.expandFlag==0?'false':'true'}">
								<th colspan="${config.table.col*2}">${wpf:lan(field.busiName)}</th>
								<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
									<td>${wpf:script(field.tipType,field.tipScript,context)}</td>
								</c:if>
							</tr>
						</c:if>
					</c:when>
					<c:when test="${field.name!=null&&field.showFlag!=null}">
						<%-- 固定字段 --%>
						<c:if test="${wpf:checkExt(field.pri,context)}">
							<%-- 自增长主键不需要表单 --%>
							<c:if test="${!isCreate||field.createPri!=null}">
								<c:set var="widgetStateFlag" value="${isCreate?wpf:check(field.createPri):wpf:checkExt(field.updatePri,context)}" />
								<tr whole="${field.whole==1}" self="${field.whole==2}">
									<th>${wpf:lan(field.busiName)}<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
											<br />
											<span style="color: red; font-weight: bold; cursor: help;" tip="true" title="${wpf:script(field.tipType,field.tipScript,context)}">${wpf:lan("#:zh[(提示)]:en[(TIPS)]#")}</span>
										</c:if>
									</th>
									<%-- widgetValue--%>
									<c:choose>
										<c:when test="${vo!=null}">
											<c:set var="widgetValue" value="${vo[field.name]}" />
										</c:when>
										<c:when test="${field.widgetContentScript!=null&&field.widgetContentScript!=''}">
											<c:set var="widgetValue" value="${wpf:script(field.widgetContentType,field.widgetContentScript,context)}" />
										</c:when>
										<c:otherwise>
											<c:set var="widgetValue" value="${null}" />
										</c:otherwise>
									</c:choose>
									<td style="${wcm:widget('style[width;text-align]',field.style)}"><wcm:widget name="${field.name}" cmd="${field.widget}" value="${widgetValue}"
											state="${widgetStateFlag?'normal':'readonly'}" params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}" /></td>
								</tr>
							</c:if>
						</c:if>
					</c:when>
					<c:when test="${field.name!=null}">
						<%-- 表单字段 --%>
						<c:if test="${wpf:checkExt(field.pri,context)}">
							<tr whole="${field.whole==1}" self="${field.whole==2}">
								<th>${wpf:lan(field.busiName)}<c:if test="${field.tipScript!=null&&field.tipScript!=''}">
										<br />
										<span style="color: red; font-weight: bold; cursor: help;" tip="true" title="${wpf:script(field.tipType,field.tipScript,context)}">${wpf:lan("#:zh[(提示)]:en[(TIPS)]#")}</span>
									</c:if>
								</th>
								<td><wcm:widget name="${field.name}" cmd="${field.widget}" value="${wpf:script(field.contentType,field.contentScript,context)}"
										state="${wpf:checkExt(field.editPri,context)?'normal':'readonly'}" params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}">${wpf:lan("#:zh[不支持命令]:en[Do not support the command]#")}</wcm:widget></td>
							</tr>
						</c:if>
					</c:when>
					<c:otherwise>
						<%-- 展示字段 --%>
						<c:if test="${wpf:checkExt(field.pri,context)}">
							<tr whole="${field.whole==1}" self="${field.whole==2}">
								<th>${wpf:lan(field.busiName)}</th>
								<td class="left" style="${wcm:widget('style[width;text-align]',field.style)}"><wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" /></td>
							</tr>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</table>
	</c:if>
</form>


<%-- 中/底部按钮bar(业务性操作) --%>
<div class="ws-bar">
	<div class="center ws-group">
		<button type="button" icon="disk" text="true" name="submitAndDone">${wpf:lan("#:zh[保存并关闭]:en[Save and Close]#")}</button>
		<button type="button" icon="disk" text="true" name="submitAndClose">${wpf:lan("#:zh[保存]:en[Save]#")}</button>
		<c:if test="${isCreate}">
			<button type="button" icon="disk" text="true" name="submitAndNext">${wpf:lan("#:zh[保存并编辑下一条]:en[Save and Edit Next]#")}</button>
		</c:if>
	</div>
</div>

<c:if test="${!isCreate&&config.subs!=null&&fn:length(config.subs)>0}">
	<div id="${_zone}_subTab_main"></div>
</c:if>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>