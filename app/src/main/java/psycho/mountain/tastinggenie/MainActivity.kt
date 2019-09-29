package psycho.mountain.tastinggenie

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            // 戻り先はnull
            fragmentTransaction.addToBackStack(null)

            fragmentTransaction.replace(R.id.container, TestFragment.newInstance())
            fragmentTransaction.commit()

        }
    }
}
