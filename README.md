# StockChart-MPAndroidChart

[![](https://jitpack.io/v/LambdaXiao/StockChart-MPAndroidChart.svg)](https://jitpack.io/#LambdaXiao/StockChart-MPAndroidChart)

**基于MPAndroidChart 最新3.1.0版本的专业分时图和K线图**

先上效果图：
<video controls="controls">
  <source type="video/mp4" src="https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/record.mp4"></source>
</video>

![Image text](https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/oneday.jpg)

![Image text](https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/fiveday.jpg)

![Image text](https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/kline.jpg)

![Image text](https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/oneday-land.jpg)

![Image text](https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/kline-land.jpg)
## 快速开始
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```
 implementation 'com.github.LambdaXiao:StockChart-MPAndroidChart:1.0'
```
如果需要根据不同业务修改代码，请直接依赖项目中的MPChartLib库。


## 简介

StockApp是Android平台的金融图表库，包括**分时图**和**K线图**。本项目通过继承的方式定制了最新版本的**MPAndroidChart**

- 使用简单，几行代码就可以实现专业分时K线图效果
- 支持五日的分时图
- 支持MACD、KDJ、RSI、SAR、BOLL、CCI、MA、SMA、EXPMA、KMA等指标的显示和切换
- 支持图表的缩放，可以进行放大和缩小操作
- 支持刻度值画在图表内侧和外侧，可自由定制
- 横屏模式的展示
- 分时图增加最近一点的数值闪动显示并在右侧画出最新价数值
- 支持夜间模式的展示

## apk体验

   ![Image text](https://github.com/LambdaXiao/StockChart-MPAndroidChart/raw/master/screenshot/stockChart.png)

## 反馈

本Demo根据以往从事股票类App开发经验实现通用画图模块，因为当初也是为了快速实现功能，bug在所难免，后面会持续更新，欢迎各位提issues和star！
也希望志同道合者能来一起完善该项目。

**联系方式**

_email: lambdaxiao@gmail.com_

_QQ: 932599583_

_Android股票画图交流群: 322672046_
