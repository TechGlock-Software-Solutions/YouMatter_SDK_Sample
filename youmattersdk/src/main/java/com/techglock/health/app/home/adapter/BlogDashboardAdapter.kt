package com.techglock.health.app.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.techglock.health.app.R
import com.techglock.health.app.common.view.CommonBinding.setImgUrl
import com.techglock.health.app.databinding.ItemBlogDashboardBinding
import com.techglock.health.app.home.ui.HomeScreenFragment
import com.techglock.health.app.home.viewmodel.DashboardViewModel
import com.techglock.health.app.model.blogs.BlogItem

class BlogDashboardAdapter(
    private val abc: HomeScreenFragment,
    private val viewModel: DashboardViewModel,
    private val listener: OnBlogClickListener
) : RecyclerView.Adapter<BlogDashboardAdapter.BlogViewHolder>() {

    private val blogList: MutableList<BlogItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder =
        BlogViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_blog_dashboard, parent, false)
        )

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        try {
            val blog = blogList[position]
            holder.bindTo(blog, viewModel)

            holder.binding.layoutBlog.setOnClickListener {
                listener.onBlogSelection(blog, it)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun updateData(blogList: List<BlogItem>?) {
        this.blogList.clear()
        this.blogList.addAll(blogList!!)
        notifyDataSetChanged()
        abc.stopBlogsShimmer()
    }

    interface OnBlogClickListener {
        fun onBlogSelection(blog: BlogItem, view: View)
    }

    inner class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val binding = ItemBlogDashboardBinding.bind(view)

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

}