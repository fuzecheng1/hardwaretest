package com.hollyland.hardwaretest.constants;

public class Constants {


    public static final String TAG = "hollyland";



    public static class EVENT {

        //连接网络成功
        public static final int EVENT_CONNECT_NETWORK_SUCCESS = 0;

        //连接网络失败
        public static final int EVENT_CONNECT_NETWORK_FAIL = 1;

        //开始测试
        public static final int EVENT_START_TEST = 3;

        //更新当前测试次数
        public static final int EVENT_UPDATE_CURRRENT_COUNT = 4;

        //测试完成
        public static final int EVENT_UPDATE_TEST_COMPLETE = 5;

        //自动重启测试，更新当前时间
        public static final int EVENT_AUTO_REBOOT_UPDATE_CURRENT_TIME = 8;

        //自动重启测试，更新当前错误信息
        public static final int EVENT_AUTO_REBOOT_UPDATE_ERROR_TIP = 9;

        //自动重启测试，更新当前USB信息
        public static final int EVENT_AUTO_REBOOT_UPDATE_USB = 10;

        //读卡更新密码信息
        public static final int EVENT_READ_CARD_UPDATE_PWD = 11;

        //更新扫描结果值
        public static final int EVENT_READ_CARD_UPDATE_SCAN_RESULT = 12;

        //更新当前时间值
        public static final int EVENT_READ_CARD_UPDATE_CURRENT_TIME = 13;

        //更新测试结果
        public static final int EVENT_READ_CARD_UPDATE_TEST_RESULT = 14;

        //更新外网ping测试结果
        public static final int EVENT_NETWORK_PING_OUT_RESULT = 15;

        //更新内网ping测试结果
        public static final int EVENT_NETWORK_PING_INTERNAL_RESULT = 16;

        //i2c测试完成
        public static final int EVENT_I2C_TEST_COMPLETE = 17;

        //i2c测试更新结果
        public static final int EVENT_I2C_UPDATE_RESULT = 18;

        //打印机，UDP协议更新设备
        public static final int EVENT_PRINTER_UDP_UPDATE_DEVICE = 19;

        //打印机，UDP协议 关闭接收数据socket成功
        public static final int EVENT_PRINTER_UDP_CLOSE_RECEIVESOCKET_SUCCESS = 20;

        //HID测试开始
        public static final int EVENT_HID_TEST_START = 21;

        //Iperf测试更新结果
        public static final int EVENT_IPERF_TEST_RESULT = 22;

        //Iperf测试完成
        public static final int EVENT_IPERF_TEST_COMPLETE = 23;

        //连接蓝牙成功
        public static final int EVENT_CONNECT_BLUETOOTH_SUCCESS = 24;

        //连接蓝牙失败
        public static final int EVENT_CONNECT_BLUETOOTH_FAIL = 25;

        //打印机状态
        public static final int EVENT_PRINTER_STATUS_VALUE = 26;

        //自动重启测试，更新当前USB信息
        public static final int EVENT_AUTO_REBOOT_UPDATE_BLUETOOTH = 27;

        //打印机状态异常值
        public static final int EVENT_PRINTER_STATUS_EXCEPTION = 28;

        //测试异常
        public static final int EVENT_TEST_ERROR = 29;

        //新大陆扫描枪测试开始
        public static final int EVENT_NEW_LAND_SCANNER_START = 30;

        //新大陆扫描枪虚拟串口模式扫描得到数据
        public static final int EVENT_NEW_LAND_SCANNER_SERIAL_VALUE = 31;

        //蓝牙扫描设备列表
        public static final int EVENT_BT_SEARCH_DEVICE_LIST = 32;

        //蓝牙压力测试结果
        public static final int EVENT_BT_STESS_RESULT  = 33;

        //打印错误
        public static final int EVENT_PRINTER_ERROR = 34;

        //开始打印
        public static final int EVENT_PRINTER_START = 40;

        public static final int EVENT_WIFI_STESS_RESULT = 41;

        public static final int STRESS_CURRENT_TIME = 42;

        //连接USB成功
        public static final int EVENT_CONNECT_USB_SUCCESS = 36;

        //连接USB失败
        public static final int EVENT_CONNECT_USB_FAIL = 37;

        //usb断开成功
        public static final int EVENT_DISCONN_USB_SUCCESS = 38;

        //网络断开成功
        public static final int EVENT_DISCONN_NETWORK_SUCCESS = 39;

        //参数错误
        public static final int EVENT_PARAMETER_ERROR = 43;

        //SDK测试结果
        public static final int SOFTWARE_DEVELOPMENT_KIT_TEST_RESULT = 44;

        //SDK 图像识别数据
        public static final int RECOGNITION_DATA = 46;

        //休眠结果
        public static final int SCREEN_TEST_SLEEP = 47;

        //唤醒结果
        public static final int SCREEN_TEST_WAKEUP = 48;

        public static final int EVENT_USB_STRESS_RESULT = 49;
        public static final int STRESS_USB_CHECKED_CHANGE = 50;
    }



    public static class EVENT_STRESS {

        public static final int STRESS_BT_EABLE = 1001;

        public static final int STRESS_BT_DISCOVERY = 1002;

        public static final int STRESS_BT_BOND =  1003 ;

        public static final int STRESS_BT_CONNECT = 1004;

        public static final int STRESS_BT_DISABLE = 1005;

        public static final int STRESS_BT_PLAYING = 1006;

        public static final int STOP_TEST = 9999 ;

        public static final long ENABLE_BT_DELAY = 2000L;

        public static final long DICOVERY_BT_DELAY = 5000L;

        public static final long BIND_BT_DEVICE_DELAY = 25000L;

        public static final long DISABLE_BT_DEVICE_DELAY = 55000L;
    }

    public class SP {
        //开关机重启值
        public static final String UPDOWN_RESTART = "UPDOWN_RESTART";
        //开关机总值
        public static final String UPDOWN_TOTAL = "UPDOWN_TOTAL";
        //是否开启开关机测试
        public static final String UPDOWN_IS_TEST = "UPDOWN_IS_TEST";
        //检查开关机后是否可以访问网络
        public static final String REQUIRE_HTTP = "REQUIRE_HTTP";
        //接入USB设备的开关机模式
        public static final String UPDOWN_USB_MODE = "UPDOWN_USB_MODE";
        //开关机时间间隔
        public static final String UPDOWN_INTERVALS = "UPDOWN_INTERVALS";
    }
}
