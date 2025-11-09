<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
    $(function () {
        var $zone = $('#${_zone}');

        Core.fn("${_zone}_tree_zone", 'cmd', function (name) {
            $('#${_zone}_cmd').show();
            $('input[name=filename]', ${_zone}_cmd).val(name);
            $('input[name=newfilename]', ${_zone}_cmd).val(name);
            var $name = $('input[name=filename]', $zone);
            if((name.indexOf('.7z') != -1) || (name.indexOf('.zip') != -1) || (name.indexOf('.rar') != -1)) {
                $('button[name=unzip]', ${_zone}_cmd).show();
            } else {
                $('button[name=unzip]', ${_zone}_cmd).hide();
            }
        });

        Core.fn("${_zone}_tree_zone", 'delete', function (id) {
            Ajax.post('${_zone}_msg', '${_acp}/fileDelete.shtml', {
                data: {
                    fileName: id
                },
                callback: function (flag) {
                    Ajax.post('${_zone}_tree_zone', '${_acp}/fileTree.shtml?zone=${_zone}');
                    $('input[name=filename]', ${_zone}_cmd).val('');
                    $('input[name=newfilename]', ${_zone}_cmd).val('');
                }
            });
        });

        Core.fn($zone, 'rename', function () {
            var $newname = $('input[name=newfilename]', $zone);
            var $name = $('input[name=filename]', $zone);
            if ($newname.val() == '' || $name.val() == '') {
                Ui.alert("请选择一个文件.");
                return;
            }
            Ui.confirm('确认重命名?', function () {

                Ajax.post('${_zone}_msg', '${_acp}/fileRename.shtml', {
                    data: {
                        fileName: $name.val(),
                        newFileName: $newname.val()
                    },
                    callback: function (flag) {
                        Ajax.post('${_zone}_tree_zone', '${_acp}/fileTree.shtml?zone=${_zone}');
                    }
                });
            });
        });

        Core.fn($zone, 'unzip', function () {
            Ui.confirm('确认解压缩?', function () {
                var $name = $('input[name=filename]', $zone);
                Ajax.post('${_zone}_msg', '${_acp}/fileUnzip.shtml', {
                    data: {
                        fileName: $name.val()
                    },
                    callback: function (flag) {
                        Ajax.post('${_zone}_tree_zone', '${_acp}/fileTree.shtml?zone=${_zone}');
                    }
                });
            });
        });

        Core.fn($zone, 'upload', function () {
            var $form = $('#${_zone}_upload_form');
            var zone = '${_zone}_msg';//信息提示区域
            var option = {
                confirmMsg: '确认提交？',
                callback: function (flag) {
                    if (flag) {//调用成功
                        $(':reset', $form).click();
                        Ajax.post('${_zone}_tree_zone', '${_acp}/fileTree.shtml?zone=${_zone}');
                    }
                }
            };
            var $file = $('input[type=file]', $form);
            $('input[name=fileName]').val($file.val());
            Ajax.form(zone, $form, option);
        });

        $('button[name=upload]', $zone).click(function () {
            Core.fn($zone, 'upload')();
        });

        $('button[name=rename]', $zone).click(function () {
            Core.fn($zone, 'rename')();
        });

        $('button[name=unzip]', $zone).click(function () {
            Core.fn($zone, 'unzip')();
        });

        Ajax.post('${_zone}_tree_zone', '${_acp}/fileTree.shtml?zone=${_zone}');

        $('#${_zone}_cmd').hide();
    });
</script>

<div style="position: relative;">
	<div style="position: absolute; float: left; width: 500px;">
		<div id="${_zone}_tree_zone"></div>
	</div>

	<div style=" margin-left: 510px;  min-height: 600px;">
		<div panel="文件上载">
			<%--表单 --%>
			<form action="${_acp}/fileUpload.shtml" method="post" id="${_zone}_upload_form">
				<input type="hidden" name="fileName" />
				<table class="ws-table">
					<tr>
						<th style="width: 80px;">文件</th>
						<td><input type="file" name="file" class="{required:true}" /></td>
					</tr>
					<tr>
						<th class="ws-bar center">
							<button type="button" icon="disk" text="true" name="upload">提交</button>
						</th>
					</tr>
				</table>
			</form>
		</div>
		<div id="${_zone}_msg"></div>
		<div panel="文件操作">
			<div id="${_zone}_cmd">
				<table class="ws-table">
					<tr>
						<th style="width: 80px;">文件名</th>
						<input type="hidden" name="filename" value="" />
						<td><input type="text" name="newfilename" value="" class="{required:true}" /></td>
					</tr>
					<tr>
						<th class="ws-bar center">
							<button type="button" icon="disk" text="true" name="rename">重命名</button>
							<button type="button" icon="folder-open" text="true" name="unzip">解压缩</button>
						</th>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>


<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>