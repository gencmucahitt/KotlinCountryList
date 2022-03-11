package com.mucahit.kotlincountrylist.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.mucahit.kotlincountrylist.databinding.FragmentCountryBinding
import com.mucahit.kotlincountrylist.util.downloadFromUrl
import com.mucahit.kotlincountrylist.util.placeHolderProgressBar

import com.mucahit.kotlincountrylist.viewmodel.CountryViewModel

class CountryFragment : Fragment() {
    private var countryUuid = 0
    private lateinit var viewModel : CountryViewModel
    private var _binding : FragmentCountryBinding? =null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountryBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            countryUuid = CountryFragmentArgs.fromBundle(it).countryUuid
        }
        viewModel = ViewModelProviders.of(this).get(CountryViewModel::class.java)
        viewModel.getDataFromRoom(countryUuid)
        observeLiveData()




    }
    private fun observeLiveData() {
        viewModel.countryLiveData.observe(viewLifecycleOwner, Observer { country->
            country?.let {
                binding.countryName.text = country.countryName
                binding.countryCapital.text = country.countryCapital
                binding.countryCurrency.text = country.countryCurrency
                binding.countryLanguage.text = country.countryLanguage
                binding.countryRegion.text = country.countryRegion
                context?.let {
                    binding.countryImage.downloadFromUrl(country.imageUrl, placeHolderProgressBar(it))
                }

            }
        })
    }

}