package com.example.tipper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipper.ui.theme.TipperTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipperTheme {
                Scaffold(
                    topBar = {
                        AppBar()
                    },
                ) { paddingValues ->
                    Body(paddingValues)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar() {
    TopAppBar({
        Text("Tip Calculator")
    })
}

@Composable
fun Body(paddingValues: PaddingValues) {
    var baseValue by remember { mutableStateOf("") }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var tipValue by remember { mutableStateOf("") }
    var totalValue by remember { mutableStateOf("") }
    fun updateCalculations() {
        if(baseValue.isNotBlank()) {
            tipValue = calculateTip(baseValue, sliderPosition)
            totalValue = (baseValue.toFloat() + tipValue.toFloat()).toString()
        } else {
            tipValue = "0.0"
            totalValue = "0.0"
        }
    }
    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(paddingValues)
            .padding(
                start = 40.dp,
                top = 30.dp,
                bottom = 30.dp,
                end = 50.dp,
            )
            .fillMaxSize()

    ) {
        BaseValueComponent(baseValue) { newValue ->
            baseValue = newValue
            updateCalculations()
        }
        Spacer(Modifier.height(30.dp))
        SliderComponent(sliderPosition) { newPosition ->
            sliderPosition = newPosition
            updateCalculations()
        }
        Spacer(Modifier.height(50.dp))
        TipComponent(tipValue)
        Spacer(Modifier.height(30.dp))
        TotalValueComponent(totalValue)
    }
}


@Composable
fun TextComponent(text: String) {
    Text(
        text, style = TextStyle(
            fontSize = 17.sp

        )
    )
}

@Composable
fun SpacerComponent() {
    Spacer(Modifier.width(20.dp))
}

@Composable
fun BaseValueComponent(baseValue: String, onValueChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextComponent("Base")
        SpacerComponent()
        TextField(
            singleLine = true,
            value = baseValue, onValueChange = onValueChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            label = { Text("Bill Amount", style = TextStyle(
                color = Color.Gray,
                fontSize = 18.sp
            ) )},
            shape = RoundedCornerShape(4.dp)
        )
    }
}

@Composable
fun SliderComponent(sliderPosition: Float, onValueChange: (Float) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        var tipTag by remember { mutableStateOf(TipTag.Poor) }
        TextComponent("${sliderPosition.toInt()}%")
        SpacerComponent()
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Slider(
                modifier = Modifier.width(270.dp),
                value = sliderPosition,
                onValueChange = onValueChange,
                valueRange = 0f..30f
            )
            tipTag = when (sliderPosition.toInt()) {
                in 0..10 -> TipTag.Poor
                in 10..15 -> TipTag.Average
                in 15..25 -> TipTag.Good
                else -> TipTag.Amazing
            }
            Text(
                tipTag.toString(), style = TextStyle(
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Composable
fun TipComponent(tipValue: String) {
    Row {
        TextComponent("Tip")
        SpacerComponent()
        TextComponent(tipValue)
        Spacer(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun TotalValueComponent(totalValue: String) {
    Row {
        TextComponent("Total")
        SpacerComponent()
        TextComponent(totalValue)
        Spacer(modifier = Modifier.fillMaxWidth())
    }

}

enum class TipTag {
    Poor,
    Average,
    Good,
    Amazing
}

fun calculateTip(baseValue: String, sliderPosition: Float): String {
    val base = baseValue.toFloatOrNull() ?: 0.0f
    val tipPercentage = sliderPosition / 100
    val tip = base * tipPercentage
    return "%.2f".format(tip)
}