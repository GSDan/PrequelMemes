package com.gsdan.prequelmemes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.find
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity()
{

    private val api: RestAPI = RestAPI()
    private lateinit var titleView: TextView
    private lateinit var authorView: TextView
    private lateinit var scoreView: TextView
    private lateinit var imageView: ImageView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var voteRatio: ProgressBar
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
    private var currentMeme: Meme? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        titleView = findViewById(R.id.memeTitle)
        authorView = findViewById(R.id.authorInfo)
        imageView = findViewById(R.id.memeImage)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        voteRatio = findViewById(R.id.voteRatio)
        scoreView = findViewById(R.id.score)

        swipeRefresh.setOnRefreshListener { getMeme() }
        fab.setOnClickListener { _ -> openReddit() }

        getMeme()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showMeme(meme: Meme)
    {
        currentMeme = meme

        val netDate = Date(meme.created_utc * 1000)

        titleView.text = meme.title
        authorView.text = String.format(getString(R.string.author), meme.author, dateFormat.format(netDate))
        scoreView.text = String.format(getString(R.string.score), meme.score.toString())
        voteRatio.progress = (meme.upvote_ratio * 100).toInt()

        if (!meme.is_video)
        {
            Picasso.get().load(meme.url).into(imageView)
        }
    }

    private fun getMeme()
    {

        swipeRefresh.isRefreshing = true

        doAsync {
            val callResponse = api.getRandom()
            val response = callResponse.execute()

            uiThread {
                swipeRefresh.isRefreshing = false
            }

            if (response.isSuccessful)
            {
                val iterator = response.body().iterator()
                iterator.forEach {
                    if (!it.data.children.isEmpty())
                    {
                        var childIterator = it.data.children.iterator()
                        childIterator.forEach {

                            val item = it.data
                            if (!item.url.isBlank())
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

    private fun openReddit()
    {
        val thisMeme = currentMeme
        if (thisMeme === null) return

        val uris = Uri.parse("https://www.reddit.com" + thisMeme.permalink)
        val intent = Intent(Intent.ACTION_VIEW, uris)
        startActivity(intent)
    }
}
