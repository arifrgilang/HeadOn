package com.express.headon.home

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.express.headon.CustomRvItemDecor
import com.express.headon.R
import com.express.headon.ar.FaceArActivity
import com.express.headon.model.HeadObject
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var rvAdapter: HomeRvAdapter
    private val list = mutableListOf<HeadObject>()
    private lateinit var arrPath: Array<String>
    private lateinit var arrName: Array<String>
    private lateinit var arrImgUrl: Array<String>
    private lateinit var arrPrice: Array<String>
    private val OBJECT_RETURN_ID = 2004

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        generateList()
        initRv()
    }

    private fun generateList() {
        arrPath = resources.getStringArray(R.array.object_path)
        arrName = resources.getStringArray(R.array.object_name)
        arrImgUrl = resources.getStringArray(R.array.object_img_url)
        arrPrice = resources.getStringArray(R.array.object_price)
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
            object : HomeRvAdapter.OnBarangClickListener {
                override fun onClick(position: Int) {
                    navigateToAr(list[position])
                }
            }
        )
        with(rvBarang){
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = rvAdapter
            addItemDecoration(CustomRvItemDecor(16, "top"))
        }
    }

    private fun navigateToAr(obj: HeadObject){
        startActivityForResult(
            Intent(this@HomeActivity, FaceArActivity::class.java)
                .apply {
                    putExtra("objectPath", obj.path)
                    putExtra("objectName", obj.name)
                }
        , OBJECT_RETURN_ID
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OBJECT_RETURN_ID){
                val objectPosition = data!!
                    .extras!!
                    .getInt(FaceArActivity.OBJECT_RESULT_ID)
                navigateToAr(list[objectPosition])
            }
        }
    }
}
