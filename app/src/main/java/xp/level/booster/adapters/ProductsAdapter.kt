package xp.level.booster.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import com.android.billingclient.api.SkuDetails
import xp.level.booster.R

class ProductsAdapter(
    private val list: List<SkuDetails>,
    private val onProductClicked: (SkuDetails) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsAdapter.ViewHolder {
        val textView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_button,
            parent,
            false
        ) as Button
        val viewHolder = ViewHolder(textView)
        textView.setOnClickListener { onProductClicked(list[viewHolder.adapterPosition]) }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.textView.text = "Buy 100000XP at just " + list[position].price
//        holder.textView.text = "Buy XP"

    }

    class ViewHolder(val textView: Button) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(textView)
}