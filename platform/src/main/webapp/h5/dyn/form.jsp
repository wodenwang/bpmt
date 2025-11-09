<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/h5_head.jsp"%>

<wwx:jssdk url="${_full_url}" />

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

<script>
	$(function() {

		//增加loading
		window.onbeforeunload = function() {
			Wxui.showLoading();
		};

		//跳转
		$("#${_zone}_btn_home").on('click', function(event) {
			window.location.href = "${_acp}/list.shtml?_params=${wcm:urlEncode(param._params)}";
		});

		//表单验证
		Wxui.validateForm($('#${_zone}_form'));

		//异步提交
		$("#${_zone}_form").on("submit", function(e) {
			var successFn = function(pk) {
				Wxui.alert("保存成功.", function() {
					window.location.href = "${_acp}/detail.shtml?_key=" + encodeURIComponent(JSON.stringify(pk)) + "&_params=${wcm:urlEncode(param._params)}";
				});
			};
			Wxui.form($(this), successFn);
			return false;
		});

		//删除第一个分割线
		$('li.am-divider:first', $('ul.am-dropdown-content')).remove();
		if ($('header .am-header-right.am-header-nav ul.am-dropdown-content li').size() < 1) {
			$('header .am-header-right.am-header-nav').hide();
		}
	});
</script>

<div data-am-widget="gotop" class="am-gotop am-gotop-fixed">
	<a href="#top" title="回到顶部"> <span class="am-gotop-title">回到顶部</span> <i class="am-gotop-icon am-icon-chevron-up"></i>
	</a>
</div>

<%--顶部--%>
<header data-am-widget="header" class="am-header am-header-default">
	<div class="am-header-left am-header-nav">
		<a href="javascript:void(0);" class="" id="${_zone}_btn_home"><i class="am-header-icon am-icon-home"></i></a>
	</div>
	<h1 class="am-header-title">${title}<c:choose>
			<c:when test="${isCreate}">[新增]</c:when>
			<c:otherwise>[编辑]</c:otherwise>
		</c:choose>
	</h1>

	<c:if test="${!isCreate}">
		<script>
			$(function() {
				//删除
				$("#${_zone}_delete_btn").on('click', function(event) {
					Wxui.confirm('确认删除当前记录?', function() {
						Wxui.json("${_acp}/delete.shtml?_keys=${wcm:urlEncode(wcm:jsonKey(vo,config.keysArray))}&_params=${wcm:urlEncode(param._params)}", function(json) {
							Wxui.alert(json.msg, function() {
								window.location.href = "${_acp}/list.shtml?_params=${wcm:urlEncode(param._params)}";
							});
						});
					});
				});
			});
		</script>
		<div class="am-header-right am-header-nav " data-am-dropdown>
			<a href="###" class="am-dropdown-toggle"><i class="am-header-icon am-icon-bars"></i></a>
			<ul class="am-dropdown-content">

				<c:if test="${wpf:checkExt(config.detailBtn.pri,context)}">
					<li class="am-divider"></li>
					<li><a href="${_acp}/detail.shtml?_key=${wcm:urlEncode(wcm:jsonKey(vo,config.keysArray))}&_params=${wcm:urlEncode(param._params)}"><i
							class="am-icon-file-text-o am-icon-fw am-margin-right-xs"></i>${config.detailBtn.busiName}</a></li>
				</c:if>


				<c:if test="${wpf:checkExt(config.deleteBtn.pri,context)}">
					<li class="am-divider"></li>
					<li><a href="javascript:void(0);" id="${_zone}_delete_btn"><i class="am-icon-trash am-icon-fw am-margin-right-xs"></i>${config.deleteBtn.busiName}</a></li>
				</c:if>

				<!-- 子表 -->
				<c:if test="${fn:length(config.subs)>0}">
					<c:forEach items="${config.subs}" var="sub" varStatus="status">
						<!-- 非操作日志子表,同时有权限 -->
						<c:if test="${sub.$type$ != 'VwDynSubSys' && wpf:checkExt(sub.pri, context)}">
							<li class="am-divider"></li>
							<li><a href="${_cp}${sub.action}?_params=${wpf:script(sub.paramType,sub.paramScript,context)}"><i class="am-icon-chevron-circle-down am-icon-fw am-margin-right-xs"></i>${wpf:lan(sub.busiName)}</a></li>
						</c:if>
					</c:forEach>
				</c:if>

			</ul>
		</div>
	</c:if>
</header>

<%--表单 --%>
<form id="${_zone}_form" method="post" class="am-form" action="${_acp}/submit.shtml">
	<c:if test="${!isCreate}">
		<textarea name="_key" style="display: none;" id="${_zone}_key">${wcm:jsonKey(vo,config.keysArray)}</textarea>
	</c:if>
	<textarea style="display: none;" name="_params" id="${_zone}_params">${param._params}</textarea>

	<div class="am-panel-group" id="${_zone}_accordion">
		<c:forEach items="${config.h5FormList}" var="line" varStatus="status">
			<c:if test="${line.whole==null}">
				<%--分割线 --%>
				<c:if test="${line.name==null&&wpf:checkExt(line.pri,context)}">
					<div class="am-panel am-panel-secondary">
						<div class="am-panel-hd" data-am-collapse="{target: '#${_zone}_accordion_p${status.index}'}">
							<h4 class="am-panel-title">
								${line.busiName}
								<c:if test="${line.tipScript!=null&&line.tipScript!=''}">(${wpf:script(line.tipType,line.tipScript,context)})</c:if>
							</h4>
						</div>
						<div id="${_zone}_accordion_p${status.index}" class="am-panel-collapse am-collapse ${line.expandFlag==0?'':'am-in'}">
							<div class="am-panel-bd">
								<dl>
									<c:forEach items="${config.h5FormList}" var="field" varStatus="fieldStatus" begin="${line.form_begin}" end="${line.form_end}">
										<%-- 是否出现分割线 --%>
										<c:set var="hrFlag" value="${false}" />

										<c:choose>
											<c:when test="${field.name!=null&&field.showFlag!=null}">
												<%-- 固定字段 --%>
												<c:if test="${wpf:checkExt(field.pri,context)}">
													<%-- 自增长主键不需要表单 --%>
													<c:if test="${!isCreate||field.createPri!=null}">
														<c:set var="widgetStateFlag" value="${isCreate?wpf:check(field.createPri):wpf:checkExt(field.updatePri,context)}" />
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

														<c:set var="hrFlag" value="${true}" />
														<dt>${field.busiName}</dt>
														<dd>
															<wcm:widget name="${field.name}" cmd="${field.widget}" value="${widgetValue}" state="${widgetStateFlag?'normal':'readonly'}" actionMode="h5"
																params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}" />
														</dd>
													</c:if>
												</c:if>
											</c:when>
											<c:when test="${field.name!=null}">
												<%-- 表单字段 --%>
												<c:if test="${wpf:checkExt(field.pri,context)}">
													<c:set var="hrFlag" value="${true}" />
													<dt>${field.busiName}</dt>
													<dd>
														<wcm:widget name="${field.name}" cmd="${field.widget}" value="${wpf:script(field.contentType,field.contentScript,context)}" actionMode="h5"
															state="${wpf:checkExt(field.editPri,context)?'normal':'readonly'}" params="${field.widgetParamScript!=null?(wpf:script(field.widgetParamType,field.widgetParamScript,context)):null}" />
													</dd>
												</c:if>
											</c:when>
											<c:otherwise>
												<%-- 展示字段 --%>
												<c:if test="${wpf:checkExt(field.pri,context)}">
													<c:set var="hrFlag" value="${true}" />
													<dt>${field.busiName}</dt>
													<dd>
														<wpf:script script="${field.contentScript}" type="${field.contentType}" context="${context}" />
													</dd>
												</c:if>
											</c:otherwise>
										</c:choose>

										<%-- 分割线 --%>
										<c:if test="${hrFlag&&fieldStatus.index!=fieldStatus.end}">
											<hr data-am-widget="divider" style="" class="am-divider am-divider-dashed" />
										</c:if>

									</c:forEach>
								</dl>
							</div>
						</div>
					</div>
				</c:if>
			</c:if>
		</c:forEach>
	</div>

	<div class="am-container am-margin-top am-margin-bottom">
		<button type="submit" class="am-btn am-btn-success am-radius am-btn-block">保存</button>
	</div>
</form>

<%-- 客户端脚本，必须写在form表单下面，否则widget不会初始化 --%>
<wpf:javascript script="${config.table.formJsScript}" type="${config.table.formJsType}" context="${context}" form="${_zone}_form" actionMode="h5" />

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/h5_bottom.jsp"%>