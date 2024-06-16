package com.capstone.medichat.ui.main.content.rekomendasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.capstone.medichat.data.api.ApiConfig
import com.capstone.medichat.databinding.FragmentRecomendasiBinding
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

    private lateinit var editTextUserMessage: EditText
    private lateinit var textRekomendasiResult: TextView
    private lateinit var textRecomendasiDescription: TextView
    private lateinit var buttonCariDokter: Button
    private lateinit var buttonInputKeluhan: Button

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

        buttonInputKeluhan.setOnClickListener {
            sendMessage()
        }

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
                    var recommendation = jsonObject.optString("response")

                    recommendation = recommendation.replace("\"", "")
                    recommendation = recommendation.replace("{", "")
                    recommendation = recommendation.replace("}", "")

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
