package clwater.androidanimation02

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationviewbuttonwater_main.onClick {
            animationviewbuttonwater_main.startView()
//            animationviewbuttonwater_main.changeView(100F , 0)
        }
    }
}
