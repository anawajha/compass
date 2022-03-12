package com.anawajha.compass

import android.animation.ObjectAnimator
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity(), SensorEventListener {
    lateinit var sm: SensorManager
    private var accelerometor: Sensor? = null
    private var magenetic_filed: Sensor? = null
    private var accValues = FloatArray(3)
    private var mfValues = FloatArray(3)
    private var rotationMatrix = FloatArray(9)
    private var orientationValues = FloatArray(3)
    lateinit var compass: ImageView
    lateinit var degrees:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sm = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magenetic_filed = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        compass = findViewById(R.id.img_compass)
        degrees = findViewById(R.id.tv_degrees)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            mfValues = event.values
        }

        if (event!!.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accValues = event.values
        }
        SensorManager.getRotationMatrix(rotationMatrix, null, accValues, mfValues)
        SensorManager.getOrientation(rotationMatrix, orientationValues)

        val azimuth = orientationValues[0]
        val pitch = orientationValues[1]
        val roll = orientationValues[2]

        compass(azimuth, pitch, roll)

    }

    private fun compass(azimuth: Float, pitch: Float, roll: Float) {
        ObjectAnimator.ofFloat(compass, "rotation",Math.toDegrees(-azimuth.toDouble()).toFloat()).apply {
            duration = 100
            start()
        }


        val deg = Math.toDegrees(azimuth.toDouble())
        if (deg >= 0){
            degrees.text = (deg.toInt()).toString()
        }else {
            degrees.text = (deg.toInt() + 306).toString()
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        if (accelerometor != null && magenetic_filed != null) {
            sm.registerListener(this, accelerometor, SensorManager.SENSOR_DELAY_UI)
            sm.registerListener(this, magenetic_filed, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sm.unregisterListener(this)
    }


}