package com.example.kotlin_app_study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlin_app_study.ui.theme.KotlinappstudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinappstudyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E3A5F),
                        Color(0xFF4A90D9),
                        Color(0xFFF9A825)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "北京",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Icon(
            imageVector = Icons.Rounded.WbSunny,
            contentDescription = null,
            tint = Color(0xFFFDD835),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "26°",
            color = Color.White,
            fontSize = 72.sp,
            fontWeight = FontWeight.Thin
        )

        Text(
            text = "晴朗",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 20.sp
        )

        Text(
            text = "最高 30° / 最低 18°",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetail(icon = Icons.Rounded.Air, value = "12 km/h", label = "风速")
                WeatherDetail(icon = Icons.Rounded.WaterDrop, value = "45%", label = "湿度")
                WeatherDetail(icon = Icons.Rounded.Visibility, value = "10 km", label = "能见度")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "今日预报",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    HourItem(time = "09:00", temp = "22°", icon = Icons.Rounded.WbSunny)
                    HourItem(time = "12:00", temp = "26°", icon = Icons.Rounded.WbSunny)
                    HourItem(time = "15:00", temp = "30°", icon = Icons.Rounded.WbSunny)
                    HourItem(time = "18:00", temp = "25°", icon = Icons.Rounded.Opacity)
                    HourItem(time = "21:00", temp = "20°", icon = Icons.Rounded.NightsStay)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "未来几天",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))
                DayForecastRow("周四", Icons.Rounded.WbSunny, "30°", "18°")
                DayForecastRow("周五", Icons.Rounded.Opacity, "27°", "16°")
                DayForecastRow("周六", Icons.Rounded.WaterDrop, "22°", "14°")
                DayForecastRow("周日", Icons.Rounded.WbSunny, "28°", "17°")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WeatherDetail(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = label, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
    }
}

@Composable
fun HourItem(time: String, temp: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = time, color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFDD835),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = temp, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DayForecastRow(day: String, icon: ImageVector, high: String, low: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = day,
            color = Color.White,
            fontSize = 15.sp,
            modifier = Modifier.width(48.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFFDD835),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = low, color = Color.White.copy(alpha = 0.5f), fontSize = 15.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = high, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    KotlinappstudyTheme {
        WeatherScreen()
    }
}