package com.mucahit.kotlincountrylist.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mucahit.kotlincountrylist.model.Country
import com.mucahit.kotlincountrylist.service.CountryAPIService
import com.mucahit.kotlincountrylist.service.CountryDatabase
import com.mucahit.kotlincountrylist.util.CustomSharedPreferences
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class FeedViewModel(application: Application) : BaseViewModel(application) {
    private val countryApiService = CountryAPIService()
    private val disposable = CompositeDisposable()
    private var customPreferences = CustomSharedPreferences(getApplication())
    private var refreshTime = 10*60*1000*1000*1000L
    val countries = MutableLiveData<List<Country>>()
    val countryError = MutableLiveData<Boolean>()
    val countryLoading = MutableLiveData<Boolean>()

    fun refreshData() {
    val updateTime = customPreferences.getTime()
    if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime){
        getDataFromSQLite()
    }else{
        getDataFromAPI()
    }

    }
    private fun getDataFromSQLite(){
        countryLoading.value=true
        launch {
            val countries = CountryDatabase(getApplication()).countryDao().getAllCountries()
            showCountries(countries)
            Toast.makeText(getApplication(), "Countries From SQLite", Toast.LENGTH_SHORT).show()
        }
    }
    fun refreshFromAPI(){
        getDataFromAPI()
    }


    private fun getDataFromAPI(){
        countryLoading.value=true
        disposable.add(countryApiService.getData()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<List<Country>>(){
                override fun onSuccess(t: List<Country>) {
                    storeInSQLite(t)
                    Toast.makeText(getApplication(), "Countries From APİ", Toast.LENGTH_SHORT).show()
                }

                override fun onError(e: Throwable) {
                    countryError.value=false
                    countryError.value=true
                    e.printStackTrace()
                }

            })
        )

    }

    private fun showCountries(countryList: List<Country>){
        countries.value=countryList
        countryError.value=false
        countryLoading.value=false
    }
    private fun storeInSQLite(list : List<Country>){
    launch {
        val dao = CountryDatabase(getApplication()).countryDao()
        dao.deleteAllCountries()
        val listlong = dao.insertAll(*list.toTypedArray())
        var i =0
        while (i<list.size){
           list[i].uuid=listlong[i].toInt()
            i=i+1
        }
        showCountries(list)
    }
        customPreferences.saveTime(System.nanoTime())

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}