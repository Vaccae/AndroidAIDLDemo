package vac.test.aidldemo

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import vac.test.aidlservice.ITestDataAidlInterface

object AidlProcessUtil {

    private var aidlService: ITestDataAidlInterface? = null
    private var mAidlServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.i("aidlpkg", "onServiceConnected")
            aidlService = ITestDataAidlInterface.Stub.asInterface(p1)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.i("aidlpkg", "onServiceDisconnected")
            aidlService = null
        }

    }

    fun getAidlService(): ITestDataAidlInterface? {
        return aidlService
    }

    fun getAidlServiceConnection(): ServiceConnection {
        return mAidlServiceConnection
    }


    //获取当前进程名
    fun getCurrentProcessName(context: Context): String? {
        val pid = android.os.Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val runApps = am.runningAppProcesses
        if (runApps.isEmpty()) return null

        for (procinfo in runApps) {
            if (procinfo.pid == pid) {
                return procinfo.processName
            }
        }
        return null
    }
}