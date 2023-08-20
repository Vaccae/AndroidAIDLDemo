package vac.test.aidldemo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseDifferAdapter
import com.google.android.material.snackbar.Snackbar
import vac.test.aidldemo.databinding.RclTestdataBinding
import vac.test.aidlservice.TestData

class TestDataDiffCallback : DiffUtil.ItemCallback<TestData>() {
    override fun areItemsTheSame(oldItem: TestData, newItem: TestData): Boolean {
        // 判断是否是同一个 item（通常使用id字段判断）
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: TestData, newItem: TestData): Boolean {
        // 如果是同一个item，则判断item内的数据内容是否有变化
        return oldItem.qty == newItem.qty
    }

}

class TestDataAdapter : BaseDifferAdapter<TestData, TestDataAdapter.VH>(TestDataDiffCallback()) {

    // 自定义ViewHolder类
    class VH(
        parent: ViewGroup,
        val binding: RclTestdataBinding = RclTestdataBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: VH, position: Int, item: TestData?) {
        item?.let {
            holder.binding.rclcodetext.text = "编码：${it.code}"
            holder.binding.rclnametext.text = "名称：${it.name}"
            holder.binding.rclpricetext.text = "价格：${it.price}"
            holder.binding.rclqtytext.text = "数量：${it.qty}"

            holder.binding.rclqtytext.setOnClickListener { view ->
                Log.i("aidlpkg", "rclqtytext OnClick")
                run {
                    var lastqty = it.qty
                    it.qty += 1
                    AidlProcessUtil.getAidlService()?.updateTestData(it)?.let { b ->
                        if (!b) {
                            it.qty = lastqty
                            Snackbar.make(
                                view,
                                "${it.code}${it.name}修改失败",
                                Snackbar.LENGTH_LONG
                            ).setAction("Action", null).show()
                        } else {
                            notifyItemChanged(position)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
        // 返回一个 ViewHolder
        return VH(parent)
    }
}