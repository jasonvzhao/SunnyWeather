package com.sunnyweather.android.ui.place

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Place
import kotlinx.android.synthetic.main.frag_palce.*

class PlaceFragment : Fragment() {

    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }


    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_palce, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerview.layoutManager = LinearLayoutManager(context)
        adapter = PlaceAdapter(viewModel.places)
        recyclerview.adapter = adapter
        edit_query.addTextChangedListener {
            val content = it.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerview.visibility = View.GONE
                viewModel.places.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(this, Observer {
            val places = it.getOrNull()
            if (places != null) {
                recyclerview.visibility=View.VISIBLE
                viewModel.places.clear()
                viewModel.places.addAll(places)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(context, "未查询到任何地点", Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        })

    }

    inner class PlaceAdapter(private val data: List<Place>) :
        RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

        inner class PlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

            val addressTv: TextView = view.findViewById(R.id.address)
            val nameTv: TextView = view.findViewById(R.id.name)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_place, null, false)

            return PlaceViewHolder(view)
        }


        override fun getItemCount(): Int {
            return data.size
        }


        override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
            val place = data[position]
            holder.addressTv.text = place.address
            holder.nameTv.text = place.name
        }
    }

}