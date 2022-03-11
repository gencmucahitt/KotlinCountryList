package com.mucahit.kotlincountrylist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mucahit.kotlincountrylist.adapter.CountryAdapter
import com.mucahit.kotlincountrylist.databinding.FragmentFeedBinding

import com.mucahit.kotlincountrylist.viewmodel.FeedViewModel

class FeedFragment : Fragment() {
    private var _binding : FragmentFeedBinding? =null
    private val binding get() = _binding!!
    private lateinit var viewModel : FeedViewModel
    private val countryAdapter = CountryAdapter(arrayListOf())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFeedBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)
        viewModel.refreshData()

        binding.countryList.layoutManager=LinearLayoutManager(context)
        binding.countryList.adapter=countryAdapter
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.countryList.visibility = View.GONE
            binding.countryError.visibility = View.GONE
            binding.countryLoading.visibility = View.VISIBLE
            viewModel.refreshFromAPI()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        observeLiveData()

    }

    fun observeLiveData(){
        viewModel.countries.observe(viewLifecycleOwner, Observer { countries ->
            countries?.let {
                binding.countryList.visibility=View.VISIBLE
                countryAdapter.updateCountryList(countries)
            }

        })
        viewModel.countryError.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                if (it){
                    binding.countryError.visibility=View.VISIBLE
                }else{
                    binding.countryError.visibility=View.GONE
                }
            }

        })
        viewModel.countryLoading.observe(viewLifecycleOwner, Observer { loading ->
            loading?.let {
                if (it){
                    binding.countryLoading.visibility = View.VISIBLE
                    binding.countryError.visibility = View.GONE
                    binding.countryList.visibility = View.GONE
                }
                else{
                    binding.countryLoading.visibility = View.GONE
                }
            }
        })
    }

}