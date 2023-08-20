package vac.test.aidldemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import vac.test.aidldemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mAdapter: TestDataAdapter

    private fun bindAidlService() {
        val intent = Intent()
        // AIDLService中的包名
        val pkg = "vac.test.aidlservice"
        // AIDLService中定义的action
        val act = "AIDL_SERVICE"
        val cls = "vac.test.aidlservice.AidlService"
        intent.action = act
        intent.setClassName(pkg, cls)

        val aidlserviceconnect = AidlProcessUtil.getAidlServiceConnection()

        val bl = bindService(intent, aidlserviceconnect, BIND_AUTO_CREATE)
        Log.i("aidlpkg", "bindservice ${bl}")
        if (!bl) {
            Snackbar.make(
                binding.recyclerView,
                "AIDL_SERVICEE服务绑定失败，请检查是否安装服务程序",
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction("关闭") {
                    Toast.makeText(this, "点击关闭按钮", Toast.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAdapter = TestDataAdapter()
        mAdapter.submitList(null)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = mAdapter

        val curProcessName = AidlProcessUtil.getCurrentProcessName(this)
        if (curProcessName == packageName) {
            bindAidlService()
        }

        binding.btngetlist.setOnClickListener {
            AidlProcessUtil.getAidlService()?.testDatas?.let {
                mAdapter.submitList(it)
            }
        }

        binding.btngetdata.setOnClickListener { view ->
            AidlProcessUtil.getAidlService()?.getTestData("00001")?.let { p ->
                val msg = "编码${p.code} 名称：${p.name} 价格：${p.price} 数量：${p.qty}"
                Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
            }
        }
    }
}