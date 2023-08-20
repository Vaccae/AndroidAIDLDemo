package vac.test.aidlservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log

class AidlService : Service() {

    val CHANNEL_STRING = "vac.test.aidlservice"
    val CHANNEL_ID = 0x11

    val mTestDatas: MutableList<TestData> = mutableListOf()

    fun initList() {
        for (i in 1..5) {
            val price = ((0..10).random()).toFloat()
            val qty = (10..50).random()
            val item = TestData("0000${i}", "测试数据${i}", price, qty)
            mTestDatas.add(item)
        }
    }

    fun startServiceForeground() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_STRING, "AidlServer",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
        val notification = Notification.Builder(applicationContext, CHANNEL_STRING).build()
        startForeground(CHANNEL_ID, notification)
    }

    override fun onCreate() {
        super.onCreate()
        /*Debug版本时调试使用 */
        // Debug.waitForDebugger()
        startServiceForeground()
        //初始化数据
        initList()
    }

    override fun onBind(intent: Intent): IBinder {

        return object : ITestDataAidlInterface.Stub() {
            override fun basicTypes(
                anInt: Int,
                aLong: Long,
                aBoolean: Boolean,
                aFloat: Float,
                aDouble: Double,
                aString: String?
            ) {
                TODO("Not yet implemented")
            }

            override fun getTestData(code: String?): TestData? {
                return mTestDatas.firstOrNull { t -> t.code == code }
            }

            override fun getTestDatas(): MutableList<TestData> {
                return mTestDatas
            }

            override fun updateTestData(data: TestData?): Boolean {
                data?.let {
                    var item: TestData? =
                        mTestDatas.firstOrNull { t -> t.code == it.code } ?: return false
                    item?.let { t ->
                        t.code = it.code
                        t.name = it.name
                        t.price = it.price
                        t.qty = it.qty
                    }
                    return true
                } ?: return false
            }

        }
    }
}