package com.specknet.pdiotapp.pose

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.ml.Model
import com.specknet.pdiotapp.ml.TfLiteModel
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.ThingyLiveData
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


typealias RecognitionListener = (recognition: List<Recognition>) -> Unit
private const val MAX_RESULT_DISPLAY = 3 // Maximum number of results displayed
private const val TAG = "TFL Classify"

class Pose : AppCompatActivity() {
    var time = 0f
    var activities = arrayOf("ascending_stairs", "descending_stairs", "desk_work", "lying_down_left", "lying_down_on_back",
        "lying_down_on_stomach", "lying_down_right", "general_movement", "running", "sitting_bent_backward", "sitting_bent_forward", "sitting",
        "standing", "walking", "general_movement", "general_movement", "general_movement", "general_movement")

    var currentActivityIndex = 0
    lateinit var currentActivity : String

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var thingyLiveUpdateReceiver: BroadcastReceiver
    lateinit var looperRespeck: Looper
    lateinit var looperThingy: Looper
    lateinit var activityImage: ImageView
    var resID: Int = R.drawable.general_movement

    var tfInput = FloatArray(50*6){0.toFloat()}
    var counter = 0
    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)
    val filterTestThingy = IntentFilter(Constants.ACTION_THINGY_BROADCAST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //val resources: Resources = context.resources
        //val resourceId: Int = resources.getIdentifier(agent, "drawable", context.packageName)
        //resID = resources.getIdentifier("standing", "drawable", packageName)
        setContentView(R.layout.activity_pose)


        //val pose = findViewById<View>(R.id.imageView) as ImageView
        //pose.setImageResource(resID)

        setUpImage()

// set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                    val liveData_ras =
                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData

                    val liveData_thi =
                        intent.getSerializableExtra(Constants.THINGY_LIVE_DATA) as ThingyLiveData

                    Log.d("Live", "onReceive: liveData = " + liveData_ras)

                    // get all relevant intent contents
                    val x = liveData_ras.accelX
                    val y = liveData_ras.accelY
                    val z = liveData_ras.accelZ
                    val g_x = liveData_ras.gyro.x
                    val g_y = liveData_ras.gyro.y
                    val g_z = liveData_ras.gyro.z

                    val thi_x = liveData_thi.accelX
                    val thi_y = liveData_thi.accelY
                    val thi_z = liveData_thi.accelZ





                    // set up connection with server
                    // if connected, use cloudClassifyActivity, otherwise use classifyActivity
                    val activity = cloudClassifyActivity(x, y, z, g_x, g_y, g_z, thi_x, thi_y, thi_z)
                    if (activity != "None"){
                        resID = resources.getIdentifier(activity, "drawable", packageName)
                        Log.d("activity is:", "$activity  $resID")
                        //pose.setImageResource(resID)
                        updateActivity()
                    }
                    time += 1
                }
            }
        }

        // register receiver on another thread
        val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
        handlerThreadRespeck.start()
        looperRespeck = handlerThreadRespeck.looper
        val handlerRespeck = Handler(looperRespeck)
        //handlerRespeck.post(Runnable { pose.setImageResource(resID)})
        this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)
    }

    fun updateActivity() {
        // take the first element from the queue
        // and update the graph with it
        runOnUiThread {
            //activityImage.notifyDataSetChanged()
            activityImage.setImageResource(resID)
            activityImage.invalidate()
            //activityImage.setVisibleXRangeMaximum(150f)
            //activityImage.moveViewToX(respeckChart.lowestVisibleX + 40)
        }


    }

    fun setUpImage(){
        resID = resources.getIdentifier("joke1", "drawable", packageName)
        activityImage = findViewById(R.id.imageView)
        activityImage.setImageResource(resID)
        activityImage.invalidate()
    }

    private class MyBroadcastReceiver(time1: Float, resID1: Int) : BroadcastReceiver() {
        var time = time1
        var resID = resID1
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

            val action = intent.action

            if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                val liveData =
                    intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData
                Log.d("Live", "onReceive: liveData = " + liveData)

                // get all relevant intent contents
                val x = liveData.accelX
                val y = liveData.accelY
                val z = liveData.accelZ
                val g_x = liveData.gyro.x
                val g_y = liveData.gyro.y
                val g_z = liveData.gyro.z

                val mypose = Pose()
                val activity = mypose.classifyActivity(x, y, z, g_x, g_y, g_z)
                Log.d("activity is:", "$activity.png")
                if (activity != "None"){
                    resID = mypose.resources.getIdentifier("$activity.png", "drawable", mypose.packageName)
                    val pose = mypose.findViewById<View>(R.id.imageView) as ImageView
                    pose.setImageResource(resID)
                }
                time += 1
            }
        }


    }

    fun cloudClassifyActivity(x: Float, y: Float, z: Float, x1:Float, y1:Float, z1: Float, thi_x: Float, thi_y: Float, thi_z: Float): String {
        if (counter <= 291){
            this.tfInput.set(counter, x)
            this.tfInput.set(counter+1, y)
            this.tfInput.set(counter+2, z)
            this.tfInput.set(counter+3, x1)
            this.tfInput.set(counter+4, y1)
            this.tfInput.set(counter+5, z1)
            this.tfInput.set(counter+6, thi_x)
            this.tfInput.set(counter+7, thi_y)
            this.tfInput.set(counter+8, thi_z)

            counter += 9
            Log.d("input", "$tfInput")
        }else if (counter > 291) {
            val model = Model.newInstance(this)


            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 50, 9), DataType.FLOAT32)
            inputFeature0.loadArray(tfInput)

            // convert to Json, then send this to the server

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val floatArray = outputFeature0.getFloatArray()
            currentActivityIndex = 0
            var maxProb = floatArray.max()
            if (maxProb != null) {
                currentActivityIndex = floatArray.indexOf(maxProb)
                currentActivity = activities[currentActivityIndex]
            }
            Log.d("index of max", "${currentActivityIndex}")
            Log.d("currentActivity", "${currentActivity}")

            // Releases model resources if no longer used.
            model.close()
            this.tfInput = FloatArray(50 * 9) { 0.toFloat() }
            counter = 0
            return currentActivity
        }
        return "None"
    }

    fun classifyActivity(x: Float, y: Float, z: Float, x1:Float, y1:Float, z1: Float): String {
        if (counter <= 294){
            this.tfInput.set(counter, x)
            this.tfInput.set(counter+1, y)
            this.tfInput.set(counter+2, z)
            this.tfInput.set(counter+3, x1)
            this.tfInput.set(counter+4, y1)
            this.tfInput.set(counter+5, z1)
            counter += 6
            Log.d("input", "$tfInput")
        }else if (counter > 294) {
            val model = Model.newInstance(this)


            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 50, 6), DataType.FLOAT32)
            inputFeature0.loadArray(tfInput)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer
            val floatArray = outputFeature0.getFloatArray()
            currentActivityIndex = 0
            var maxProb = floatArray.max()
            if (maxProb != null) {
                currentActivityIndex = floatArray.indexOf(maxProb)
                currentActivity = activities[currentActivityIndex]
            }
            Log.d("index of max", "${currentActivityIndex}")
            Log.d("currentActivity", "${currentActivity}")

            // Releases model resources if no longer used.
            model.close()
            this.tfInput = FloatArray(50 * 6) { 0.toFloat() }
            counter = 0
            return currentActivity
        }
        return "None"
    }


}