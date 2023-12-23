# object-helper-plugin

<div align="center">
    <a href="https://plugins.jetbrains.com/plugin/15788-objecthelper" style="text-decoration:none"><img src="./src/main/resources/META-INF/pluginIcon.svg" width="128px"></a>
</div>
<br/>
<div align="center">
	<img src="https://img.shields.io/badge/version-v1.3.0-blue">
	<img src="https://img.shields.io/badge/license-Apache%202-red">
	<img src="https://img.shields.io/badge/size-600%20kB-yellowgreen">
	<img src="https://img.shields.io/badge/downloads-3.1k-green">
</div>

JetBrains Intellij IDEA ObjectHelper 插件旨在减少开发者重复低效的劳动，使开发者能够更专注于业务逻辑的开发。

该插件包含以下功能：

- 对象拷贝

  ![](https://image.bigcoder.cn/7fce876e-fa94-4780-bb14-584068c35963.gif)

  对象拷贝的快捷键默认是 `Alt+Insert`，如果该快捷键无效，可以在settings->keymap中搜索“Generate”关键字查看具体的快捷键：

  ![](https://image.bigcoder.cn/20220916173117.png)

- Java类转JSON

  ![](https://image.bigcoder.cn/20210227223302.gif)

- Java类转Thrift IDL

  ![](https://image.bigcoder.cn/6eee7a02-8e4e-4f11-9b8c-81d661a920c5.gif)

- Java类转XML

  ![](https://image.bigcoder.cn/20220916170144.gif)

- 插件配置

File->Settings->Tools->Object Helper 即可进入插件的配置页面

![](https://image.bigcoder.cn/20220916173227.png)

## 未来功能支持计划

object-helper插件未来功能支持计划：

- [x] Class转IDL（Class To Thrift IDL）
- [x] Class转XML（Class To XML）
- [x] 个性化配置
- [ ] JSON转Class（JSON TO Class）
- [ ] Class转Protobuf IDL（JSON TO Class）
- [ ] All Setter
- [ ] 菜单分组显示
