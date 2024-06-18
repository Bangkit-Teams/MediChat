package com.capstone.medichat.ui.main.content.rekomendasi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.capstone.medichat.data.api.ApiConfig
import com.capstone.medichat.databinding.FragmentRecomendasiBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class RecomendasiFragment : Fragment() {

    private var _binding: FragmentRecomendasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var editTextUserMessage: EditText
    private lateinit var textRekomendasiResult: TextView
    private lateinit var textRecomendasiDescription: TextView
    private lateinit var buttonCariDokter: Button
    private lateinit var buttonInputKeluhan: Button

    private var recommendation: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecomendasiBinding.inflate(inflater, container, false)
        val root: View = binding.root

        editTextUserMessage = binding.editTextUserMessage
        textRekomendasiResult = binding.textRekomendasiResult
        buttonCariDokter = binding.buttonCariDokter
        textRecomendasiDescription = binding.textRekomendasiDescription
        buttonInputKeluhan = binding.buttonInputKeluhan

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        buttonInputKeluhan.setOnClickListener {
            sendMessage()
        }

        buttonCariDokter.setOnClickListener {
            checkLocationPermissionAndOpenMaps()
        }

        // Tambahkan log handler intent map
        logAvailableMapHandlers()

        return root
    }

    private fun sendMessage() {
        val userMessage = editTextUserMessage.text.toString().trim()

        if (userMessage.isEmpty()) {
            editTextUserMessage.error = "Tulis keluhan Anda terlebih dahulu"
            return
        }

        val requestBody = JSONObject().apply {
            put("user_message", userMessage)
        }.toString().toRequestBody("application/json".toMediaTypeOrNull())

        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                val response = ApiConfig.recommendationService.postRecommendation(requestBody)

                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val jsonObject = JSONObject(responseBody)
                    recommendation = jsonObject.optString("response")

                    recommendation = recommendation?.replace("\"", "")
                    recommendation = recommendation?.replace("{", "")
                    recommendation = recommendation?.replace("}", "")

                    withContext(Dispatchers.Main) {
                        textRekomendasiResult.text = recommendation
                        textRekomendasiResult.visibility = View.VISIBLE
                        textRecomendasiDescription.visibility = View.VISIBLE
                        buttonCariDokter.visibility = View.VISIBLE

                        editTextUserMessage.text.clear()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        textRekomendasiResult.text = "Gagal mendapatkan rekomendasi"
                        textRekomendasiResult.visibility = View.VISIBLE
                        textRecomendasiDescription.visibility = View.GONE
                        buttonCariDokter.visibility = View.GONE
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    textRekomendasiResult.text = "Error: ${e.message}"
                    textRekomendasiResult.visibility = View.VISIBLE
                    textRecomendasiDescription.visibility = View.GONE
                    buttonCariDokter.visibility = View.GONE
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    textRekomendasiResult.text = "Error: ${e.message}"
                    textRekomendasiResult.visibility = View.VISIBLE
                    textRecomendasiDescription.visibility = View.GONE
                    buttonCariDokter.visibility = View.GONE
                }
            } finally {
                // Hide progress bar after response
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun checkLocationPermissionAndOpenMaps() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getCurrentLocationAndOpenMaps()
        }
    }

    private fun getCurrentLocationAndOpenMaps() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    openGoogleMaps(it.latitude, it.longitude)
                } ?: run {
                    // Handle location is null
                    textRekomendasiResult.text = "Tidak dapat menemukan lokasi Anda"
                    textRekomendasiResult.visibility = View.VISIBLE
                    textRecomendasiDescription.visibility = View.GONE
                    buttonCariDokter.visibility = View.GONE
                }
            }
    }

    private fun openGoogleMaps(latitude: Double, longitude: Double) {
        recommendation?.let {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=${Uri.encode(it)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
            }

            if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(mapIntent)
                Log.d("RecomendasiFragment", "Opening map with URI: $gmmIntentUri")
            } else {
                // Fallback ke browser
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(it)}"))
                if (webIntent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(webIntent)
                    Log.d("RecomendasiFragment", "Opening map in web with URI: https://maps.google.com/?q=${Uri.encode(it)}")
                } else {
                    textRekomendasiResult.text = "Tidak ada aplikasi yang dapat membuka URI"
                    textRekomendasiResult.visibility = View.VISIBLE
                    textRecomendasiDescription.visibility = View.GONE
                    buttonCariDokter.visibility = View.GONE
                    Log.e("RecomendasiFragment", "No application can handle the map intent")
                }
            }
        } ?: run {
            textRekomendasiResult.text = "Rekomendasi tidak tersedia"
            textRekomendasiResult.visibility = View.VISIBLE
            textRecomendasiDescription.visibility = View.GONE
            buttonCariDokter.visibility = View.GONE
            Log.e("RecomendasiFragment", "No recommendation available")
        }
    }

    private fun logAvailableMapHandlers() {
        val gmmIntentUri = Uri.parse("geo:0,0")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        val resolveInfos = requireActivity().packageManager.queryIntentActivities(mapIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfos) {
            Log.d("RecomendasiFragment", "Package that can handle map intent: ${resolveInfo.activityInfo.packageName}")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocationAndOpenMaps()
            } else {
                // Handle permission denied case
                textRekomendasiResult.text = "Izin lokasi ditolak"
                textRekomendasiResult.visibility = View.VISIBLE
                textRecomendasiDescription.visibility = View.GONE
                buttonCariDokter.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
