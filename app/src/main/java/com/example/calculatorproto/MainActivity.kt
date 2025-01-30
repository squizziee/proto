package com.example.calculatorproto

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.calculatorproto.ui.theme.CalculatorProtoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<CalculatorViewModel>();
    private lateinit var cameraManager: CameraManager
    private val PREFS_NAME = "historyPreferences"
    private var notificationService = NotificationService()

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM,
                android.Manifest.permission.USE_EXACT_ALARM),
                1)
        }

        notificationService.scheduleNotification(applicationContext, 10)
        notificationService.scheduleNotification(applicationContext, 60)
        notificationService.scheduleNotification(applicationContext, 300)
        notificationService.scheduleNotification(applicationContext, 15000)
        notificationService.scheduleNotification(applicationContext, 86400)

        cameraManager = getSystemService("camera") as CameraManager

        setContent {
            CalculatorProtoTheme {
                val configuration = LocalConfiguration.current
                val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
                var uid = settings.getString("firestoreDeviceId", null);

                if (uid == null) {
                    val editor = settings.edit()

                    editor.putString("firestoreDeviceId", UUID.randomUUID().toString())

                    editor.apply()

                    uid = settings.getString("firestoreDeviceId", null)
                }
                viewModel.setUid(uid!!)

                Scaffold (
                    topBar = {
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                TopAppBar(modifier = Modifier.height(20.dp), title = {})
                            }
                            else -> {
                                TopAppBar(title= { Text("Proto", fontSize = 22.sp, fontWeight = FontWeight.Bold)},
                                    actions={
                                        IconButton({ }) {Icon(Icons.Filled.Settings, contentDescription = "Settings")}
                                        IconButton(onClick = {
                                            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
                                            startActivity(intent)
                                        }) {Icon(Icons.Filled.DateRange, contentDescription = "History")}
                                    })
                            }
                        }

                    },
                    bottomBar = {
                        BottomAppBar (modifier = Modifier.height(20.dp)) {  }
                    },
                    content = { innerPadding ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> {
                                    Row (modifier = Modifier.fillMaxWidth()) {
                                        val coroutineScope = rememberCoroutineScope()
                                        if (viewModel.getEvaluated() == "Error") {
                                            LaunchedEffect(coroutineScope) {
                                                val cameraID = cameraManager.cameraIdList[0]
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, true)
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, false)
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, true)
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, false)
                                            }
                                        }
                                        Screen(
                                            viewModel.getStringExpression(),
                                            "= " + viewModel.getEvaluated(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .wrapContentHeight(Alignment.Bottom)
                                        )
                                        ButtonGridBasic(viewModel, Modifier.weight(1f).fillMaxHeight().padding(4.dp))
                                    }
                                }
                                else -> {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        val coroutineScope = rememberCoroutineScope()
                                        if (viewModel.getEvaluated() == "Error") {
                                            LaunchedEffect(coroutineScope) {
                                                val cameraID = cameraManager.cameraIdList[0]
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, true)
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, false)
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, true)
                                                delay(100)
                                                cameraManager.setTorchMode(cameraID, false)
                                            }
                                        }
                                        Screen(
                                            viewModel.getStringExpression(),
                                            "= " + viewModel.getEvaluated(),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .wrapContentHeight(Alignment.Bottom)
                                        )
                                        ButtonGridBasic(viewModel, Modifier.fillMaxWidth().padding(4.dp))
                                    }
                                }
                            }

                        }
                    }
                )

            }
        }
    }
}

@Composable
fun ButtonGridBasic(viewModel: CalculatorViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(viewModel, CalculatorToken.CLEAR,
                customFontColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            DoubleCalcButton(viewModel, CalculatorToken.BACKSPACE,
                customFontColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            CalcButton(viewModel, CalculatorToken.DIVIDE,
                customFontColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(viewModel, CalculatorToken.SEVEN)
            CalcButton(viewModel, CalculatorToken.EIGHT)
            CalcButton(viewModel, CalculatorToken.NINE)
            CalcButton(viewModel, CalculatorToken.MULTIPLY,
                customFontColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(viewModel, CalculatorToken.FOUR)
            CalcButton(viewModel, CalculatorToken.FIVE)
            CalcButton(viewModel, CalculatorToken.SIX)
            CalcButton(viewModel, CalculatorToken.SUBTRACT,
                customFontColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(viewModel, CalculatorToken.ONE)
            CalcButton(viewModel, CalculatorToken.TWO)
            CalcButton(viewModel, CalculatorToken.THREE)
            CalcButton(viewModel, CalculatorToken.ADD,
                customFontColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CalcButton(viewModel, CalculatorToken.FRACTION)
            CalcButton(viewModel, CalculatorToken.ZERO)
            DoubleCalcButton(viewModel, CalculatorToken.EQUALS,
                customBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer,
                customFontColor = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}

@Composable
fun CalcButton(
        viewModel: CalculatorViewModel,
        token: CalculatorToken,
        modifier: Modifier = Modifier,
        customFontColor: Color = MaterialTheme.colorScheme.onPrimary,
        customBackgroundColor: Color = MaterialTheme.colorScheme.primary,
    ) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val vibrator = context.getSystemService("vibrator") as Vibrator

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Button(
                onClick = {
                    viewModel.updateExpression(token)
                    val vibrationEffect1 =
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)

                    vibrator.cancel()
                    vibrator.vibrate(vibrationEffect1)
                },
                colors = ButtonColors(
                    containerColor = customBackgroundColor,
                    contentColor = customFontColor,
                    disabledContainerColor = customBackgroundColor,
                    disabledContentColor = customFontColor,
                ),
                modifier = Modifier
                    .width(100.dp)
                    .height(70.dp)
                    .padding(4.dp),
//            .shadow(5.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ){

                Text(token.symbol, fontSize = 24.sp)
            }
        }
        else -> {
            Button(
                onClick = {
                    viewModel.updateExpression(token)
                    val vibrationEffect1 =
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)

                    vibrator.cancel()
                    vibrator.vibrate(vibrationEffect1)
                },
                colors = ButtonColors(
                    containerColor = customBackgroundColor,
                    contentColor = customFontColor,
                    disabledContainerColor = customBackgroundColor,
                    disabledContentColor = customFontColor,
                ),
                modifier = Modifier
                    .size(100.dp)
                    .padding(4.dp),
//            .shadow(5.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ){

                Text(token.symbol, fontSize = 24.sp)
            }
        }
    }

}

@Composable
fun DoubleCalcButton(
    viewModel: CalculatorViewModel,
    token: CalculatorToken,
    modifier: Modifier = Modifier,
    customFontColor: Color = MaterialTheme.colorScheme.onPrimary,
    customBackgroundColor: Color = MaterialTheme.colorScheme.primary,
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val vibrator = context.getSystemService("vibrator") as Vibrator

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            Button(
                onClick = {
                    viewModel.updateExpression(token)
                    val vibrationEffect1 =
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)

                    vibrator.cancel()
                    vibrator.vibrate(vibrationEffect1)
                },

                colors = ButtonColors(
                    containerColor = customBackgroundColor,
                    contentColor = customFontColor,
                    disabledContainerColor = customBackgroundColor,
                    disabledContentColor = customFontColor,
                ),
                modifier = Modifier
                    .width(200.dp)
                    .height(70.dp)
                    .padding(4.dp),
//            .shadow(5.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ){

                Text(token.symbol, fontSize = 24.sp)
            }
        }
        else -> {
            Button(
                onClick = {
                    viewModel.updateExpression(token)
                    val vibrationEffect1 =
                        VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)

                    vibrator.cancel()
                    vibrator.vibrate(vibrationEffect1)
                },
                colors = ButtonColors(
                    containerColor = customBackgroundColor,
                    contentColor = customFontColor,
                    disabledContainerColor = customBackgroundColor,
                    disabledContentColor = customFontColor,
                ),
                modifier = Modifier
                    .height(100.dp)
                    .width(200.dp)
                    .padding(4.dp),
//            .shadow(5.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ){

                Text(token.symbol, fontSize = 24.sp)
            }
        }
    }

}

@Composable
fun Screen(text: String, lowerText: String, modifier: Modifier = Modifier) {
    val scroll = rememberScrollState(0)

    Row (modifier = modifier) {
        Column (
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text,
                fontSize = 60.sp,
                textAlign = TextAlign.Right,
                maxLines = 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f).
                    verticalScroll(scroll)
                    .wrapContentHeight(align = Alignment.Bottom),
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        lineHeight = 0.85.em
                    )
                )
            )
            Text(
                lowerText,
                fontSize = 30.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}