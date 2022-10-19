package com.specknet.pdiotapp.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RESpeckLiveData
import com.specknet.pdiotapp.utils.ThingyLiveData
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import kotlin.collections.ArrayList
import com.specknet.pdiotapp.ml.TfLiteModel


class LiveDataActivity : AppCompatActivity() {

    // global graph variables
    lateinit var dataSet_res_accel_x: LineDataSet
    lateinit var dataSet_res_accel_y: LineDataSet
    lateinit var dataSet_res_accel_z: LineDataSet

    lateinit var dataSet_thingy_accel_x: LineDataSet
    lateinit var dataSet_thingy_accel_y: LineDataSet
    lateinit var dataSet_thingy_accel_z: LineDataSet

    var time = 0f
    var a3 = 0
    var activities = arrayOf("standing", "sitting", "sitting forward", "sitting backward", "lying up",
    "lying down", "lying left", "lying right", "walking", "running", "walk upstairs", "walk downstaris",
    "deskwork", "movement", "movement", "movement", "movement", "movement")
    lateinit var currentActivity : String

    lateinit var allRespeckData: LineData

    lateinit var allThingyData: LineData

    lateinit var respeckChart: LineChart
    lateinit var thingyChart: LineChart

    // global broadcast receiver so we can unregister it
    lateinit var respeckLiveUpdateReceiver: BroadcastReceiver
    lateinit var thingyLiveUpdateReceiver: BroadcastReceiver
    lateinit var looperRespeck: Looper
    lateinit var looperThingy: Looper
    var tfinput = FloatArray(50*6){0.toFloat()}
    var counter = 0
//    var tfinput = Array<FloatArray>(50,6)
    val filterTestRespeck = IntentFilter(Constants.ACTION_RESPECK_LIVE_BROADCAST)
    val filterTestThingy = IntentFilter(Constants.ACTION_THINGY_BROADCAST)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_data)

        setupCharts()

        // set up the broadcast receiver
        respeckLiveUpdateReceiver = object : BroadcastReceiver() {
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

                    modelinput(x,y,z,g_x,g_y,g_z)

                    Log.d("test", "$a3")



//                    val input = makingdatapacket(x,y,z,g_x,g_y,g_z)
//
//                    val model = TfLiteModel.newInstance(context)
//
//// Creates inputs for reference.
//                    val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 50, 6), DataType.FLOAT32)
//                    inputFeature0.loadArray(tfinput)
//
//// Runs model inference and gets result.
//                    val outputs = model.process(inputFeature0)
//                    val outputFeature0 = outputs.outputFeature0AsTensorBuffer
//                    Log.d("testtest", "${outputFeature0.floatArray[0]}")
//                    var a = 0f
//                    var a1 = FloatArray(18){0.toFloat()}
//                    for(i in 0..17){
//                        a1[i] = outputFeature0.floatArray[i]
//                    }
////                    var max = a1[0]
////                    var counter1 = 0
////                    for(j in 1..17 ){
////                        if (a1[j] >= max){
////                            max = a1[j]
////                            counter1 =
////                        }
////                    }
//                    var a3 = 0
//                    var a2= a1.max()
//                    if (a2 != null) {
//
//                       a3 = a1.indexOf(a2)
//
//                    }
//
//                    Log.d("index of max", "${a2}")
//
//                    Log.d("index of max", "${a3}")
//
//
//// Releases model resources if no longer used.
//                    model.close()
//
//
//
//                    print(input)
//                    assetManager = ctx.assets
//                    // grab files with tflite extensions
//                    models =
//                        assetManager.list(ActivityClassifier.MODEL_DIR)?.filter { f -> f.endsWith(".tflite") }
//                            .orEmpty()
//                    Log.i(TAG, "models found: $models")
//
//
//                    val model = .newInstance(context)


                    time += 1
                    updateGraph("respeck", x, y, z)

                }
            }
        }



        // register receiver on another thread
        val handlerThreadRespeck = HandlerThread("bgThreadRespeckLive")
        handlerThreadRespeck.start()
        looperRespeck = handlerThreadRespeck.looper
        val handlerRespeck = Handler(looperRespeck)
        this.registerReceiver(respeckLiveUpdateReceiver, filterTestRespeck, null, handlerRespeck)

        // set up the broadcast receiver
        thingyLiveUpdateReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                Log.i("thread", "I am running on thread = " + Thread.currentThread().name)

                val action = intent.action

                if (action == Constants.ACTION_THINGY_BROADCAST) {

                    val liveData =
                        intent.getSerializableExtra(Constants.THINGY_LIVE_DATA) as ThingyLiveData
                    Log.d("Live", "onReceive: liveData = " + liveData)

                    // get all relevant intent contents
                    val x = liveData.accelX
                    val y = liveData.accelY
                    val z = liveData.accelZ


                    time += 1

                    updateGraph("thingy", x, y, z)

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


    fun setupCharts() {
        respeckChart = findViewById(R.id.respeck_chart)
        thingyChart = findViewById(R.id.thingy_chart)

        // Respeck

        time = 0f
        val entries_res_accel_x = ArrayList<Entry>()
        val entries_res_accel_y = ArrayList<Entry>()
        val entries_res_accel_z = ArrayList<Entry>()

        dataSet_res_accel_x = LineDataSet(entries_res_accel_x, "Accel X")
        dataSet_res_accel_y = LineDataSet(entries_res_accel_y, "Accel Y")
        dataSet_res_accel_z = LineDataSet(entries_res_accel_z, "Accel Z")

        dataSet_res_accel_x.setDrawCircles(false)
        dataSet_res_accel_y.setDrawCircles(false)
        dataSet_res_accel_z.setDrawCircles(false)

        dataSet_res_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_res_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_res_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsRes = ArrayList<ILineDataSet>()
        dataSetsRes.add(dataSet_res_accel_x)
        dataSetsRes.add(dataSet_res_accel_y)
        dataSetsRes.add(dataSet_res_accel_z)

        allRespeckData = LineData(dataSetsRes)
        respeckChart.data = allRespeckData
        respeckChart.invalidate()

        // Thingy

        time = 0f
        val entries_thingy_accel_x = ArrayList<Entry>()
        val entries_thingy_accel_y = ArrayList<Entry>()
        val entries_thingy_accel_z = ArrayList<Entry>()

        dataSet_thingy_accel_x = LineDataSet(entries_thingy_accel_x, "Accel X")
        dataSet_thingy_accel_y = LineDataSet(entries_thingy_accel_y, "Accel Y")
        dataSet_thingy_accel_z = LineDataSet(entries_thingy_accel_z, "Accel Z")

        dataSet_thingy_accel_x.setDrawCircles(false)
        dataSet_thingy_accel_y.setDrawCircles(false)
        dataSet_thingy_accel_z.setDrawCircles(false)

        dataSet_thingy_accel_x.setColor(
            ContextCompat.getColor(
                this,
                R.color.red
            )
        )
        dataSet_thingy_accel_y.setColor(
            ContextCompat.getColor(
                this,
                R.color.green
            )
        )
        dataSet_thingy_accel_z.setColor(
            ContextCompat.getColor(
                this,
                R.color.blue
            )
        )

        val dataSetsThingy = ArrayList<ILineDataSet>()
        dataSetsThingy.add(dataSet_thingy_accel_x)
        dataSetsThingy.add(dataSet_thingy_accel_y)
        dataSetsThingy.add(dataSet_thingy_accel_z)

        allThingyData = LineData(dataSetsThingy)
        thingyChart.data = allThingyData
        thingyChart.invalidate()
    }

    fun updateGraph(graph: String, x: Float, y: Float, z: Float) {
        // take the first element from the queue
        // and update the graph with it
        if (graph == "respeck") {
            dataSet_res_accel_x.addEntry(Entry(time, x))
            dataSet_res_accel_y.addEntry(Entry(time, y))
            dataSet_res_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allRespeckData.notifyDataChanged()
                respeckChart.notifyDataSetChanged()
                respeckChart.invalidate()
                respeckChart.setVisibleXRangeMaximum(150f)
                respeckChart.moveViewToX(respeckChart.lowestVisibleX + 40)
            }
        } else if (graph == "thingy") {
            dataSet_thingy_accel_x.addEntry(Entry(time, x))
            dataSet_thingy_accel_y.addEntry(Entry(time, y))
            dataSet_thingy_accel_z.addEntry(Entry(time, z))

            runOnUiThread {
                allThingyData.notifyDataChanged()
                thingyChart.notifyDataSetChanged()
                thingyChart.invalidate()
                thingyChart.setVisibleXRangeMaximum(150f)
                thingyChart.moveViewToX(thingyChart.lowestVisibleX + 40)
            }
        }


    }
    fun makingdatapacket(x: Float, y: Float, z: Float, x1:Float, y1:Float, z1: Float): Array<Float> {
        val arraypacket = arrayOf(x,y,z, x1,y1,z1)
        return arraypacket

    }

    fun modelinput(x: Float, y: Float, z: Float, x1:Float, y1:Float, z1: Float) {
         if (counter <= 294){
             this.tfinput.set(counter, x)
             this.tfinput.set(counter+1, y)
             this.tfinput.set(counter+2, z)
             this.tfinput.set(counter+3, x1)
             this.tfinput.set(counter+4, y1)
             this.tfinput.set(counter+5, z1)
             counter += 6
             Log.d("input", "$tfinput")
         }else if (counter > 294){
             val model = TfLiteModel.newInstance(this)

// Creates inputs for reference.
             val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 50, 6), DataType.FLOAT32)
             inputFeature0.loadArray(tfinput)

// Runs model inference and gets result.
             val outputs = model.process(inputFeature0)
             val outputFeature0 = outputs.outputFeature0AsTensorBuffer
             val floatArray = outputFeature0.getFloatArray()
             Log.d("testtest", "${outputFeature0.floatArray[0]}")

             a3 = 0
             var a2= floatArray.max()
             if (a2 != null) {
                 a3 = floatArray.indexOf(a2)
                 currentActivity = activities[a3]
             }
             Log.d("index of max", "${a3}")
             Log.d("currentActivity", "${activities[a3]}")

// Releases model resources if no longer used.
             model.close()
             this.tfinput = FloatArray(50*6){0.toFloat()}
             counter = 0
         }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(respeckLiveUpdateReceiver)
        unregisterReceiver(thingyLiveUpdateReceiver)
        looperRespeck.quit()
        looperThingy.quit()
    }
}
