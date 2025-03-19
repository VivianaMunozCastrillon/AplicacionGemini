package com.example.aplicaciongemini

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicaciongemini.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the chat interaction with the Gemini AI model.
 * This ViewModel handles user prompts, API requests, and UI state updates.
 */
class TextGeminiViewModel : ViewModel() {

    /**
     * Holds the chat history as a list of messages.
     * It is a mutable state, so changes will trigger UI recomposition.
     */
    var chatHistory by mutableStateOf<List<String>>(emptyList())
        private set // Private setter to prevent modification outside the ViewModel.

    /**
     * Tracks whether the AI response is currently loading.
     * This is used to show a loading indicator in the UI.
     */
    var isLoading by mutableStateOf(false)
        private set

    /**
     * Instance of the GenerativeModel to interact with the Gemini AI API.
     * Uses the API key stored in BuildConfig for authentication.
     */
    private val generativeModel = GenerativeModel(
        modelName = "models/gemini-1.5-flash-latest", // Specifies the AI model version
        apiKey = BuildConfig.apiKey // Retrieves the API key from BuildConfig
    )

    /**
     * Processes the user input (prompt) and sends it to the Gemini AI model.
     * The AI response is added to the chat history.
     */
    fun makePrompt(prompt: String) {
        if (isLoading) return // Prevents multiple requests while a response is being processed.

        viewModelScope.launch {
            try {
                isLoading = true // 🔹 Activates the loading state

                // Build the full conversation history for context
                val fullPrompt = chatHistory.joinToString("\n") + "\n🧑‍💻: $prompt"
                val result = generativeModel.generateContent(fullPrompt) // Request AI response
                val response = result.text ?: "No se recibió respuesta" // Handle null responses

                // Update chat history with both user input and AI response
                chatHistory = chatHistory + "🧑‍💻: $prompt" + "🤖: $response"

            } catch (e: Exception) {
                e.printStackTrace()
                chatHistory = chatHistory + "⚠️ Error en la consulta" // Append error message
            } finally {
                isLoading = false // 🔹 Deactivates the loading state after response
            }
        }
    }
}
