package com.example.aplicaciongemini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.aplicaciongemini.ui.theme.AplicacionGeminiTheme

/**
 * MainActivity serves as the entry point of the application.
 * It extends ComponentActivity and sets up the UI using Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge mode, allowing the UI to extend behind system bars.
        enableEdgeToEdge()

        // Sets up the Compose UI.
        setContent {
            // Applies the application's theme.
            AplicacionGeminiTheme {
                // Scaffold provides a layout structure, ensuring proper padding and UI hierarchy.
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Displays the HomeScreen with appropriate padding to handle system bars.
                    HomeScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * A preview function for the Compose UI.
 * This allows Android Studio's Preview tool to render a UI representation.
 */
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicacionGeminiTheme {
        // Currently, this preview does not display any content.
        // You may add a Composable function here to preview UI components.
    }
}
