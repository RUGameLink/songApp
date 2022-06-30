package com.example.imdbapp.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DbManager (context: Context) {
    val dbHelper = DbHelper(context)
    var dataBase: SQLiteDatabase ?= null

    fun openDb(){  //Функция открытия БД
        dataBase = dbHelper.writableDatabase
    }
    fun insertToDb(title: String, artist: String){ //Функция записи БД
        val values = ContentValues().apply {
            put(DbName.COLUMN_NAME_TITLE, title)
            put(DbName.COLUMN_NAME_SUBTITLE, artist)
        }
        dataBase?.insert(DbName.TABLE_NAME, null, values)
    }

    @SuppressLint("Range")
    fun readDbDataTitles(): ArrayList<String>{ //Считывание из бд в лист
        val dataList = ArrayList<String>()

        val cursor = dataBase?.query(DbName.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)
        with(cursor){
            while (this?.moveToNext()!!){
                val dataText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE)) //Считывание в переменную из столбца
                dataList.add(dataText.toString())
            }
        }
        cursor?.close()
        return dataList//Возврат листа

    }

    @SuppressLint("Range")
    fun readDbDataArtist(): ArrayList<String>{//Считывание из бд в лист
        val dataList = ArrayList<String>()

        val cursor = dataBase?.query(DbName.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null)
        with(cursor){
            while (this?.moveToNext()!!){
                val dataText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_SUBTITLE)) //Считывание в переменную из столбца
                dataList.add(dataText.toString())
            }
        }
        cursor?.close()
        return dataList //Возврат листа

    }

    fun closeDb(){ //Функция закрытия БД
        dbHelper.close()
    }

    @SuppressLint("Range")
    fun searchInDb(text: String): ArrayList<String>{ //ПОИСК В БД
        val dataList = ArrayList<String>()
        val section = "${DbName.COLUMN_NAME_TITLE} like ?" //ЗАПРОС ПОИСКА В СТОЛБЦЕ ПО СОВПАДЕНИЮ

        val cursor = dataBase?.query(DbName.TABLE_NAME,
            null,
            section, //ЗАПРОС
            arrayOf("%${text}%"), //КЛЮЧЕВОЕ СЛОВО ДЛЯ ПОИСКА (% % - ДЛЯ ПОИСКА ПО НЕПОЛНОМУ СОВПАДЕНИЮ)
            null,
            null,
            null)
        with(cursor){
            while (this?.moveToNext()!!){
                val dataText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_TITLE)) //Считывание в переменную из столбца
                dataList.add(dataText.toString())
            }
        }
        cursor?.close()
        return dataList//Возврат листа
    }

    @SuppressLint("Range")
    fun searchInDbArtist(text: String): ArrayList<String>{ //ПОИСК В БД
        val dataList = ArrayList<String>()
        val section = "${DbName.COLUMN_NAME_TITLE} like ?" //ЗАПРОС ПОИСКА В СТОЛБЦЕ ПО СОВПАДЕНИЮ

        val cursor = dataBase?.query(DbName.TABLE_NAME,
            null,
            section, //ЗАПРОС
            arrayOf("%${text}%"), //КЛЮЧЕВОЕ СЛОВО ДЛЯ ПОИСКА (% % - ДЛЯ ПОИСКА ПО НЕПОЛНОМУ СОВПАДЕНИЮ)
            null,
            null,
            null)
        with(cursor){
            while (this?.moveToNext()!!){
                val dataText = cursor?.getString(cursor.getColumnIndex(DbName.COLUMN_NAME_SUBTITLE)) //Считывание в переменную из столбца
                dataList.add(dataText.toString())
            }
        }
        cursor?.close()
        return dataList//Возврат листа
    }

    fun deleteFromDb(){ //ОЧИСТКА БД
        dataBase?.execSQL(DbName.SQL_DELETE_TABLE) //УДАЛЕНИЕ ТЕКУЩЕЙ ТАБЛИЦЫ
        dataBase?.execSQL(DbName.CREATE_TABLE) //СОЗДАНИЕ НОВОЙ ТАБЛИЦЫ
    }
}