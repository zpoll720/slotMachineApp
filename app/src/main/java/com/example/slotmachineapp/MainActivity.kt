package com.example.slotmachineapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.slotmachineapp.ui.theme.SlotMachineAppTheme
//import com.example.counter.ui.theme.CounterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlotMachineAppTheme {   //uses Theme.kt under ui.theme folder. if user is in dark mode, will put app into dark mode
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    SlotMachineScreen(
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "slotMachine") {
                        composable("slotMachine") {
                            SlotMachineScreen(modifier = Modifier.padding(innerPadding), onNavigateToDetails = { navController.navigate("details") })
                        }
                        composable("details") {
                            DetailsScreen(modifier = Modifier.padding(innerPadding), onNavigateToSlotMachine = { navController.navigate("slotMachine") })
                        }
                    }
                }
            }


        }
    }





}

@SuppressLint("DefaultLocale")
@Composable
fun SlotMachineScreen (modifier: Modifier = Modifier, onNavigateToDetails: () -> Unit) {
    var countA by remember { mutableIntStateOf(0) }
    var countB by remember { mutableIntStateOf(0) }
    var countC by remember { mutableIntStateOf(0) }

    var isCounting by remember { mutableIntStateOf(0) }
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
            Image (
                if (countA == 0) {
                    painterResource(id = R.drawable.strawberry)
                } else if (countA == 1){
                    painterResource(id = R.drawable.blueberry)
                } else if (countA == 2) {
                    painterResource(id = R.drawable.pear)
                } else {
                    painterResource(id = R.drawable.cherry)
                },
                contentDescription = "Slot Machine Wheel A",
                modifier = modifier.size(120.dp)
            )
            Image (
                if (countB == 0) {
                painterResource(id = R.drawable.strawberry)
            } else if (countB == 1){
                painterResource(id = R.drawable.blueberry)
            } else if (countB == 2) {
                painterResource(id = R.drawable.pear)
            } else {
                painterResource(id = R.drawable.cherry)
            },
                contentDescription = "Slot Machine Wheel B",
                modifier = modifier.size(120.dp)
            )
            Image (
                if (countC == 0) {
                painterResource(id = R.drawable.strawberry)
            } else if (countC == 1){
                painterResource(id = R.drawable.blueberry)
            } else if (countC == 2) {
                painterResource(id = R.drawable.pear)
            } else {
                painterResource(id = R.drawable.cherry)
            },
                contentDescription = "Slot Machine Wheel B",
                modifier = modifier.size(120.dp)
            )
            /*Text (
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
            )*/
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
        if (isCounting > 0) {   //stop button
            Button (
                onClick = {
                    if (isCounting == 3) {
                        countJobA?.cancel()
                    } else if (isCounting == 2) {
                        countJobB?.cancel()
                    } else if (isCounting == 1) {
                        countJobC?.cancel()
                    }

                    isCounting--

                    if (isCounting == 0) {
                        output = winOrLose(countA, countB, countC).first
                        mult = winOrLose(countA, countB, countC).second
                    }
                },
                modifier = modifier.padding(bottom = 40.dp)
            ) {
                Text("Stop!")
            }
        } else {    //start button
            Button (
                onClick = {
                    isCounting = 3
                    countJobA = coroutine.launch(Dispatchers.Default) {     //something that is run separate from the main activity.
                        while(true) {
                            if (countA == 3) {
                                delay(speed.toLong()/12)       //pauses every second; 1000ms = 1s
                                countA = 0
                            } else {
                                delay(speed.toLong()/12)
                                countA++
                            }
                        }
                    }
                    countJobB = coroutine.launch(Dispatchers.Default) {     //something that is run separate from the main activity.
                        while(true) {
                            if (countB == 3) {
                                delay(speed.toLong()/9)       //pauses every second; 1000ms = 1s
                                countB = 0
                            } else {
                                delay(speed.toLong()/9)
                                countB++
                            }
                        }
                    }
                    countJobC = coroutine.launch(Dispatchers.Default) {     //something that is run separate from the main activity.
                        while(true) {
                            if (countC == 3) {
                                delay(speed.toLong()/15)       //pauses every second; 1000ms = 1s
                                countC = 0
                            } else {
                                delay(speed.toLong()/15)
                                countC++
                            }
                        }
                    }
//                    speed

                },
                modifier = modifier.padding(bottom = 40.dp)
            ) {
                Text("Start!")
            }
        }
        Row (
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End,
            modifier = modifier.fillMaxWidth().padding(end = 10.dp)
        ) {
            Button (
                onClick = {
                    onNavigateToDetails()
                }
            ) {
                Text("Extra Info")
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
        return Pair("3 Strawberries x1.25", 1.25)
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


@Composable
fun MyAppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "slotMachine") {
        composable("slotMachine") {
            SlotMachineScreen(onNavigateToDetails = { navController.navigate("details") })
        }
        composable("details") {
            DetailsScreen(onNavigateToSlotMachine = { navController.navigate("slotMachine") })
        }
    }
}

@Composable
fun DetailsScreen(modifier: Modifier = Modifier, onNavigateToSlotMachine: () -> Unit) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text (
            text = "Winning Combos!",
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 70.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 15.dp, bottom = 50.dp)
        )
        Column (

        ) {
            Row (

            ) {
                Image (
                    painter = painterResource(id = R.drawable.strawberry),
                    contentDescription = "Strawberry",
                    modifier = Modifier
                )
                Text (
                    text = "1 Strawberry = x0.25",
                    //                fontSize = 40.sp,
                    modifier = Modifier
                )
            }
            Row (

            ) {
                Image (
                    painter = painterResource(id = R.drawable.strawberry),
                    contentDescription = "Strawberry",
                    modifier = Modifier
                )
                Image (
                    painter = painterResource(id = R.drawable.strawberry),
                    contentDescription = "Strawberry",
                    modifier = Modifier
                )
                Text (
                    text = "2 Strawberries = x0.75",
                    //                fontSize = 40.sp,
                    modifier = Modifier
                )
            }
            Row (

            ) {
                Image (
                    painter = painterResource(id = R.drawable.strawberry),
                    contentDescription = "Strawberry",
                    modifier = Modifier
                )
                Image (
                    painter = painterResource(id = R.drawable.strawberry),
                    contentDescription = "Strawberry",
                    modifier = Modifier
                )
                Image (
                    painter = painterResource(id = R.drawable.strawberry),
                    contentDescription = "Strawberry",
                    modifier = Modifier
                )
                Text (
                    text = "3 Strawberries = x1.25",
                    //                fontSize = 40.sp,
                    modifier = Modifier
                )
            }
        }

        Row (
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Image (
                painter = painterResource(id = R.drawable.blueberry),
                contentDescription = "Blueberry",
                modifier = Modifier
            )
            Image (
                painter = painterResource(id = R.drawable.blueberry),
                contentDescription = "Blueberry",
                modifier = Modifier
            )
            Image (
                painter = painterResource(id = R.drawable.blueberry),
                contentDescription = "Blueberry",
                modifier = Modifier
            )
            Text (
                text = "3 Blueberries = x3",
                //                fontSize = 40.sp,
                modifier = Modifier
            )
        }
        Row (

        ) {
            Image (
                painter = painterResource(id = R.drawable.pear),
                contentDescription = "Pear",
                modifier = Modifier
            )
            Image (
                painter = painterResource(id = R.drawable.pear),
                contentDescription = "Pear",
                modifier = Modifier
            )
            Image (
                painter = painterResource(id = R.drawable.pear),
                contentDescription = "Pear",
                modifier = Modifier
            )
            Text (
                text = "3 Pears = x5",
                //                fontSize = 40.sp,
                modifier = Modifier
            )
        }
        Row (

        ) {
            Image (
                painter = painterResource(id = R.drawable.cherry),
                contentDescription = "Cherry",
                modifier = Modifier
            )
            Image (
                painter = painterResource(id = R.drawable.cherry),
                contentDescription = "Cherry",
                modifier = Modifier
            )
            Image (
                painter = painterResource(id = R.drawable.cherry),
                contentDescription = "Cherry",
                modifier = Modifier
            )
            Text (
                text = "3 Cherrys = x20",
                //                fontSize = 40.sp,
                modifier = Modifier
            )
        }
        Row (
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxSize().padding(bottom = 50.dp)
        ) {
            Button (
                onClick = {
                    onNavigateToSlotMachine()
                },
                modifier = Modifier
            ) {
                Text("Back to Slots!")
            }
        }


    }
}




//add a topAppBar to reset the app to zero. give the user the ability to make it count up or down (try radio buttons).
/*
strawberries = 0 = small win (1 strawberry = .25, 2 strawberries = .75, 3 strawberries = 1.25)
blueberries = 1 (3 for 3x)
pear = 2  x5
cherry = 3 = JACKPOT!  x20
*/