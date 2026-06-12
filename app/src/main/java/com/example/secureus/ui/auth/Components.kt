package com.example.secureus.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Design Colors from the image
val GlossyGreen = listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
val GlossyBlue = listOf(Color(0xFF1976D2), Color(0xFF0D47A1))
val GlossyRed = listOf(Color(0xFFD32F2F), Color(0xFFB71C1C))
val GlassWhite = Color(0x40B3E5FC) // Even more transparent blue (25% alpha)

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF64B5F6), Color(0xFF1976D2)),
                )
            )
    ) {
        // Add subtle wave pattern or texture if available, or just the gradient
        content()
    }
}

@Composable
fun DigiLogo(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp)
    ) {
        // Mock of the DW logo from the icon we made
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(2.dp, RoundedCornerShape(20.dp))
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(2.dp)
        ) {
             // We'll use a placeholder for the DW icon inside the UI
             Surface(
                 modifier = Modifier.fillMaxSize(),
                 shape = RoundedCornerShape(20.dp),
                 color = Color.Black
             ) {
                 Text(
                     "DW", 
                     color = Color.White, 
                     fontSize = 12.sp, 
                     fontWeight = FontWeight.Bold,
                     modifier = Modifier.wrapContentSize(Alignment.Center)
                 )
             }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "DIGI-",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black
        )
        Text(
            text = "WATCH",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFD32F2F)
        )
    }
}

@Composable
fun GlossyButton(
    text: String,
    onClick: () -> Unit,
    colors: List<Color>,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(Brush.verticalGradient(colors), RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun DigiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(label, color = Color.Black.copy(alpha = 0.6f)) },
        label = { Text(label, color = Color.Black) },
        leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = Color.Black) },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(4.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            unfocusedContainerColor = Color.White.copy(alpha = 0.9f),
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color.Black.copy(alpha = 0.3f),
            focusedBorderColor = Color.Black,
            focusedLabelColor = Color.Black,
            unfocusedLabelColor = Color.Black
        ),
        visualTransformation = visualTransformation,
        singleLine = true
    )
}
