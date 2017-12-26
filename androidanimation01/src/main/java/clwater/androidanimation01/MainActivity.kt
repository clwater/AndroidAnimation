package clwater.androidanimation01

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationView.initView(0F,0F, 0xFFF9FAF9.toInt())

        button_main_start.setOnClickListener{ animationView.startView() }

        button_main_stop.setOnClickListener{ animationView.stopView() }


    }
}


