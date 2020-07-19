package com.example.mobile_final_project
import android.Manifest
import android.content.Context
import java.time.LocalDateTime
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class MainActivity : AppCompatActivity()
{
    private var mTrafficSpeedMeasurer: TrafficSpeedMeasurer? = null
    private var mTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*val permissionCheck = ContextCompat.checkSelfPermission(
            MainActivity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(
                MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
        }*/
        prameters()
        mTextView = findViewById(R.id.connection_class)
        mTrafficSpeedMeasurer = TrafficSpeedMeasurer(TrafficSpeedMeasurer.TrafficType.ALL)
        mTrafficSpeedMeasurer!!.startMeasuring()
        //println("hi")
    }

    override fun onDestroy()
    {
        super.onDestroy()
        mTrafficSpeedMeasurer?.stopMeasuring()
    }

    override fun onPause()
    {
        super.onPause()
        mTrafficSpeedMeasurer?.removeListener(mStreamSpeedListener)
    }

    override fun onResume()
    {
        super.onResume()
        mTrafficSpeedMeasurer?.registerListener(mStreamSpeedListener)
    }

    private val mStreamSpeedListener: ITrafficSpeedListener = object : ITrafficSpeedListener
    {
        override fun onTrafficSpeedMeasured(
            upStream: Double,
            downStream: Double
        ) {
            runOnUiThread {
                val upStreamSpeed: String =
                    Utils.parseSpeed(upStream, SHOW_SPEED_IN_BITS)
                val downStreamSpeed: String =
                    Utils.parseSpeed(downStream, SHOW_SPEED_IN_BITS)
                mTextView!!.text =
                    "Up Stream Speed: $upStreamSpeed\nDown Stream Speed: $downStreamSpeed"
            }
        }
    }

    companion object
    {
        private const val SHOW_SPEED_IN_BITS = false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun prameters()
    {
        var RSRP_SCView = findViewById<TextView>(R.id.RSRP_SC)
        var RSRP_NCView = findViewById<TextView>(R.id.RSRP_NC)
        var RSRQ_SCView = findViewById<TextView>(R.id.RSRQ_SC)
        var RSRQ_NCView = findViewById<TextView>(R.id.RSRQ_NC)
        var CINR_SCView = findViewById<TextView>(R.id.CINR_SC)
        var CINR_NCView = findViewById<TextView>(R.id.CINR_NC)
        var ACView = findViewById<TextView>(R.id.AC)
        var PLMNView = findViewById<TextView>(R.id.PLMN)
        var CellIdView = findViewById<TextView>(R.id.Cell_Id)
        var NetworkTypeView = findViewById<TextView>(R.id.NetworkType)
        var currentTime = findViewById<TextView>(R.id.current_time)

        var servingCellSignalStrength = 0
        var servingCellSignalQuality = 0
        var servingCellSignalnoise = 0
        var neighborCellSignalStrength = 0
        var neighborCellSignalQuality = 0
        var neighborCellSignalnoise = 0
        var servingCellTAC = 0
        var servingCellLAC = 0
        var servingCellPLMN = ""
        var servingCellRAC = 0
        var servingCellId = 0

        //Set an instance of telephony manager
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        //Check location permission
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
        }

        //Check type of network and assign parameters to signal strength and quality
        val cellInfoList = tm.allCellInfo
        for (cellInfo in cellInfoList)
        {
            if (cellInfo.isRegistered)
            {
                if (cellInfo is CellInfoLte)
                {
                    servingCellSignalStrength = cellInfo.cellSignalStrength.rsrp
                    servingCellSignalQuality = cellInfo.cellSignalStrength.rsrq
                    servingCellSignalnoise = cellInfo.cellSignalStrength.rssnr
                    servingCellTAC = cellInfo.cellIdentity.tac
                    servingCellPLMN = tm.networkOperator
                    servingCellId = cellInfo.cellIdentity.ci
                }
                else if (cellInfo is CellInfoWcdma && servingCellSignalStrength == 0)
                {
                    val b = cellInfo.cellSignalStrength.dbm
                    servingCellSignalStrength = b
                    servingCellLAC = cellInfo.cellIdentity.lac
                    servingCellPLMN = tm.networkOperator
                    servingCellId = cellInfo.cellIdentity.cid
                }
                else if (cellInfo is CellInfoGsm && servingCellSignalStrength == 0)
                {
                    val gsm = cellInfo.cellSignalStrength
                    servingCellSignalStrength = gsm.dbm
                    servingCellPLMN = tm.networkOperator
                    servingCellLAC = cellInfo.cellIdentity.lac
                    servingCellId = cellInfo.cellIdentity.cid
                }

            }
            else
            {
                if (cellInfo is CellInfoLte)
                {
                    neighborCellSignalStrength = cellInfo.cellSignalStrength.rsrp
                    neighborCellSignalQuality = cellInfo.cellSignalStrength.rsrq
                    neighborCellSignalnoise = cellInfo.cellSignalStrength.rssnr
                }
                else if (cellInfo is CellInfoWcdma && neighborCellSignalStrength == 0)
                {
                    val b = cellInfo.cellSignalStrength.dbm
                    neighborCellSignalStrength = b
                }
                else if (cellInfo is CellInfoGsm && neighborCellSignalStrength == 0)
                {
                    val gsm = cellInfo.cellSignalStrength
                    neighborCellSignalStrength = gsm.dbm
                }
            }
        }
        RSRP_SCView.text = "Serving cell signal strength : " + servingCellSignalStrength.toString()
        RSRP_NCView.text = "Neighbor cell signal strength : " + neighborCellSignalStrength.toString()
        RSRQ_SCView.text = "Serving cell signal quality : " + servingCellSignalQuality.toString()
        RSRQ_NCView.text = "Neighbor cell signal quality : " + neighborCellSignalQuality.toString()
        CINR_SCView.text = "Serving cell signal noise : " + servingCellSignalnoise.toString()
        CINR_NCView.text = "Neighbor cell signal noise : " + neighborCellSignalnoise.toString()
        PLMNView.text = "Serving cell PLMN : " + servingCellPLMN
        CellIdView.text = "Serving cell id : " + servingCellId.toString()
        NetworkTypeView.text = "Network Type : " + getNetworkType()

        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        val formatted = current.format(formatter)
        currentTime.text = "date and time : " +  formatted.toString()

        if (servingCellLAC != 0)
        {
            ACView.text = "Serving cell area code : " + servingCellLAC.toString()
        }
        else if(servingCellTAC != 0)
        {
            ACView.text = "Serving cell area code : " + servingCellTAC.toString()
        }
        else if(servingCellRAC != 0)
        {
            ACView.text = "Serving cell area code : " + servingCellRAC.toString()
        }
        //Build a request to turn on the location
    }
    fun getNetworkType(): String{
        val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var textV1 : String = ""
        when (telephonyManager.networkType) {
            7 -> textV1 = "1xRTT"
            4 -> textV1 = "CDMA"
            2 -> textV1 = "EDGE"
            14 -> textV1 = "eHRPD"
            5 -> textV1 = "EVDO rev. 0"
            6 -> textV1 = "EVDO rev. A"
            12 -> textV1 = "EVDO rev. B"
            1 -> textV1 = "GPRS"
            8 -> textV1 = "HSDPA"
            10 -> textV1 = "HSPA"
            15 -> textV1 = "HSPA+"
            9 -> textV1 = "HSUPA"
            11 -> textV1 = "iDen"
            13 -> textV1 = "LTE"
            3 -> textV1 = "UMTS"
            0 -> textV1 = "Unknown"
        }
        return textV1
    }
}