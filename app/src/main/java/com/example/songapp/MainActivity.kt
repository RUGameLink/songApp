package com.example.songapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.example.imdbapp.db.DbManager
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    //Инициализация переменных для объектов лайаута
    private lateinit var textLyricView: TextView
    private lateinit var searchButton: Button
    private lateinit var songTitleView: EditText
    private lateinit var songArtView: EditText
    private lateinit var progressBar: ProgressBar

    private val dbManager = DbManager(this) //Инициализация бд-менеджера


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        progressBar.isVisible = false

        searchButton.setOnClickListener(searchButtonListener)
    }

    private var searchButtonListener: View.OnClickListener = View.OnClickListener { //Слушатель кнопки Проверки совместимости
        if (songTitleView.text.isEmpty() || songArtView.text.isEmpty()){ //Если текстовое поля пустые, то закончить выполнение функции
            Toast.makeText(this, "Заполните поля!!", Toast.LENGTH_SHORT).show()
            return@OnClickListener
        }
        else{ //Продолжение работы в случае заполненных полей
            //Запись истории поиска в бд
            dbManager.openDb() //Открытие бд
            dbManager.insertToDb(songTitleView.text.toString(), songArtView.text.toString()) //Запись
            dbManager.closeDb() //Закрытие бд

            progressBar.isVisible = true
            Toast.makeText(this, "Поиск...", Toast.LENGTH_SHORT).show()

            var title = songTitleView.text.toString() //Запись текста из textview в переменную
            title = title.replace(" ", "%20", false) //Замена пробелов в тексте на символ "%20"
            var artist = songArtView.text.toString()
            artist = artist.replace(" ","%20", false ) //Замена пробелов в тексте на символ "%20"
            val thread = Thread{  //Открытие потока
                try {
                    //Работа с api
                    val client = OkHttpClient()

                    val request = Request.Builder()
                        .url("https://powerlyrics.p.rapidapi.com/getlyricsfromtitleandartist?title=$title&artist=$artist") //Формирование запроса
                        .get()
                        .addHeader("X-RapidAPI-Host", "powerlyrics.p.rapidapi.com") //Обращение к api
                        .addHeader("X-RapidAPI-Key", "8fac8d93edmshc4380d7d88505cdp17d5dfjsndf4c3b2501a4") //Авторазация пользователя в api
                        .build()

                    val response = client.newCall(request).execute() //Отправка запроса в api
                    val result = response.body()?.string() //Получение результатов в видо json файла
                    var error = JSONObject(result).getString("success").toBoolean() //Считывание объекта-статуса из результата
                    
                    if (!error) { //Если переменная равна false
                        runOnUiThread { //Возврат в основной поток
                            progressBar.isVisible = false //Скрытие progressBar
                            Toast.makeText(this, "Ошибка поиска", Toast.LENGTH_SHORT).show() //Вывод тоста об ошибке
                        }
                        return@Thread
                    }
                    else{
                        var lyric = JSONObject(result).getString("lyrics") //Считывание текста песни


                        runOnUiThread { //Возврат в основной поток
                            textLyricView.text = ""
                            progressBar.isVisible = false
                            textLyricView.text = lyric.toString() //Запись результата в textview
                        }
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
            thread.start() //Открытие потока
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //Инициализация меню приложения
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //Слушатель элементов меню
        if(item.itemId == R.id.history){ //Если id кнопки совпал с id кнопки истории
            dbManager.openDb() //Открытие бд
            val historySongData = dbManager.readDbDataTitles() //Считывание из бд колонки имен в лист
            val historyArtistData = dbManager.readDbDataArtist() //Считывание из бд колонки результата в лист
            dbManager.closeDb() //Закрытие бд

            val i = Intent(this, HistoryActivity::class.java) //Инициализация интента для открытия новой активити
            i.putStringArrayListExtra("titlesList", historySongData) //Добавление в интент листов с соответсткующими ключами
            i.putStringArrayListExtra("artistList", historyArtistData)
            startActivity(i) //Старт активити
        }
        return super.onOptionsItemSelected(item)
    }

    private fun init(){
        textLyricView = findViewById(R.id.textLyricView)
        textLyricView.movementMethod = ScrollingMovementMethod.getInstance();
        textLyricView.scrollBarStyle = View.SCROLLBARS_INSIDE_INSET;
        textLyricView.isVerticalScrollBarEnabled = true;
        searchButton = findViewById(R.id.searchButton)
        songTitleView = findViewById(R.id.songTitleView)
        songArtView = findViewById(R.id.songArtView)
        progressBar = findViewById(R.id.progressBar)
    }
}