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

    var output by remember { mutableStateOf("") }
    var mult by remember { mutableDoubleStateOf(0.0) }

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

        Text (
            text = output,
            fontSize = 40.sp,
//            modifier = modifier.padding(bottom = 40.dp)
        )

        //operations
        if (isCounting) {   //stop button
            Button (
                onClick = {
                    isCounting = false
                    countJobA?.cancel()
                    countJobB?.cancel()
                    countJobC?.cancel()
                    output = winOrLose(countA, countB, countC).first
                    mult = winOrLose(countA, countB, countC).second
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
                                delay(speed.toLong()/4)       //pauses every second; 1000ms = 1s
                                countA = 0
                            } else {
                                delay(speed.toLong()/4)
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
                                delay(speed.toLong()/8)       //pauses every second; 1000ms = 1s
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

fun winOrLose(a: Int, b: Int, c: Int): Pair<String, Double>  {
    var strawCount = 0

    //how many strawberries?
    if (a == 0) {
       strawCount++
    }
    if (b == 0) {
        strawCount++
    }
    if (c == 0) {
        strawCount++
    }

    //strawberry wins
    if (strawCount == 1) {
        return Pair("1 Strawberry x0.25", 0.25)
    } else if (strawCount == 2) {
        return Pair("2 Strawberries x0.75", 0.75)
    } else if (strawCount == 3) {
        return Pair("3 Strawberries x1.5", 1.5)
    }
    //blueberry, pear, and cherry win
    if (a == 1 && b == 1 && c == 1) {
        return Pair("3 Blueberries x3", 3.0)
    } else if (a == 2 && b == 2 && c == 2) {
        return Pair("3 Pears x5", 5.0)
    } else if (a == 3 && b == 3 && c == 3) {
        return Pair("3 Cherries x20", 20.0)
    } else {
        return Pair("Loser", 0.0)
    }
}

//add a topAppBar to reset the app to zero. give the user the ability to make it count up or down (try radio buttons).
/*
strawberries = 0 = small win (1 strawberry = .25, 2 strawberries = .75, 3 strawberries = 1.5)
blueberries = 1 (3 for 3x)
pear = 2
cherry = 3 = JACKPOT!
*/