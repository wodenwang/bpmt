<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		//我的组织/我个人
		$('button[name=my]', $zone).click(function() {
			var $this = $(this);
			$('#${_zone}_data_group_form [name=groupKey]').val($this.val());
			$("#${_zone}_data_group_form").submit();
		});

		//传递函数
		Core.fn('${_zone}_data_main_zone', 'confirmFn', function($this, o) {
			Core.fn($zone, 'confirmFn')($zone, o);
		});

		//初始化
		{
			$('#${_zone}_data_group_form [name=groupKey]').val('$mygroup');
			$("#${_zone}_data_group_form").submit();
		}

	});
</script>

<div style="overflow: auto; zoom: 1;">
	<div style="float: left; width: 450px;" tabs="true">
		<c:if test="${queryFlag}">
			<div title="${wpf:lan('#:zh[普通查询]:en[General query]#')}">
				<form zone="${_zone}_data_main_zone" action="${_acp}/userList.shtml" id="${_zone}_data_main_form">
					<input type="hidden" name="_limit" value="10" /> <input type="hidden" name="value" value="${value}" /> <input type="hidden" name="checkType" value="${param.checkType}" />
					<textarea id="${_zone}_params" style="display: none;" name="_params">${param._params}</textarea>
					<table class="ws-table">
						<tr>
							<th>${wpf:lan("#:zh[用户]:en[User]#")} ID</th>
							<td><wcm:widget name="_sl_uid" cmd="text" /></td>
						</tr>
						<tr>
							<th>${wpf:lan("#:zh[用户名称]:en[User name]#")}</th>
							<td><wcm:widget name="_sl_busiName" cmd="text" /></td>
						</tr>
						<tr>
							<th class="ws-bar"><span class="left ws-group"> <c:if test="${myselfFlag}">
										<button type="button" icon="person" name="my" value="$myself">${wpf:lan("#:zh[我自己]:en[Myself]#")}</button>
									</c:if> <c:if test="${mygroupFlag}">
										<button type="button" icon="person" name="my" value="$mygroup">${wpf:lan("#:zh[我的组织]:en[My organization]#")}</button>
									</c:if>
							</span> <span class="right ws-group">
									<button type="reset" icon="arrowreturnthick-1-w">${wpf:lan("#:zh[重置]:en[Reset]#")}</button>
									<button type="submit" icon="search">${wpf:lan("#:zh[查询]:en[Query]#")}</button>
							</span></th>
						</tr>
					</table>
				</form>
			</div>
		</c:if>
		<c:if test="${groupFlag}">
			<script type="text/javascript">
				$(function() {
					var $zone = $("#${_zone}");
					var $tree = $("#${_zone}_tree", $zone);
					var treeSetting = {
						view : {
							fontCss : function(treeId, treeNode) {
								if (treeNode.font != undefined) {
									return treeNode.font;
								} else {
									return {};
								}
							}
						},
						data : {
							key : {
								name : "busiName"
							},
							simpleData : {
								enable : true,
								idKey : "groupKey",
								pIdKey : "parentKey"
							}
						},
						callback : {
							onClick : function(event, treeId, treeNode, clickFlagNumber) {
								var groupKey = treeNode.groupKey;
								if (groupKey == null) {
									return;
								}

								$('#${_zone}_data_group_form [name=groupKey]').val(groupKey);
								$("#${_zone}_data_group_form").submit();
							}
						}
					};

					var strData = $('textarea', $tree).html();
					var datas = eval("(" + strData + ")"); //增加icon
					$.each(datas, function(i, o) {
						o.icon = "${_cp}/css/icon/group.png";
					});

					//增加"我所在组织"
					if ('${mygroupFlag?1:0}' == '1') {
						var o = {};
						o.busiName = "${wpf:lan('#:zh[我的组织]:en[My organization]#')}[${group.busiName}]";
						o.icon = "${_cp}/css/icon/group.png";
						o.groupKey = "$mygroup";
						o.font = {
							color : 'blue',
							'font-weight' : 'bold'
						};
						datas.unshift(o);
					}

					//增加"我自己"
					if ('${myselfFlag?1:0}' == '1') {
						var o = {};
						o.busiName = "${wpf:lan('#:zh[我自己]:en[Myself]#')}[${user.busiName}]";
						o.icon = "${_cp}/css/icon/user.png";
						o.groupKey = "$myself";
						o.font = {
							color : 'blue',
							'font-weight' : 'bold'
						};
						datas.unshift(o);
					}
					var zTree = $.fn.zTree.init($tree, treeSetting, datas);
					$tree.addClass("ztree");
					zTree.expandAll(true);
				});
			</script>
			<div title="${wpf:lan('#:zh[组织查询]:en[Organization query]#')}">
				<ul id="${_zone}_tree">
					<textarea>${wcm:json(tree)}</textarea>
				</ul>
			</div>
		</c:if>
	</div>
	<form zone="${_zone}_data_main_zone" action="${_acp}/userGroupList.shtml" id="${_zone}_data_group_form">
		<input type="hidden" name="treeId" value="${_zone}_tree" /> <input type="hidden" name="value" value="${value}" /><input type="hidden" name="checkType" value="${param.checkType}" />
		<textarea style="display: none;" name="groupKey"></textarea>
		<textarea id="${_zone}_params" style="display: none;" name="_params">${param._params}</textarea>
		<input type="hidden" name="_limit" value="10" />
	</form>
	<div style="margin-left: 470px;">
		<div id="${_zone}_data_main_zone"></div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>