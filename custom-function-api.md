# 自定义函数库API文档

## 函数库列表

- [cm](#cm) - 无描述
- [db](#db) - 无描述
- [excel](#excel) - 无描述
- [file](#file) - 无描述
- [fmt](#fmt) - 无描述
- [http](#http) - 无描述
- [img](#img) - 无描述
- [json](#json) - 无描述
- [log](#log) - 无描述
- [mail](#mail) - 邮件函数
- [math](#math) - 无描述
- [mq](#mq) - 无描述
- [orm](#orm) - 无描述
- [pdf](#pdf) - 无描述
- [qrcode](#qrcode) - 无描述
- [queue](#queue) - 无描述
- [seq](#seq) - 无描述
- [sms](#sms) - 无描述
- [store](#store) - 无描述
- [user](#user) - 无描述
- [util](#util) - 无描述
- [wx](#wx) - 无描述
- [xml](#xml) - 无描述

---

## cm

**描述**: 无描述

### 函数列表

#### map

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) map(ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) po, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) tableName)`

**描述**: * 从request获取表动态信息 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| po | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) | * |
| tableName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### map

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) map(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) tableName)`

**描述**: * 从request获取表动态信息 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| tableName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### invoke

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) invoke(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) key, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) args)`

**描述**: * 自定义函数调用 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| key | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| args | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### widget

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) widget(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) cmd, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value)`

**描述**: * 控件翻译的EL函数调用 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| cmd | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### widget

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) widget(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) cmd, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) params)`

**描述**: * 控件翻译的EL函数调用 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| cmd | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| params | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | *            动态参数,支持字符和对象 	 * |

**返回值**: */

#### view

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) view(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) viewKey, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) params)`

**描述**: * 调用视图做链接 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| viewKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| params | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### view

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) view(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) viewKey, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value)`

**描述**: * 调用视图做链接 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| viewKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### params

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) params()`

**描述**: * 获取动态参数 	 *  	 *

**返回值**: */

#### error

**签名**: `None error(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) msg)`

**描述**: * 主动抛异常 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### info

**签名**: `None info(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) msg)`

**描述**: * 主动抛出提示 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### warn

**签名**: `None warn(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) msg)`

**描述**: * 主动抛出警告 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### db

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) db(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) type, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) code)`

**描述**: * 字典翻译 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| type | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| code | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### db

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Code2NameVO, sub_type=None))], dimensions=[], name=List, sub_type=None) db(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) type)`

**描述**: * 获取字典列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| type | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### form

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) form(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) html, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) params)`

**描述**: * 解析form控件内容 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| html | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| params | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) | *            填空 	 * |

**返回值**: */

#### form

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) form(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) html)`

**描述**: * 解析form控件内容 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| html | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### lan

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) lan(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) str)`

**描述**: * 自适应语言 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| str | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)

#### xhtml

**签名**: `boolean xhtml()`

**描述**: * xhtml客户端(pc版) 	 *  	 *

**返回值**: */

#### h5

**签名**: `boolean h5()`

**描述**: * h5客户端(微信端) 	 *  	 *

**返回值**: */

#### title

**签名**: `None title(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) title)`

**描述**: * 设值标题 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| title | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### title

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) title()`

**描述**: * 获取标题 	 *  	 *

**返回值**: */

#### request

**签名**: `ReferenceType(arguments=None, dimensions=[], name=RequestContext, sub_type=None) request()`

**描述**: * 获取request 	 *  	 *

**返回值**: */

#### session

**签名**: `ReferenceType(arguments=None, dimensions=[], name=SessionContext, sub_type=None) session()`

**描述**: * 获取session 	 *  	 *

**返回值**: */

---

## db

**描述**: 无描述

### 函数列表

无公共静态方法

## excel

**描述**: 无描述

### 函数列表

#### parseSheetListWithEnd

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetListWithEnd(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) endChecker, int titleRow, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| endChecker | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| titleRow | int | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseSheetList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, int titleRow, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| titleRow | int | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseSheetList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, int titleRow)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| titleRow | int | * |

**返回值**: */

#### parseSheetList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseSheetList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### parseList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, int titleRow, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| titleRow | int | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, int titleRow)`

**描述**: * 解析文件-列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| titleRow | int | * |

**返回值**: */

#### parseTitleListWhithEnd

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseTitleListWhithEnd(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) endChecker, int titleRow)`

**描述**: * 解析文件-按表头生成列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| endChecker | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| titleRow | int | * |

**返回值**: */

#### parseTitleList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseTitleList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, int titleRow)`

**描述**: * 解析文件-按表头生成列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| titleRow | int | * |

**返回值**: */

#### parseTitleList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseTitleList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 解析文件-按表头生成列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### parseSheetTitleList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetTitleList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, int titleRow)`

**描述**: * 解析文件-按表头生成列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| titleRow | int | * |

**返回值**: */

#### parseSheetTitleList

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) parseSheetTitleList(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName)`

**描述**: * 解析文件-按表头生成列表 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseSheetMap

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None) parseSheetMap(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) sheetName, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-MAP 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| sheetName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### parseMap

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None) parseMap(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fields)`

**描述**: * 解析文件-MAP 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| fields | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### toFile

**签名**: `byte toFile(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) template, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) fileName, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) context)`

**描述**: * 生成文件 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| template | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| fileName | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| context | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) | * |

**返回值**: */

#### toFile

**签名**: `byte toFile(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) template, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) context)`

**描述**: * 生成文件 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| template | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| context | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) | * |

**返回值**: */

---

## file

**描述**: 无描述

### 函数列表

#### disk

**签名**: `byte disk(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 将文件转换成disk类型(存放在磁盘) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### db

**签名**: `byte db(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 将文件转换成db类型(存放在数据库) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### files

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UploadFile, sub_type=None))], dimensions=[], name=List, sub_type=None) files(byte file)`

**描述**: * 获取可以编程的文件(列表) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | byte | * |

**返回值**: */

#### file

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UploadFile, sub_type=None) file(byte file)`

**描述**: * 获取可以编程的文件 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | byte | * |

**返回值**: */

#### url

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) url(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 获取下载链接 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### base64

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) base64(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 生成base64字符 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### request

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UploadFile, sub_type=None))], dimensions=[], name=List, sub_type=None) request(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) name)`

**描述**: * 从request获取文件 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| name | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

---

## fmt

**描述**: 无描述

### 函数列表

#### formatDate

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatDate(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date)`

**描述**: * 格式化日期成字符串（yyyy-MM-dd） 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |

**返回值**: */

#### formatDatetime

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatDatetime(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date)`

**描述**: * 格式化日期成字符串（yyyy-MM-dd HH:mm:ss） 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |

**返回值**: */

#### formatDatetime

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatDatetime(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) pattern)`

**描述**: * 格式化日期成自定义格式字符串 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |
| pattern | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### formatChinesePrice

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatChinesePrice(ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) price)`

**描述**: * 数字格式化成大写 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| price | ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) | * |

**返回值**: */

#### formatPrice

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatPrice(ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) price, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) pattern)`

**描述**: * 格式化价格 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| price | ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) | * |
| pattern | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### formatPrice

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatPrice(ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) price)`

**描述**: * 格式化价格 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| price | ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) | * |

**返回值**: */

#### formatPercent

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatPercent(ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) num)`

**描述**: * 格式化百分比 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| num | ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) | * |

**返回值**: */

#### formatNumber

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatNumber(ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) num, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) pattern)`

**描述**: * 格式化数字 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| num | ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) | * |
| pattern | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### formatNumber

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatNumber(ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) num)`

**描述**: * 格式化数字 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| num | ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) | * |

**返回值**: */

#### formatPinyin

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatPinyin(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) chinese)`

**描述**: * 中文格式化成拼音(首字母) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| chinese | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### formatPinyinFull

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatPinyinFull(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) chinese)`

**描述**: * 中文转换成拼音(全拼) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| chinese | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### formatJson

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatJson(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) o)`

**描述**: * 对象转换成JSON 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| o | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### formatDuring

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) formatDuring(ReferenceType(arguments=None, dimensions=[], name=Long, sub_type=None) s)`

**描述**: * 将秒数格式化成天-小时-分-秒<br> 	 * 返回两段式即可,即:X天Y小时或X小时Y分钟 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| s | ReferenceType(arguments=None, dimensions=[], name=Long, sub_type=None) | * |

**返回值**: */

#### toJson

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) toJson(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value)`

**描述**: * 将字符串转换成JSON 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### toDate

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) toDate(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value)`

**描述**: * 转换成日期 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### toNumber

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Number, sub_type=None) toNumber(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) value)`

**描述**: * 转换成数字 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| value | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

---

## http

**描述**: 无描述

### 函数列表

#### given

**签名**: `ReferenceType(arguments=None, dimensions=[], name=RequestSpecification, sub_type=None) given()`

**描述**: * 初始化一个http client      *

**返回值**: RequestSpecification      *

---

## img

**描述**: 无描述

### 函数列表

#### img

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) img(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text)`

**描述**: * 根据文字生成彩色图片(base64字符流) 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| text | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### img

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) img(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) option)`

**描述**: * 根据配置生成图片(base64字符流) 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| text | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| option | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) | *            图片配置:<br> 	 *            width: 200px,默认值200<br> 	 *            height: 200px,默认值200<br> 	 *            color:#ff88ff,留空则根据文字内容算出固定颜色<br> 	 *            bg:true/false,默认true.false表示白色背景,color颜色的字体;true表示反转, 	 *            color背景白色字体 	 * |

**返回值**: */

#### head

**签名**: `byte head(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, int size)`

**描述**: * 根据图片生成头像 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| size | int | * |

**返回值**: */

#### base64

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) base64(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 根据图片生成base64图片 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### save

**签名**: `ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) save(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) base64)`

**描述**: * base64图片另存为文件 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| base64 | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### byte2file

**签名**: `ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) byte2file(byte bytes, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) type)`

**描述**: * 字节数组转文件 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| bytes | byte | * |
| type | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### byte2base64

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) byte2base64(byte bytes, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) type)`

**描述**: * 字节数组转base64 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| bytes | byte | * |
| type | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### rotate

**签名**: `byte rotate(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, double rotation)`

**描述**: * 图片翻转 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| rotation | double | * |

**返回值**: */

#### mark

**签名**: `byte mark(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) waterMark)`

**描述**: * 加水印， 默认0.5f透明程度 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| waterMark | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### mark

**签名**: `byte mark(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) waterMark, float transparent)`

**描述**: * 加水印 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| waterMark | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| transparent | float | *            透明程度 	 * |

**返回值**: */

#### resize

**签名**: `byte resize(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, int width, int height)`

**描述**: * 保持比例缩放 resize(file, 200, 300) 若图片横比200小，高比300小，不变 	 * 若图片横比200小，高比300大，高缩小到300，图片比例不变 若图片横比200大，高比300小，横缩小到200，图片比例不变 	 * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| width | int | *            宽 	 * |
| height | int | *            高 	 * |

**返回值**: */

#### resize

**签名**: `byte resize(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, double scale)`

**描述**: * 按比例缩放 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| scale | double | *            比例， 如0.5d 	 * |

**返回值**: */

#### forceResize

**签名**: `byte forceResize(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file, int width, int height)`

**描述**: * 强制按指定尺寸缩放 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |
| width | int | * |
| height | int | * |

**返回值**: */

#### merge

**签名**: `byte merge(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) files)`

**描述**: * 合并多个图片, 生成一个640*640的图片 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| files | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

#### merge

**签名**: `byte merge(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) files)`

**描述**: * 合并多个图片, 生成一个640*640的图片 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| files | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) |  |

**返回值**: */

---

## json

**描述**: 无描述

### 函数列表

#### to

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) to(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) o)`

**描述**: * 对象格式化为JSON字符串      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| o | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | 对象      * |

**返回值**: JSON字符串      */

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=JsonPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) json)`

**描述**: * 把字符串解析为JsonPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| json | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | JSON字符串      * |

**返回值**: JsonPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=JsonPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=URL, sub_type=None) url)`

**描述**: * 把URL指代的内容解析为JsonPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| url | ReferenceType(arguments=None, dimensions=[], name=URL, sub_type=None) | url      * |

**返回值**: JsonPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=JsonPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=InputStream, sub_type=None) stream)`

**描述**: * 把stream指代的内容解析为JsonPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| stream | ReferenceType(arguments=None, dimensions=[], name=InputStream, sub_type=None) | stream      * |

**返回值**: JsonPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=JsonPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) file)`

**描述**: * 把文件指代的内容解析为JsonPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) | file      * |

**返回值**: JsonPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=JsonPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=Reader, sub_type=None) reader)`

**描述**: * 把reader指代的内容解析为JsonPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| reader | ReferenceType(arguments=None, dimensions=[], name=Reader, sub_type=None) | reader      * |

**返回值**: JsonPath      *

---

## log

**描述**: 无描述

### 函数列表

#### debug

**签名**: `None debug(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) msg, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) args)`

**描述**: * 登记后台日志 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| args | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### info

**签名**: `None info(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) msg, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) args)`

**描述**: * 登记后台日志 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| args | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### warn

**签名**: `None warn(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) msg, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) args)`

**描述**: * 登记后台日志 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| args | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### error

**签名**: `None error(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) msg, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) args)`

**描述**: * 登记后台日志 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| args | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: None

#### print

**签名**: `None print(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) msg)`

**描述**: * 登记web日志 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### loop

**签名**: `None loop(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) msg, int max)`

**描述**: * 开始循环 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| msg | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| max | int | */ |

**返回值类型**: None

#### signal

**签名**: `None signal()`

**描述**: * 循环标记

**返回值类型**: None

---

## mail

**描述**: 邮件函数

### 函数列表

#### send

**签名**: `None send(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, byte attachment, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) toAddrs)`

**描述**: * 邮件发送 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| attachment | byte | * |
| toAddrs | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### send

**签名**: `None send(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) toAddrs)`

**描述**: * 邮件发送 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| toAddrs | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### systemSend

**签名**: `None systemSend(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) toAddrs)`

**描述**: * 系统邮件发送 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| toAddrs | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### systemSend

**签名**: `None systemSend(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, byte attachment, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) toAddrs)`

**描述**: * 系统邮件发送 	 * 	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| attachment | byte | * |
| toAddrs | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### systemSend

**签名**: `None systemSend(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) toAddrs, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) toCCs)`

**描述**: * 系统邮件发送 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| toAddrs | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) | * |
| toCCs | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) | */ |

**返回值类型**: None

#### systemSend

**签名**: `None systemSend(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, byte attachment, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) toAddrs, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) toCCs)`

**描述**: * 系统邮件发送 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| attachment | byte | * |
| toAddrs | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) | * |
| toCCs | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=List, sub_type=None) | */ |

**返回值类型**: None

#### asyncSystemSend

**签名**: `None asyncSystemSend(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, byte attachment, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) toAddrs)`

**描述**: * 系统邮件发送(异步) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| attachment | byte | * |
| toAddrs | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

#### asyncSystemSend

**签名**: `None asyncSystemSend(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) subject, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) content, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) toAddrs)`

**描述**: * 系统邮件发送(异步) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| subject | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| content | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| toAddrs | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | */ |

**返回值类型**: None

---

## math

**描述**: 无描述

### 函数列表

无公共静态方法

## mq

**描述**: 无描述

### 函数列表

无公共静态方法

## orm

**描述**: 无描述

### 函数列表

无公共静态方法

## pdf

**描述**: 无描述

### 函数列表

#### parse

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None) parse(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) file)`

**描述**: * 把pdf文件里面form抽取出来放到map里面      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | * |

**返回值**: */

---

## qrcode

**描述**: 无描述

### 函数列表

#### file

**签名**: `ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) file(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text)`

**描述**: * 默认的二维码 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| text | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### stream

**签名**: `ReferenceType(arguments=None, dimensions=[], name=ByteArrayOutputStream, sub_type=None) stream(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text)`

#### img

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) img(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text)`

#### file

**签名**: `ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) file(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, int width, int height)`

**描述**: * 可以设置长宽 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| text | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| width | int | * |
| height | int | * |

**返回值**: */

#### stream

**签名**: `ReferenceType(arguments=None, dimensions=[], name=ByteArrayOutputStream, sub_type=None) stream(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, int width, int height)`

#### img

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) img(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, int width, int height)`

#### file

**签名**: `ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) file(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, int width, int height, int onColor, int offColor)`

**描述**: * 可以设置长宽和颜色 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| text | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| width | int | * |
| height | int | * |
| onColor | int | * |
| offColor | int | * |

**返回值**: */

#### stream

**签名**: `ReferenceType(arguments=None, dimensions=[], name=ByteArrayOutputStream, sub_type=None) stream(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, int width, int height, int onColor, int offColor)`

#### img

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) img(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text, int width, int height, int onColor, int offColor)`

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=QRCode, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) text)`

**描述**: * 也可以使用原生QRCode的API 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| text | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

---

## queue

**描述**: 无描述

### 函数列表

#### add

**签名**: `None add(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) queue, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) element)`

**描述**: * 增加一个element到队列中      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| queue | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| element | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) | */ |

**返回值类型**: None

#### add

**签名**: `None add(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) queue, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) elements)`

**描述**: * 增加一批element到队列中      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| queue | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| elements | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None))], dimensions=[], name=List, sub_type=None) | */ |

**返回值类型**: None

---

## seq

**描述**: 无描述

### 函数列表

#### uuid

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) uuid()`

**描述**: * 获取uuid 	 *  	 *

**返回值**: */

#### next

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) next()`

**描述**: * 获取下一个唯一字符 	 *  	 *

**返回值**: */

#### pattern

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) pattern(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) code, ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) objs)`

**描述**: * 模式创建序列号.目前支持的模式:<br> 	 * 时间 {now}:yyyyMMdd<br> 	 * 序号{seq:u/l}:tableName,column,size<br> 	 * 表单传值{req}:NAME<br> 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |
| objs | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | *            命令对应入参 	 * |

**返回值**: */

#### randomNumber

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) randomNumber(int size)`

**描述**: * 生成随机数字 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| size | int | * |

**返回值**: */

#### randomWord

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) randomWord(int size)`

**描述**: * 生成随机字符 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| size | int | * |

**返回值**: */

#### random

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) random(int size)`

**描述**: * 生成随机字符+数字混合 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| size | int | * |

**返回值**: */

---

## sms

**描述**: 无描述

### 函数列表

#### send

**签名**: `None send(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) templateId, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) mobile, ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None) params)`

**描述**: * 发送模板短信给指定手机号      *      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| templateId | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | 模板ID      * |
| mobile | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | 手机号      * |
| params | ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Map, sub_type=None) | 模板参数      */ |

**返回值类型**: None

#### code

**签名**: `None code(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) mobile)`

**描述**: * 发送验证码      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| mobile | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | 手机号      */ |

**返回值类型**: None

#### verify

**签名**: `boolean verify(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) mobile, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) code)`

**描述**: * 校验验证码      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| mobile | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | 手机号      * |
| code | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | 用户输入的验证码      * |

**返回值**: */

---

## store

**描述**: 无描述

### 函数列表

无公共静态方法

## user

**描述**: 无描述

### 函数列表

#### getUser

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsUser, sub_type=None) getUser()`

**描述**: * 获取当前用户 	 *  	 *

**返回值**: */

#### getGroup

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) getGroup()`

**描述**: * 获取当前组织 	 *  	 *

**返回值**: */

#### getParentGroup

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) getParentGroup(ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) group)`

**描述**: * 获取父组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| group | ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) | * |

**返回值**: */

#### getGroup

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) getGroup(int level)`

**描述**: * 获取所属部门 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| level | int | *            上推层级 	 * |

**返回值**: */

#### getRole

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsRole, sub_type=None) getRole()`

**描述**: * 获取当前角色 	 *  	 *

**返回值**: */

#### getUid

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) getUid()`

**描述**: * 获取当前用户主键 	 *  	 *

**返回值**: */

#### getRoleKey

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) getRoleKey()`

**描述**: * 获取当前角色主键 	 *  	 *

**返回值**: */

#### getGroupKey

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) getGroupKey()`

**描述**: * 获取当前组织主键 	 *  	 *

**返回值**: */

#### listSubGroup

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listSubGroup(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) groupKey)`

**描述**: * 获取组织及其所有子组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| groupKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listSubGroupKey

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listSubGroupKey(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) groupKey)`

**描述**: * 获取所有组织主键 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| groupKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listUidByGroup

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listUidByGroup(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) groupKey)`

**描述**: * 获取组织下属所有员工ID 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| groupKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listUserByNameLike

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UsUser, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listUserByNameLike(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) name)`

**描述**: * 通过名字模糊查询用户 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| name | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listUserByName

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UsUser, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listUserByName(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) name)`

**描述**: * 通过名字获取用户 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| name | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listUserByGroup

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UsUser, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listUserByGroup(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) groupKey)`

**描述**: * 获取组织下属所有员工 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| groupKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listGroupByUser

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listGroupByUser(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) uid)`

**描述**: * 获取员工对应组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| uid | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### listRoleByUser

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=UsRole, sub_type=None))], dimensions=[], name=Collection, sub_type=None) listRoleByUser(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) uid)`

**描述**: * 获取员工对应角色 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| uid | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### getGroupByUser

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) getGroupByUser(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) uid)`

**描述**: * 获取员工对应组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| uid | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### getRoleByUser

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsRole, sub_type=None) getRoleByUser(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) uid)`

**描述**: * 获取员工对应组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| uid | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### checkSameGroup

**签名**: `boolean checkSameGroup(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) target, int level)`

**描述**: * 判断指定用户是否与当前用户同在一个组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| target | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | *            指定用户 	 * |
| level | int | *            上推层级 	 * |

**返回值**: */

#### checkSameGroup

**签名**: `boolean checkSameGroup(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) target)`

**描述**: * 判断指定用户是否与当前用户同在一个组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| target | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | *            指定用户 	 * |

**返回值**: */

#### findRole

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsRole, sub_type=None) findRole(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) roleKey)`

**描述**: * 翻译角色 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| roleKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### findGroup

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsGroup, sub_type=None) findGroup(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) groupKey)`

**描述**: * 翻译组织 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| groupKey | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### findUser

**签名**: `ReferenceType(arguments=None, dimensions=[], name=UsUser, sub_type=None) findUser(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) uid)`

**描述**: * 翻译用户 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| uid | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### checkAdmin

**签名**: `boolean checkAdmin()`

**描述**: * 校验当前登录用户是否管理员 	 *  	 *

**返回值**: */

#### lan

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) lan()`

**描述**: * 返回当前用户所在的语言环境 	 *  	 *

**返回值**: */

---

## util

**描述**: 无描述

### 函数列表

#### compareDate

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Long, sub_type=None) compareDate(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date1, ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date2)`

**描述**: * 计算两个时间之间的差值 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date1 | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |
| date2 | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |

**返回值**: */

#### compareDate

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Long, sub_type=None) compareDate(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date1, ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date2, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) pattern)`

**描述**: * 计算两个时间之间的差值 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date1 | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |
| date2 | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |
| pattern | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: */

#### calDate

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) calDate(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date, ReferenceType(arguments=None, dimensions=[], name=Integer, sub_type=None) offset)`

**描述**: * 日期加减计算(单位:天) 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | * |
| offset | ReferenceType(arguments=None, dimensions=[], name=Integer, sub_type=None) | * |

**返回值**: */

#### calDate

**签名**: `ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) calDate(ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) date, ReferenceType(arguments=None, dimensions=[], name=Integer, sub_type=None) offset, ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) pattern)`

**描述**: * 日期加减计算 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| date | ReferenceType(arguments=None, dimensions=[], name=Date, sub_type=None) | *            待计算时间 	 * |
| offset | ReferenceType(arguments=None, dimensions=[], name=Integer, sub_type=None) | *            正数为加,复数为减 	 * |
| pattern | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | *            单位,默认为天 	 * |

**返回值**: */

#### unzip

**签名**: `ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None) unzip(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) obj)`

**描述**: * 文件解压 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| obj | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) | */ |

**返回值类型**: ReferenceType(arguments=[TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None)), TypeArgument(pattern_type=None, type=ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None))], dimensions=[], name=Map, sub_type=None)

#### zip

**签名**: `ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) zip(ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) obj)`

**描述**: * 压缩附件 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| obj | ReferenceType(arguments=None, dimensions=[], name=Object, sub_type=None) |  |

**返回值**: */

#### urlEncode

**签名**: `ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) urlEncode(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) value)`

**描述**: * url encode 	 *  	 *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| value | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | * |

**返回值**: *

---

## wx

**描述**: 无描述

### 函数列表

无公共静态方法

## xml

**描述**: 无描述

### 函数列表

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=XmlPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) xml)`

**描述**: * 把xml文本解析为XmlPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| xml | ReferenceType(arguments=None, dimensions=[], name=String, sub_type=None) | xml      * |

**返回值**: XmlPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=XmlPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=InputStream, sub_type=None) stream)`

**描述**: * 把stream指代的文本解析为XmlPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| stream | ReferenceType(arguments=None, dimensions=[], name=InputStream, sub_type=None) | stream      * |

**返回值**: XmlPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=XmlPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=InputSource, sub_type=None) source)`

**描述**: * 把source指代的文本解析为XmlPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| source | ReferenceType(arguments=None, dimensions=[], name=InputSource, sub_type=None) | source      * |

**返回值**: XmlPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=XmlPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) file)`

**描述**: * 把file指代的文本解析为XmlPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| file | ReferenceType(arguments=None, dimensions=[], name=File, sub_type=None) | file      * |

**返回值**: XmlPath      *

#### from

**签名**: `ReferenceType(arguments=None, dimensions=[], name=XmlPath, sub_type=None) from(ReferenceType(arguments=None, dimensions=[], name=Reader, sub_type=None) reader)`

**描述**: * 把reader指代的文本解析为XmlPath      *

| 参数名 | 类型 | 描述 |
|--------|------|------|
| reader | ReferenceType(arguments=None, dimensions=[], name=Reader, sub_type=None) | reader      * |

**返回值**: XmlPath      *

---

