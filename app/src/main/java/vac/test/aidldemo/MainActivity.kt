package vac.test.aidldemo

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import vac.test.aidldemo.databinding.ActivityMainBinding
import vac.test.aidlservice.TestData


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
            try {
                AidlProcessUtil.getAidlService()?.getTestData("00001")?.let { p ->
                    val msg = "编码${p.code} 名称：${p.name} 价格：${p.price} 数量：${p.qty}"
                    Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null).show()
                }
            } catch (e: Exception) {
                Snackbar.make(view, e.message.toString(), Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        binding.btnupdlist.setOnClickListener { view ->
            val datas = mutableListOf<TestData>()
            for (i in 1..2) {
                val price = ((0..10).random()).toFloat()
                val qty = (10..50).random()
                val item = TestData("1000${i}", "客户端数据${i}", price, qty)
                datas.add(item)
            }
            AidlProcessUtil.getAidlService()?.updateTestDatsList(datas)?.let {
                if(it){
                    mAdapter.submitList(datas)
                }else{
                    Snackbar.make(view, "更新数据列表失败", Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }

        binding.btntest.setOnClickListener { view ->
            try {
                val bundle = Bundle()
                bundle.putFloat("price", 0.1f)
                bundle.putInt("qty", 5)

                val datas = mutableListOf<TestData>()
                for (i in 1..2) {
                    val price = ((0..10).random()).toFloat()
                    val qty = (10..50).random()
                    val item = TestData("1000${i}", "客户端数据${i}", price, qty)
                    datas.add(item)
                }
                bundle.putParcelableArrayList("listdatas", ArrayList(datas))
                AidlProcessUtil.getAidlService()?.transBundle(bundle)?.let {
                    mAdapter.submitList(it)
                }
            } catch (e: Exception) {
                Snackbar.make(view, e.message.toString(), Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }
}