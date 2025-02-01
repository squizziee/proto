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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorproto.ui.theme.CalculatorProtoTheme
import com.example.calculatorproto.viewmodels.SettingsViewModel
import com.example.calculatorproto.viewmodels.ThemeViewModel
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import java.util.UUID

class SettingsActivity : ComponentActivity() {

    private val viewModel by viewModels<SettingsViewModel>()
    private val themeViewModel by viewModels<ThemeViewModel>();
    private val PREFS_NAME = "historyPreferences"

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        var uid = settings.getString("firestoreDeviceId", null);
        if (uid == null) {
            val editor = settings.edit()
            editor.putString("firestoreDeviceId", UUID.randomUUID().toString())
            editor.apply()
            uid = settings.getString("firestoreDeviceId", null)
        }

        setContent {

            CalculatorProtoTheme (uid = uid!!) {
                val configuration = LocalConfiguration.current
                val fallbackColorScheme = MaterialTheme.colorScheme.copy()

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
                                        IconButton(onClick = {
                                            viewModel.addCustomTheme(uid, fallbackColorScheme)
                                        }) { Icon(Icons.Filled.Done, contentDescription = "Home") }
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
//                                Button(onClick = {
//                                    viewModel.addCustomTheme(
//                                        uid!!
//                                    )
//                                }) {
//                                    Text("Apply")
//                                }
                                Tabs(viewModel)
                            }
//                            SettingsSetup(viewModel)
                        }

                }
            }
        }
    }
}

data class TabItem (
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Tabs(viewModel: SettingsViewModel) {
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    val tabItems = listOf(
        TabItem(
            title = "Button",
            icon = Icons.AutoMirrored.Outlined.List,
            selectedIcon = Icons.AutoMirrored.Filled.List
        ),
        TabItem(
            title = "Button text",
            icon = Icons.AutoMirrored.Outlined.List,
            selectedIcon = Icons.AutoMirrored.Filled.List
        ),
        TabItem(
            title = "Op. Button",
            icon = Icons.AutoMirrored.Outlined.Send,
            selectedIcon = Icons.AutoMirrored.Filled.Send
        ),
        TabItem(
            title = "Op. Button Text",
            icon = Icons.Outlined.Build,
            selectedIcon = Icons.Filled.Build
        ),
        TabItem(
            title = "Spec. Button",
            icon = Icons.Outlined.FavoriteBorder,
            selectedIcon = Icons.Filled.Favorite
        ),
        TabItem(
            title = "Spec. Button Text",
            icon = Icons.Outlined.Face,
            selectedIcon = Icons.Filled.Face
        ),
        TabItem(
            title = "Background",
            icon = Icons.Outlined.Home,
            selectedIcon = Icons.Filled.Home
        ),
    )

    val pagerState = rememberPagerState {
        tabItems.size
    }

    val pageContent = listOf(
        Pair("c1", "Button Text Color"),
        Pair("c2", "Input Text Color"),
        Pair("c3", "Status Bar Color"),
        Pair("c4", "Status Bar Color"),
        Pair("c6", "Status Bar Color"),
        Pair("c5", "Status Bar Color"),
        Pair("c7", "Status Bar Color"),
    )

    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    Column (modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                if (selectedTabIndex < tabPositions.size) {
                    SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        ) {
            tabItems.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = {
                        Text(
                            item.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    },
                    icon = {
                        Icon (
                            imageVector =
                                if (index == selectedTabIndex) {
                                    item.selectedIcon
                                } else {
                                    item.icon
                                },
                            contentDescription = item.title,
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                )
            }
        }

        HorizontalPager(
            beyondBoundsPageCount = tabItems.size,
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
        ) { index ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SettingsSetup(
                    viewModel,
                    pageContent[index].first,
                )
            }
        }
    }

}

@Composable
fun SettingsSetup(viewModel: SettingsViewModel, type: String) {
    val controller = rememberColorPickerController()
    Column {
        AlphaTile(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            controller = controller
        )
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .padding(vertical = 50.dp),
            controller = controller,
            onColorChanged = { colorEnvelope ->  
                when (type) {
                    "c1" -> viewModel.currentC1 = colorEnvelope.color.toArgb()
                    "c2" -> viewModel.currentC2 = colorEnvelope.color.toArgb()
                    "c3" -> viewModel.currentC3 = colorEnvelope.color.toArgb()
                    "c4" -> viewModel.currentC4 = colorEnvelope.color.toArgb()
                    "c5" -> viewModel.currentC5 = colorEnvelope.color.toArgb()
                    "c6" -> viewModel.currentC6 = colorEnvelope.color.toArgb()
                    "c7" -> viewModel.currentC7 = colorEnvelope.color.toArgb()
                }
            },
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            controller = controller,
            borderRadius = 0.dp
        )
    }
}