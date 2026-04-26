#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import os
import re
import javalang
from javalang.ast import Node

# 收集所有ScriptSupport类的信息
def collect_script_support_classes():
    # 从grep结果中提取所有ScriptSupport类
    grep_result = """/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/store/StoreHelper.java:3:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/store/StoreHelper.java:14:@ScriptSupport("store")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/db/ORMHelper.java:13:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/db/ORMHelper.java:21:@ScriptSupport("orm")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/db/DbHelper.java:13:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/db/DbHelper.java:21:@ScriptSupport("db")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/mail/script/MailHelper.java:25:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/mail/script/MailHelper.java:43:@ScriptSupport(value = "mail", description = "邮件函数")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/web/CommonHelper.java:29:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/web/CommonHelper.java:51:@ScriptSupport("cm")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/web/FileHelper.java:21:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/web/FileHelper.java:29:@ScriptSupport("file")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/mq/MQHelper.java:8:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/mq/MQHelper.java:15:@ScriptSupport("mq")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/UserHelper.java:18:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/UserHelper.java:30:@ScriptSupport("user")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/LoggerHelper.java:11:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/LoggerHelper.java:18:@ScriptSupport("log")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/ImageHelper.java:25:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/ImageHelper.java:37:@ScriptSupport("img")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/Util.java:32:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/Util.java:41:@ScriptSupport("util")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/QRCodeHelper.java:8:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/QRCodeHelper.java:15:@ScriptSupport("qrcode")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/PdfHelper.java:5:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/PdfHelper.java:18:@ScriptSupport("pdf")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/QueueHelper.java:7:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/QueueHelper.java:19:@ScriptSupport("queue")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/ExcelHelper.java:24:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/ExcelHelper.java:37:@ScriptSupport("excel")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/SequenceHelper.java:25:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/platform/script/function/SequenceHelper.java:34:@ScriptSupport("seq")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/wx/WxHelper.java:11:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/wx/WxHelper.java:20:@ScriptSupport("wx")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/FormatterFunction.java:15:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/FormatterFunction.java:25:@ScriptSupport("fmt")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/JsonUtil.java:4:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/JsonUtil.java:15:@ScriptSupport("json")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/HttpUtil.java:4:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/HttpUtil.java:9:@ScriptSupport("http")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/XmlUtil.java:4:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/XmlUtil.java:14:@ScriptSupport("xml")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/MathUtil.java:12:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/core/script/function/MathUtil.java:20:@ScriptSupport("math")
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/ali/SMSHelper.java:6:import com.riversoft.core.script.annotation.ScriptSupport;
/Users/wenzhewang/workspace/trae/riversoft/trunk/platform/src/main/java/com/riversoft/ali/SMSHelper.java:19:@ScriptSupport("sms")
"""
    
    # 解析grep结果，提取类文件路径和ScriptSupport信息
    class_map = {}
    for line in grep_result.split('\n'):
        if not line:
            continue
        parts = line.split(':')
        if len(parts) < 3:
            continue
        file_path = parts[0]
        line_num = int(parts[1])
        content = ':'.join(parts[2:])
        
        if '@ScriptSupport' in content:
            # 提取ScriptSupport注解信息
            support_match = re.search(r'@ScriptSupport\(([^)]+)\)', content)
            if support_match:
                support_content = support_match.group(1)
                # 解析value和description
                value_match = re.search(r'value\s*=\s*["\']([^"\']+)["\']', support_content)
                desc_match = re.search(r'description\s*=\s*["\']([^"\']+)["\']', support_content)
                
                # 如果没有显式指定value，直接取字符串值
                if not value_match:
                    value_match = re.search(r'["\']([^"\']+)["\']', support_content)
                
                name = value_match.group(1) if value_match else ""
                description = desc_match.group(1) if desc_match else ""
                
                class_map[file_path] = {
                    'name': name,
                    'description': description
                }
    
    return class_map

# 读取Java文件内容
def read_java_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        return f.read()

# 解析Java类，提取公共静态方法
def parse_java_class(file_content):
    tree = javalang.parse.parse(file_content)
    methods = []
    
    # 提取类注释（跳过，因为javalang版本问题）
    class_comment = ""
    
    # 遍历所有类型声明
    for type_decl in tree.types:
        if isinstance(type_decl, javalang.tree.ClassDeclaration):
            # 遍历类的所有成员
            for member in type_decl.body:
                if isinstance(member, javalang.tree.MethodDeclaration):
                    # 检查是否是公共静态方法
                    is_public = 'public' in member.modifiers
                    is_static = 'static' in member.modifiers
                    if is_public and is_static:
                        # 提取方法注释
                        method_comment = member.documentation or ""
                        
                        # 提取方法参数
                        params = []
                        for param in member.parameters:
                            param_type = param.type.name if isinstance(param.type, javalang.tree.BasicType) else str(param.type)
                            params.append({
                                'name': param.name,
                                'type': param_type
                            })
                        
                        # 提取返回类型
                        return_type = member.return_type.name if isinstance(member.return_type, javalang.tree.BasicType) else str(member.return_type)
                        
                        methods.append({
                            'name': member.name,
                            'comment': method_comment,
                            'params': params,
                            'return_type': return_type,
                            'signature': f"{return_type} {member.name}({', '.join([f'{p['type']} {p['name']}' for p in params])})"
                        })
    
    return {
        'class_comment': class_comment,
        'methods': methods
    }

# 生成Markdown文档
def generate_markdown_doc(class_map):
    markdown = "# 自定义函数库API文档\n\n"
    markdown += "## 函数库列表\n\n"
    
    # 按函数库名称排序
    sorted_libraries = sorted(class_map.items(), key=lambda x: x[1]['name'])
    
    # 生成函数库列表
    for file_path, lib_info in sorted_libraries:
        markdown += f"- [{lib_info['name']}](#{lib_info['name']}) - {lib_info['description'] or '无描述'}\n"
    
    markdown += "\n---\n\n"
    
    # 生成每个函数库的详细文档
    for file_path, lib_info in sorted_libraries:
        markdown += f"## {lib_info['name']}\n\n"
        markdown += f"**描述**: {lib_info['description'] or '无描述'}\n\n"
        
        # 读取并解析Java文件
        file_content = read_java_file(file_path)
        class_info = parse_java_class(file_content)
        
        if class_info['class_comment']:
            markdown += f"**类注释**:\n\n```\n{class_info['class_comment']}\n```\n\n"
        
        markdown += "### 函数列表\n\n"
        
        if not class_info['methods']:
            markdown += "无公共静态方法\n\n"
            continue
        
        for method in class_info['methods']:
            markdown += f"#### {method['name']}\n\n"
            markdown += f"**签名**: `{method['signature']}`\n\n"
            
            if method['comment']:
                # 解析JavaDoc注释
                javadoc = method['comment']
                # 提取@param和@return
                param_comments = re.findall(r'@param\s+(\w+)\s+([^@]+)', javadoc)
                return_comment = re.search(r'@return\s+([^@]+)', javadoc)
                
                # 提取函数描述（@param和@return之前的内容）
                desc_match = re.match(r'\s*/\*\*\s*(.*?)\s*(?:@param|@return|\*/)', javadoc, re.DOTALL)
                if desc_match:
                    method_desc = desc_match.group(1).strip().replace('\n', ' ').replace('\r', '')
                    markdown += f"**描述**: {method_desc}\n\n"
                
                # 生成参数表格
                if param_comments or method['params']:
                    markdown += "| 参数名 | 类型 | 描述 |\n"
                    markdown += "|--------|------|------|\n"
                    
                    # 创建参数注释映射
                    param_comment_map = {}
                    for param_name, comment in param_comments:
                        param_comment_map[param_name] = comment.strip().replace('\n', ' ')
                    
                    for param in method['params']:
                        comment = param_comment_map.get(param['name'], '')
                        markdown += f"| {param['name']} | {param['type']} | {comment} |\n"
                    markdown += "\n"
                
                # 生成返回值描述
                if return_comment:
                    markdown += f"**返回值**: {return_comment.group(1).strip().replace('\n', ' ')}\n\n"
                else:
                    markdown += f"**返回值类型**: {method['return_type']}\n\n"
        
        markdown += "---\n\n"
    
    return markdown

# 主函数
def main():
    # 收集ScriptSupport类
    print("正在收集ScriptSupport类信息...")
    class_map = collect_script_support_classes()
    print(f"共收集到 {len(class_map)} 个ScriptSupport类")
    
    # 生成Markdown文档
    print("正在生成API文档...")
    markdown_doc = generate_markdown_doc(class_map)
    
    # 保存文档
    output_path = "/Users/wenzhewang/workspace/trae/riversoft/trunk/custom-function-api.md"
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(markdown_doc)
    
    print(f"API文档已生成: {output_path}")

if __name__ == "__main__":
    main()
