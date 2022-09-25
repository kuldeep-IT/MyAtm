package com.kuldeep.atm1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kuldeep.atm1.data.Transactions
import com.kuldeep.atm1.databinding.ItemTransactionBinding

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TranViewHolder>() {

    inner class TranViewHolder(val mBinding: ItemTransactionBinding): BaseViewHolder(mBinding.root) {
        override fun onBind(position: Int) {
            val transactions = differ.currentList[position]
            mBinding.transcViewModel = transactions

            mBinding.executePendingBindings()
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Transactions>() {

        override fun areItemsTheSame(oldItem: Transactions, newItem: Transactions): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transactions, newItem: Transactions): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TranViewHolder {
        var mBinding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TranViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: TranViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}