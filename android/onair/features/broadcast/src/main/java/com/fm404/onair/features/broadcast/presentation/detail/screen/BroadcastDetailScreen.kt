package com.fm404.onair.features.broadcast.presentation.detail.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fm404.onair.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastDetailScreen(
    broadcastId: String,
    onStoryClick: (String) -> Unit,
    onBack: () -> Unit = {}
) {

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
                    IconButton(onClick = { /* TODO: Handle email action */ }) {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = "Message")
                    }
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Channel Cover Image
            Image(
                painter = painterResource(id = R.drawable.wy1),
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
                modifier = Modifier.align(Alignment.Start).padding(start = 32.dp)
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
                    text = "사연 읽는 중",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "진로에 대해서 고민이 있어요...",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Playback Controls
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
            ) {
//                IconButton(onClick = { /* TODO: Handle previous action */ }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_previous),
//                        contentDescription = "Previous"
//                    )
//                }
                IconButton(onClick = { onStoryClick(broadcastId) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.play),
                        contentDescription = "Play/Pause"
                    )
                }
//                IconButton(onClick = { /* TODO: Handle next action */ }) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_next),
//                        contentDescription = "Next"
//                    )
//                }
            }
        }
    }

}