package vac.test.aidlservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.IOException
import kotlin.jvm.Throws

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
//                return mTestDatas.firstOrNull { t -> t.code == code }
                throw SecurityException("我是AidlService进程中的异常，你看到了吗？")
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

            override fun updateTestDatsList(datas: MutableList<TestData>?): Boolean {
                datas?.let {
                    val item = TestData("99999", "我是新加数据", 1.0f, 1)
                    it.add(item)

                    mTestDatas.addAll(it)

                    it.clear()
                    it.addAll(mTestDatas)
                    return true
                } ?: return false
            }

            override fun transBundle(bundle: Bundle?): MutableList<TestData> {
                bundle?.let { it ->
                    /*
                          Android有两种不同的classloaders：framework classloader和apk classloader，
                          其中framework classloader知道怎么加载android classes，
                          apk classloader继承自framework classloader，所以也知道怎么加载android classes。
                          但在应用刚启动时，默认class loader是apk classloader，在系统内存不足应用被系统回收会再次启动，
                          这个默认class loader会变为framework classloader了，所以对于自己的类会报ClassNotFoundException
                          就会出现android.os.BadParcelableException: ClassNotFoundException when unmarshalling
                    */
                    //所以在bundle数据读取前，先设置classloader后，才能正确的读取自定义类
                    it.classLoader = TestData::class.java.classLoader

                    val price = it.getFloat("price")
                    val qty = it.getInt("qty")

                    mTestDatas.map { t->
                        t.price = price
                        t.qty = qty
                    }

                    val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        it.getParcelableArrayList("listdatas", TestData::class.java)
                    } else {
                        it.getParcelableArrayList<TestData>("listdatas")
                    }
                    list?.let { item->
                        mTestDatas.addAll(item)
                    }
                }
                return mTestDatas
            }

        }
    }
}