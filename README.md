# object-helper-plugin

<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/15788-objecthelper" style="text-decoration:none"><img src="./src/main/resources/META-INF/pluginIcon.svg" width="128px"></a>
</div>
<br/>
<div align="center">
	<img src="https://img.shields.io/badge/version-v1.3.3-blue">
	<img src="https://img.shields.io/badge/license-Apache%202-red">
	<img src="https://img.shields.io/badge/size-3.94%20MB-yellowgreen">
	<img src="https://img.shields.io/badge/download-4.8k-green">
</div>

JetBrains Intellij IDEA ObjectHelper 插件旨在减少开发者重复低效的劳动，使开发者能够更专注于业务逻辑的开发。

该插件包含以下功能：

- 对象拷贝
  set模式：

  ![](https://image.bigcoder.cn/7fce876e-fa94-4780-bb14-584068c35963.gif)

  对象拷贝的快捷键默认是 `Alt+Insert`，如果该快捷键无效，可以在settings->keymap中搜索“Generate”关键字查看具体的快捷键：

  ![](https://image.bigcoder.cn/20220916173117.png)

  当对象中包含`builder` 或者 `newBuilder`方法时，则插件默认会采用 builder 模式生成代码：

  ![](https://image.bigcoder.cn/20240505142735.gif)

  如果你的builder类生成的方法名与插件默认生成的不同，可以在设置中更改：

  ![](https://image.bigcoder.cn/20240505143027.png)

- Java类转JSON

  ![](https://image.bigcoder.cn/20231224171155.gif)

- Java类转Thrift IDL

  ![](https://image.bigcoder.cn/6eee7a02-8e4e-4f11-9b8c-81d661a920c5.gif)

- Java类转XML

  ![](https://image.bigcoder.cn/20231224171113.gif)

- 插件配置

File->Settings->Tools->Object Helper 即可进入插件的配置页面

![](https://image.bigcoder.cn/20231224170305.png)

- `generate field mode = target` 代表以方法返回类型的字段为基础生成对象拷贝；
  `generate field mode = source` 代表以方法入参类型的字段为基础生成对象拷贝。

- `non-existent field generate annotation = enable` 代表当目标字段在源对象中不存在时，是否以注释的形式生成代码，如果为 `disable`，则代表不生成这一个字段拷贝代码。

## 未来功能支持计划

object-helper插件未来功能支持计划：

- [x] Class 转 IDL（Class To Thrift IDL）
- [x] Class 转 XML（Class To XML）
- [x] 个性化配置
- [x] Object Copy Method 功能支持 Builder 模式
- [ ] Object Copy Method 功能支持 Lambda 表达式
- [ ] JSON 转 Class（JSON To Class）
- [ ] Class 转 Protobuf IDL（JSON To Class）
- [ ] All Setter
- [ ] 菜单分组显示
