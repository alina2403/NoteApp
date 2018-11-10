package com.example.alina.noteapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.ClipboardManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.row.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun LoadQuery(title: String) {
        var dbManager = DBManager(this)
        val projections = arrayOf("Id", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections, "Title like?", selectionArgs, "Title")
        listNotes.clear()
        if (cursor?.moveToFirst()!!) {
            do {
                val Id = cursor.getInt(cursor.getColumnIndex("Id"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(Id, Title, Description))
            } while (cursor.moveToNext())
        }
        var notesAdapter = NotesAdapter(this, listNotes)
        notesList.adapter = NotesAdapter(this, listNotes)
        var total = notesList.count

        val ActionBar = supportActionBar
        if (ActionBar!=null){
            ActionBar.subtitle = "Your notes"+total
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val sv:SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                LoadQuery("%"+query+"%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                LoadQuery("%"+newText+"%")
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null){
            when (item.itemId){
                R.id.addButton->{
                    startActivity(Intent(this, AddNoteActivity::class.java))
                }
                R.id.action_settings->{
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class NotesAdapter : BaseAdapter {
        var listNotesAdapter = ArrayList<Note>()
        var context:Context?=null

        constructor(context: Context, listNotesAdapter:ArrayList<Note>): super(){
            this.listNotesAdapter = listNotesAdapter
        }


        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.row, null)
            val myNote = listNotesAdapter[position]
            myView.titleTv.text = myNote.nodeName
            myView.descTv.text = myNote.nodeDescription
            //delete
            myView.deleteBtn.setOnClickListener{
                var dbManager = DBManager(this.context!!)
                val selectionArgs = arrayOf(myNote.nodeID.toString())
                dbManager.delete("Id=?", selectionArgs)
                LoadQuery("%")
            }
            //edit
            myView.editBtn.setOnClickListener {
                UpdateFun(myNote)
            }
            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }
    }

    private fun UpdateFun(myNote: Note){
        var intent = Intent(this, AddNoteActivity::class.java)
        intent.putExtra("Id", myNote.nodeID)
        intent.putExtra("name", myNote.nodeName)
        intent.putExtra("Description", myNote.nodeDescription)
        startActivity(intent)
    }
}
