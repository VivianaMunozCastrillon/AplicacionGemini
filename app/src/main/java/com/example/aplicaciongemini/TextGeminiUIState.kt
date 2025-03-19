package com.example.aplicaciongemini

/**
 * Sealed class representing different UI states for the TextGemini feature.
 * Using a sealed class allows for type safety and ensures that all possible states are explicitly handled.
 */
sealed class TextGeminiUIState {  // <- "UI" (User Interface) instead of "IU" (Interfaz de Usuario)

    /**
     * Represents the initial state when the user has not yet interacted with the application.
     */
    object WelcomeState : TextGeminiUIState()

    /**
     * Represents an error state, typically used when an operation fails.
     */
    object Error : TextGeminiUIState()

    /**
     * Represents a successful operation.
     */
    data class Success(val result: String) : TextGeminiUIState()
}
