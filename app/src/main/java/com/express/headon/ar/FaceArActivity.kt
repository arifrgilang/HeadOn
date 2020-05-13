package com.express.headon.ar

import android.app.ActivityManager
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.express.headon.R
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_wear.*

class FaceArActivity : AppCompatActivity() {
    private val MIN_OPENGL_VERSION = 3.0
    private lateinit var arFragment: FaceArFragment
    private var faceMeshTexture: Texture? = null
    private var regionsRenderable: ModelRenderable? = null

    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var changeModel: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear)
        if (!checkIsSupportedDeviceOrFinish()) return
        arFragment = fragmentFace as FaceArFragment
        setRenderable(intent.getStringExtra("objectPath")!!) // load model

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
}
