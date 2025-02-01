package com.example.calculatorproto

import android.Manifest
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import android.content.IntentFilter
import android.content.res.Configuration
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calculatorproto.misc.CalculatorToken
import com.example.calculatorproto.misc.CustomTheme
import com.example.calculatorproto.viewmodels.CalculatorViewModel
import com.example.calculatorproto.misc.NotificationReceiver
import com.example.calculatorproto.services.NotificationService
import com.example.calculatorproto.ui.theme.CalculatorProtoTheme
import com.example.calculatorproto.viewmodels.ThemeViewModel
import kotlinx.coroutines.delay
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<CalculatorViewModel>();
    private val themeViewModel by viewModels<ThemeViewModel>();
    private lateinit var cameraManager: CameraManager
    private val PREFS_NAME = "historyPreferences"

    @RequiresApi(Build.VERSION_CODES.S)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SCHEDULE_EXACT_ALARM,
                Manifest.permission.USE_EXACT_ALARM),
                1)
        }

        val notificationService = NotificationService()

        cameraManager = getSystemService("camera") as CameraManager

        val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        var uid = settings.getString("firestoreDeviceId", null);

        if (uid == null) {
            val editor = settings.edit()
            editor.putString("firestoreDeviceId", UUID.randomUUID().toString())
            editor.apply()
            uid = settings.getString("firestoreDeviceId", null)
        }
        viewModel.setUid(uid!!)

        setContent {
            CalculatorProtoTheme (uid = uid) {
                notificationService.scheduleNotificationSet(applicationContext)

                val configuration = LocalConfiguration.current
                val fallbackColorScheme: ColorScheme = MaterialTheme.colorScheme.copy()
//                if (isSystemInDarkTheme()) {
//                    fallbackColorScheme = MaterialTheme.colorScheme.
//                }

                val isLoading by themeViewModel.isLoading.collectAsStateWithLifecycle()
                LaunchedEffect(key1 = true) {
                    try {
                        themeViewModel.loadCustomTheme(uid, fallbackColorScheme)
                    } catch (ex: Exception) {
                        println(ex)
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    if (themeViewModel.theme == null) {
                        CircularProgressIndicator()
                    } else {
                        Scaffold(
                            topBar = {
                                when (configuration.orientation) {
                                    Configuration.ORIENTATION_LANDSCAPE -> {
                                        TopAppBar(modifier = Modifier.height(20.dp), title = {})
                                    }

                                    else -> {
                                        TopAppBar(title = {
                                            Text(
                                                "Proto",
                                                fontSize = 22.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                            colors = TopAppBarColors(
                                                containerColor = themeViewModel.theme!!.surface!!,
                                                scrolledContainerColor = themeViewModel.theme!!.surface!!,
                                                navigationIconContentColor = themeViewModel.theme!!.onPrimary!!,
                                                titleContentColor = themeViewModel.theme!!.onPrimary!!,
                                                actionIconContentColor = themeViewModel.theme!!.onPrimary!!
                                            ),
                                            actions = {
                                                IconButton(onClick = {
                                                    val intent = Intent(
                                                        this@MainActivity,
                                                        SettingsActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                }) {
                                                    Icon(
                                                        Icons.Filled.Settings,
                                                        contentDescription = "Settings"
                                                    )
                                                }
                                                IconButton(onClick = {
                                                    val intent = Intent(
                                                        this@MainActivity,
                                                        HistoryActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                }) {
                                                    Icon(
                                                        Icons.Filled.DateRange,
                                                        contentDescription = "History"
                                                    )
                                                }
                                            })
                                    }
                                }

                            },
                            bottomBar = {
                                BottomAppBar(
                                    modifier = Modifier.height(20.dp),
                                    containerColor = themeViewModel.theme!!.surface!!
                                ) { }
                            },
                            content = { innerPadding ->
                                Surface(
                                    color = themeViewModel.theme!!.surface!!,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {

                                    when (configuration.orientation) {
                                        Configuration.ORIENTATION_LANDSCAPE -> {
                                            Row(modifier = Modifier.fillMaxWidth()) {
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
                                                ButtonGridBasic(
                                                    viewModel,
                                                    themeViewModel,
                                                    Modifier.weight(1f).fillMaxHeight().padding(4.dp)
                                                )
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
                                                ButtonGridBasic(
                                                    viewModel,
                                                    themeViewModel,
                                                    Modifier.fillMaxWidth().padding(4.dp)
                                                )
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
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ButtonGridBasic(viewModel: CalculatorViewModel, themeViewModel: ThemeViewModel, modifier: Modifier = Modifier) {

    val buttonTokens = listOf(
        listOf(
            CalculatorToken.CLEAR,
            CalculatorToken.BACKSPACE,
            CalculatorToken.OPEN_PARENTHESES,
            CalculatorToken.CLOSE_PARENTHESES,
            CalculatorToken.SQRT),
        listOf(
            CalculatorToken.SEVEN,
            CalculatorToken.EIGHT,
            CalculatorToken.NINE,
            CalculatorToken.MULTIPLY,
            CalculatorToken.SIN),
        listOf(
            CalculatorToken.FOUR,
            CalculatorToken.FIVE,
            CalculatorToken.SIX,
            CalculatorToken.SUBTRACT,
            CalculatorToken.COS),
        listOf(
            CalculatorToken.ONE,
            CalculatorToken.TWO,
            CalculatorToken.THREE,
            CalculatorToken.ADD,
            CalculatorToken.TAN),
        listOf(
            CalculatorToken.FRACTION,
            CalculatorToken.ZERO,
            CalculatorToken.EQUALS,
            CalculatorToken.DIVIDE,
            CalculatorToken.COT)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        buttonTokens.forEach { buttonTokenRow ->
            Row (
                modifier = Modifier.fillMaxWidth()
            ) {
                buttonTokenRow.forEach { buttonToken ->
                    when (buttonToken) {
                        CalculatorToken.CLEAR,
                        CalculatorToken.OPEN_PARENTHESES,
                        CalculatorToken.CLOSE_PARENTHESES,
                        CalculatorToken.BACKSPACE,
                        CalculatorToken.SUBTRACT,
                        CalculatorToken.DIVIDE,
                        CalculatorToken.MULTIPLY,
                        CalculatorToken.ADD -> {
                            CalcButton(
                                viewModel,
                                buttonToken,
                                customFontColor = themeViewModel.theme!!.onPrimaryContainer!!,
                                customBackgroundColor = themeViewModel.theme!!.primaryContainer!!,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        CalculatorToken.SQRT,
                        CalculatorToken.SIN,
                        CalculatorToken.COS,
                        CalculatorToken.TAN,
                        CalculatorToken.COT -> {
                            CalcButton(
                                viewModel,
                                buttonToken,
                                customBackgroundColor = themeViewModel.theme!!.onSecondary!!,
                                customFontColor = themeViewModel.theme!!.secondary!!,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        CalculatorToken.EQUALS -> {
                            CalcButton(
                                viewModel,
                                buttonToken,
                                customBackgroundColor = themeViewModel.theme!!.onPrimaryContainer!!,
                                customFontColor = themeViewModel.theme!!.primaryContainer!!,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        else -> {
                            CalcButton(
                                viewModel,
                                buttonToken,
                                customBackgroundColor = themeViewModel.theme!!.primary!!,
                                customFontColor = themeViewModel.theme!!.onPrimary!!,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
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
                modifier = modifier
                    .width(100.dp)
                    .height(70.dp)
                    .padding(4.dp),
                shape = RoundedCornerShape(20.dp)
            ){
                Text(token.displaySymbol, fontSize = 20.sp)
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
                contentPadding = PaddingValues(
                    all = 0.dp
                ),
                colors = ButtonColors(
                    containerColor = customBackgroundColor,
                    contentColor = customFontColor,
                    disabledContainerColor = customBackgroundColor,
                    disabledContentColor = customFontColor,
                ),
                modifier = modifier
                    .height(80.dp)
                    .padding(4.dp),

//            .shadow(5.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ){

                Text(token.displaySymbol, fontSize = 20.sp)
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
                fontSize = 50.sp,
                textAlign = TextAlign.Right,
                maxLines = 100,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f).
                    verticalScroll(scroll)
                    .wrapContentHeight(align = Alignment.Bottom),
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        lineHeight = 1.25.em
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