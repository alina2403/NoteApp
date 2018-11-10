package com.example.alina.noteapp

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_note.*

class AddNoteActivity : AppCompatActivity() {

    val dbTable = "Notes"
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        try {
            val bundle:Bundle = intent.extras
            id = bundle.getInt("Id", 0)
            if (id!=0){
                titleEdit.setText(bundle.getString("name"))
                descriptionEdit.setText(bundle.getString("Description"))
            }
        }catch (ec:Exception){}
    }
    fun addFunc(view: View){
        var dbManager = DBManager(this)
        var values = ContentValues()
        values.put("Title", titleEdit.text.toString())
        values.put("Description", descriptionEdit.text.toString())

        if (id == 0 ){
            val Id = dbManager.insert(values)
            if (Id>0){
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
            finish()
            }
            else{
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()

            }
        }
        else{
            var selectionArgs = arrayOf(id.toString())
            val Id = dbManager.update(values, "Id=?", selectionArgs)
            if (Id>0){
                Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
