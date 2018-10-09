package com.gsdan.prequelmemes

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val api: RestAPI = RestAPI()
    private lateinit var titleView: TextView
    private lateinit var authorView: TextView
    private lateinit var imageView: ImageView
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        titleView = findViewById(R.id.memeTitle)
        authorView = findViewById(R.id.authorInfo)
        imageView = findViewById(R.id.memeImage)

        getMeme()

        fab.setOnClickListener { _ ->
            getMeme()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMeme(meme: Meme)
    {
        val netDate = Date(meme.created_utc * 1000)

        titleView.text = meme.title
        authorView.text = String.format(getString(R.string.author), meme.author, dateFormat.format(netDate))

        if(!meme.is_video)
        {
            Picasso.get().load(meme.url).into(imageView)
        }

    }

    private fun getMeme() {
        doAsync {
            val callResponse = api.getRandom()
            val response = callResponse.execute()

            if (response.isSuccessful) {
                val iterator = response.body().iterator()
                iterator.forEach {
                    if(!it.data.children.isEmpty())
                    {
                        var childIterator  = it.data.children.iterator()
                        childIterator.forEach {

                            val item = it.data
                            if(!item.url.isBlank())
                            {
                                println(item.title)
                                uiThread {
                                    showMeme(item)
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}
