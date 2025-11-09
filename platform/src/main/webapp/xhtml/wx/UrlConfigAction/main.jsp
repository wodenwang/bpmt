<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
  $(function() {
    var $zone = $('#${_zone}');

    //列表页
    Core.fn('${_zone}_list', 'create', function() {
      var $tab = Ui.openTab('创建超链接', '${_acp}/createZone.shtml');
      Core.fn($tab, 'submitForm', Core.fn($zone, 'submitForm'));
    });

    Core.fn('${_zone}_list', 'del', function(key) {
      Ui.confirm('确认删除超链接?', function() {
        Ajax.post('${_zone}_msg', '${_acp}/delete.shtml?urlKey=' + key, {
          callback : function(flag) {
            if (flag) {
              $('#${_zone}_list_form').submit();
            }
          }
        });
      });
    });

    Core.fn('${_zone}_list', 'edit', function(key) {
      var $tab = Ui.openTab('编辑', '${_acp}/updateZone.shtml?urlKey=' + key);
      Core.fn($tab, 'submitForm', Core.fn($zone, 'submitForm'));
    });

    //表单页
    Core.fn($zone, 'submitForm', function($form, $tab, option) {
      option = $.extend({}, option, {
        callback : function(flag) {
          if (flag) {
            Ui.closeTab($tab);
            $('#${_zone}_list_form').submit();
          }
        }
      });
      Ajax.form('${_zone}_msg', $form, option);
    });

    //初始化查询
    $('#${_zone}_list_form').submit();
  });
</script>

<div tabs="true" max="10" id="${_zone}_tabs" main="true">
  <div title="超链接管理">
    <form zone="${_zone}_list" action="${_acp}/list.shtml" query="true" id="${_zone}_list_form" method="get">
      <input type="hidden" name="_field" value="updateDate" /> <input
            type="hidden" name="_dir" value="desc" />
      <table class="ws-table">
        <tr>
          <th>URL(模糊)</th>
          <td><wcm:widget name="_sl_urlKey" cmd="text">不支持命令</wcm:widget></td>
          <th>描述(模糊)</th>
          <td><input type="text" name="_sl_description" /></td>
        </tr>
        <tr>
          <th>平台类型</th>
          <td><wcm:widget name="_ne_wxType" cmd="select[@com.riversoft.platform.translate.WxType(请选择)]" /></td>
          <th>微信平台</th>
          <td><wcm:widget name="_sl_wxKey" cmd="select[$WxMp(请选择);mpKey;title;null;true]" /></td>
        </tr>
        <tr>
          <th>创建时间(&gt;=)</th>
          <td><wcm:widget name="_dnl_createDate" cmd="date" /></td>
          <th>创建时间(&lt;=)</th>
          <td><wcm:widget name="_dnm_createDate" cmd="date" /></td>
        </tr>
        <tr>
          <th>更新时间(&gt;=)</th>
          <td><wcm:widget name="_dnl_updateDate" cmd="date" /></td>
          <th>更新时间(&lt;=)</th>
          <td><wcm:widget name="_dnm_updateDate" cmd="date" /></td>
        </tr>
        <tr>
          <th class="ws-bar ">
            <div class="ws-group right">
              <button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
              <button type="submit" icon="search" text="true">查询</button>
            </div>
          </th>
        </tr>
      </table>
    </form>

    <%--错误提示区域 --%>
    <div id="${_zone}_msg"></div>

    <%--查询结果 --%>
    <div id="${_zone}_list"></div>

  </div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>