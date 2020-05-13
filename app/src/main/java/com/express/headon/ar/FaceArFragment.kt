package com.express.headon.ar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.sceneform.ux.ArFragment
import java.util.*

class FaceArFragment : ArFragment(){
    // tell arcore we intend to detect faces
    override fun getSessionConfiguration(session: Session?): Config =
        Config(session).apply { augmentedFaceMode = Config.AugmentedFaceMode.MESH3D }

    // request arcore to use front facing camera
    override fun getSessionFeatures(): MutableSet<Session.Feature> =
        EnumSet.of(Session.Feature.FRONT_CAMERA)

    // disable plane detection here
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val frameLayout = super.onCreateView(inflater, container, savedInstanceState) as? FrameLayout
        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        return  frameLayout
    }
}