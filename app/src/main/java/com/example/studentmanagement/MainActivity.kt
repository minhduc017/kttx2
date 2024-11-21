package com.example.studentmanagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.btnManageStudents).setOnClickListener { view: View? ->
            val intent = Intent(
                this@MainActivity,
                StudentManagementActivity::class.java
            )
            startActivity(intent)
        }

        findViewById<View>(R.id.btnManageClasses).setOnClickListener { view: View? ->
            val intent = Intent(
                this@MainActivity,
                ClassManagementActivity::class.java
            )
            startActivity(intent)
        }
    }
}