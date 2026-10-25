
package com.bhanumoti.btvideoplayer

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import android.graphics.Color
import android.view.Gravity

class MainActivity : Activity() {

    private val videos = mutableListOf<Uri>()
    private lateinit var list: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 28, 24, 24)
            setBackgroundColor(Color.WHITE)
        }

        val title = TextView(this).apply {
            text = "▶  BT Video Player"
            textSize = 25f
            setTextColor(Color.rgb(15, 65, 150))
            gravity = Gravity.CENTER_VERTICAL
        }
        root.addView(title)

        val credit = TextView(this).apply {
            text = "Developed by Bhanumoti Tamuli"
            textSize = 14f
            setTextColor(Color.rgb(0, 120, 230))
        }
        root.addView(credit)

        val search = EditText(this).apply {
            hint = "Search videos..."
            singleLine = true
        }
        root.addView(search)

        val add = Button(this).apply {
            text = "+ Add Videos"
            setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "video/*"
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                startActivityForResult(intent, 101)
            }
        }
        root.addView(add)

        val scroll = android.widget.ScrollView(this)
        list = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(list)
        root.addView(
            scroll,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
            )
        )

        search.addTextChangedListener(
            object : android.text.TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int,
                    count: Int, after: Int
                ) {}
                override fun onTextChanged(
                    s: CharSequence?, start: Int,
                    before: Int, count: Int
                ) {
                    showVideos(s.toString())
                }
                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        setContentView(root)
        showVideos("")
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            data?.clipData?.let { clips ->
                for (i in 0 until clips.itemCount) {
                    videos.add(clips.getItemAt(i).uri)
                }
            } ?: data?.data?.let { videos.add(it) }

            Toast.makeText(
                this, "${videos.size} video(s) selected",
                Toast.LENGTH_SHORT
            ).show()
            showVideos("")
        }
    }

    private fun showVideos(query: String) {
        list.removeAllViews()
        val filtered = videos.filter {
            it.lastPathSegment.orEmpty()
                .contains(query, ignoreCase = true)
        }

        if (filtered.isEmpty()) {
            list.addView(TextView(this).apply {
                text = "Your video library will appear here.\n\nTap Add Videos to select local videos."
                textSize = 17f
                gravity = Gravity.CENTER
                setPadding(16, 40, 16, 40)
                setTextColor(Color.DKGRAY)
            })
        }

        filtered.forEach { uri ->
            list.addView(Button(this).apply {
                text = "▶  ${uri.lastPathSegment ?: "Video"}"
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "video/*")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(intent)
                }
            })
        }
    }
}
