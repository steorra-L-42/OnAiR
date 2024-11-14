package com.fm404.onair.features.broadcast.presentation.detail.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.fm404.onair.features.broadcast.R
import com.fm404.onair.features.broadcast.presentation.detail.BroadcastDetailViewModel
import com.fm404.onair.features.broadcast.presentation.detail.state.BroadcastDetailEvent

private const val TAG = "BroadcastDetailScreen"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastDetailScreen(
    viewModel: BroadcastDetailViewModel = hiltViewModel(),
    broadcastId: String,
    onStoryClick: (String) -> Unit,
    onBack: () -> Unit = {}
) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Channel 404", fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick =  onBack ) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onStoryClick(broadcastId) }) {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "Message")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
//                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

//            Button(
//                onClick = { viewModel.onEvent(BroadcastDetailEvent.ToggleStreaming) },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = if (state.isPlaying) "Stop Streaming" else "Start Streaming"
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp))

            // Channel Cover Image
            Image(
                painter = rememberImagePainter(
                    data = state.coverImageUrl ?: R.drawable.wy1
                ),
                contentDescription = "Channel Cover",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Channel Title and Status
            Text(
                text = "Channel 404",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Live Radio",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live Indicator and SeekBar
            Text(
                text = "• Live",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 32.dp)
            )
            Slider(
                value = 0.5f,
                onValueChange = { /* TODO: Update progress */ },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Now Playing Information
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.contentType,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                if (state.contentType == "음악") {
                    Text(
                        text = state.title ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }else{
                    Log.d(TAG, "BroadcastDetailScreen: 타입: ${state.contentType}")
                }
//                Text(
//                    text = "진로에 대해서 고민이 있어요...",
//                    fontSize = 14.sp,
//                    color = Color.Gray,
//                    textAlign = TextAlign.Center
//                )
            }

//            Spacer(modifier = Modifier.height(32.dp))

//            AudioVisualizerScreen()

            // Playback Controls
//            Row(
//                horizontalArrangement = Arrangement.SpaceAround,
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 64.dp)
//            ) {
//            }
            IconButton(onClick = { viewModel.onEvent(BroadcastDetailEvent.ToggleStreaming) }) {
                Icon(
                    painter = painterResource(id = if (state.isPlaying) R.drawable.stop else R.drawable.play),
                    contentDescription = "재생 및 중지 버튼"
                )
            }
        }
    }

}