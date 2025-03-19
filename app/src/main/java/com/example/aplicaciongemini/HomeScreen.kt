package com.example.aplicaciongemini

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Send
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

/**
 * Main composable function that determines whether to show the chat history
 * or the welcome screen based on the state of the ViewModel.
 */
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // Retrieve the ViewModel instance
    val viewModel: TextGeminiViewModel = viewModel()

    // If there is chat history, display the chat screen; otherwise, show the welcome screen
    if (viewModel.chatHistory.isNotEmpty()) {
        SuccessScreen(
            chatHistory = viewModel.chatHistory,
            onSend = { prompt -> viewModel.makePrompt(prompt) }
        )
    } else {
        WelcomeScreen(makePrompt = { prompt -> viewModel.makePrompt(prompt) })
    }
}

/**
 * Composable function to display an error message when something goes wrong.
 */
@Composable
fun ErrorScreen(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Error Loading", modifier = Modifier.padding(16.dp))
    }
}

/**
 * Composable function that displays the chat history along with an input field
 * where the user can send new messages.
 *
 * @param chatHistory A list of previous chat messages.
 * @param onSend Function to handle message sending.
 */
@Composable
fun SuccessScreen(chatHistory: List<String>, onSend: (String) -> Unit, modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") } // State to manage the input text
    // Reference to control the software keyboard
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .imePadding() // Ensures the keyboard doesn't cover the input field
    ) {
        // Spacer to push the content down slightly
        Spacer(modifier = Modifier.height(60.dp))

        // Chat history section - scrollable column to display previous messages
        Column(
            modifier = Modifier
                .weight(1f) // Makes this section take most of the space
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // Enables vertical scrolling
        ) {
            chatHistory.forEach { message ->
                Text(
                    text = message, // Displays each message from chat history
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        // Input field and send button section
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Limits the width of the input box
                .border(2.dp, MaterialTheme.colorScheme.primary) // Adds a border around the input field
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Text input field
                BasicTextField(
                    value = inputText,
                    onValueChange = { inputText = it }, // Updates the state when the user types
                    modifier = Modifier
                        .weight(1f) // Takes up most of the row space
                        .padding(8.dp),
                    // Enables "Done" action on keyboard+
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (inputText.isNotBlank()) {
                            onSend(inputText) // Sends the input text
                            inputText = "" // Clears the input field
                            keyboardController?.hide() // Hides the keyboard
                        }
                    })
                )

                // Send button - only enabled if input is not empty
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSend(inputText)
                            inputText = ""
                            keyboardController?.hide()
                        }
                    },
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Enviar", // Keeps description in Spanish
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Composable function that displays the welcome screen with an input field.
 * If the user submits input, it triggers a prompt to the system.
 *
 * @param makePrompt Function that handles input submission.
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    makePrompt: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") } // State for managing user input
    val viewModel: TextGeminiViewModel = viewModel() // Retrieve ViewModel instance

    // Gradient brush for styling the welcome text
    val gradientBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF4285F4), Color(0xFFEA4335), Color(0xFFFBBC05), Color(0xFF34A853))
    )

    Column(
        verticalArrangement = Arrangement.Center, // Centers content vertically
        horizontalAlignment = Alignment.CenterHorizontally, // Centers content horizontally
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Welcome text with Google-like gradient styling
        Text(
            text = "Bienvenido a Gemini 1.5 LV", // Keeps text in Spanish
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, brush = gradientBrush)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Space between elements

        // Input field container with a border
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f) // Limits width
                .height(120.dp) // Fixed height
                .border(2.dp, MaterialTheme.colorScheme.primary) // Border around input field
                .padding(4.dp)
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (!viewModel.isLoading) makePrompt(inputText) // Sends the input if not loading
                })
            )
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space before button

        // Shows loading indicator if the system is processing a request
        if (viewModel.isLoading) {
            CircularProgressIndicator() // Displays a progress indicator while loading
        } else {
            // Button to send the prompt, only enabled when input is not empty
            Button(
                onClick = { makePrompt(inputText) },
                enabled = inputText.isNotBlank()
            ) {
                Text("Generar consulta") // Keeps button text in Spanish
            }
        }
    }
}
