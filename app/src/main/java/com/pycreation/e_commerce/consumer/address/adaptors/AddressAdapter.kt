package com.pycreation.e_commerce.consumer.address.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.pycreation.e_commerce.consumer.address.model.res.AddressListModelResModel
import com.pycreation.e_commerce.consumer.address.model.res.AddressListModelResModelItem
import com.pycreation.e_commerce.databinding.AddressItemLyBinding

class AddressAdapter(
    private val context: Context,
    private val addressListModelResModel: AddressListModelResModel,
    private val onEditClicked: (AddressListModelResModelItem) -> Unit,
    private val onRemovedItem: (String) -> Unit
) : Adapter<AddressAdapter.AddressHolder>() {
    inner class AddressHolder(val binding: AddressItemLyBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressHolder {
        val binding = AddressItemLyBinding.inflate(LayoutInflater.from(context), parent, false)
        return AddressHolder(binding)
    }

    override fun getItemCount(): Int {
        return addressListModelResModel.size
    }

    override fun onBindViewHolder(holder: AddressHolder, position: Int) {
        holder.binding.fullNameItem.text = addressListModelResModel[position].fullName
        holder.binding.numberItem.text = addressListModelResModel[position].phoneNumber.toString()
        holder.binding.addressTypeItem.text = addressListModelResModel[position].type
        holder.binding.fullAddressItem.text =
            addressListModelResModel[position].area + ", " + addressListModelResModel[position].city + ", " + addressListModelResModel[position].pinCode + ", " + addressListModelResModel[position].state

        holder.binding.editItemBtn.setOnClickListener {
            onEditClicked(addressListModelResModel[position])
        }

        holder.binding.deleteItemBtn.setOnClickListener {
            onRemovedItem(addressListModelResModel[position]._id)
        }
    }
}