<#noparse>
<%@ page language="java" pageEncoding="UTF-8"%>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/common.jsp"%>
<%@ include file="/include/html_head.jsp"%>

<script type="text/javascript">
  /**
   * 提交查询
   */
  var query = function() {
    $('#${_zone}_list_form').submit();
  };

  /**
   * 提交删除
   */
  var delete = function(obj) {
    Core.confirm("确认删除所选项？", function() {
      $btn = $(obj);
      Ajax.post('${_zone}_msg',
          $('#${_zone}_delete_form').attr("action"), {
            callback : function(flag) {
              if (flag) {//调用成功
                query();
              }
            },
            btn : $btn,
            data : {
              _keys : $btn.val()
            }
          });
    });
  };

  /**
   * 展示
   */
  var show = function(obj) {
    $btn = $(obj);
    Ajax.tab($("#${_zone}_tabs"), '${_acp}/detail.shtml', {
      title : '查看记录',
      btn : $btn,
      data : {
        _key : $btn.val()
      }
    });
  };

  /**
   *编辑
   */
  var edit = function(obj) {
    $btn = $(obj);
    Ajax.tab($("#${_zone}_tabs"), '${_acp}/updateZone.shtml', {
      title : '编辑记录',
      btn : $btn,
      data : {
        _key : $btn.val()
      }
    });
  }

  /**
   * 提交删除
   */
  var deleteAll = function() {
    var $checkbox = $('#${_zone}_delete_form input:checked[name=_keys]');
    if ($checkbox.size() < 1) {
      Core.alert("请选择至少一项。");
      return;
    }

    Core.confirm("确认删除所选项？", function() {
      var form = $('#${_zone}_delete_form');
      var zone = '${_zone}_msg';
      //滚动到提示区域
      $.scrollTo($("#" + zone));
      Ajax.form(zone, form, {
        callback : function(flag) {
          if (flag) {//调用成功
            query();
          }
        },
        btn : $('button', form)
      });
    });
  };

  /**
   * 打开创建表单
   */
  var create = function() {
    Ajax.tab($("#${_zone}_tabs"), '${_acp}/createZone.shtml', {
      title : '新增记录'
    });
  };
  
  /**
   * 打开创建表单
   */
  var batch = function() {
    Ajax.tab($("#${_zone}_tabs"), '${_acp}/batchZone.shtml', {
      title : '批量处理'
    });
  };

  /**
   *关闭当前tab
   */
  var closeTab = function(tabid) {
    $('li[aria-controls=' + tabid + '] span', $('#${_zone}')).click();
  };
  
  /**
   * 新建tab
   */
  var openTab = function(title,url){
    Ajax.tab($("#${_zone}_tabs"), url, {
      title : title,
    });
  };

  /**
   * 表单提交
   */
  var submitForm = function(formid, tabid) {
    var form = $('#' + formid);
    var zone = '${_zone}_msg';//信息提示区域
    var option = eval('(' + form.attr("option") + ')');

    option = $.extend({}, {
      callback : function(flag) {
        if (flag) {//调用成功
          //切换到首标签页并刷新
          $('#${_zone}_tabs').tabs("option", "active", 0);
          query();
          //关闭tab
          closeTab(tabid);
        }
      },
      btn : $('button', form)
    }, option);
    $.scrollTo($("#" + zone));
    Ajax.form(zone, form, option);
  };
  
  /**
   * 下载批量文件
   */
  var downloadBath = function(type){
    var params = '';
    if('all'!=type){
      params = '?type='+type+'&'+  $('#${_zone}_list_form').serialize();
    }
    Ajax.jump("${_acp}/downloadBatch.shtml"+params);
  };

  $(function() {
    ${(config!=null && config.mainJs!=null) ? config.mainJs : ''}
    query();
  });
</script>

<div tabs="true" max="10" id="${_zone}_tabs">
  <div
    title="${config!=null&&config.busiName!=null&&config.busiName!=''?config.busiName:'查询'}">
    <form zone="${_zone}_list" action="${_acp}/list.shtml"
      id="${_zone}_list_form" method="get">
      <%--查询条件 --%>
      <c:if
        test="${config!=null && config.queryFields !=null && fn:length(config.queryFields)>0}">
        <table class="ws-table">
          <c:forEach varStatus="states" begin="0" step="2"
            end="${fn:length(config.queryFields)-1}">
            <tr>
              <c:forEach begin="0" end="1" varStatus="states2">
                <c:choose>
                  <c:when
                    test="${fn:length(config.queryFields)>(states.index + states2.index)}">
                    <c:set var="vo"
                      value="${config.queryFields[states.index + states2.index]}" />
                    <th>${vo.busiName}</th>
                    <td><wcm:widget name="${vo.name}" cmd="${vo.widgetCmd}"
                        value="${vo.defVal}">不支持命令</wcm:widget></td>
                  </c:when>
                  <c:otherwise>
                    <th></th>
                    <td></td>
                  </c:otherwise>
                </c:choose>
              </c:forEach>
            </tr>
          </c:forEach>
          <tr>
            <th class="ws-bar ">
              <div class="ws-group right">
                <button type="reset" icon="arrowreturnthick-1-w" text="true">重置查询</button>
                <button type="submit" icon="search" text="true">查询</button>
              </div>
            </th>
          </tr>
        </table>
      </c:if>
    </form>

    <%--错误提示区域 --%>
    <div id="${_zone}_msg"></div>

    <form action="${_acp}/delete.shtml" method="post"
      id="${_zone}_delete_form">
      <%--查询结果 --%>
      <div id="${_zone}_list"></div>
    </form>

  </div>
</div>

<%-- 每个模块页面必须引入 --%>
<%@ include file="/include/html_bottom.jsp"%>
</#noparse>