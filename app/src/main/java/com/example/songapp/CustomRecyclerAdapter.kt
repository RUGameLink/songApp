package com.example.songapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomRecyclerAdapter(private val titles: List<String>, private val artist: List<String>): RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){ //Инициализация объектов лайаута айтемов ресайклера
        val largeTextView: TextView = itemView.findViewById(R.id.textViewLarge)
        val textArtistView: TextView = itemView.findViewById(R.id.textArtistView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { //Подвязка лайаута к адаптеру ресайклера
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerview_item, parent, false) //Определение лайаута
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) { //Запись в айтем данных в заготовленные textview
        holder.largeTextView.text = titles[position]
        holder.textArtistView.text = artist[position]
    }

    override fun getItemCount(): Int {
        return titles.size
    }
}