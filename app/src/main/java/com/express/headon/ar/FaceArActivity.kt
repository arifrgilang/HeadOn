package com.express.headon.ar

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.express.headon.R
import com.express.headon.home.HomeRvAdapter
import com.express.headon.model.HeadObject
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_face_ar.*

class FaceArActivity : AppCompatActivity() {
    private lateinit var rvAdapter: FaceArRvAdapter
    private val list = mutableListOf<HeadObject>()
    private val arrPath = resources.getStringArray(R.array.object_path)
    private val arrName = resources.getStringArray(R.array.object_name)
    private val arrImgUrl = resources.getStringArray(R.array.object_img_url)
    private val arrPrice = resources.getStringArray(R.array.object_price)

    private val MIN_OPENGL_VERSION = 3.0
    private lateinit var arFragment: FaceArFragment
    private var faceMeshTexture: Texture? = null
    private var regionsRenderable: ModelRenderable? = null

    companion object{
        val OBJECT_RESULT_ID = "RESULT"
    }

    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
//    private var changeModel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_ar)

        if (!checkIsSupportedDeviceOrFinish()) return
        initView()
        generateList()
        initRv()
        // make sure camera renderfirst
        val sceneView = arFragment.arSceneView.apply {
            cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        }

        val scene = sceneView.scene
        scene.addOnUpdateListener {
            val faceList = sceneView.session!!.getAllTrackables(AugmentedFace::class.java)
            // make new AugmentedFaceNodes for any new faces
            for(face in faceList){
                if(!faceNodeMap.containsKey(face)){
                    // set the model when face is detected
                    with(AugmentedFaceNode(face)){
                        setParent(scene)
                        faceRegionsRenderable = regionsRenderable
                        createTexture()
                        faceNodeMap[face] = this
                    }
                }
            }

            // remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking
            val faceIterator = faceNodeMap.entries.iterator()
            while (faceIterator.hasNext()) {
                val entry = faceIterator.next()
                val face = entry.key
                if (face.trackingState == TrackingState.STOPPED) {
                    with(entry.value) {
                        setParent(null)
                        children.clear()
                    }
                    faceIterator.remove()
                }
            }
        }
    }

    private fun initRv() {
        rvAdapter = FaceArRvAdapter(
            this,
            list,
            object : FaceArRvAdapter.OnBarangClickListener{
                override fun onClick(position: Int) {
                    changeArObject(position)
                }
            }
        )
        with(rvFaceAr){
            layoutManager = LinearLayoutManager(
                this@FaceArActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = rvAdapter
        }
    }

    private fun changeArObject(position: Int) {
        this.setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putExtras(
                    Bundle().apply {
                        putInt(OBJECT_RESULT_ID, position)
                    }
                )
            }
        )
        finish()
    }

    private fun initView() {
        arFragment = fragmentFace as FaceArFragment
        setRenderable(intent.getStringExtra("objectPath")!!) // load model
        tvTitle.text = intent.getStringExtra("objectName")
        ivBack.setOnClickListener { finish() }
    }

    private fun createTexture(){
        Texture.builder()
            .setSource(this, R.drawable.texture)
            .build()
            .thenAccept { texture -> faceMeshTexture = texture }
    }

    private fun setRenderable(fileName: String){
        ModelRenderable.builder()
            .setSource(this, Uri.parse(fileName))
            .build()
            .thenAccept { modelRenderable ->
                regionsRenderable = modelRenderable
//                modelRenderable.isShadowCaster = false
//                modelRenderable.isShadowReceiver = false
            }
    }

    private fun checkIsSupportedDeviceOrFinish() : Boolean {
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(this, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString =  (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.deviceConfigurationInfo
            ?.glEsVersion

        openGlVersionString?.let { s ->
            if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
                Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show()
                finish()
                return false
            }
        }
        return true
    }

    private fun generateList() {
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
}
