package psycho.mountain.tastinggenie

import android.app.Person
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_register_db_fragment.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select

class MainActivity : AppCompatActivity(), TestFragment.TestFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null){
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

            //fragmentTransaction.addToBackStack(null)

            fragmentTransaction.replace(R.id.container, TestFragment.newInstance())
            fragmentTransaction.commit()
        }
    }

    override fun onClickTestButton() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        // 戻り先がnull菜のではなく，バックスタックに遷移を記録する（戻せるようにする）という意味
        fragmentTransaction.addToBackStack(null)

        fragmentTransaction.replace(R.id.container, RegisterDBFragment.newInstance())
        fragmentTransaction.commit()

        val helper = PersonalDatabaseOpenHelper.newInstance(this)
    }

}
