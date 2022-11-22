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
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.ml.Model
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.ThingyLiveData
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import com.google.gson.annotations.SerializedName
import com.github.kittinunf.result.Result

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

    var tfInput_res = FloatArray(50*7){0.toFloat()}
    var tfInput_thi = FloatArray(50*10){0.toFloat()}
    var tfInput_res_1 = FloatArray(75*7){0.toFloat()}
    var tfInput_thi_1 = FloatArray(75*10){0.toFloat()}
    var counter_res = 0
    var counter_thi = 0
    var counter_res_1 = 0
    var counter_thi_1 = 0
    var send_counter_thi = 0
    var send_counter_res = 0
    var first_loop = true
    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)
    val filterTestThingy = IntentFilter(Constants.ACTION_THINGY_BROADCAST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_pose)


        setUpImage()

        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_RESPECK_LIVE_BROADCAST) {

                    val liveData_res =
                        intent.getSerializableExtra(Constants.RESPECK_LIVE_DATA) as RESpeckLiveData

                    Log.d("Live", "onReceive: liveData = " + liveData_res)

                    // get all relevant intent contents
                    val phone_time = liveData_res.phoneTimestamp - 1660000000000
                    val ras_x = liveData_res.accelX
                    val ras_y = liveData_res.accelY
                    val ras_z = liveData_res.accelZ
                    val ras_g_x = liveData_res.gyro.x
                    val ras_g_y = liveData_res.gyro.y
                    val ras_g_z = liveData_res.gyro.z


                    // set up connection with server
                    // if connected, use cloudClassifyActivity, otherwise use classifyActivity
                    if (send_counter_thi == 50 && send_counter_res == 50){
                        val activity = cloudClassifyActivity(tfInput_res_1, tfInput_thi_1,1)
                        if (activity != "None"){
                            resID = resources.getIdentifier(activity, "drawable", packageName)
                            Log.d("activity is:", "$activity  $resID")
                            //pose.setImageResource(resID)
                            updateActivity()
                        }
                        first_loop = false
                    }else if (send_counter_thi == 75 && send_counter_res == 75){
                        val activity = cloudClassifyActivity(tfInput_res_1, tfInput_thi_1,2)
                        if (activity != "None"){
                            resID = resources.getIdentifier(activity, "drawable", packageName)
                            Log.d("activity is:", "$activity  $resID")
                            updateActivity()
                        }
                        first_loop = false
                    }
                    else if(send_counter_thi == 25 && send_counter_res == 25 && !first_loop){
                        val activity = cloudClassifyActivity(tfInput_res_1, tfInput_thi_1,3)
                        if (activity != "None" && activity != "desk_work"){
                            resID = resources.getIdentifier(activity, "drawable", packageName)
                            Log.d("activity is:", "$activity  $resID")
                            updateActivity()
                        }
                    }
                    storeResData(phone_time.toFloat(), ras_x, ras_y, ras_z, ras_g_x, ras_g_y, ras_g_z)
                    time += 1
                }
            }
        }

        // register receiver on another thread
        val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
        handlerThreadRespeck.start()
        looperRespeck = handlerThreadRespeck.looper
        val handlerRespeck = Handler(looperRespeck)
        this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)


        thingyLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_THINGY_BROADCAST) {

                    val liveData_thi =
                        intent.getSerializableExtra(Constants.THINGY_LIVE_DATA) as ThingyLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData_thi)

                    // get all relevant intent contents
                    val phone_time = liveData_thi.phoneTimestamp - 1660000000000
                    val thi_x = liveData_thi.accelX
                    val thi_y = liveData_thi.accelY
                    val thi_z = liveData_thi.accelZ
                    val thi_g_x = liveData_thi.gyro.x
                    val thi_g_y = liveData_thi.gyro.y
                    val thi_g_z = liveData_thi.gyro.z
                    val thi_m_x = liveData_thi.mag.x
                    val thi_m_y = liveData_thi.mag.y
                    val thi_m_z = liveData_thi.mag.z

                    time += 1

                    // set up connection with server
                    // if connected, use cloudClassifyActivity, otherwise use classifyActivity

                    Log.d("send_counter", "$send_counter_thi  $send_counter_res")
                    if (send_counter_thi == 50 && send_counter_res == 50){
                        var activity = cloudClassifyActivity(tfInput_res_1, tfInput_thi_1,1)
                        if (activity != "None"){
                            if(activity.equals("desk_work")){
                                activity = "sitting"
                            }
                            resID = resources.getIdentifier(activity, "drawable", packageName)
                            Log.d("activity is:", "$activity  $resID")
                            //pose.setImageResource(resID)
                            updateActivity()
                        }
                        first_loop = false
                    }else if (send_counter_thi == 75 && send_counter_res == 75){
                        var activity = cloudClassifyActivity(tfInput_res_1, tfInput_thi_1,2)
                        if (activity != "None"){
                            if(activity.equals("desk_work")){
                                activity = "sitting"
                            }
                            resID = resources.getIdentifier(activity, "drawable", packageName)
                            Log.d("activity is:", "$activity  $resID")
                            //pose.setImageResource(resID)
                            updateActivity()
                        }
                        first_loop = false
                    }
                    else if(send_counter_thi == 25 && send_counter_res == 25 && !first_loop){
                        var activity = cloudClassifyActivity(tfInput_res_1, tfInput_thi_1,3)
                        if (activity != "None"){
                            if(activity.equals("desk_work")){
                                activity = "sitting"
                            }
                            resID = resources.getIdentifier(activity, "drawable", packageName)
                            Log.d("activity is:", "$activity  $resID")
                            //pose.setImageResource(resID)
                            updateActivity()
                        }
                    }
                    storeThiData(phone_time.toFloat(), thi_x, thi_y, thi_z, thi_g_x, thi_g_y, thi_g_z, thi_m_x, thi_m_y, thi_m_z)
                }
            }
        }

        // register receiver on another thread
        val handlerThreadThingy = HandlerThread("bgThreadThingyLive")
        handlerThreadThingy.start()
        looperThingy = handlerThreadThingy.looper
        val handlerThingy = Handler(looperThingy)
        this.registerReceiver(thingyLiveUpdateReceiver, filterTestThingy, null, handlerThingy)
    }

    fun updateActivity() {

        // take the first element from the queue
        // and update the graph with it
        runOnUiThread {
            //activityImage.notifyDataSetChanged()
            activityImage.setImageResource(resID)
            activityImage.invalidate()

        }


    }

    fun setUpImage(){
        resID = resources.getIdentifier("general_movement", "drawable", packageName)
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

    fun storeResData(time: Float, x: Float, y: Float, z: Float, x1:Float, y1:Float, z1: Float){
        if ((counter_res_1 <= 168 && send_counter_thi <= 25 && send_counter_res <= 24)||
            (counter_res_1 <= 343 && send_counter_thi <= 50 && send_counter_res <= 49 && send_counter_thi >= 25 && send_counter_res >= 25)||
            (counter_res_1 <= 518 && send_counter_thi <= 75 && send_counter_res <= 74 && send_counter_thi >= 50 && send_counter_res >= 50)){
            this.tfInput_res_1.set(counter_res_1, time)
            this.tfInput_res_1.set(counter_res_1+1, x)
            this.tfInput_res_1.set(counter_res_1+2, y)
            this.tfInput_res_1.set(counter_res_1+3, z)
            this.tfInput_res_1.set(counter_res_1+4, x1)
            this.tfInput_res_1.set(counter_res_1+5, y1)
            this.tfInput_res_1.set(counter_res_1+6, z1)

            counter_res_1 += 7
            send_counter_res += 1
        }

    }

    fun storeThiData(time: Float,thi_x: Float, thi_y: Float, thi_z: Float, thi_g_x: Float, thi_g_y: Float, thi_g_z: Float, thi_m_x: Float, thi_m_y: Float, thi_m_z: Float){
        if ((counter_thi_1 <= 240 && send_counter_res <= 25 && send_counter_thi <= 24) ||
            (counter_thi_1 <= 490 && send_counter_res <= 50 && send_counter_thi <= 49 && send_counter_res >= 25 && send_counter_thi >= 25) ||
            (counter_thi_1 <= 740 && send_counter_res <= 75 && send_counter_thi <= 74 && send_counter_res >= 50 && send_counter_thi >= 50)){
            this.tfInput_thi_1.set(counter_thi_1, time)
            this.tfInput_thi_1.set(counter_thi_1+1, thi_x)
            this.tfInput_thi_1.set(counter_thi_1+2, thi_y)
            this.tfInput_thi_1.set(counter_thi_1+3, thi_z)
            this.tfInput_thi_1.set(counter_thi_1+4, thi_g_x)
            this.tfInput_thi_1.set(counter_thi_1+5, thi_g_y)
            this.tfInput_thi_1.set(counter_thi_1+6, thi_g_z)
            this.tfInput_thi_1.set(counter_thi_1+7, thi_m_x)
            this.tfInput_thi_1.set(counter_thi_1+8, thi_m_y)
            this.tfInput_thi_1.set(counter_thi_1+9, thi_m_z)

            counter_thi_1 += 10
            send_counter_thi += 1
        }
    }


    fun cloudClassifyActivity(resData_2: FloatArray, thiData_2: FloatArray, flag: Int): String {

        if(flag == 1){
            val thiStr = thiDataFormater(thiData_2.sliceArray(0..499))
            val resStr = resDataFormater(resData_2.sliceArray(0..349))

            Log.d("http", "data1 sent!")
            Log.d("timestamp333:", "$thiStr")
            val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
            val student_id = sharedPreferences.getString(Constants.USER_ID,"")
            val (request, response, result) = Fuel.post("http://34.89.117.73:5000/predict")
                .jsonBody("{ \"id\" : \"$student_id\", \"thi\" : ${thiStr}, \"res\" : ${resStr}}")
                .responseString()

            when (result) {
                is Result.Failure -> {
                    Log.d("http", "fail")
                    val ex = result.getException()
                    currentActivity = "None"
                    println(ex)
                }
                is Result.Success -> {
                    Log.d("http", "success")
                    val data = result.get()
                    currentActivity = data
                    //println(data)
                }
            }

            Log.d("currentActivity", currentActivity)
            return currentActivity
        }

        //if (counter_res_1 == 525 && counter_thi_1 == 750){
        if(flag == 2){

            val thiStr = thiDataFormater(thiData_2.sliceArray(250..749))
            val resStr = resDataFormater(resData_2.sliceArray(175..524))

            Log.d("http", "data2 sent!")
            Log.d("timestamp333:", "$thiStr")
            val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
            val student_id = sharedPreferences.getString(Constants.USER_ID,"")
            val (request, response, result) = Fuel.post("http://34.89.117.73:5000/predict")
                .jsonBody("{ \"id\" : \"$student_id\", \"thi\" : ${thiStr}, \"res\" : ${resStr}}")
                .responseString()

            when (result) {
                is Result.Failure -> {
                    Log.d("http", "fail")
                    val ex = result.getException()
                    currentActivity = "None"
                    println(ex)
                }
                is Result.Success -> {
                    Log.d("http", "success")
                    val data = result.get()
                    currentActivity = data
                    //println(data)
                }
            }

            counter_res_1 = 0
            counter_thi_1 = 0
            send_counter_res = 0
            send_counter_thi = 0
            Log.d("currentActivity", currentActivity)
            return currentActivity
        }
        if(flag == 3){

            val thiStr = thiDataFormater(thiData_2.sliceArray(500..749) + thiData_2.sliceArray(0..249))
            val resStr = resDataFormater(resData_2.sliceArray(350..524) + resData_2.sliceArray(0..174))

            Log.d("http", "data3 sent!")

            val sharedPreferences = getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
            val student_id = sharedPreferences.getString(Constants.USER_ID,"")

            val (request, response, result) = Fuel.post("http://34.89.117.73:5000/predict")
                .jsonBody("{ \"id\" : \"$student_id\", \"thi\" : ${thiStr}, \"res\" : ${resStr}}")
                .responseString()
            Log.d("student_id:", "$student_id")
            when (result) {
                is Result.Failure -> {
                    Log.d("http", "fail")
                    val ex = result.getException()
                    currentActivity = "None"
                    println(ex)
                }
                is Result.Success -> {
                    Log.d("http", "success")
                    val data = result.get()
                    currentActivity = data
                    //println(data)
                }
            }



            Log.d("currentActivity", currentActivity)
            return currentActivity
        }


        return "None"
    }

    fun resDataFormater(resData: FloatArray): String{
        var str: String = "[["
        var counter = 1
        for (i in resData){
            if(counter % 7 != 0){
                str = str + i + ","
            }else{
                str = str + i + "],["
            }
            counter++
        }
        return str.dropLast(3)+"]]"
    }

    fun thiDataFormater(thiData: FloatArray): String{
        var str: String = "[["
        var counter = 1
        for (i in thiData){
            if(counter % 10 != 0){
                str = str + i + ","
            }else{
                str = str + i + "],["
            }
            counter++
        }
        Log.d("thi_counter", "${counter}")
        return str.dropLast(3)+"]]"
    }

    data class JsonDataParser(
        @SerializedName("id") val id: String,
        @SerializedName("res") val res: String,
        @SerializedName("thi") val thi: String
    )


    fun classifyActivity(x: Float, y: Float, z: Float, x1:Float, y1:Float, z1: Float): String {
        if (counter_res <= 294){
            this.tfInput_res.set(counter_res, x)
            this.tfInput_res.set(counter_res+1, y)
            this.tfInput_res.set(counter_res+2, z)
            this.tfInput_res.set(counter_res+3, x1)
            this.tfInput_res.set(counter_res+4, y1)
            this.tfInput_res.set(counter_res+5, z1)
            counter_res += 6
            Log.d("input", "$tfInput_res")
        }else if (counter_res > 294) {
            val model = Model.newInstance(this)


            // Creates inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 50, 6), DataType.FLOAT32)
            inputFeature0.loadArray(tfInput_res)

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
            this.tfInput_res = FloatArray(50 * 6) { 0.toFloat() }
            counter_res = 0
            return currentActivity
        }
        return "None"
    }


}