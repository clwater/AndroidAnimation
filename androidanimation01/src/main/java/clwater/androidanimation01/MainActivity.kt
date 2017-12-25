package clwater.androidanimation01

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var index = 800F
    var isRight = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        animationView.viewHeight = 10
//        animationView.changeView(800F)
        val animationView = findViewById<AnimationView>(R.id.animationView)



        findViewById<Button>(R.id.button_change).setOnClickListener {
            index = 1000 - index
            isRight = !isRight
            animationView.changeView(index , isRight , 10000)
        }

        findViewById<Button>(R.id.button_start).setOnClickListener{
            animationView.startView()
        }

        val seekbar = findViewById<SeekBar>(R.id.seekBar)
        seekbar.max = 1000
//        seekbar.setOnSeekBarChangeListener(SeekbarListener())
        seekbar.setOnSeekBarChangeListener(SeekbarListener(animationView))
    }
    class SeekbarListener(animationView: AnimationView) : SeekBar.OnSeekBarChangeListener {
        val animationView = animationView
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            val index = progress/ 0.6 * 1080 + 1080 / 5
            animationView.changeView(index.toFloat() / 1000 , true , 0 )
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

    }

}


