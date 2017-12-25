package clwater.androidanimation01

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var index = 800F
    var isRight = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val animationView = findViewById<AnimationView>(R.id.animationView)
//        animationView.viewHeight = 10
//        animationView.changeView(800F)

        findViewById<Button>(R.id.button_change).setOnClickListener {
            index = 1000 - index
            isRight = !isRight
            animationView.changeView(index , isRight , 10000)
        }

        findViewById<Button>(R.id.button_start).setOnClickListener{
            animationView.startView()
        }
    }
}
