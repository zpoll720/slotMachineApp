package com.example.slotmachineapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.slotmachineapp.ui.theme.SlotMachineAppTheme
//import com.example.counter.ui.theme.CounterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlotMachineAppTheme {   //uses Theme.kt under ui.theme folder. if user is in dark mode, will put app into dark mode
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SlotMachineScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun SlotMachineScreen (modifier: Modifier = Modifier) {
    var countA by remember { mutableIntStateOf(0) }
    var countB by remember { mutableIntStateOf(0) }
    var countC by remember { mutableIntStateOf(0) }

    var isCounting by remember { mutableStateOf(false) }
    val coroutine = rememberCoroutineScope()
    //has to specify its a job because it is getting set to null
    var countJobA: Job? by remember { mutableStateOf(null) }
    var countJobB: Job? by remember { mutableStateOf(null) }
    var countJobC: Job? by remember { mutableStateOf(null) }

    var speed by remember { mutableFloatStateOf(1000f) }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxSize()
    ) {
        Text (
            text = "Slots",
            fontSize = 80.sp,
            modifier = modifier.padding(top = 40.dp)
        )
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Text (
//            text = String.format("%.2f", count.toString()),
                text = "$countA",
                fontSize = 120.sp,
            )
            Text (
//            text = String.format("%.2f", count.toString()),
                text = "$countB",
                fontSize = 120.sp,
            )
            Text (
//            text = String.format("%.2f", count.toString()),
                text = "$countC",
                fontSize = 120.sp,
            )
        }

        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Slider (
                value = speed,
                onValueChange = { speed = it },
                valueRange = 100f .. 2000f,
                modifier = Modifier.padding(horizontal = 80.dp)
            )
            Text (
                text = "Speed: ${speed.toInt()}"
            )
        }

        //operations
        if (isCounting) {   //stop button
            Button (
                onClick = {
                    isCounting = false
                    countJobA?.cancel()
                    countJobB?.cancel()
                    countJobC?.cancel()
                },
                modifier = modifier.padding(bottom = 40.dp)
            ) {
                Text("Stop!")
            }
        } else {    //start button
            Button (
                onClick = {
                    isCounting = true
                    countJobA = coroutine.launch(Dispatchers.Default) {     //something that is run separate from the main activity.
                        while(true) {
                            if (countA == 3) {
                                delay(speed.toLong())       //pauses every second; 1000ms = 1s
                                countA = 0
                            } else {
                                delay(speed.toLong())
                                countA++
                            }
                        }
                    }
                    countJobB = coroutine.launch(Dispatchers.Default) {     //something that is run separate from the main activity.
                        while(true) {
                            if (countB == 3) {
                                delay(speed.toLong()/2)       //pauses every second; 1000ms = 1s
                                countB = 0
                            } else {
                                delay(speed.toLong()/2)
                                countB++
                            }
                        }
                    }
                    countJobC = coroutine.launch(Dispatchers.Default) {     //something that is run separate from the main activity.
                        while(true) {
                            if (countC == 3) {
                                delay(speed.toLong()/4)       //pauses every second; 1000ms = 1s
                                countC = 0
                            } else {
                                delay(speed.toLong()/8)
                                countC++
                            }
                        }
                    }


                },
                modifier = modifier.padding(bottom = 40.dp)
            ) {
                Text("Start!")
            }
        }

    }

}

//add a topAppBar to reset the app to zero. give the user the ability to make it count up or down (try radio buttons).