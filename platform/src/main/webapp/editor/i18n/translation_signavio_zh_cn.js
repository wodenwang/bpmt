ORYX.I18N.PropertyWindow.dateFormat = "d/m/y";

ORYX.I18N.View.East = "属性";
ORYX.I18N.View.West = "建模元素";

ORYX.I18N.Oryx.title	= "Signavio";
ORYX.I18N.Oryx.pleaseWait = "请稍候.Signavio流程编辑器正在加载...";
ORYX.I18N.Edit.cutDesc = "剪切所选对象到剪贴板";
ORYX.I18N.Edit.copyDesc = "复制所选对象到剪贴板";
ORYX.I18N.Edit.pasteDesc = "粘贴剪贴板到画布";
ORYX.I18N.ERDFSupport.noCanvas = "XML 文档中不存在 Signavio流程编辑器 的 canvas 节点!";
ORYX.I18N.ERDFSupport.noSS = "Signavio 流程编辑器的 canvas 节点中没包含 stencil set 定义!";
ORYX.I18N.ERDFSupport.deprText = "不推荐导出eRDF, 因为在以后的Signavio 流程编辑器版本将不再支持eRDF. 如果可以的话, 请使用JSON格式导出model. 仍需要进行导出吗?";
ORYX.I18N.Save.pleaseWait = "请稍候<br/>正在保存...";

ORYX.I18N.Save.saveAs = "另存副本...";
ORYX.I18N.Save.saveAsDesc = "另存副本...";
ORYX.I18N.Save.saveAsTitle = "另存副本...";
ORYX.I18N.Save.savedAs = "已保存副本";
ORYX.I18N.Save.savedDescription = "流程图已被保存在";
ORYX.I18N.Save.notAuthorized = "当前你没有登录. 请在新窗口中进行<a href='/p/login' target='_blank'>登录</a> , 这样你就能保存当前图形.";
ORYX.I18N.Save.transAborted = "保存请求用时过长. 你可能需要更换一个更快的网络连接. 如果你使用无限网络, 请检查你无线连接的信号强度.";
ORYX.I18N.Save.noRights = "你没有足够权限来保存模型. 如果你还是有写进目标仓库的权限, 请检查<a href='/p/explorer' target='_blank'>Signavio 资源管理器</a>.";
ORYX.I18N.Save.comFailed = "与Signavio服务器通讯失败. 请检查你的网络连接. 如果问题依然存在, 请通过工具条上的信封符号联系 Signavio Support.";
ORYX.I18N.Save.failed = "保存图像时发生错误. 请重试. 如果问题依然存在, 请通过工具条上的信封符号联系 Signavio Support.";
ORYX.I18N.Save.exception = "保存图像时发生异常. 请重试. 如果问题依然存在, 请通过工具条上的信封符号联系 Signavio Support.";
ORYX.I18N.Save.retrieveData = "请稍候, 数据正在恢复.";

/** New Language Properties: 10.6.09*/
if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};
ORYX.I18N.ShapeMenuPlugin.morphMsg = "转换模型";
ORYX.I18N.ShapeMenuPlugin.morphWarningTitleMsg = "转换模型";
ORYX.I18N.ShapeMenuPlugin.morphWarningMsg = "存在一个不能包含于转换模型里的子模型.<br/>仍要继续转换吗?";

if (!Signavio) { var Signavio = {}; }
if (!Signavio.I18N) { Signavio.I18N = {}; }
if (!Signavio.I18N.Editor) { Signavio.I18N.Editor = {}; }

if (!Signavio.I18N.Editor.Linking) { Signavio.I18N.Editor.Linking = {}; }
Signavio.I18N.Editor.Linking.CreateDiagram = "创建新图形";
Signavio.I18N.Editor.Linking.UseDiagram = "使用已有图形";
Signavio.I18N.Editor.Linking.UseLink = "使用网页连接";
Signavio.I18N.Editor.Linking.Close = "关闭";
Signavio.I18N.Editor.Linking.Cancel = "取消";
Signavio.I18N.Editor.Linking.UseName = "采用图形名称";
Signavio.I18N.Editor.Linking.UseNameHint = "使用连接图形名称替换当前建模元素 ({type})名称.";
Signavio.I18N.Editor.Linking.CreateTitle = "建立连接";
Signavio.I18N.Editor.Linking.AlertSelectModel = "必须选择一个模型.";
Signavio.I18N.Editor.Linking.ButtonLink = "连接图形";
Signavio.I18N.Editor.Linking.LinkNoAccess = "你没有访问这个图形.";
Signavio.I18N.Editor.Linking.LinkUnavailable = "图形不可用.";
Signavio.I18N.Editor.Linking.RemoveLink = "移除连接";
Signavio.I18N.Editor.Linking.EditLink = "编辑连接";
Signavio.I18N.Editor.Linking.OpenLink = "打开";
Signavio.I18N.Editor.Linking.BrokenLink = "连接失效!";
Signavio.I18N.Editor.Linking.PreviewTitle = "预览";

if(!Signavio.I18N.Glossary_Support) { Signavio.I18N.Glossary_Support = {}; }
Signavio.I18N.Glossary_Support.renameEmpty = "没有字典属性";
Signavio.I18N.Glossary_Support.renameLoading = "正在查询...";

/** New Language Properties: 08.09.2009*/
if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = "主要属性";
ORYX.I18N.PropertyWindow.moreProps = "更多属性";

ORYX.I18N.PropertyWindow.btnOpen = "打开";
ORYX.I18N.PropertyWindow.btnRemove = "移除";
ORYX.I18N.PropertyWindow.btnEdit = "编辑";
ORYX.I18N.PropertyWindow.btnUp = "上移";
ORYX.I18N.PropertyWindow.btnDown = "下移";
ORYX.I18N.PropertyWindow.createNew = "新建";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = "主要属性";
ORYX.I18N.PropertyWindow.moreProps = "更多属性";
ORYX.I18N.PropertyWindow.characteristicNr = "成本 &amp; 资源分析";
ORYX.I18N.PropertyWindow.meta = "自定义属性";

if(!ORYX.I18N.PropertyWindow.Category){ORYX.I18N.PropertyWindow.Category = {};}
ORYX.I18N.PropertyWindow.Category.popular = "主要属性";
ORYX.I18N.PropertyWindow.Category.characteristicnr = "成本 &amp; 资源分析";
ORYX.I18N.PropertyWindow.Category.others = "更多属性";
ORYX.I18N.PropertyWindow.Category.meta = "自定义属性";

if(!ORYX.I18N.PropertyWindow.ListView) ORYX.I18N.PropertyWindow.ListView = {};
ORYX.I18N.PropertyWindow.ListView.title = "编辑: ";
ORYX.I18N.PropertyWindow.ListView.dataViewLabel = "已存在项.";
ORYX.I18N.PropertyWindow.ListView.dataViewEmptyText = "没有项列表.";
ORYX.I18N.PropertyWindow.ListView.addEntryLabel = "添加项";
ORYX.I18N.PropertyWindow.ListView.buttonAdd = "添加";
ORYX.I18N.PropertyWindow.ListView.save = "保存";
ORYX.I18N.PropertyWindow.ListView.cancel = "取消";

if(!Signavio.I18N.Buttons) Signavio.I18N.Buttons = {};
Signavio.I18N.Buttons.save		= "保存";
Signavio.I18N.Buttons.cancel 	= "取消";
Signavio.I18N.Buttons.remove	= "移除";

if(!Signavio.I18N.btn) {Signavio.I18N.btn = {};}
Signavio.I18N.btn.btnEdit = "编辑";
Signavio.I18N.btn.btnRemove = "移除";
Signavio.I18N.btn.moveUp = "上移";
Signavio.I18N.btn.moveDown = "下移";

if(!Signavio.I18N.field) {Signavio.I18N.field = {};}
Signavio.I18N.field.Url = "URL";
Signavio.I18N.field.UrlLabel = "Label";
