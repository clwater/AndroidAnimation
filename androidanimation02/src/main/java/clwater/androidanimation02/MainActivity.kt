package clwater.androidanimation02

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    var rs = mutableListOf<Int>(0,0,0,0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        animationviewater_main.onClick {
//            animationviewater_main.changeView()
//        }

        animationviefire_main.onClick {
            animationviefire_main.changeView()
//            animationviefire_main.changeTest()
        }

        seek_00.max = 360
        seek_01.max = 360
        seek_02.max = 360
        seek_03.max = 360



        seek_00.setOnSeekBarChangeListener(SeekbarListener(0,rs , animationviefire_main))
        seek_01.setOnSeekBarChangeListener(SeekbarListener(1,rs , animationviefire_main))
        seek_02.setOnSeekBarChangeListener(SeekbarListener(2,rs , animationviefire_main))
        seek_03.setOnSeekBarChangeListener(SeekbarListener(3,rs , animationviefire_main))
    }
    class SeekbarListener(index: Int , rs: MutableList<Int> , animationviefire_main: clwater.androidanimation02.AnimationViewFire) : SeekBar.OnSeekBarChangeListener {
        val rs = rs
        val index = index
        val animationviefire_main = animationviefire_main
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            rs[index] = progress
            animationviefire_main.changeTest(rs[0] , rs[1] , rs[2] , rs[3])
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

    }
}
