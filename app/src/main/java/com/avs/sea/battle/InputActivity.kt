package com.avs.sea.battle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.avs.sea.battle.main.MainActivity

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        val button:Button = findViewById(R.id.button)
        button.setOnClickListener{

            val intent: Intent = Intent(this@InputActivity,MainActivity ::class.java)
            startActivity(intent)
        }
    }
}