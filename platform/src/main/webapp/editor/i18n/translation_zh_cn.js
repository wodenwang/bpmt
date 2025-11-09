/**
 * @author Huang xu
 * 
 * Contains all strings for the default language (zh-cn).
 * Version 1 - 2013/06/26
 */
if(!ORYX) var ORYX = {};

if(!ORYX.I18N) ORYX.I18N = {};

ORYX.I18N.Language = "zh_cn"; //Pattern <ISO language code>_<ISO country code> in lower case!

if(!ORYX.I18N.Oryx) ORYX.I18N.Oryx = {};

ORYX.I18N.Oryx.title		= "Oryx";
ORYX.I18N.Oryx.noBackendDefined	= "Caution! \nNo Backend defined.\n The requested model cannot be loaded. Try to load a configuration with a save plugin.";
ORYX.I18N.Oryx.pleaseWait 	= "Please wait while loading...";
ORYX.I18N.Oryx.notLoggedOn = "Not logged on";
ORYX.I18N.Oryx.editorOpenTimeout = "The editor does not seem to be started yet. Please check, whether you have a popup blocker enabled and disable it or allow popups for this site. We will never display any commercials on this site.";

if(!ORYX.I18N.AddDocker) ORYX.I18N.AddDocker = {};

ORYX.I18N.AddDocker.group = "辅助点";
ORYX.I18N.AddDocker.add = "添加辅助点";
ORYX.I18N.AddDocker.addDesc = "点击添加一个辅助点到连线上";
ORYX.I18N.AddDocker.del = "删除辅助点";
ORYX.I18N.AddDocker.delDesc = "删除一个辅助点";

if(!ORYX.I18N.Arrangement) ORYX.I18N.Arrangement = {};

ORYX.I18N.Arrangement.groupZ = "Z-顺序";
ORYX.I18N.Arrangement.btf = "置于顶层";
ORYX.I18N.Arrangement.btfDesc = "置于顶层";
ORYX.I18N.Arrangement.btb = "置于底层";
ORYX.I18N.Arrangement.btbDesc = "置于底层";
ORYX.I18N.Arrangement.bf = "置前";
ORYX.I18N.Arrangement.bfDesc = "置前";
ORYX.I18N.Arrangement.bb = "置后";
ORYX.I18N.Arrangement.bbDesc = "置后";
ORYX.I18N.Arrangement.groupA = "对齐";
ORYX.I18N.Arrangement.ab = "底部对齐";
ORYX.I18N.Arrangement.abDesc = "底部对齐";
ORYX.I18N.Arrangement.am = "水平对齐";
ORYX.I18N.Arrangement.amDesc = "水平对齐";
ORYX.I18N.Arrangement.at = "顶部对齐";
ORYX.I18N.Arrangement.atDesc = "顶部对齐";
ORYX.I18N.Arrangement.al = "左对齐";
ORYX.I18N.Arrangement.alDesc = "左对齐";
ORYX.I18N.Arrangement.ac = "垂直对齐";
ORYX.I18N.Arrangement.acDesc = "垂直对齐";
ORYX.I18N.Arrangement.ar = "右对齐";
ORYX.I18N.Arrangement.arDesc = "右对齐";
ORYX.I18N.Arrangement.as = "同等大小";
ORYX.I18N.Arrangement.asDesc = "同等大小";

if(!ORYX.I18N.Edit) ORYX.I18N.Edit = {};

ORYX.I18N.Edit.group = "编辑";
ORYX.I18N.Edit.cut = "剪切";
ORYX.I18N.Edit.cutDesc = "剪切所选对象到剪贴板";
ORYX.I18N.Edit.copy = "复制";
ORYX.I18N.Edit.copyDesc = "复制所选对象到剪贴板";
ORYX.I18N.Edit.paste = "粘帖";
ORYX.I18N.Edit.pasteDesc = "粘贴剪贴板到画布";
ORYX.I18N.Edit.del = "删除";
ORYX.I18N.Edit.delDesc = "删除所有已选图形";

if(!ORYX.I18N.EPCSupport) ORYX.I18N.EPCSupport = {};

ORYX.I18N.EPCSupport.group = "EPC";
ORYX.I18N.EPCSupport.exp = "导出 EPC";
ORYX.I18N.EPCSupport.expDesc = "导出图形到EPML文件";
ORYX.I18N.EPCSupport.imp = "导入 EPC";
ORYX.I18N.EPCSupport.impDesc = "导入EPML文件";
ORYX.I18N.EPCSupport.progressExp = "正在导出模型";
ORYX.I18N.EPCSupport.selectFile = " 选择一个EPML文件 (.empl) 进行导入.";
ORYX.I18N.EPCSupport.file = "文件";
ORYX.I18N.EPCSupport.impPanel = "导入 EPML 文件";
ORYX.I18N.EPCSupport.impBtn = "导入";
ORYX.I18N.EPCSupport.close = "关闭";
ORYX.I18N.EPCSupport.error = "错误";
ORYX.I18N.EPCSupport.progressImp = "导入中...";

if(!ORYX.I18N.ERDFSupport) ORYX.I18N.ERDFSupport = {};

ORYX.I18N.ERDFSupport.exp = "导出至 ERDF";
ORYX.I18N.ERDFSupport.expDesc = "导出至 ERDF";
ORYX.I18N.ERDFSupport.imp = "导入 ERDF";
ORYX.I18N.ERDFSupport.impDesc = "导入 ERDF";
ORYX.I18N.ERDFSupport.impFailed = "导入 ERDF 请求失败.";
ORYX.I18N.ERDFSupport.impFailed2 = "导入时发生错误! <br/>请检查错误信息: <br/><br/>";
ORYX.I18N.ERDFSupport.error = "错误";
ORYX.I18N.ERDFSupport.noCanvas = "XML 文档中不存在 Oryx 的 canvas 节点!";
ORYX.I18N.ERDFSupport.noSS = " Oryx 的 canvas 节点中没包含 stencil set 定义!";
ORYX.I18N.ERDFSupport.wrongSS = "指定的stencil set 不适合当前编辑器!";
ORYX.I18N.ERDFSupport.selectFile = "选择一个ERDF(.xml)文件 或 ERDF的类型进行导入!";
ORYX.I18N.ERDFSupport.file = "文件";
ORYX.I18N.ERDFSupport.impERDF = "导入 ERDF";
ORYX.I18N.ERDFSupport.impBtn = "导入";
ORYX.I18N.ERDFSupport.impProgress = "正在导入...";
ORYX.I18N.ERDFSupport.close = "关闭";
ORYX.I18N.ERDFSupport.deprTitle = "确定要导出 eRDF?";
ORYX.I18N.ERDFSupport.deprText = "不推荐导出eRDF, 因为在以后的Oryx editor 版本将不再支持eRDF. 如果可以的话, 请使用JSON格式导出model. 仍需要进行导出吗?";

if(!ORYX.I18N.jPDLSupport) ORYX.I18N.jPDLSupport = {};

ORYX.I18N.jPDLSupport.group = "执行BPMN";
ORYX.I18N.jPDLSupport.exp = "导出至 jPDL";
ORYX.I18N.jPDLSupport.expDesc = "导出至 jPDL";
ORYX.I18N.jPDLSupport.imp = "导入 jPDL";
ORYX.I18N.jPDLSupport.impDesc = "导入 jPDL 文件";
ORYX.I18N.jPDLSupport.impFailedReq = "导入 jPDL 请求失败.";
ORYX.I18N.jPDLSupport.impFailedJson = "转换 jPDL 失败.";
ORYX.I18N.jPDLSupport.impFailedJsonAbort = "导入失败.";
ORYX.I18N.jPDLSupport.loadSseQuestionTitle = "需要加载 jBPM stencil set  "; 
ORYX.I18N.jPDLSupport.loadSseQuestionBody = "为了执行导入 jPDL, 需要加载 stencil set 扩展. 你确定要继续执行吗?";
ORYX.I18N.jPDLSupport.expFailedReq = "导出模型请求失败.";
ORYX.I18N.jPDLSupport.expFailedXml = "导出 jPDL失败. 导出器记录: ";
ORYX.I18N.jPDLSupport.error = "错误";
ORYX.I18N.jPDLSupport.selectFile = "选择一个 jPDL (.xml) 文件 或者 jPDL 类型进行导入 !";
ORYX.I18N.jPDLSupport.file = "文件";
ORYX.I18N.jPDLSupport.impJPDL = "导入 jPDL";
ORYX.I18N.jPDLSupport.impBtn = "导入";
ORYX.I18N.jPDLSupport.impProgress = "正在导入...";
ORYX.I18N.jPDLSupport.close = "关闭";

if(!ORYX.I18N.Save) ORYX.I18N.Save = {};

ORYX.I18N.Save.group = "文件";
ORYX.I18N.Save.save = "保存";
ORYX.I18N.Save.saveDesc = "保存";
ORYX.I18N.Save.saveAs = "另存为...";
ORYX.I18N.Save.saveAsDesc = "另存为...";
ORYX.I18N.Save.unsavedData = "数据未保存, 请离开前保存好你的数据, 否则你的变更将会丢失!";
ORYX.I18N.Save.newProcess = "新流程";
ORYX.I18N.Save.saveAsTitle = "另存为...";
ORYX.I18N.Save.saveBtn = "保存";
ORYX.I18N.Save.close = "关闭";
ORYX.I18N.Save.savedAs = "另存为";
ORYX.I18N.Save.saved = "已保存!";
ORYX.I18N.Save.failed = "保存失败.";
ORYX.I18N.Save.noRights = "你没有权限进行保存.";
ORYX.I18N.Save.saving = "正在保存";
ORYX.I18N.Save.saveAsHint = "流程已保存在:";

if(!ORYX.I18N.File) ORYX.I18N.File = {};

ORYX.I18N.File.group = "文件";
ORYX.I18N.File.print = "打印";
ORYX.I18N.File.printDesc = "打印当前模型";
ORYX.I18N.File.pdf = "导出为PDF";
ORYX.I18N.File.pdfDesc = "导出为PDF";
ORYX.I18N.File.info = "信息";
ORYX.I18N.File.infoDesc = "信息";
ORYX.I18N.File.genPDF = "正在生成 PDF";
ORYX.I18N.File.genPDFFailed = "生成 PDF 失败.";
ORYX.I18N.File.printTitle = "打印";
ORYX.I18N.File.printMsg = "目前,我们在打印解决方案上遇到了问题. 我们推荐使用PDF导出功能来实现打印图像. 你确定还要继续打印吗?";

if(!ORYX.I18N.Grouping) ORYX.I18N.Grouping = {};

ORYX.I18N.Grouping.grouping = "分组";
ORYX.I18N.Grouping.group = "组";
ORYX.I18N.Grouping.groupDesc = "组合所选图形";
ORYX.I18N.Grouping.ungroup = "拆分";
ORYX.I18N.Grouping.ungroupDesc = "拆分所选图形";

if(!ORYX.I18N.Loading) ORYX.I18N.Loading = {};

ORYX.I18N.Loading.waiting ="请稍等...";

if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};

ORYX.I18N.PropertyWindow.name = "名称";
ORYX.I18N.PropertyWindow.value = "值";
ORYX.I18N.PropertyWindow.selected = "已选";
ORYX.I18N.PropertyWindow.clickIcon = "点击图标";
ORYX.I18N.PropertyWindow.add = "添加";
ORYX.I18N.PropertyWindow.rem = "移除";
ORYX.I18N.PropertyWindow.complex = "复杂类型编辑器";
ORYX.I18N.PropertyWindow.text = "文本编辑器";
ORYX.I18N.PropertyWindow.ok = "确定";
ORYX.I18N.PropertyWindow.cancel = "取消";
ORYX.I18N.PropertyWindow.dateFormat = "m/d/y";

if(!ORYX.I18N.ShapeMenuPlugin) ORYX.I18N.ShapeMenuPlugin = {};

ORYX.I18N.ShapeMenuPlugin.drag = "拖拽";
ORYX.I18N.ShapeMenuPlugin.clickDrag = "点击或拖拽";
ORYX.I18N.ShapeMenuPlugin.morphMsg = "变形形状";

if(!ORYX.I18N.SyntaxChecker) ORYX.I18N.SyntaxChecker = {};

ORYX.I18N.SyntaxChecker.group = "校验";
ORYX.I18N.SyntaxChecker.name = "语法校验器";
ORYX.I18N.SyntaxChecker.desc = "检查语法";
ORYX.I18N.SyntaxChecker.noErrors = "没有语法错误.";
ORYX.I18N.SyntaxChecker.invalid = "无效语法.";
ORYX.I18N.SyntaxChecker.checkingMessage = "检查中 ...";

if(!ORYX.I18N.Deployer) ORYX.I18N.Deployer = {};

ORYX.I18N.Deployer.group = "部署";
ORYX.I18N.Deployer.name = "部署器";
ORYX.I18N.Deployer.desc = "部署至引擎";

if(!ORYX.I18N.Undo) ORYX.I18N.Undo = {};

ORYX.I18N.Undo.group = "撤销";
ORYX.I18N.Undo.undo = "撤销";
ORYX.I18N.Undo.undoDesc = "撤销上一动作";
ORYX.I18N.Undo.redo = "恢复";
ORYX.I18N.Undo.redoDesc = "恢复上一未完成的动作";

if(!ORYX.I18N.View) ORYX.I18N.View = {};

ORYX.I18N.View.group = "缩放";
ORYX.I18N.View.zoomIn = "放大";
ORYX.I18N.View.zoomInDesc = "放大模型";
ORYX.I18N.View.zoomOut = "缩小";
ORYX.I18N.View.zoomOutDesc = "缩小模型";
ORYX.I18N.View.zoomStandard = "标准缩放";
ORYX.I18N.View.zoomStandardDesc = "标准大小";
ORYX.I18N.View.zoomFitToModel = "自适应缩放";
ORYX.I18N.View.zoomFitToModelDesc = "自适应缩放";

/** New Language Properties: 08.12.2008 */

ORYX.I18N.PropertyWindow.title = "属性";

if(!ORYX.I18N.ShapeRepository) ORYX.I18N.ShapeRepository = {};
ORYX.I18N.ShapeRepository.title = "图形仓库";

ORYX.I18N.Save.dialogDesciption = "请输入名称、描述以及备注.";
ORYX.I18N.Save.dialogLabelTitle = "标题";
ORYX.I18N.Save.dialogLabelDesc = "描述";
ORYX.I18N.Save.dialogLabelType = "类型";
ORYX.I18N.Save.dialogLabelComment = "修订备注";

Ext.MessageBox.buttonText.yes = "是";
Ext.MessageBox.buttonText.no = "否";
Ext.MessageBox.buttonText.cancel = "取消";
Ext.MessageBox.buttonText.ok = "确定";

if(!ORYX.I18N.Perspective) ORYX.I18N.Perspective = {};
ORYX.I18N.Perspective.no = "没有透视";
ORYX.I18N.Perspective.noTip = "未加载当前透视";

/** New Language Properties: 21.04.2009 */
ORYX.I18N.JSONSupport = {
    imp: {
        name: "导入JSON",
        desc: "导入JSON格式模型",
        group: "导出",
        selectFile: "选择一个 JSON (.json) 文件或者其他JSON类型进行导入!",
        file: "文件",
        btnImp: "导入",
        btnClose: "关闭",
        progress: "正在导入 ...",
        syntaxError: "语法错误"
    },
    exp: {
        name: "导出为JSON",
        desc: "导出当前模型为 JSON",
        group: "导出"
    }
};

/** New Language Properties: 09.05.2009 */
if(!ORYX.I18N.JSONImport) ORYX.I18N.JSONImport = {};

ORYX.I18N.JSONImport.title = "JSON 导入";
ORYX.I18N.JSONImport.wrongSS = "导入文件的 stencil set ({0}) 与加载的 stencil set ({1})不匹配.";

/** New Language Properties: 14.05.2009 */
if(!ORYX.I18N.RDFExport) ORYX.I18N.RDFExport = {};
ORYX.I18N.RDFExport.group = "导出";
ORYX.I18N.RDFExport.rdfExport = "导出到RDF";
ORYX.I18N.RDFExport.rdfExportDescription = "导出当前模型到一个Resource Description Framework (RDF) 序列化定义的XML";

/** New Language Properties: 15.05.2009*/
if(!ORYX.I18N.SyntaxChecker.BPMN) ORYX.I18N.SyntaxChecker.BPMN={};
ORYX.I18N.SyntaxChecker.BPMN_NO_SOURCE = "边线必须要有输入源.";
ORYX.I18N.SyntaxChecker.BPMN_NO_TARGET = "边线必须要有输出目标.";
ORYX.I18N.SyntaxChecker.BPMN_DIFFERENT_PROCESS = "源节点和目标节点必须在同一个流程当中.";
ORYX.I18N.SyntaxChecker.BPMN_SAME_PROCESS = "源节点和目标节点必须在不同的泳道上.";
ORYX.I18N.SyntaxChecker.BPMN_FLOWOBJECT_NOT_CONTAINED_IN_PROCESS = "流程中必须包含一个流对象.";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITHOUT_INCOMING_CONTROL_FLOW = "结束事件必须有一个输入顺序流.";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "开始事件必须有一个输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN_STARTEVENT_WITH_INCOMING_CONTROL_FLOW = "开始事件不能有输入顺序流.";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITH_INCOMING_CONTROL_FLOW = "附加的中间事件不能有输入顺序流.";
ORYX.I18N.SyntaxChecker.BPMN_ATTACHEDINTERMEDIATEEVENT_WITHOUT_OUTGOING_CONTROL_FLOW = "附加的中间事件必须有一个精确的输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN_ENDEVENT_WITH_OUTGOING_CONTROL_FLOW = "结束事件不能有输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN_EVENTBASEDGATEWAY_BADCONTINUATION = "网关或者子流程的后续不能接一个基于事件的网关.";
ORYX.I18N.SyntaxChecker.BPMN_NODE_NOT_ALLOWED = "非法的节点类型.";

if(!ORYX.I18N.SyntaxChecker.IBPMN) ORYX.I18N.SyntaxChecker.IBPMN={};
ORYX.I18N.SyntaxChecker.IBPMN_NO_ROLE_SET = "交互操作必须设置发送者和接收者角色.";
ORYX.I18N.SyntaxChecker.IBPMN_NO_INCOMING_SEQFLOW = "此节点必须有输入顺序流.";
ORYX.I18N.SyntaxChecker.IBPMN_NO_OUTGOING_SEQFLOW = "此节点必须有输出顺序流.";

if(!ORYX.I18N.SyntaxChecker.InteractionNet) ORYX.I18N.SyntaxChecker.InteractionNet={};
ORYX.I18N.SyntaxChecker.InteractionNet_SENDER_NOT_SET = "没有设置发送这";
ORYX.I18N.SyntaxChecker.InteractionNet_RECEIVER_NOT_SET = "没有设置接受者";
ORYX.I18N.SyntaxChecker.InteractionNet_MESSAGETYPE_NOT_SET = "没有设置消息类型";
ORYX.I18N.SyntaxChecker.InteractionNet_ROLE_NOT_SET = "没有设置角色";

if(!ORYX.I18N.SyntaxChecker.EPC) ORYX.I18N.SyntaxChecker.EPC={};
ORYX.I18N.SyntaxChecker.EPC_NO_SOURCE = "每条边线都必须有来源.";
ORYX.I18N.SyntaxChecker.EPC_NO_TARGET = "每天边线都必须有目标.";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED = "节点必须使用边线连接.";
ORYX.I18N.SyntaxChecker.EPC_NOT_CONNECTED_2 = "节点必须使用更多边线连接.";
ORYX.I18N.SyntaxChecker.EPC_TOO_MANY_EDGES = "节点连接太多边线.";
ORYX.I18N.SyntaxChecker.EPC_NO_CORRECT_CONNECTOR = "节点没有正确的接头.";
ORYX.I18N.SyntaxChecker.EPC_MANY_STARTS = "只能有一个开始事件.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_OR = "一个 OR/XOR 拆分 后面不能跟有方法.";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_OR = "一个 OR/XOR 拆分 后不能跟有流程接口.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_FUNCTION =  "方法后面不能跟方法.";
ORYX.I18N.SyntaxChecker.EPC_EVENT_AFTER_EVENT =  "事件后不能跟事件.";
ORYX.I18N.SyntaxChecker.EPC_PI_AFTER_FUNCTION =  "方法后不能跟流程接口.";
ORYX.I18N.SyntaxChecker.EPC_FUNCTION_AFTER_PI =  "流程接口后不能跟方法.";
ORYX.I18N.SyntaxChecker.EPC_SOURCE_EQUALS_TARGET = "边线必须连接两个不一样的节点.";

if(!ORYX.I18N.SyntaxChecker.PetriNet) ORYX.I18N.SyntaxChecker.PetriNet={};
ORYX.I18N.SyntaxChecker.PetriNet_NOT_BIPARTITE = "该图不是二分图";
ORYX.I18N.SyntaxChecker.PetriNet_NO_LABEL = "标记转换没有设置label";
ORYX.I18N.SyntaxChecker.PetriNet_NO_ID = "有节点没有设置id";
ORYX.I18N.SyntaxChecker.PetriNet_SAME_SOURCE_AND_TARGET = "有两条流关系拥有同样的来源和目标";
ORYX.I18N.SyntaxChecker.PetriNet_NODE_NOT_SET = "流关系没有设置节点.";

/** New Language Properties: 02.06.2009*/
ORYX.I18N.Edge = "边线";
ORYX.I18N.Node = "节点";

/** New Language Properties: 03.06.2009*/
ORYX.I18N.SyntaxChecker.notice = "将鼠标移动到红色交叉标志处查看更多错误信息.";

/** New Language Properties: 05.06.2009*/
if(!ORYX.I18N.RESIZE) ORYX.I18N.RESIZE = {};
ORYX.I18N.RESIZE.tipGrow = "增加画布尺寸:";
ORYX.I18N.RESIZE.tipShrink = "减少画布尺寸:";
ORYX.I18N.RESIZE.N = "上";
ORYX.I18N.RESIZE.W = "左";
ORYX.I18N.RESIZE.S ="下";
ORYX.I18N.RESIZE.E ="右";

/** New Language Properties: 15.07.2009*/
if(!ORYX.I18N.Layouting) ORYX.I18N.Layouting ={};
ORYX.I18N.Layouting.doing = "布局中...";

/** New Language Properties: 18.08.2009*/
ORYX.I18N.SyntaxChecker.MULT_ERRORS = "多个错误";

/** New Language Properties: 08.09.2009*/
if(!ORYX.I18N.PropertyWindow) ORYX.I18N.PropertyWindow = {};
ORYX.I18N.PropertyWindow.oftenUsed = "常用";
ORYX.I18N.PropertyWindow.moreProps = "更多属性";

/** New Language Properties 01.10.2009 */
if(!ORYX.I18N.SyntaxChecker.BPMN2) ORYX.I18N.SyntaxChecker.BPMN2 = {};

ORYX.I18N.SyntaxChecker.BPMN2_DATA_INPUT_WITH_INCOMING_DATA_ASSOCIATION = "数据输入必须没有任何传入的数据关联。";
ORYX.I18N.SyntaxChecker.BPMN2_DATA_OUTPUT_WITH_OUTGOING_DATA_ASSOCIATION = "数据输出必须没有任何输出的数据关联。";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_TARGET_WITH_TOO_MANY_INCOMING_SEQUENCE_FLOWS = "基于事件的网关目标只能有一个输入顺序流.";

/** New Language Properties 02.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_OUTGOING_SEQUENCE_FLOWS = "基于事件的网关必须有两个或两个以上的输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_EVENT_TARGET_CONTRADICTION = "如果信息中间事件被用于配置,那么接收任务不能被使用, 反之亦然.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_TRIGGER = "只有以下的中间事件触发器有效: Message, Signal, Timer, Conditional, Multiple.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WRONG_CONDITION_EXPRESSION = "事件网关的输出顺序流不能含有条件表达式.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_NOT_INSTANTIATING = "网关不能满足实例化过程的条件. 请使用一个启动事件或实例属性的网关.";

/** New Language Properties 05.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_MIXED_FAILURE = "网关必选含有多条输入输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_CONVERGING_FAILURE = "网关必选含有多条输入顺序流 ,但不能含有多条输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAYDIRECTION_DIVERGING_FAILURE = "网关不能含有多条输入顺序流, 但必须含有多条输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN2_GATEWAY_WITH_NO_OUTGOING_SEQUENCE_FLOW = "网关必须至少含有一条输出顺序流.";
ORYX.I18N.SyntaxChecker.BPMN2_RECEIVE_TASK_WITH_ATTACHED_EVENT = "用于事件网关配置的接收任务不能有任何附加中间事件.";
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_SUBPROCESS_BAD_CONNECTION = "事件子流程不能有任务输入输出顺序流.";

/** New Language Properties 13.10.2009 */
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_CONNECTED = "消息流至少要有一边是被连接.";

/** New Language Properties 24.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_MESSAGES = "一个编排活动只能有一个启动消息.";
ORYX.I18N.SyntaxChecker.BPMN_MESSAGE_FLOW_NOT_ALLOWED = "有一个不允许消息流.";

/** New Language Properties 27.11.2009 */
ORYX.I18N.SyntaxChecker.BPMN2_EVENT_BASED_WITH_TOO_LESS_INCOMING_SEQUENCE_FLOWS = "没有实例化的基于事件的网关必须至少含有一个输入顺序流.";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_FEW_INITIATING_PARTICIPANTS = "Choreography Activity 必须有实例化的参与者 (白色).";
ORYX.I18N.SyntaxChecker.BPMN2_TOO_MANY_INITIATING_PARTICIPANTS = "Choreography Acitivity 不能有超过1个的参与者 (白色).";

ORYX.I18N.SyntaxChecker.COMMUNICATION_AT_LEAST_TWO_PARTICIPANTS = "通讯至少连接两个参与者";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_START_MUST_BE_PARTICIPANT = "消息流的来源必须是一个参与者.";
ORYX.I18N.SyntaxChecker.MESSAGEFLOW_END_MUST_BE_PARTICIPANT = "消息流的目标必须是一个参与者.";
ORYX.I18N.SyntaxChecker.CONV_LINK_CANNOT_CONNECT_CONV_NODES = "会话连接必须连接一个通许或者一个带有参与者的子会话节点.";
