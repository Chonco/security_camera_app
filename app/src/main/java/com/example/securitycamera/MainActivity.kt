package com.example.securitycamera

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.securitycamera.adapter.ReportAdapter
import com.example.securitycamera.model.Report
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.Collections

class MainActivity : ComponentActivity() {
    private lateinit var reportAdapter: ReportAdapter
    private val reportReference = Firebase.database.getReference("reports")
    private val nowReference = Firebase.database.getReference("now")
    private val threadSafeReports = Collections.synchronizedMap(LinkedHashMap<String, Report>())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reportNowBtn = findViewById<FloatingActionButton>(R.id.report_now_btn)
        reportNowBtn.setOnClickListener { sendTakeReportNowSignal() }

        reportAdapter = ReportAdapter(baseContext) { toFullImageActivity(it) }

        val reportRecyclerView = findViewById<RecyclerView>(R.id.reports_holder)
        reportRecyclerView.layoutManager = GridLayoutManager(baseContext, 2)
        reportRecyclerView.adapter = reportAdapter
    }

    private fun sendTakeReportNowSignal() {
        Log.i("MainActivity", "Sending 'Take Report Now' Signal")
        nowReference.setValue("now")
    }

    private fun toFullImageActivity(report: Report) {
        val intent = Intent(this, ViewFullImage::class.java)
        intent.putExtra("imgURL", report.imageURL)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        listenReportsReference()
    }

    override fun onPause() {
        super.onPause()
        stopListeningReportReference()
    }

    private fun listenReportsReference() {
        Log.i("MainActivity", "Listening reports reference")
        reportReference.addChildEventListener(reportListener)
    }

    private fun stopListeningReportReference() {
        Log.i("MainActivity", "Stop listening reports reference")
        reportAdapter.submitList(null)
        reportReference.removeEventListener(reportListener)
    }

    private val reportListener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            synchronized(threadSafeReports) {
                threadSafeReports[snapshot.key] = snapshot.getValue<Report>()
                val tempList = threadSafeReports.values.toMutableList()
                reportAdapter.submitList(tempList)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            synchronized(threadSafeReports) {
                threadSafeReports[snapshot.key] = snapshot.getValue<Report>()
                val tempList = threadSafeReports.values.toMutableList()
                reportAdapter.submitList(tempList)
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            synchronized(threadSafeReports) {
                threadSafeReports.remove(snapshot.key)
                val tempList = threadSafeReports.values.toMutableList()
                reportAdapter.submitList(tempList)
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {
            Log.w(
                "SecurityCamera.MainActivity",
                "Failed to read value.",
                error.toException()
            )
        }
    }
}