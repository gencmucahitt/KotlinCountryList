package com.mucahit.kotlincountrylist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.mucahit.kotlincountrylist.R
import com.mucahit.kotlincountrylist.databinding.ItemCountryBinding
import com.mucahit.kotlincountrylist.model.Country
import com.mucahit.kotlincountrylist.util.downloadFromUrl
import com.mucahit.kotlincountrylist.util.placeHolderProgressBar
import com.mucahit.kotlincountrylist.view.FeedFragmentDirections

class CountryAdapter(val countryList : ArrayList<Country>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
    class CountryViewHolder(var bind: ItemCountryBinding) : RecyclerView.ViewHolder(bind.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val bind = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return CountryViewHolder(bind)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind.apply {
            name.text=countryList[position].countryName
            region.text=countryList[position].countryRegion
            linearView.setOnClickListener{
                val action = FeedFragmentDirections.actionFeedFragmentToCountryFragment(countryList[position].uuid)

                Navigation.findNavController(it).navigate(action)
            }
            imageView.downloadFromUrl(countryList[position].imageUrl, placeHolderProgressBar(root.context))

        }


    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    fun updateCountryList(newCountryList : List<Country>){
        countryList.clear()
        countryList.addAll(newCountryList)
        notifyDataSetChanged()
    }
}