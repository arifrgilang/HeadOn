package com.express.headon.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.express.headon.R
import com.express.headon.ar.FaceArActivity
import com.express.headon.model.HeadObject
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var rvAdapter: HomeRvAdapter
    private val list = mutableListOf<HeadObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        generateList()
        initRv()
    }

    private fun generateList() {
        val arrPath = resources.getStringArray(R.array.object_path)
        val arrName = resources.getStringArray(R.array.object_name)
        val arrImgUrl = resources.getStringArray(R.array.object_img_url)
        val arrPrice = resources.getStringArray(R.array.object_price)
        for(x in arrName.indices){
            list.add(
                HeadObject(
                    arrPath[x],
                    arrName[x],
                    "", // currently empty for debugging
                    arrImgUrl[x],
                    arrPrice[x]
                )
            )
        }
    }

    private fun initRv() {
        rvAdapter = HomeRvAdapter(
            this,
            list,
            object :
                HomeRvAdapter.OnBarangClickListener {
                override fun onClick(objectPath: String) {
                    startActivity(
                        Intent(this@HomeActivity, FaceArActivity::class.java)
                            .apply { putExtra("objectPath", objectPath) }
                    )
                }
            })
        with(rvBarang){
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = rvAdapter
        }
    }
}
