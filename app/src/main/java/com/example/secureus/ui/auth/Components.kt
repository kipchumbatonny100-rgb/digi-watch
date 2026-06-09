package com.example.secureus.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val DigiGreen = Color(0xFF4CAF50)
val DigiGreenDark = Color(0xFF388E3C)
val DigiBlue = Color(0xFF2196F3)
val DigiBlueDark = Color(0xFF1976D2)
val DigiGrey = Color(0xFFE0E0E0)
val DigiRed = Color(0xFFD32F2F)
val DigiRedDark = Color(0xFFC62828)

@Composable
fun DigiLogo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
    ) {
        // Simple text logo representation
        Text(
            text = "DIGI-",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "WATCH",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = DigiRed
        )
    }
}

@Composable
fun DigiButton(
    text: String,
    onClick: () -> Unit,
    colors: List<Color> = listOf(DigiGreen, DigiGreenDark),
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                brush = Brush.verticalGradient(colors),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DigiSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        modifier = modifier.fillMaxWidth().height(50.dp)
    ) {
        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
fun DigiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = DigiGreen
        ),
        visualTransformation = visualTransformation,
        singleLine = true
    )
}
