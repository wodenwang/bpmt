<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
	$(function() {
		var $zone = $('#${_zone}');

		//绑定提交事件
		$("#${_zone}_form").submit(function() {
			var $this = $(this);
			Core.fn($zone, 'submitCreate')($this);
			return false;
		});

		//人员表自动创建
		$(":checkbox[name=visitorTableFlag]", $zone).on('ifChanged', function(event) {
			var flag = $(this).prop('checked');
			if (flag) {//选中
				$("#${_zone}_visitorTable_zone1").show();
				$("#${_zone}_visitorTable_zone2").hide();

				$('input[name=visitorTable_text]', $zone).prop('disabled', false);
				$('select[name=visitorTable]', $zone).val('').prop('disabled', true).trigger("liszt:updated");
			} else {//没有选中
				$("#${_zone}_visitorTable_zone2").show();
				$("#${_zone}_visitorTable_zone1").hide();

				$('input[name=visitorTable_text]', $zone).prop('disabled', true);
				$('select[name=visitorTable]', $zone).val('').prop('disabled', false).trigger("liszt:updated");
			}
		});

		//人员分组表自动创建
		$(":checkbox[name=visitorTagTableFlag]", $zone).on('ifChanged', function(event) {
			var flag = $(this).prop('checked');
			if (flag) {//选中
				$("#${_zone}_visitorTagTable_zone1").show();
				$("#${_zone}_visitorTagTable_zone2").hide();

				$('input[name=visitorTagTable_text]', $zone).prop('disabled', false);
				$('select[name=visitorTagTable]', $zone).val('').prop('disabled', true).trigger("liszt:updated");
			} else {//没有选中
				$("#${_zone}_visitorTagTable_zone2").show();
				$("#${_zone}_visitorTagTable_zone1").hide();

				$('input[name=visitorTagTable_text]', $zone).prop('disabled', true);
				$('select[name=visitorTagTable]', $zone).val('').prop('disabled', false).trigger("liszt:updated");
			}
		});
	});
</script>

<div id="${_zone}_msg_zone" name="msgZone"></div>

<form action="${_acp}/submitCreate.shtml" sync="true" id="${_zone}_form">
	<div accordion="true" multi="true">
		<div title="基础信息">
			<table class="ws-table">
				<tr>
					<th>逻辑主键</th>
					<td><wcm:widget name="mpKey" cmd="key{required:true}" /></td>
				</tr>
				<tr>
					<th>AppId</th>
					<td><input name="appId" type="text" class="{required:true}" /></td>
				</tr>
				<tr>
					<th>AppSecret</th>
					<td><textarea name="appSecret" class="{required:true}"></textarea></td>
				</tr>
				<tr>
					<th>名称</th>
					<td><input name="title" type="text" class="{required:true}" /></td>
				</tr>
				<tr>
					<th>描述</th>
					<td><textarea name="description"></textarea></td>
				</tr>
			</table>
		</div>
		<div title="人员模型">
			<table class="ws-table">
				<tr>
					<th rowspan="2">绑定人员表</th>
					<td><input type="checkbox" value="1" name="visitorTableFlag" checked="checked" /><label>自动创建</label></td>
				</tr>
				<tr>
					<td><div id="${_zone}_visitorTable_zone1">
							<span style="margin-right: 5px; font-weight: bold;">创建表名:</span><input type="text" class="{required:true,maxlength:29,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}"
								name="visitorTable_text" />
						</div>

						<div id="${_zone}_visitorTable_zone2" style="display: none;">
							<span style="margin-right: 5px; font-weight: bold;">选择表:</span> <select name="visitorTable" class="chosen needValid {required:true}" disabled="disabled">
								<option value="">请选择</option>
								<c:forEach items="${visitorTables}" var="o">
									<option value="${o.name}">[${o.name}]${o.description}</option>
								</c:forEach>
							</select>
						</div></td>
				</tr>

				<tr>
					<th rowspan="2">绑定人员分组表</th>
					<td><input type="checkbox" value="1" name="visitorTagTableFlag" checked="checked" /><label>自动创建</label></td>
				</tr>
				<tr>
					<td><div id="${_zone}_visitorTagTable_zone1">
							<span style="margin-right: 5px; font-weight: bold;">创建表名:</span><input type="text" class="{required:true,maxlength:29,pattern2:['[A-Z]{2,5}_[A-Z0-9_]+','仅允许大写字符与下划线,必须指定域前缀,如RV_.']}"
								name="visitorTagTable_text" />
						</div>

						<div id="${_zone}_visitorTagTable_zone2" style="display: none;">
							<span style="margin-right: 5px; font-weight: bold;">选择表:</span> <select name="visitorTagTable" class="chosen needValid {required:true}" disabled="disabled">
								<option value="">请选择</option>
								<c:forEach items="${visitorTagTables}" var="o">
									<option value="${o.name}">[${o.name}]${o.description}</option>
								</c:forEach>
							</select>
						</div></td>
				</tr>
			</table>
		</div>
		<div title="回调设置">
			<table class="ws-table">
				<tr>
					<th>Token</th>
					<td><input name="token" type="text" class="{required:true}" /></td>
				</tr>
				<tr>
					<th>EncodingAESKey</th>
					<td><textarea name="encodingAESKey" class="{required:true}"></textarea></td>
				</tr>
			</table>
		</div>
		<div title="高级">
		    <table class="ws-table">
				<tr>
				    <th>AccessTokenUrl<font color="red" tip="true" title="用于自定义微信accessToken的获取地址,若留空为自动去微信官方处获取">(提示)</font></th>
				    <td><textarea name="accessTokenUrl" ></textarea></td>
				</tr>
		    </table>
		</div>
	</div>

	<div class="ws-bar">
		<button type="submit" icon="disk">保存</button>
	</div>
</form>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>