package com.example.simpleweatherapp.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.simpleweatherapp.BuildConfig
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.databinding.FragmentMapBinding
import com.microsoft.maps.*


class MapFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView

    private lateinit var pinLayer: MapElementLayer

    private val LAKE_WASHINGTON = Geopoint(47.609466, -122.265185)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Set toolbar top margin with respect of the status bar
        (binding.mapToolbar.layoutParams as ViewGroup.MarginLayoutParams)
            .topMargin = binding.mapToolbarLayout.marginTop + getStatusBarHeight()

        binding.mapToolbar.setOnClickListener(this)

        mapView = MapView(requireActivity(), MapRenderMode.VECTOR) // or use MapRenderMode.RASTER for 2D map
        mapView.setCredentialsKey(BuildConfig.BING_MAPS_KEY)
        binding.mapViewLayout.addView(mapView)
//        pinLayer = MapElementLayer()
//        mapView.layers.add(pinLayer)
        mapView.mapStyleSheet = MapStyleSheets.roadCanvasLight()
// На темной теме
// mapView.mapStyleSheet = MapStyleSheets.roadDark()

        mapView.language = "en-us"

        val weatherLayer = UriTileMapLayer()
        weatherLayer.uriFormatString = "https://tile.openweathermap.org/map/precipitation_new/" +
                "{zoomlevel}/{x}/{y}.png?appid=${BuildConfig.OPENWEATHER_KEY}"
        mapView.layers.add(weatherLayer)

        mapView.onCreate(savedInstanceState)



        // Add pins
//        val location = LAKE_WASHINGTON  // your pin lat-long coordinates
//        val title = "Lake Washington"       // title to be shown next to the pin
//        val pushpin = MapIcon()
//        pushpin.location = location
//        pushpin.title = title
//        pinLayer.elements.add(pushpin)

        // pin map on click
//        mapView.addOnMapTappedListener { args ->
//            val pushpin = MapIcon()
//            pushpin.location = args.location
//            pushpin.title = "title"
//            pinLayer.elements.clear()
//            pinLayer.elements.add(pushpin)
//        }


//        val listStyles = listOf<MapStyleSheet>(
//            MapStyleSheets.empty(),
//            MapStyleSheets.roadLight(), // светлая с яркими цветами
//            MapStyleSheets.roadDark(),  // темная
//            MapStyleSheets.roadCanvasLight(),   // светлая бледная
//            MapStyleSheets.roadHighContrastLight(), // светлая высококонтрастная
//            MapStyleSheets.roadHighContrastDark(),  // темная высококонтрастная
//            MapStyleSheets.aerial(), // фото
//            MapStyleSheets.aerialWithOverlay(), // фото с наложенной картой
//        )
//
//        var listStylesIterator = listStyles.iterator()
//        var listStyle: MapStyleSheet = listStylesIterator.next()
//
//        binding.fab.setOnClickListener {
//
//            mapView.mapStyleSheet = listStyle
//            Snackbar.make(it, listStyle.javaClass.toString(), Snackbar.LENGTH_SHORT)
//                .show()
//            if (!listStylesIterator.hasNext()) {
//                listStylesIterator = listStyles.iterator()
//            }
//            listStyle = listStylesIterator.next()
//        }

//        var on = true
//        binding.fab.setOnClickListener {
//            if (on) {
//                mapView.mapProjection = MapProjection.GLOBE
//            } else {
//                mapView.mapProjection = MapProjection.WEB_MERCATOR
//            }
//            on = !on
//        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        mapView.setScene(
            MapScene.createFromLocationAndZoomLevel(LAKE_WASHINGTON, 10.0),
            MapAnimationKind.NONE)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.map_toolbar -> navigateToSearchFragment()
        }
    }

    private fun navigateToSearchFragment() {
//        val action = MapFragmentDirections.actionMapFragmentToSearchFragment()
//        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}