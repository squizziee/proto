package com.example.calculatorproto

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calculatorproto.ui.theme.CalculatorProtoTheme
import com.example.calculatorproto.viewmodels.HistoryViewModel
import com.example.calculatorproto.viewmodels.SettingsViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.util.UUID

class SettingsActivity : ComponentActivity() {

    private val viewModel by viewModels<SettingsViewModel>()
    private val PREFS_NAME = "historyPreferences"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        when (configuration.orientation) {
                            Configuration.ORIENTATION_LANDSCAPE -> {
                                TopAppBar(modifier = Modifier.height(20.dp), title = {})
                            }
                            else -> {
                                TopAppBar(title= { Text("Proto", fontSize = 22.sp, fontWeight = FontWeight.Bold)},
                                    actions={
                                        IconButton(onClick = {
                                            val intent = Intent(this@SettingsActivity, HistoryActivity::class.java)
                                            startActivity(intent)
                                        }) { Icon(Icons.Filled.DateRange, contentDescription = "History") }
                                        IconButton(onClick = {
                                            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                                            startActivity(intent)
                                        }) { Icon(Icons.Filled.Home, contentDescription = "Home") }
                                    })
                            }
                        }

                    },
                    bottomBar = {
                        BottomAppBar (modifier = Modifier.height(20.dp)) {  }
                    },
                ) { innerPadding ->
                        Surface (
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(innerPadding)
                        ) {
                            Column (
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                Button(onClick = {
                                    viewModel.addCustomTheme(
                                        uid!!
                                    )
                                }) {
                                    Text("Apply")
                                }
                                SettingsSetup(viewModel, "A", "Button Text Color")
                                SettingsSetup(viewModel, "B", "Input Text Color")
                                SettingsSetup(viewModel, "C", "Status Bar Color")
                            }
//                            SettingsSetup(viewModel)
                        }

                }
            }
        }
    }
}

//@OptIn(ExperimentalFoundationApi::class)
//@Composable
//fun Tabs(viewModel: SettingsViewModel) {
//    var selectedTabIndex by remember {
//        mutableIntStateOf(0)
//    }
//
//    val periodLabels = listOf(
//        "Button Text Color",
//        "Status Bar Color",
//        "Input Text Color",
//    )
//
//    val periodContent = listOf(
//        SettingsSetup(viewModel, "#000000"),
////        SettingsSetup(viewModel, "2"),
////        SettingsSetup(viewModel, "3")
//    )
//
//    val pagerState = rememberPagerState {
//        periodContent.size
//    }
//
//    Column (modifier = Modifier.fillMaxSize()) {
//        TabRow(
//            selectedTabIndex = selectedTabIndex,
//        ) {
//            periodLabels.forEachIndexed { index, title ->
//                Tab(
//                    selected = selectedTabIndex == index,
//                    onClick = {
//                        selectedTabIndex = index
//                    },
//                    text = {
//                        Text(
//                            text = title,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis,
//                            color = MaterialTheme.colorScheme.onSurface,
//                        )
//                    },
//                )
//            }
//        }
//
//        HorizontalPager(
//            state = pagerState,
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(1f)
//        ) { index ->
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                periodContent[index]
//            }
//        }
//    }
//
//}

@Composable
fun SettingsSetup(viewModel: SettingsViewModel, type: String, title: String) {
    val controller = rememberColorPickerController()
    //(Color(android.graphics.Color.parseColor(color)))
    Column {
        Text(title, style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp), modifier = Modifier.fillMaxWidth().height(40.dp))
        AlphaTile(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .clip(RoundedCornerShape(6.dp)),
            controller = controller
        )
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = { colorEnvelope ->  
                when (type) {
                    "A" -> viewModel.currentA = colorEnvelope.hexCode
                    "B" -> viewModel.currentB = colorEnvelope.hexCode
                    "C" -> viewModel.currentC = colorEnvelope.hexCode
                }
            },

        )
    }
}