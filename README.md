# FastSpigot介绍 / Introduction

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/cd503329e69a48439a0aa1d8cb115b0b)](https://www.codacy.com/gh/HyrKG/FastSpigot/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=HyrKG/FastSpigot&amp;utm_campaign=Badge_Grade)
[![codebeat badge](https://codebeat.co/badges/e83a46a7-aa0a-4dbd-ac26-a68ca734dfd4)](https://codebeat.co/projects/github-com-hyrkg-fastspigot-master)
![](https://img.shields.io/badge/minecraft-1.8.8+-blue.svg)
![](https://img.shields.io/badge/framework-IoC-red.svg)
![](https://img.shields.io/badge/language-Java-orange.svg)
![](https://img.shields.io/badge/license-GNU-purple.svg)

![](https://bstats.org/signatures/bukkit/fastspigot.svg)
![image](https://github.com/HyrKG/FastSpigot/blob/master/logo.png)

这是一个帮助进行快速插件开发的控制反转(IoC)框架。我将会使用ASM、注释和反射等方法，帮助你进行更加快速与简洁的开发。该项目主要具有一下特性：
- 强调模块化开发，便于框架构建。
- 方便解耦，简化开发。
- 使用接口实现服务，易于调用。
- 减少不必要的冗余代码。

> __中文文档:__ https://fastspigot.doc.hyrkg.cn/

> 该项目主要为学习用途，很多专业知识并不具备，若有错误请指出！
>

# 主要特性 / Main Feature

#### 控制反转(IoC)特性 / IoC feature

该项目是针对spigot实现的一个轻型的控制反转(IoC)框架，主要通过@Inject注释来进行对模块/处理器的依赖注入，从而进行解耦并且易于框架搭建。 其中FastInnerCore为该框架的IoC容器。

#### 依赖注入与动态注入 / Dependency Injection, Dynamic Injection

该项目利用反射实现了对@Inject所注释类的依赖注入。 同时利用ASM进行动态注入，使该框架中接口同样实现了服务提供。

#### 接口不只是接口，同样可以提供服务 / Interface can also provide service

在该框架中，接口已经脱离了接口的作用，而同样作为服务提供者。


# 前后对比 / Before and After

#### 管理器 Handler

```Java
//使用前 Before
public class ExampleOriginPlugin extends JavaPlugin {

    public YourHandler yourHandler;

    @Override
    public void onEnable() {
        yourHandler = new YourHandler();
        yourHandler.onInit();
    }
}
//##################################################################

//使用后 After
public class ExampleFastPlugin extends FastPlugin {

    @Inject
    public YourHandler yourHandler;
}

class YourHandler{

    /**
    * 在此进行初始化
    **/
    @OnHandlerInit
    void onInit();
}
```

#### 注册事件 Register Event

```Java
//使用前 Before
public class ExampleOriginPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new YourEventHandler(), this);
    }
}

//##################################################################

//使用后 After
public class ExampleFastPlugin extends FastPlugin {
    @Inject
    public YourEventHandler yourEventHandler;
}
```

#### 指令 Command

```Java
//使用前 Before
public class ExampleOriginPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("yourcommand").setExecutor(new YourCommandExecutor());
    }
}

//##################################################################

//使用后 After
public class ExampleFastPlugin extends FastPlugin {

    @Inject
    public YourFastCommandExecutor yourFastCommandExecutor;
}

class YourFastCommandExecutor implements IFastCommandExecutor {

    @FastCommand(index = "a", desc = "your command a")
    public void onA(CommandSender sender) {
        //TODO A
    }

    @Override
    public String[] getCommands() {
        return new String[]{"yourcommand"};
    }
}
```
