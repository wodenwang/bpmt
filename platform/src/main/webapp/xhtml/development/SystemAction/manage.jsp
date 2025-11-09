<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>
<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');
		var $pause = $('button[name=pause][value=1]', $zone);
		var $play = $('button[name=pause][value=0]', $zone);

		//刷新状态
		Core.fn($zone, 'refreshState', function(val) {
			if (val == '1') {//暂停
				$pause.button("option", "disabled", true);
				$play.button("option", "disabled", false);
				$('#${_zone}_pause_state', $zone).html('<span style="color:red;">已暂停</span>');
			} else {//运行中
				$pause.button("option", "disabled", false);
				$play.button("option", "disabled", true);
				$('#${_zone}_pause_state', $zone).html('<span style="color:green;">运行中</span>');
			}
		});

		//启/停系统
		$('button[name=pause]', $zone).click(function() {
			var val = $(this).val();
			var msg = '确认对系统进行[' + (val == '1' ? '暂停' : '恢复运行') + ']操作?';
			Ui.confirmPassword(msg, function() {
				Ajax.post('${_zone}_msg', '${_acp}/pausePlatform.shtml', {
					data : {
						pause : val
					},
					callback : function(flag) {
						if (flag) {
							Core.fn($zone, 'refreshState')(val);
						}
					}
				});
			});
		});

		//日志树
		var setting = {
			data : {
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "pname"
				}
			},
			callback : {
				onClick : function(event, treeId, treeNode, clickFlagNumber) {
					if (treeNode.leaf != 1) {//文件夹不触发
						return;
					}

					Ajax.post('${_zone}_log_zone', '${_acp}/showLog.shtml', {
						data : {
							fileName : treeNode.id
						}
					});
				}

			}
		};
		var $tree = $('#${_zone}_tree');
		var strData = $('textarea', $tree).html();
		var datas = eval("(" + strData + ")");
		var zTree = $.fn.zTree.init($tree, setting, datas);
		$tree.addClass("ztree");
		zTree.expandAll(true);

		Core.fn($zone, 'refreshState')('${pause?1:0}');

	});
</script>

<div tabs="true">
	<div title="系统概况">
		<div tabs="true" button="left">
			<div title="服务器信息">
				<table class="ws-table">
					<tr>
						<th>服务器架构</th>
						<td>${osArch}</td>
					</tr>
					<tr>
						<th>操作系统</th>
						<td>${osName}(版本:${osVersion})</td>
					</tr>
					<tr>
						<th>所在磁盘总容量</th>
						<td><span>${totalSpace}</span> GB</td>
					</tr>
					<tr>
						<th>可用磁盘容量</th>
						<td><span>${usableSpace}</span> GB</td>
					</tr>
					<tr>
						<th>Java SDK版本</th>
						<td>${jdk}</td>
					</tr>
					<tr>
						<th>数据库</th>
						<td>${db}</td>
					</tr>
				</table>
			</div>
			<div title="系统信息">
				<div id="${_zone}_msg"></div>
				<table class="ws-table">
					<tr>
						<th>当前运行状态</th>
						<td><span id="${_zone}_pause_state"></span> <span class="ws-group">
								<button name="pause" icon="pause" value="1" text="false" tip="true" title="暂停后除系统内置用户外,其他用户暂停登陆.">暂停系统</button>
								<button name="pause" icon="play" value="0" text="false">恢复运行</button>
						</span></td>
					</tr>
					<tr>
						<th>当前系统类型</th>
						<td>[${safeRoleType}]${safeRole.busiName}<span style="color: red; margin-left: 5px; cursor: help;" tip="true" title="${safeRole.description}">(提示)</span></td>
					</tr>
				</table>
			</div>
			<div title="系统日志">
				<div style="overflow: auto; zoom: 1;">
					<div style="float: left; width: 250px; height: 400px; overflow: auto;">
						<ul id="${_zone}_tree">
							<textarea>${wcm:json(logs)}</textarea>
						</ul>
					</div>
					<div id="${_zone}_log_zone" style="margin-left: 260px; min-height: 500px; overflow: auto; zoom: 1;"></div>
				</div>
			</div>
		</div>
	</div>
	<div title="界面设置" init="${_acp}/setPage.shtml"></div>
	<div title="微信设置" init="${_acp}/wxNetSetting.shtml"></div>
	<div title="邮箱设置" init="${_acp}/setMail.shtml"></div>
	<div title="文档设置" init="${_acp}/setOffice.shtml"></div>
	<div title="文件空间" init="${_acp}/setFileSpace.shtml"></div>
	<div title="版本信息">
		<div tabs="true" button="left">
			<div title="注册信息">
				<table class="ws-table">
					<tr>
						<th>当前版本</th>
						<td>${version}</td>
					</tr>
					<tr>
						<th>注册名</th>
						<td><c:choose>
								<c:when test="${identifier!=null&&identifier.register}">${identifier.name} (${wcm:widget('select[@com.riversoft.module.development.IdentifierLevel]',identifier.level)})</c:when>
								<c:otherwise>
									<span style="color: red;">系统未注册,部分系统功能将受到限制.</span>
								</c:otherwise>
							</c:choose></td>
					</tr>
				</table>
			</div>
			<div title="扩展组件">
				<table class="ws-table">
					<tr>
						<th>组件</th>
						<th>版本</th>
						<th>描述</th>
					</tr>
					<c:forEach items="${list}" var="vo">
						<tr>
							<td class="center">${vo.name}</td>
							<td class="center">${vo.version}</td>
							<td class="left">${vo.description}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
			<c:if test="${roleIsPro}">
				<div title="当前采用快照">
					<c:choose>
						<c:when test="${template.key!=null}">
							<table class="ws-table">
								<tr>
									<th>快照名称</th>
									<td>${template.name}</td>
								</tr>
								<tr>
									<th>唯一健</th>
									<td>${template.key}</td>
								</tr>
								<tr>
									<th>版本</th>
									<td>${template.version}</td>
								</tr>
								<tr>
									<th>部署时间</th>
									<td><f:formatDate value="${template.date}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
								</tr>
								<tr>
									<th>描述</th>
									<td>${template.description}</td>
								</tr>
							</table>
						</c:when>
						<c:otherwise>
							<div class="ws-msg info">未采用快照.</div>
						</c:otherwise>
					</c:choose>
				</div>
			</c:if>
		</div>
	</div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>