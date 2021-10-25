package com.itcomca.testfactum.views.mapafragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.itcomca.testfactum.R
import com.itcomca.testfactum.base.BaseApplication

class MapaFragment : Fragment(),
    OnMyLocationClickListener,
    LocationListener,
    OnMapReadyCallback,
    OnMyLocationButtonClickListener{

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var mMap: GoogleMap;
    var locationPermissionGranted: Boolean = false
    lateinit var mActivity: Activity
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:Int = 1890
    private val DEFAULT_ZOOM = 15
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mActivity = requireActivity()
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        InitLocation()
    }

    override fun onMyLocationClick(p0: Location) {

    }

    override fun onLocationChanged(p0: Location) {

    }

    override fun onMapReady(p0: GoogleMap) {
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = p0
        mMap.setOnMyLocationButtonClickListener(this)
        CheckPermissions()
        updateLocationUI()
        getDeviceLocation()
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
    }

    fun CheckPermissions() {
        if (ContextCompat.checkSelfPermission(
                activity?.getApplicationContext()!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /** Resultado de los permisos de location **/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permission: Array<String?>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }

    /**  Configuracion del mapa dependiendo si tiene o no los permisos otorgados **/
    private fun updateLocationUI() {
        if (mMap == null) return
        try {
            if (locationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
                mMap.uiSettings.setAllGesturesEnabled(true)
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                CheckPermissions()
            }
        } catch (e: SecurityException) {
            //Utils.writeLog("Exception UPDATEUI", e.getMessage());
        }
    }

    /** Se obtiene la ubicacion del dispositivo **/
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(mActivity,
                    OnSuccessListener<Location?> { location ->
                        if (location != null) {
                            // Logic to handle location object
                            mMap.moveCamera(
                                /** Ubicacion del dispositivo con un Zoom definido **/
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                            /** Se coloca la marca de la ubicacion actual **/
                            val currentLatitude = location.latitude
                            val currentLongitude = location.longitude
                            val latLng = LatLng(currentLatitude, currentLongitude)
                            val mMarkerOptions = MarkerOptions()
                            mMarkerOptions.position(latLng)
                            mMarkerOptions.title("Mi ubicación actual")

                            /** Se coloca un circulo alrededor de la marca de posicion **/
                            val mOptions = CircleOptions()
                                .center(LatLng(currentLatitude, currentLongitude)).radius(5000.0)
                                .strokeColor(0x110000FF).strokeWidth(1f).fillColor(0x110000FF)
                            mMap.addCircle(mOptions)
                            mMap.addMarker(mMarkerOptions)

                            /** Animacion del acercamiento de la camara en la posicion **/
                            val camPosition = CameraPosition.Builder()
                                .target(LatLng(currentLatitude, currentLongitude)).zoom(12f).build()
                            mMap.animateCamera(
                                CameraUpdateFactory
                                    .newCameraPosition(camPosition)
                            )
                        }
                    })


        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /** Ubicacion inicial del dispositivo **/
    private fun InitLocation() {
        /** Configuracion del request de la posicion **/
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mActivity)
        locationRequest = LocationRequest.create()
        locationRequest?.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        /** Tiempo de solicitud de posicion **/
        locationRequest?.setInterval((20 * 1000).toLong())


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        //mMap.clear();
                        val currentLatitude = location.latitude
                        val currentLongitude = location.longitude
                        val latLng = LatLng(currentLatitude, currentLongitude)
                        val mMarkerOptions = MarkerOptions()
                        mMarkerOptions.position(latLng)
                        mMarkerOptions.title("Mi ubicación actual")
                        val mOptions = CircleOptions()
                            .center(LatLng(currentLatitude, currentLongitude)).radius(5000.0)
                            .strokeColor(0x110000FF).strokeWidth(1f).fillColor(0x110000FF)
                        mMap.addCircle(mOptions)
                        mMap.addMarker(mMarkerOptions)
                        val camPosition = CameraPosition.Builder()
                            .target(LatLng(currentLatitude, currentLongitude)).zoom(12f).build()
                        mMap.animateCamera(
                            CameraUpdateFactory
                                .newCameraPosition(camPosition)
                        )
                    }
                }
            }
        }
    }

    fun SetEveryXTime(){
        val alarmManager = BaseApplication.applicationContext().getSystemService(Context.ALARM_SERVICE)

    }
}