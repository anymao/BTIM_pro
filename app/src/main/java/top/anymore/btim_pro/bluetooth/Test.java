package top.anymore.btim_pro.bluetooth;

public class Test {
    public static String TEST = "博客\n" +
            "学院\n" +
            "下载\n" +
            "GitChat\n" +
            "论坛\n" +
            "问答\n" +
            "商城\n" +
            "VIP\n" +
            "活动\n" +
            "招聘\n" +
            "ITeye\n" +
            "码云\n" +
            "CSTO\n" +
            "\n" +
            "搜博主文章\n" +
            "写博客\n" +
            "发Chat\n" +
            "\n" +
            "AndroidStudio、gradle、buildToolsVersion关系\n" +
            "原创 2017年03月10日 14:04:09 标签：gradle 24949\n" +
            "使用AndroidStudio 开发也已经2年了，每次gradle 或者studio 有推荐更新后,项目重新sync后都会报错，提示更新相应的其他版本，比如AndroidStudio、gradle、buildToolsVersion版本；\n" +
            "\n" +
            "先来说说概念\n" +
            "AndroidStudio: 是Google官方基于IntelliJ IDEA开发的一款Android应用开发工具,，类似之前的eclipse,但是比eclipse 强大很多，推荐使用；\n" +
            "\n" +
            "Gradle：是一个工具，同时它也是一个编程框架。使用这个工具可以完成app的编译打包等工作；\n" +
            "\n" +
            "buildToolsVersion: android构建工具的版本，其中包括了打包工具aapt、dx等等。这个工具的目录位于..your_sdk_path/build-tools/XX.XX.XX，通过SDK Manager 更新； \n" +
            "comileSdkVersion：编译版本，就是运行我们这个项目的需要的android SDK版本；\n" +
            "\n" +
            "comileSdkVersion 和buildToolsVersion区别\n" +
            "新建一个项目，看build.gradle文件的配置，如下：\n" +
            "\n" +
            "build.gradle\n" +
            "\n" +
            "CompileSdkVersion:是告诉gradle 用哪个SDK版本来编译，和运行时要求的版本号没有关系；使用任何新添加的 API 就需要使用对应 Level 的 Android SDK。\n" +
            "\n" +
            "buildToolsVersion: android构建工具的版本,在SDK Manager中安装选择版本，buildToolsVersion的版本需要>=CompileSdkVersion; 高版本的build-tools 可以构建低版本编译的android程序；\n" +
            "\n" +
            "SDK Manager\n" +
            "\n" +
            "gradle版本和com.android.tools.build:gradle配置的版本关系\n" +
            "之前会奇怪，为什么一更新gradle 插件版本，会报错 \n" +
            "如下图，红色部分配置的就是android gradle 插件的版本，gradle插件的版本号和gradle版本号是对应的，较新的插件版本需要要求较新版的gradle，所以提示你更新gradle； \n" +
            "build.gradle\n" +
            "\n" +
            "版权声明：本文为博主原创文章，未经博主允许不得转载。 https://blog.csdn.net/lixin88/article/details/61196274\n" +
            "   \n" +
            "写下你的评论…\n" +
            " u013795543\n" +
            "u0137955432017-08-05 14:29#1楼 1条回复 回复\n" +
            "buildToolsVersion的版本需要&gt;=CompileSdkVersion\n" +
            "不对吧，我使用compileSdkVersion 23\n" +
            "buildToolsVersion &#39;22.0.1&#39;这样的配置也可以正常的安装apk呀\n" +
            "\n" +
            "个人理解CompileSdkVersion与编译出来的apk关系不大，但是buildToolsVersion与apk关系就大了，高版本buildToolsVersion编译出的apk根本不能安装在低版本的android手机中\n" +
            "如何查看buildToolsVersion的版本 hlllmr1314hlllmr13142016年08月16日 13:2112399\n" +
            "打开build.gradle 你会发现如下配置： android {     compileSdkVersion 23     buildToolsVersion \"23.0.1\" }...\n" +
            "AndroidStudio本地化配置gradle的buildToolsVersion和gradleBuildTools\n" +
            "gradle.properties有两个非常有用的属性： 1. **在Android项目中的任何一个build.gradle文件中都可以把gradle.properties中的常量读取出来，不管这...\n" +
            "guiying712guiying7122017年05月22日 20:478610\n" +
            "AndroidStudio 更新到BuildTool 25.0.0遇到的问题的解决\n" +
            "最近AS更新到25.0.0遇到问题，Android2.3一运行就崩溃， 但是在〉=4.0上就没问题。后来借助谷歌，必须用谷歌啊。发现是25.0.0Support的库存在问题，想办法更新到25.0.1后...\n" +
            "sunslifesunslife2016年11月23日 17:527610\n" +
            "Android关于buildToolVersion与CompileSdkVersion的区别\n" +
            "StackOverFlow中对这个问题进行了详细的讨论：http://stackoverflow.com/questions/24521017/android-gradle-buildtoolsver...\n" +
            "showbaba3showbaba32015年07月31日 11:2332127\n" +
            "如何查看buildToolsVersion的版本 xiaoshitou_2015xiaoshitou_20152017年03月12日 13:153086\n" +
            "打开build.gradle 你会发现如下配置: android {     compileSdkVersion 23     buildToolsVersion \"23.0.1\" }那么问...\n" +
            "Could not find com.android.tools.build:gradle:3.0.0.\n" +
            "android studio升级3.0，gradle升级4.1以后项目报错，如下 Could not resolve all files for configuration ‘:classpath...\n" +
            "zhouxianling233zhouxianling2332017年10月26日 21:5313931\n" +
            "Android关于buildToolVersion与CompileSdkVersion的区别\n" +
            "1、CompileSdkVersion是你SDK的版本号，也就是API Level，例如API-19、API-20、API-21等等。 2、buildeToolVersion是你构建工具的版本，...\n" +
            "sinat_19917631sinat_199176312016年10月31日 15:361503\n" +
            "更新Android Studio版本出现Could not find com.android.tools.build:gradle:2.2.3\n" +
            "如题，因为前阵子google更新了studio版本，像我这种喜欢作的人懒了一段时间还是去更新了，(其实是因为项目打包出来的apk太大了，刚好studio2.3版本支持转换WebP我不得已才更新=。=）...\n" +
            "u012911704u0129117042017年03月16日 22:5616788\n" +
            "'com.android.tools.build:gradle:2.3.0' 版本报错解决Plugin used. Try disabling Instant Run (or updating. ..\n" +
            "今天下载路由框架源码进行阅读的时候，gradle 报错了，“Plugin used. Try disabling Instant Run (or updating either the IDE or ...\n" +
            "qq_35366908qq_353669082017年04月10日 10:09817\n" +
            "Android Studio Error:Could not find com.android.tools.build:gradle\n" +
            "今天更新Android Studio后打开Project，报如下错误： Error:Could not find com.android.tools.build:gradle:2.2.1. Sea...\n" +
            "chy555chychy555chy2016年10月11日 17:0520260\n" +
            "AndroidStudio 报错:Could not find com.android.tools.build:gradle\n" +
            "【AndroidStudio 报错:Could not find com.android.tools.build:gradle】 报错 Error: Could not find com.an...\n" +
            "Rtia33Rtia332018年03月21日 21:0221\n" +
            "Android Gradle 构建工具(Android Gradle Build Tools)是什么？\n" +
            "转载地址:http://mrfu.me/android/2015/07/17/New_Android_Gradle_Build_Tools/ 译者地址：【翻】一览新的 Android Gradl...\n" +
            "lijinhua7602lijinhua76022015年09月20日 14:528353\n" +
            "Could not resolve com.android.tools.build:gradle:0.5.+\n" +
            "使用Android studio Gradle 打包Android项目遇到 couldnot resolve com.android.tools.build:gradle:1.3.0  错误，与h...\n" +
            "mao520741111mao5207411112015年09月11日 16:2610377\n" +
            "android gradle tools 3.X 中依赖，implementation 和compile区别\n" +
            "转载：android gradle tools 3.X 中依赖，implement、api 指令  转载地址：http://blog.csdn.net/soslinken/article/det...\n" +
            "lyh1299259684lyh12992596842017年11月18日 11:571351\n" +
            "Android Studio下项目编译出错could not find com.android.tools.buildgradle:2.2.2\n" +
            "今天更新Android Studio后打开Project，报如下错误： Error:Could not find com.android.tools.build:gradle:2.2.2. Sea...\n" +
            "z1035075390z10350753902017年04月13日 10:4110411\n" +
            "Could not find com.android.tools.build:gradle:2.14.1.\n" +
            "使用2.14.1版本gradle 结果显示无法找到。 Error:A problem occurred configuring root project 'Client'. > Could not r...\n" +
            "zh453030035zh4530300352017年01月11日 10:347992\n" +
            "Could not find com.android.tools.build:gradle:3.3\n" +
            "1. 问题描述： android studio中，新导入一个android项目，编译错误如下： Error:Could not find com.android.tools.build:g...\n" +
            "lirankeliranke2017年06月06日 11:476547\n" +
            "Android关于buildToolVersion与CompileSdkVersion的区别\n" +
            "点击打开链接StackOverFlow中对这个问题进行了详细的讨论：http://stackoverflow.com/questions/24521017/android-gradle-buildto...\n" +
            "cuiyufeng2cuiyufeng22015年12月14日 17:391119\n" +
            "gradle 之compileSdkVersion,buildToolsVersion等\n" +
            "gradle 之compileSdkVersion,buildToolsVersion等新建一个项目，看build.gradle文件的配置，如下：apply plugin: 'com.android....\n" +
            "fengzijinliangfengzijinliang2017年03月31日 21:231527\n" +
            "compileSdkVersion,targetSdkVersion,minSdkVersion,buildToolsVersion，兼容包版本\n" +
            "compileSdkVersion:编译版本，真正决定代码是否能编译的关键，比如设置成23，就无法使用httpclient,低版本编译出来的apk可以在高版本上运行，因为向下兼容，高版本编译的apk运...\n" +
            "LAMP_zyLAMP_zy2016年01月14日 16:594685\n" +
            " \n" +
            "笑一笑没什么大不了\n" +
            "关注\n" +
            "原创\n" +
            "45\n" +
            " \n" +
            "粉丝\n" +
            "15\n" +
            " \n" +
            "喜欢\n" +
            "22\n" +
            " \n" +
            "评论\n" +
            "29\n" +
            " 持之以恒\n" +
            "等级：  4级,点击查看等级说明 访问量： 13万+ 积分： 1702 排名： 2万+\n" +
            "\n" +
            "博主最新文章更多文章\n" +
            "tensorflow 安装以及报错解决\n" +
            "Java IO\n" +
            "ios 本地化国际化\n" +
            "Objective-c 开发环境\n" +
            "Class PLBuildVersion is implemented in both frameworks\n" +
            "文章分类\n" +
            "android and opengl es2篇\n" +
            "操作系统相关4篇\n" +
            "数据结构1篇\n" +
            "java 笔面试题2篇\n" +
            "工作日志7篇\n" +
            "java 设计模式篇1篇\n" +
            "java知识自我补给12篇\n" +
            "程序员职业规划4篇\n" +
            "linux学习系列4篇\n" +
            "android开发17篇\n" +
            "Netty 学习2篇\n" +
            "局域网发现8篇\n" +
            "bug总结1篇\n" +
            "jvm4篇\n" +
            "ios 学习9篇\n" +
            "机器学习1篇\n" +
            "展开\n" +
            "文章存档\n" +
            "2018年4月1篇\n" +
            "2017年11月1篇\n" +
            "2017年10月2篇\n" +
            "2017年9月7篇\n" +
            "2017年6月1篇\n" +
            "2017年5月8篇\n" +
            "2017年4月3篇\n" +
            "2017年3月10篇\n" +
            "2017年2月10篇\n" +
            "2017年1月1篇\n" +
            "2016年9月1篇\n" +
            "2016年2月2篇\n" +
            "2015年3月1篇\n" +
            "2014年10月1篇\n" +
            "2014年4月1篇\n" +
            "2014年3月1篇\n" +
            "2014年2月2篇\n" +
            "2014年1月1篇\n" +
            "2013年12月3篇\n" +
            "2013年4月1篇\n" +
            "2013年3月9篇\n" +
            "2013年2月1篇\n" +
            "2013年1月1篇\n" +
            "2012年12月2篇\n" +
            "2012年11月6篇\n" +
            "2012年7月1篇\n" +
            "展开\n" +
            "博主热门文章\n" +
            "AndroidStudio、gradle、buildToolsVersion关系\n" +
            "24870\n" +
            "android 中一个工程引用另一个工程\n" +
            "13595\n" +
            "ubuntu 12.04 创建eclipse桌面快捷方式\n" +
            "10327\n" +
            "IP地址分为A,B,C,D,E五类\n" +
            "9312\n" +
            "SDK build Tools revision is too low\n" +
            "5598\n" +
            "androidstudio 无法启动ddms\n" +
            "5319\n" +
            "linux 下android源码下载\n" +
            "4826\n" +
            "关于 android dalvik-cache 含义\n" +
            "4341\n" +
            "局域网发现之UDP组播\n" +
            "3854\n" +
            "局域网发现设备代码实现：udp组播\n" +
            "3387\n" +
            "联系我们\n" +
            "客服\n" +
            "请扫描二维码联系客服\n" +
            "webmaster@csdn.net\n" +
            "\n" +
            "400-660-0108\n" +
            "\n" +
            "QQ客服 客服论坛\n" +
            "\n" +
            "关于招聘广告服务  百度\n" +
            "\n" +
            "©1999-2018 CSDN版权所有\n" +
            "\n" +
            "京ICP证09002463号\n" +
            "\n" +
            "经营性网站备案信息\n" +
            "\n" +
            "网络110报警服务\n" +
            "\n" +
            "中国互联网举报中心\n" +
            "\n" +
            "北京互联网违法和不良信息举报中心\n" +
            "\n" +
            "\n" +
            "8\n" +
            " \n" +
            " \n" +
            " \n" +
            " \n}";
}
