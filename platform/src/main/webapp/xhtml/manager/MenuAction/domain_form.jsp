<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//表单动作
		$('form', $zone).submit(function() {
			var $form = $(this);
			Ajax.form('${_zone}_msg', $form, {
				confirmMsg : '是否确认提交?',
				callback : function(flag) {
					if (flag) {
						Core.fn($zone, 'refresh')();
					}
				}
			});
			return false;
		});

		//写标签名
		$("[name$='.name']", $('#${_zone}_home_tab_zone')).blur(function() {
			var $this = $(this);
			var val = $(this).val();
			if (val != null && val != '') {
				$('a', $("li[aria-controls='" + $this.parents('div[tab]:first').attr('id') + "']", $('#${_zone}_home_tab_zone'))).html(val);
			}
		});

		//增加标签
		$('button[name=addHomeTag]', $zone).click(function() {
			var $tabs = $('#${_zone}_home_tab_zone');
			Ajax.tab($tabs, '${_acp}/domainHomeTab.shtml', {
				data : {
					domainKey : '${vo.domainKey}',
					columnIndex : Core.fn($zone, 'getMaxIndex')('columnIndex'),
					sort : Core.fn($zone, 'getMaxIndex')('sort') + 1,
					pixel : 'home.B' + Core.nextSeq()
				}
			});
		});

		//写标签
		Core.fn($zone, 'touchPosition', function() {
			$.each($('[name$=".columnIndex"]', $zone), function() {
				var $td = $(this).parents('td:first');
				var val = new Number($(this).val());
				$('span[name="columnIndex"]', $td).html(val + 1);
			});

			$.each($('[name$=".sort"]', $zone), function() {
				var $td = $(this).parents('td:first');
				var val = new Number($(this).val());
				$('span[name="sort"]', $td).html(val + 1);
			});
		});

		//获取最大columnIndex和sort
		Core.fn($zone, 'getMaxIndex', function(name) {
			var max = 0;
			$.each($("input[name$='." + name + "']", $zone), function() {
				var value = $(this).val();
				value = new Number(value);
				if (value > max) {
					max = value;
				}
			});
			return max;
		});

		//位置调整
		$('button[name=changePosition]', $zone).click(function() {
			var json = {};
			json.homes = [];

			//获取每个pixel
			$.each($('input[name=homes]', $zone), function() {
				var pixel = $(this).val();
				var o = {};
				o.pixel = pixel;
				o.columnIndex = $('[name="' + pixel + '.columnIndex"]', $zone).val();
				o.sort = $('[name="' + pixel + '.sort"]', $zone).val();
				o.name = $('[name="' + pixel + '.name"]', $zone).val();
				json.homes.push(o);
			});

			// 排序
			json.homes.sort(function(a, b) {
				return a.sort == b.sort ? 0 : +a.sort > +b.sort ? 1 : -1;
			});

			Ajax.win('${_acp}/changeHomePosition.shtml', {
				title : '位置调整',
				minWidth : 1024,
				data : {
					columns : $('input[name=columns]', $zone).val(),
					json : JSON.stringify(json)
				},
				buttons : [ {
					text : '关闭',
					click : function() {
						$(this).dialog("close");
					}
				}, {
					text : '保存位置',
					click : function() {
						var $this = $(this);
						var columnCount = new Number($('[name=columnCount]', $this).val());
						var columnArray = [];

						$.each($('div.ws-column', $this), function() {
							var $column = $(this);
							var columnIndex = new Number($column.attr('columnIndex'));
							var sort = 0;
							columnArray.push($column.attr("columnWidth"));
							$.each($('div[pixel]', $column), function() {
								var pixel = $(this).attr('pixel');
								$('[name="' + pixel + '.columnIndex"]', $zone).val(columnIndex);
								$('[name="' + pixel + '.sort"]', $zone).val(sort++);
							});
						});

						$('input[name=columns]', $zone).val(columnArray.join(';'));
						Core.fn($zone, 'touchPosition')();
						$this.dialog("close");
					}
				} ]
			});
		});
	});
</script>

<c:set var="isCreate" value="${vo==null}" />

<div id="${_zone}_msg"></div>

<div tabs="true">
	<div title="${isCreate?'新增域':'编辑域'}">
		<div id="${_zone}_result">
			<form action="${_acp}/submitDomainForm.shtml" method="post" sync="true">
				<input type="hidden" name="isCreate" value="${isCreate?1:0}" />
				<table class="ws-table">
					<tr>
						<th>域主键</th>
						<td><c:choose>
								<c:when test="${isCreate}">
									<wcm:widget name="domainKey" cmd="key[DOMAIN]{required:true}"></wcm:widget>
								</c:when>
								<c:otherwise>
									<span style="color: red; font-weight: bold;">${vo.domainKey}</span>
									<input name="domainKey" value="${vo.domainKey}" type="hidden" />
								</c:otherwise>
							</c:choose></td>
					</tr>
					<tr>
						<th>展示名</th>
						<td><wcm:widget name="busiName" cmd="textarea{required:true}" value="${vo.busiName}"></wcm:widget></td>
					</tr>
					<tr>
						<th>图标</th>
						<td><wcm:widget name="icon" cmd="icon" value="${vo.icon}"></wcm:widget></td>
					</tr>
					<tr>
						<th>描述</th>
						<td><wcm:widget name="description" cmd="textarea" value="${vo.description}"></wcm:widget></td>
					</tr>
					<tr>
						<th>功能点</th>
						<td><wcm:widget name="pri" cmd="pri{required:true}" value="${vo.pri}"></wcm:widget></td>
					</tr>
				</table>

				<div class="ws-bar">
					<button icon="disk" type="submit">保存</button>
				</div>
			</form>
		</div>
	</div>
	<c:if test="${!isCreate}">
		<div title="首页管理">
			<div class="ws-bar">
				<div class="left ws-group">
					<button icon="plus" type="button" name="addHomeTag">添加标签</button>
					<button icon="arrowthick-2-n-s" type="button" name="changePosition">位置调整</button>
				</div>
			</div>
			<form action="${_acp}/submitDomainHomeForm.shtml" method="post" sync="true">
				<input type="hidden" name="columns" value="${vo.columns}" /> <input type="hidden" name="domainKey" value="${vo.domainKey}" />
				<div tabs="true" id="${_zone}_home_tab_zone">
					<c:forEach items="${homes}" var="home" varStatus="state">
						<div title="${home.name}" close="${home.sysFlag==1?'false':'true'}">
							<c:set var="pixel" value="home.A${state.index}" />
							<input type="hidden" name="homes" value="${pixel}" />
							<table class="ws-table">
								<tr>
									<th>主键</th>
									<td><span style="color: red; font-weight: bold;">${home.id}</span> <input name="${pixel}.id" value="${home.id}" type="hidden" /></td>
								</tr>
								<tr>
									<th>所属子系统</th>
									<td><wcm:widget name="${pixel}.domainKey" cmd="select[$CmDomain(请选择);domainKey;busiName]{required:true}" value="${vo.domainKey}" state="readonly"></wcm:widget></td>
								</tr>
								<tr>
									<th>标签名</th>
									<td><input type="text" name="${pixel}.name" value="${home.name}" class="{required:true}" /></td>
								</tr>
								<tr>
									<th>位置</th>
									<td>第<span name="columnIndex" style="padding-left: 5px; padding-right: 5px; color: red; font-weight: bold;">${home.columnIndex+1}</span>列 第<span name="sort"
										style="padding-left: 5px; padding-right: 5px; color: blue; font-weight: bold;">${home.sort+1}</span>行 <input type="hidden" name="${pixel}.columnIndex" value="${home.columnIndex}" /> <input
										type="hidden" name="${pixel}.sort" value="${home.sort}" /></td>
								</tr>
								<tr>
									<th>高度</th>
									<td><select class="chosen" name="${pixel}.height">
											<option value="">自动适应</option>
											<c:forEach items="${'300,400,500,600,700,800,900'.split(',')}" var="v">
												<c:choose>
													<c:when test="${v==home.height}">
														<option value="${v}" selected="selected">${v}</option>
													</c:when>
													<c:otherwise>
														<option value="${v}">${v}</option>
													</c:otherwise>
												</c:choose>
											</c:forEach>
									</select></td>
								</tr>
								<c:choose>
									<c:when test="${home.sysFlag==0}">
										<tr>
											<th>链接视图</th>
											<td><wcm:widget name="${pixel}.action" cmd="view[HOME]{required:true}" value="${home.action}" /></td>
										</tr>
									</c:when>
									<c:otherwise>
										<tr>
											<th>目标地址</th>
											<td><span style="color: red;">(系统内置,无法修改)</span> <input type="hidden" name="${pixel}.action" value="${home.action}" /></td>
										</tr>
									</c:otherwise>
								</c:choose>
								<tr>
									<th>动态入参(脚本类型)</th>
									<td><wcm:widget name="${pixel}.paramType" cmd="select[@com.riversoft.platform.script.ScriptTypes]" value="${home.paramType}"></wcm:widget></td>
								</tr>
								<tr>
									<th>动态入参(脚本)<br /> <font color="red" tip="true" title="在视图模块中在request中通过[_params]命令字获取.">(提示)</font></th>
									<td><wcm:widget name="${pixel}.paramScript" cmd="codemirror[groovy]" value="${home.paramScript}"></wcm:widget></td>
								</tr>
								<tr>
									<th>功能点</th>
									<td><wcm:widget name="${pixel}.pri" cmd="pri{required:true}" value="${home.pri}"></wcm:widget></td>
								</tr>
							</table>
						</div>
					</c:forEach>
				</div>
				<div class="ws-bar">
					<button icon="disk" type="submit">保存</button>
				</div>
			</form>
		</div>
	</c:if>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>