# FastSpigot介绍 / Introduction

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/cd503329e69a48439a0aa1d8cb115b0b)](https://www.codacy.com/gh/HyrKG/FastSpigot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HyrKG/FastSpigot&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/e83a46a7-aa0a-4dbd-ac26-a68ca734dfd4)](https://codebeat.co/projects/github-com-hyrkg-fastspigot-master)
![](https://img.shields.io/badge/minecraft-1.8.8+-blue.svg)
![](https://img.shields.io/badge/framework-IoC-red.svg)
![](https://img.shields.io/badge/language-Java-orange.svg)
![](https://img.shields.io/badge/license-GNU-purple.svg)

![](https://bstats.org/signatures/bukkit/fastspigot.svg)
![image](https://github.com/HyrKG/FastSpigot/blob/master/logo.png)

FastSpigot是一个帮助进行快速插件开发的IoC/AoP框架。基于@Inject注释所展开的快捷开发系统。

### 目前提供服务 Provided Services
- 快捷指令服务
- 快捷配置服务
- 快捷箱子界面（OOP）
- 快捷的Forge界面基于信道的对接（OOP）
- 简易的基于Mysql的锁
- 内置Redis和Mysql
- 各种各样的工具


> 该项目主要为学习用途，很多专业知识并不具备，若有错误请指出！
>

# 部分功能使用举例 / Before and After

#### 嵌套管理器注入 Inject Handler

```Java
public class ExampleFastPlugin extends FastPlugin {

    @Inject
    public YourHandler yourHandler;
}

class YourHandler{

    @Inject 
    public OtherHandler otherHandler;
    
    @OnHandlerInit
    void onInit(){}
    
    @OnHandlerLoad
    void onLoad() {}
    }
    
.....and so on
}
```

#### LOG服务使用 Easy Log
```Java
public class YourHandler implements ILogService {
    
    @OnHandlerLoad
    public void onLoad() {
        info("该处理器已被加载!");
    }
}
```

#### 指令服务使用 Easy Command
```Java
public class YourCommandHandler implements IFastCommandExecutor {
    @FastCommand(index = "test", desc = "测试指令")
    public void onTest(Player player, int number) {
        player.sendMessage("你输入了 " + number);
    }
}
```
#### 快捷配置读取 Easy Config
```Java
public class YourCommandHandler  extends FastPluginConfig {
    @AutoLoad
    public static boolean you_want_to_load;
}
```