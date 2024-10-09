package com.techglock.health.app.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.view.CommonBinding.setImgUrl
import com.techglock.health.app.databinding.ItemBlogDashboardBinding
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.blogs.BlogItem

class BlogSuggestionAdapter(
    private val viewModel: DashboardViewModel
) : RecyclerView.Adapter<BlogSuggestionAdapter.BlogViewHolder>() {

    private val blogList: MutableList<BlogItem> = mutableListOf()
    private var mOnBottomReachedListener: OnBottomReachedListener? = null

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {
        this.mOnBottomReachedListener = onBottomReachedListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder =
        BlogViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_blog_dashboard, parent, false)
        )

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        try {
            val blogItem = blogList[position]
            holder.bindTo(blogItem, viewModel)

            if (position == blogList.size - 1) {
                mOnBottomReachedListener!!.onBottomReached(position)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun updateData(blogList: List<BlogItem>?) {
        this.blogList.addAll(blogList!!)
        notifyDataSetChanged()
    }

    inner class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemBlogDashboardBinding.bind(view)

        fun bindTo(blog: BlogItem, viewModel: DashboardViewModel) {
            binding.layoutBlog.setOnClickListener {
                viewModel.viewBlog(it, blog)
            }
            binding.imgBlog.setImgUrl(blog.image ?: "")
            binding.txtBlogDate.text = blog.date ?: ""
            binding.txtBlogTitle.text = blog.title ?: ""
            binding.txtBlogDesciption.text = blog.description ?: ""
        }

    }

    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

}