package com.enter.high_v1.school

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.enter.high_v1.ApiProvider
import com.enter.high_v1.MainActivity
import com.enter.high_v1.MyApplication
import com.enter.high_v1.ServerApi
import com.enter.high_v1.databinding.FragmentSchoolRecommendBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SchoolRecommendFragment(private val first: String, private val second: String) : Fragment() {
    private lateinit var binding: FragmentSchoolRecommendBinding
    // private val mainActivity = activity as MainActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSchoolRecommendBinding.inflate(layoutInflater, container, false)
        getSchoolList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textRecommendNick.text = MyApplication.prefs.getPref("nick", "")
        binding.imgRecommendProfile.setOnClickListener {
            MainActivity().addFragment(2)
        }
    }

    private fun getSchoolList() {
        val token = "Bearer " + MyApplication.prefs.getPref("token", "")
        val apiProvider = ApiProvider.getInstance().create(ServerApi::class.java)
        apiProvider.recommendSchool(token, first, second).enqueue(object : Callback<SchoolRecommendResponse> {
            override fun onResponse(call: Call<SchoolRecommendResponse>, response: Response<SchoolRecommendResponse>) {
                if (response.isSuccessful) {
                    val schoolList1: List<SchoolRecommendData>? = response.body()?.firstData
                    val schoolList2: List<SchoolRecommendData>? = response.body()?.secondData
                    val schoolList: List<SchoolRecommendData> = (schoolList1 ?: emptyList()) + (schoolList2 ?: emptyList())

                    val adapter = SchoolRecommendAdapter(schoolList)
                    binding.recyclerRecommend.adapter = adapter
                    binding.recyclerRecommend.layoutManager = LinearLayoutManager(activity)
                } else {
                    Log.d("server", response.code().toString())
                }
            }
            override fun onFailure(call: Call<SchoolRecommendResponse>, t: Throwable) {
                Log.d("server", t.message.toString())
                Toast.makeText(activity, "서버 통신에 실패하였습니다", Toast.LENGTH_SHORT).show()
            }
        })
    }
}