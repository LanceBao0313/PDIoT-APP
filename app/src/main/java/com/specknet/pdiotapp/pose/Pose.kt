package com.specknet.pdiotapp.pose

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.ml.Model
import org.tensorflow.lite.gpu.CompatibilityList

typealias RecognitionListener = (recognition: List<Recognition>) -> Unit
private const val MAX_RESULT_DISPLAY = 3 // Maximum number of results displayed
private const val TAG = "TFL Classify"

class Pose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pose)
        val pose = findViewById<View>(R.id.imageView) as ImageView
        pose.setImageResource(R.drawable.pose1)
    }

    private class poseEstimator(ctx: Context, private val listener: RecognitionListener) :



}