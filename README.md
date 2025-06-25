# object-helper-plugin

<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/15788-objecthelper" style="text-decoration:none"><img src="./src/main/resources/META-INF/pluginIcon.svg" width="128px"></a>
</div>
<br/>
<div align="center">
	<img src="https://img.shields.io/badge/version-v1.4.1-blue">
	<img src="https://img.shields.io/badge/license-Apache%202-red">
	<img src="https://img.shields.io/badge/size-3.95%20MB-yellowgreen">
	<img src="https://img.shields.io/badge/download-6.5k-green">
</div>

JetBrains Intellij ObjectHelper 插件致力于消除开发者重复性的低效工作，助力开发者将更多精力聚焦于核心业务逻辑开发，显著提升开发效率。

## 功能介绍

### 生成对象拷贝方法（Object Copy Method）

- **set 模式**：通过直观的操作流程，快速生成对象拷贝代码。默认使用快捷键 `Alt+Insert` 触发，若快捷键冲突，可在 `settings->keymap` 中搜索 “Generate” 关键字，查看并修改对应的快捷键设置。

![](https://image.bigcoder.cn/7fce876e-fa94-4780-bb14-584068c35963.gif)

- **builder 模式**：当对象中包含 builder 或者 newBuilder 方法时，插件会智能识别并采用 builder 模式生成代码，实现更优雅的对象构建。

![](https://image.bigcoder.cn/20240505142735.gif)

> **使用提示**：使用对象拷贝方法时，需先定义好方法签名，插件将自动生成对应的方法体。适用于对象属性较多且需频繁拷贝的场景，如数据传输对象（DTO）的转换等，但在一些对代码灵活性要求较高的场景中，可能需要额外调整。

### 对象拷贝（Object Copy）

支持在业务代码和 Lambda 表达式中直接进行对象拷贝操作，极大提升了代码编写的便利性和效率。无论是处理复杂业务逻辑，还是简化数据处理流程，都能轻松应对。
![](https://image.bigcoder.cn/20250614092815.gif)

### Java类转JSON

在开发过程中，若需要快速生成一个类的对象的 JSON 示例，只需右键单击目标类名，即可一键生成，方便接口调试、文档编写等场景下快速获取数据结构示例。

![](https://image.bigcoder.cn/20231224171155.gif)

### Java类转 Thrift IDL

对于涉及 Thrift 框架的开发场景，通过右键单击目标类名，可快速生成对应的 Thrift IDL 文件，加速服务接口的定义和开发流程。

![](https://image.bigcoder.cn/6eee7a02-8e4e-4f11-9b8c-81d661a920c5.gif)

### Java 类转 XML

![](https://image.bigcoder.cn/20231224171113.gif)

## 相关配置

### 插件配置

File->Settings->Tools->Object Helper 即可进入插件的配置页面

![](https://image.bigcoder.cn/202506140953046.png)

- `Class To Json/Thrift IDL/XML`：控制该功能是否启用。
- `generate field mode`：
  - `source`：以源字段类型的字段为基础生成对象拷贝代码。例如在 `Object Copy Method` 功能，方法第一个入参的类型就是源字段类型，方法返回的类型为目标类型字段。
  - `target`：以目标类为基础生成对象拷贝代码。
- `non-existent field generate annotation`：
  - `enable`：当目标字段在源对象中不存在时，以注释的形式生成代码。有助于开发者清晰了解字段缺失情况，方便后续代码维护和修改。

  - `disable`：不生成不存在字段的拷贝代码，适用于对代码简洁性要求较高，且能确保目标字段在源对象中都存在的场景。

> 假设存在两个 Java 类：`ClassA` 与 `ClassB`，`ClassA` 包含 `a`、`b`、`c` 三个字段，`ClassB` 包含 `b`、`c`、`d` 三个字段。当使用 ObjectHelper 插件将 `ClassA` 实例的数据拷贝至 `ClassB` 实例时：
>
> - **`generate field mode=source`**：插件以 `ClassA` 的字段 `a`、`b`、`c` 为基础生成拷贝代码。若 `ClassB` 中不存在对应字段（如 `a`），则根据 `non-existent field generate annotation` 配置决定是否处理该字段。
> - **`generate field mode=target`**：插件以 `ClassB` 的字段 `b`、`c`、`d` 为基准生成代码。若 `ClassA` 中无对应字段（如 `d`），同样由 `non-existent field generate annotation` 配置来决定是否生成相关代码或添加注释。

- `builder instance method`：用于插件判断当前类是否支持builder模式，配置的是一个正则表达式。如果你的builder类生成的方法名与插件默认生成的不同，可以在设置中更改：

### 快捷键配置

对象拷贝的快捷键默认是 `Alt+Insert`，如果该快捷键无效，可以在 `settings->keymap` 中搜索 “Generate” 关键字查看具体的快捷键：

![](https://image.bigcoder.cn/20220916173117.png)

## 未来功能支持计划

object-helper插件未来功能支持计划：

- [x] Class 转 IDL（Class To Thrift IDL）
- [x] Class 转 XML（Class To XML）
- [x] 个性化配置
- [x] Object Copy Method 功能支持 Builder 模式
- [x] Object Copy 功能支持 Lambda 表达式
- [ ] JSON 转 Class（JSON To Class）
- [ ] Class 转 Protobuf IDL（JSON To Protobuf）
- [ ] Class 转 Baiji（Class To Baiji）
- [ ] 菜单分组显示