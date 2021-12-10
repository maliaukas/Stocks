package maliauka.sasha.yandexstocks.presentation.list

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import maliauka.sasha.yandexstocks.R
import maliauka.sasha.yandexstocks.data.util.getLogoPlaceHolder
import maliauka.sasha.yandexstocks.databinding.ItemStockBinding
import maliauka.sasha.yandexstocks.domain.model.Stock
import javax.inject.Inject


class StocksAdapter @Inject constructor(
    private val glide: RequestManager,
) : ListAdapter<Stock, StocksAdapter.StockViewHolder>(DiffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        return StockViewHolder(
            ItemStockBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private var onFavButtonClick: ((Stock) -> Unit)? = null

    fun setOnFavButtonClickListener(onItemClick: ((Stock) -> Unit)) {
        this.onFavButtonClick = onItemClick
    }

    private var onLoadImageFailed: ((Stock) -> Unit)? = null

    fun setOnLoadImageFailedListener(onFailed: ((Stock) -> Unit)) {
        this.onLoadImageFailed = onFailed
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = getItem(position)

        holder.bind(
            stock,
            position % 2 == 1
        ) { st ->
            onFavButtonClick?.let { onClick ->
                onClick(st)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick?.let { onItemClick ->
                onItemClick(stock)
            }
        }
    }

    private var onItemClick: ((Stock) -> Unit)? = null

    fun setOnItemClickListener(onItemClick: (Stock) -> Unit) {
        this.onItemClick = onItemClick
    }

    inner class StockViewHolder(private val binding: ItemStockBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val redColor: Int
        private val greenColor: Int

        private val oddColor: Int
        private val evenColor: Int

        init {
            val typedValue = TypedValue()
            val theme = binding.root.context.theme

            theme.resolveAttribute(R.attr.colorError, typedValue, true)
            redColor =
                ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)

            theme.resolveAttribute(R.attr.colorPositive, typedValue, true)
            greenColor =
                ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)

            theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true)
            evenColor =
                ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)

            theme.resolveAttribute(R.attr.colorSurface, typedValue, true)
            oddColor =
                ResourcesCompat.getColor(binding.root.resources, typedValue.resourceId, theme)
        }

        private val makeFavourite =
            binding.root.resources.getString(R.string.ib_make_favourite_description)
        private val unmakeFavourite =
            binding.root.resources.getString(R.string.ib_unmake_favourite_description)


        fun bind(stock: Stock, isOdd: Boolean, onFavButtonClick: (stock: Stock) -> Unit) {

            with(binding) {
                // set card color
                binding.cardView.backgroundTintList =
                    ColorStateList.valueOf(if (isOdd) oddColor else evenColor)

                // set text
                tvTicker.text = stock.ticker
                tvCompany.text = stock.companyName
                tvCost.text = stock.priceString
                tvDelta.text = stock.deltaString

                // set delta text color
                tvDelta.setTextColor(if (stock.delta > 0) greenColor else redColor)

                if (stock.placeHolderColor != null) {
                    val drawablePlaceHolder = getLogoPlaceHolder(
                        stock.ticker.first().toString(),
                        stock.placeHolderColor
                    )
                    ivCompanyLogo.setImageDrawable(drawablePlaceHolder)
                } else {
                    glide
                        .load(stock.logoUri)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                onLoadImageFailed?.let { it(stock) }
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(ivCompanyLogo)
                }

                if (!stock.isFavourite) {
                    ibFavourite.setImageResource(R.drawable.ic_star_empty)
                    ibFavourite.contentDescription = makeFavourite
                } else {
                    ibFavourite.setImageResource(R.drawable.ic_star_filled)
                    ibFavourite.contentDescription = unmakeFavourite
                }

                ibFavourite.setOnClickListener {
                    onFavButtonClick(stock)
                }
            }
        }
    }


    object DiffCallBack : ItemCallback<Stock>() {
        override fun areItemsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem.ticker == newItem.ticker
        }

        override fun areContentsTheSame(oldItem: Stock, newItem: Stock): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Stock, newItem: Stock) = Any()
    }
}
