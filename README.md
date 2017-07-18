# PowerfulViewLibrary

### PowerfulEditText(功能强大的EditText)介绍  

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/PowerfulEditText.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/PowerfulEditText.md)

### NumberRunningTextView(仿支付宝数字滚动的TextView)介绍 

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/NumRunningTextView.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/NumRunningTextView.md)

### ExpandableLinearLayout(可展开收起的LinearLayout)介绍 

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/ExpandableLinearLayout.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/ExpandableLinearLayout.md)

### BottomBarLayout(轻量级底部导航栏)介绍

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/BottomBarLayout.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/BottomBarLayout.md)

#### **导入方式**
在项目根目录下的build.gradle中的allprojects{}中，添加jitpack仓库地址，如下：

    allprojects {
	    repositories {
	        jcenter()
	        maven { url 'https://jitpack.io' }//添加jitpack仓库地址
	    }
	}
 
打开app的module中的build.gradle，在dependencies{}中，添加依赖，如下：

    dependencies {
	        compile 'com.github.chaychan:PowerfulViewLibrary:1.2.1'
	}
