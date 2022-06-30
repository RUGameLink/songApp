package com.example.songapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.imdbapp.db.DbManager

class HistoryActivity: AppCompatActivity() {
    private lateinit var returnButton: Button
    private lateinit var searchView: SearchView
    private lateinit var deleteButton: Button

    private val dbManager = DbManager(this) //Инициализация бд-менеджера

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        init()
        initSearchView()

        //Получение данных из другой активити
        val titleList = intent.getStringArrayListExtra( "titlesList" )
        val artistList = intent.getStringArrayListExtra( "artistList" )

        if (titleList != null && artistList != null) {
                setAdapter(titleList, artistList)
        }

        returnButton.setOnClickListener(returnButtonListener) //Подвязка кнопки к слушателю

        deleteButton.setOnClickListener(deleteButtonListener)
    }

    private fun initSearchView(){
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                dbManager.openDb()
                val resList = p0?.let { dbManager.searchInDb(it) } //ПОИСК В БД
                val artistList = p0?.let {dbManager.searchInDbArtist(it)}
                dbManager.closeDb()
                if (resList != null && artistList != null) {
                    setAdapter(resList, artistList)
                } //ОБНОВЛЕНИЕ РЕСАЙКЛЕР ВЬЮ
                return true
            }

        })
    }

    private var deleteButtonListener: View.OnClickListener = View.OnClickListener { //Слушатель кнопки
        dbManager.openDb()
        dbManager.deleteFromDb()
        val historySongData = dbManager.readDbDataTitles() //Считывание из бд колонки имен в лист
        val historyArtistData = dbManager.readDbDataArtist() //Считывание из бд колонки результата в лист
        dbManager.closeDb()

        setAdapter(historySongData, historySongData)
    }

    private var returnButtonListener: View.OnClickListener = View.OnClickListener { //Слушатель кнопки
        val i = Intent(this, MainActivity::class.java) //интент перехода к другой активити
        startActivity(i)
    }

    private fun init(){ //Метод инициализации объектов на активити
        returnButton = findViewById(R.id.toMainButton)
        searchView = findViewById(R.id.searchView)
        deleteButton = findViewById(R.id.deleteButton)
    }

    private fun setAdapter(titleList: ArrayList<String>, artistList: ArrayList<String>){
        val recyclerView: RecyclerView = findViewById(R.id.historyView) //Подвязка ресайклера к объекту
        val linearLayoutManager = LinearLayoutManager(applicationContext) //Подготовка лайаут менеджера
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = linearLayoutManager //Инициализация лайаут менеджера
        recyclerView.adapter = CustomRecyclerAdapter(titleList!!, artistList!!) //внесение данных из листа в адаптер (заполнение данными)
    }
}
