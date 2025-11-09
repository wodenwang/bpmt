<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//新增按钮
		$('button[name=expand]', $zone).click(function() {
			var $this = $(this);
			var $table = $this.parents('table:first');
			if ($('tr:hidden:not(.last-child)', $table).size() < 1) {
				$('tr:not(.last-child)', $table).hide('fast');
			} else {
				$('tr:not(.last-child)', $table).show('fast');
			}
		});

		var querySize = new Number('${fn:length(config.querys)}');
		var initQuery = new Number('${config.initQuery}');
		var allowAdd = '${allowAdd?1:0}';
		if (allowAdd != '1' || (querySize > 0 && initQuery == 0)) {//被动查询模式
			//do nothing
		} else if (initQuery == 1) {//主动查询(收缩)
			$('button[name=expand]', $zone).click();
			$('#${_zone}_wait_list_form').submit();
		} else {//主动查询(展开)
			$('#${_zone}_wait_list_form').submit();
		}

		//重新查询则清空状态
		$('#${_zone}_wait_list_form').submit(function() {
			Core.fn('${_zone}_edit_list', 'initState')();
		});

		//高亮对应选项
		Core.fn('${_zone}_wait_list', 'highlight', function($checkbox) {
			Core.fn('${_zone}_edit_list', 'highlight')($checkbox);
		});

		//保存所选
		Core.fn('${_zone}_wait_list', 'submitSelect', function($form) {
			//已选记录
			var selectedPks = [];
			$.each($('textarea[name=pk]', $('#${_zone}_edit_list')), function() {
				var $this = $(this);
				selectedPks.push($this.val());
			});

			Ajax.form('${_zone}_work_zone', $form, {
				showFlag : false,
				errorZone : '${_zone}_wait_error_zone',
				data : {
					widgetKey : '${param.widgetKey}',
					selectedPks : selectedPks,
					_params : $('#${_zone}_text_params').val(),
				},
				callback : function(flag) {
					if (!flag) {
						return;
					}

					//增加选中列
					var $target = $('#${_zone}_work_zone');
					$.each($('tr[pk]', $target), function() {
						var $tr = $(this);
						Core.fn('${_zone}_edit_list', 'addTr')($tr);
					});
					$target.html('');
					$('#${_zone}_wait_list_form').submit();
				}
			});
		});

		//直接新增
		$('button[name=add]', $zone).click(function() {
			Ajax.post('${_zone}_work_zone', '${_acp}/addData.shtml', {
				showFlag : false,
				errorZone : '${_zone}_wait_error_zone',
				data : {
					widgetKey : '${param.widgetKey}',
					_params : $('#${_zone}_text_params').val(),
				},
				callback : function(flag) {
					if (!flag) {
						return;
					}

					//增加选中列
					var $target = $('#${_zone}_work_zone');
					$.each($('tr[pk]', $target), function() {
						var $tr = $(this);
						Core.fn('${_zone}_edit_list', 'addTr')($tr);
					});
					$target.html('');
				}
			});
		});

		//初始化编辑区域
		var initEditZone = function() {
			Ajax.post('${_zone}_edit_list', '${_acp}/editList.shtml', {
				data : {
					widgetKey : '${param.widgetKey}',
					_params : $('#${_zone}_text_params').val(),
					list : $('#${_zone}_text_list').val(),
				}
			});
		};
		var warningFlag = '${warningFlag}';
		if (warningFlag == '1') {
			Ui.confirm('${wpf:lan("#:zh[待编辑的数据过多,可能需要长时间等待并导致浏览器卡死.建议采用批量方式编辑.]:en[Too much data to be edited, it may take a long time to wait and cause the browser to die.]#")}<br/><font color="red">${wpf:lan("#:zh[您是否不理会当前警告并继续编辑数据?]:en[Do you ignore the current warnings and continue editing the data?]#")}<font>', function() {
				initEditZone();
			}, function() {
				$zone.dialog("close");
			});
		} else {
			initEditZone();
		}
		
		// 编辑区域有数据，才收起待选区域
		if ($('#${_zone}_text_list').val() != '[]' && $('#${_zone}_wait_list').length > 0) {
			// 收起待选区域
			var $waitSelectArea = $('h3.ui-accordion-header.ui-corner-top[tabindex="0"]');
			if ($waitSelectArea != "undefined" && $waitSelectArea.length > 0) {
				$waitSelectArea.last().click();
			}
		}
	});
</script>

<%-- 临时工作区 --%>
<div id="${_zone}_work_zone" style="display: none;"></div>

<textarea style="display: none;" id="${_zone}_text_list">${param.list}</textarea>
<textarea style="display: none;" id="${_zone}_text_params">${param._params}</textarea>

<%-- 错误提示区域 --%>
<div id="${_zone}_error_zone" name="errorZone"></div>

<%-- 联动js --%>
<wpf:javascript script="${config.jsScript}" type="${config.jsType}" form="${_zone}_edit_list" />

<%-- 外框 --%>
<div accordion="true" multi="true">

	<c:if test="${allowAdd}">
		<c:choose>
			<c:when test="${execType}">
				<div title="${wpf:lan('#:zh[待选数据]:en[Data to be selected]#')}">
					<%-- 待选查询 --%>
					<form zone="${_zone}_wait_list" action="${_acp}/waitList.shtml" query="true" id="${_zone}_wait_list_form" method="get">
						<textarea style="display: none;" name="widgetKey">${param.widgetKey}</textarea>
						<textarea style="display: none;" name="_params">${param._params}</textarea>
						<input type="hidden" value="${pageLimit}" name="_limit" />

						<%--查询条件 --%>
						<c:if test="${config.querys !=null && fn:length(config.querys)>0}">
							<table class="ws-table" col="2">
								<c:forEach items="${config.querys}" var="vo">
									<c:if test="${vo.name!=null&&vo.name!=''}">
										<c:set var="queryFormName" value="${vo.name}" />
									</c:if>
									<tr>
										<th>${wpf:lan(vo.busiName)}</th>
										<c:choose>
										       <c:when test="${vo.name!=null&&vo.name!=''}">
													<td><wcm:widget name="${queryFormName}" cmd="${vo.widget}" value="${vo.defVal}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}"></wcm:widget></td>
										       </c:when>
										       <c:otherwise>
													<td><wcm:widget name="querys.${vo.id}" cmd="${vo.widget}" value="${vo.defVal}" params="${vo.widgetParamScript!=null?(wpf:script(vo.widgetParamType,vo.widgetParamScript,null)):null}"></wcm:widget></td>
										       </c:otherwise>
										</c:choose>	
									</tr>
								</c:forEach>
								
								<tr whole="true">
									<th class="ws-bar">
										<div class="ws-group left">
											<button type="button" icon="arrowthick-2-n-s" name="expand">${wpf:lan("#:zh[展开/收缩查询框]:en[Expansion/contraction query box]#")}</button>
										    <button type="reset" icon="arrowreturnthick-1-w" text="true">${wpf:lan("#:zh[重置条件]:en[Reset query]#")}</button>
										</div>
										<div class="ws-group right">
											<button type="submit" icon="search" text="true">${wpf:lan("#:zh[查询]:en[Query]#")}</button>
										</div>
									</th>
								</tr>
							</table>
						</c:if>
					</form>

					<div id="${_zone}_wait_error_zone"></div>

					<%--查询结果 --%>
					<div id="${_zone}_wait_list" class="ws-scroll">
						<div class="ws-msg normal">${wpf:lan("#:zh[请输入查询条件并点击(查询)按钮.]:en[Please enter the query  and click on the (Query)button]#")}</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div title="${wpf:lan('#:zh[数据新增]:en[New data]#')}">
					<div class="ws-bar left">
						<button icon="plus" type="button" name="add">${wpf:lan("#:zh[新增数据]:en[New data]#")}</button>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
	</c:if>
	
	<div title="${wpf:lan('#:zh[数据编辑]:en[Data editing]#')}">
		<form action="${_acp}/saveEditZone.shtml" sync="true" name="editForm" method="post" onsubmit="return false;">
			<div id="${_zone}_edit_list"></div>
		</form>
	</div>

</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>