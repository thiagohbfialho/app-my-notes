package com.thbf.android.mynotes

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.DropBoxManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.textservice.TextServicesManager
import android.widget.BaseAdapter
import android.widget.SearchView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticket.view.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onResume() {
        super.onResume()
        //Load all notes from SQLite
        LoadQuery("%")
    }

    fun LoadQuery(title:String){

        var dbManager = DbManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(projections,"Title like ?",selectionArgs,"Title")
        listNotes.clear()
        if(cursor.moveToFirst()){
            do{
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString(cursor.getColumnIndex("Title"))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID,Title,Description))
            }while (cursor.moveToNext())
        }

        var myAdapter = MyNotesAdapter(this,listNotes)
        lvNotes.adapter = myAdapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        val sv:SearchView = menu!!.findItem(R.id.searchNote).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Toast.makeText(applicationContext,query,Toast.LENGTH_LONG).show()
                LoadQuery("%"+ query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item!=null){
            when(item.itemId){
                R.id.addNote->{
                    var intent = Intent(this,AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter:BaseAdapter {

        var listNotesAdapter = ArrayList<Note>()
        var context:Context?=null

        constructor(context:Context, listNotes:ArrayList<Note>):super(){
            this.listNotesAdapter = listNotes
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            var viewNotes = View.inflate(context,R.layout.ticket,null)
            var myNote = listNotesAdapter[position]
            viewNotes.tvTitle.text = myNote.nodeName
            viewNotes.tvDes.text = myNote.nodeDes
            viewNotes.ivDelete.setOnClickListener(View.OnClickListener {
                var dbManager = DbManager(context!!)
                val selectionArgs = arrayOf(myNote.nodeId.toString())
                dbManager.Delete("ID = ?",selectionArgs)
                LoadQuery("%")
            })
            viewNotes.ivEdit.setOnClickListener(View.OnClickListener {
                GoToUpdate(myNote)
            })

            return viewNotes
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

    fun GoToUpdate(note:Note){
        var intent = Intent(this,AddNotes::class.java)
        intent.putExtra("ID",note.nodeId)
        intent.putExtra("name",note.nodeName)
        intent.putExtra("des",note.nodeDes)
        startActivity(intent)
    }

}