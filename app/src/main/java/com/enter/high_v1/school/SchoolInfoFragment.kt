package com.enter.high_v1.school

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.enter.high_v1.ApiProvider
import com.enter.high_v1.MainActivity
import com.enter.high_v1.MyApplication
import com.enter.high_v1.ServerApi
import com.enter.high_v1.databinding.FragmentSchoolInfoBinding
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchoolInfoFragment(private val name: String) : Fragment() {
    private lateinit var binding: FragmentSchoolInfoBinding
    var schoolInfoAll : SchoolInfoResponse? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSchoolInfoBinding.inflate(layoutInflater, container, false)
        getSchoolResponseInfo()
        // schoolInfo?.let { MainActivity().schoolDetailInfo(0, it) }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity
        schoolInfoAll?.let { mainActivity.schoolDetailInfo(0, it) }
        binding.layInfoTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when(tab.position) {
                    0 -> schoolInfoAll?.let { mainActivity.schoolDetailInfo(0, it) }
                    1 -> schoolInfoAll?.let { mainActivity.schoolDetailInfo(1, it) }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun getSchoolResponseInfo() {
        val apiProvider = ApiProvider.getInstance().create(ServerApi::class.java)
        val token = "Bearer " + MyApplication.prefs.getPref("token", "")
        apiProvider.schoolInfo(token, name).enqueue(object : Callback<SchoolInfoResponse> {
            override fun onResponse(call: Call<SchoolInfoResponse>, response: Response<SchoolInfoResponse>) {
                if (response.isSuccessful) {
                    schoolInfoAll = response.body()
                    response.body()?.let { setSchoolInfo(it) }
                } else {
                    Log.d("server", response.code().toString())
                }
            }

            override fun onFailure(call: Call<SchoolInfoResponse>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(activity, "서버와의 연결에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setSchoolInfo(response: SchoolInfoResponse) {
        binding.textInfoName.text = response.name
        binding.textInfoType.text = response.Stype
        binding.textInfoPlace.text = response.location
    }
}