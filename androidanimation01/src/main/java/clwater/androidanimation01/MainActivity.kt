package clwater.androidanimation01

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val animationView = findViewById<AnimationView>(R.id.animationView)
//        animationView.viewHeight = 10
        animationView.changeView(800F)
    }
}
