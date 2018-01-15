package clwater.androidanimation02

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        animationviewater_main.onClick {
//            animationviewater_main.changeView()
//        }
//
//        animationvielighting_main.onClick {
//            animationvielighting_main.changeView()
//        }

        animationviefire_main.onClick {
            animationviefire_main.changeView()
        }
    }
}
