package com.example.calculatorproto

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.calculatorproto.viewmodels.HistoryViewModel
import com.example.calculatorproto.ui.theme.CalculatorProtoTheme
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class HistoryActivity : ComponentActivity() {

    private val viewModel by viewModels<HistoryViewModel>()
    private val PREFS_NAME = "historyPreferences"

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val settings = applicationContext.getSharedPreferences(PREFS_NAME, 0)
        var uid = settings.getString("firestoreDeviceId", null);

        if (uid == null) {
            val editor = settings.edit()

            editor.putString("firestoreDeviceId", UUID.randomUUID().toString())

            editor.apply()

            uid = settings.getString("firestoreDeviceId", null);
        }

        setContent {
            CalculatorProtoTheme (uid = uid!!) {
                val configuration = LocalConfiguration.current
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
                                        val intent = Intent(this@HistoryActivity, SettingsActivity::class.java)
                                        startActivity(intent)
                                    }) { Icon(Icons.Filled.Settings, contentDescription = "Settings") }
                                    IconButton(onClick = {
                                        val intent = Intent(this@HistoryActivity, MainActivity::class.java)
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

                    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                    LaunchedEffect(key1 = true) {
                        viewModel.loadHistory(uid)
                    }

                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        Surface (
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White)
                                .padding(innerPadding)
                        ) {
                            HistoryEntries(viewModel)
                        }

                    }

                }
            }
        }
    }
}



@Composable
fun HistoryEntries(viewModel: HistoryViewModel, modifier: Modifier = Modifier) {

    if (viewModel.history == null) {
        Text("No data")
    } else {
        // var entries = viewModel.history!!.entries!!

        val grouped = viewModel.history!!.entries!!.groupBy {
            val date = Instant
                .parse(it.date)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM"))
            date
        }

        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {

//            items(entries.size) { index ->
//                Row(modifier = Modifier.fillMaxWidth().padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween) {
//                    Box {
//                        Text(entries[index].operation!!)
//                    }
//                    Box {
//                        Text(Instant
//                            .parse(entries[index].date)
//                            .atZone(ZoneId.systemDefault())
//                            .format(DateTimeFormatter.ofPattern("dd.MM")),
//                            style = TextStyle(
//                                fontWeight = FontWeight.Bold
//
//                            )
//                        )
//
//                    }
//                }
//
//            }
            val keyList = grouped.keys.toList()
            items(keyList.size) { index ->
                val entriesOfSameDate = grouped[keyList[index]]

                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        keyList[index],
                        style = TextStyle(
                            fontWeight = FontWeight.Bold

                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp)
                    )
                    Column (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        entriesOfSameDate!!.forEach { entry ->
                            Text(entry.operation!!,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 8.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}