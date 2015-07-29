# ScreenShot
<img src="./screenshot/Screenshot_1.jpeg" width="30%" height="30%">
<img src="./screenshot/Screenshot_2.jpeg" width="30%" height="30%">
<img src="./screenshot/Screenshot_3.jpeg" width="30%" height="30%">
<img src="./screenshot/Screenshot_4.jpeg" width="30%" height="30%">
 
### Git@OSC非官方android客户端
本产品是Git@OSC非官方客户端，遵循Material Design设计原则，官方客户端界面实在是丑。参考官方客户端地址：http://http://git.oschina.net/appclient
- 界面采取Material Design设计风格
- 使用android support design中的控件替代原生或者其它开源控件
- 使用RecycleView代替Listview
- 使用谷歌Volley代替android-async-http和universal-image-loader
- 增加切换主题功能

### 下一步更新计划
- 代码重构
- 使用自定义view代替materialpreference和circleimageview开源框架
- 优化recyclerview

### 引用到的开源库：
- compile 'com.android.support:design:22.2.0'
- compile 'com.jakewharton:butterknife:6.1.0'
- compile 'com.jenzz:materialpreference:1.3'
- compile 'de.hdodenhof:circleimageview:1.3.0'
- compile files('libs/volley.jar')
- compile files('libs/gson-2.3.1.jar')
- compile 'de.greenrobot:eventbus:2.4.0'

### 开源协议
- [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
