# PowerfulViewLibrary

###PowerfulEditText介绍  

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/PowerfulEditText.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/PowerfulEditText.md)

###NumberRunningTextView介绍 

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/PowerfulEditText.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/NumRunningTextView.md)

###ExpandableLinearLayout介绍 

[https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/PowerfulEditText.md](https://github.com/chaychan/PowerfulViewLibrary/blob/master/readmes/ExpandableLinearLayout.md)

####**导入方式**####
在项目根目录下的build.gradle中的allprojects{}中，添加jitpack仓库地址，如下：

    allprojects {
	    repositories {
	        jcenter()
	        maven { url 'https://jitpack.io' }//添加jitpack仓库地址
	    }
	}
 
打开app的module中的build.gradle，在dependencies{}中，添加依赖，如下：

    dependencies {
			......
	        compile 'com.github.chaychan:PowerfulViewLibrary:1.1.6'
	}
