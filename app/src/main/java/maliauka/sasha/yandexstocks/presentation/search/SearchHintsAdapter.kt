package maliauka.sasha.yandexstocks.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import maliauka.sasha.yandexstocks.databinding.ItemSearchHintBinding


class SearchHintsAdapter(private val onItemClick: (String: String) -> Unit) :
    ListAdapter<String, SearchHintsAdapter.HintViewHolder>(DiffCallBack) {

    object DiffCallBack : ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    class HintViewHolder(private val binding: ItemSearchHintBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(hint: String, onFavButtonClick: (hint: String) -> Unit) {

            with(binding) {
                tvHint.text = hint
                root.setOnClickListener { onFavButtonClick(hint) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HintViewHolder {
        return HintViewHolder(
            ItemSearchHintBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HintViewHolder, position: Int) {
        val hint = getItem(position)

        holder.bind(
            hint
        ) {
            onItemClick(it)
        }
    }
}
