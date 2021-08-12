package com.blogapp.recyclerViewUtils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.*
import com.blogapp.R
import com.blogapp.databinding.ItemBlogListBinding
import com.blogapp.models.BlogPost
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade

class BlogRvAdapter(
    private val onClickListener: OnClickListener? = null,
    private val requestManager: RequestManager
) :
    PagingDataAdapter<BlogPost, BlogRvAdapter.BlogPostViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<BlogPost>() {

            override fun areItemsTheSame(oldItem: BlogPost, newItem: BlogPost) =
                oldItem.primaryKey == newItem.primaryKey

            override fun areContentsTheSame(oldItem: BlogPost, newItem: BlogPost) =
                oldItem == newItem

        }
    }

    private val NO_MORE_RESULTS = -1
    private val BLOG_ITEM = 0
    private val NO_MORE_RESULTS_BLOG_MARKER = BlogPost(
        NO_MORE_RESULTS, "", "", "", "", "", ""
    )

//    private val differ = AsyncListDiffer(
//        BlogRecyclerChangeCallback(this),
//        AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
//    )

    internal inner class BlogRecyclerChangeCallback(
        private val adapter: BlogRvAdapter
    ) : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogPostViewHolder {
        return when (viewType) {
//            NO_MORE_RESULTS -> {
//                GenericViewHolder(
//                    LayoutInflater.from(parent.context)
//                        .inflate(R.layout.item_no_more_results, parent, false)
//                )
//            }
            BLOG_ITEM -> {
                BlogPostViewHolder(
                    ItemBlogListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                    onClickListener = onClickListener,
                    requestManager = requestManager
                )
            }
            else -> {
                throw ClassNotFoundException("Invalid view holder class")
            }
        }
    }

    override fun onBindViewHolder(holder: BlogPostViewHolder, position: Int) {
//        holder.bind(differ.currentList.get(position))
        getItem(position)?.let {
            holder.bind(it)
        }
    }

//    override fun getItemCount(): Int = differ.currentList.size

//    override fun getItemViewType(position: Int): Int {
//        if (differ.currentList[position].primaryKey > -1) {
//            return BLOG_ITEM
//        }
//        return differ.currentList[position].primaryKey //-1
//    }

//    fun submitList(list: List<BlogPost>?, isQueryExhausted: Boolean) {
//        val newList = list?.toMutableList()
//        if (isQueryExhausted) {
//            newList?.add(NO_MORE_RESULTS_BLOG_MARKER)
//        }
//        differ.submitList(list)
//    }

    class BlogPostViewHolder
    constructor(
        val binding: ItemBlogListBinding,
        val requestManager: RequestManager,
        private val onClickListener: OnClickListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BlogPost) = with(itemView) {
            itemView.setOnClickListener {
                onClickListener?.onItemSelected(adapterPosition, item)
            }
            requestManager.load(item.image)
                .transition(withCrossFade())
                .into(binding.blogImage)

            binding.blogTitle.text = item.title
            binding.blogAuthor.text = item.username
            binding.blogUpdateDate.text = item.date_updated
        }
    }

}

interface OnClickListener {
    fun onItemSelected(position: Int, item: BlogPost)
}

