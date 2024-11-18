package com.fm404.onair.features.broadcast.presentation.detail.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.fm404.onair.core.designsystem.component.audiovisualizer.AudioVisualizerScreen
import com.fm404.onair.core.designsystem.theme.OnairBackground
import com.fm404.onair.core.designsystem.theme.OnairHighlight
import com.fm404.onair.core.designsystem.theme.pExtraBold
import com.fm404.onair.core.designsystem.theme.pMedium
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
    val amplitudes by viewModel.amplitudes.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.playerError) {
        if (state.playerError) {
            Log.d(TAG, "Player error detected: ${state.error}")  // 디버깅을 위한 로그 추가
            Toast.makeText(
                context,
                state.error ?: "방송이 종료되었습니다",
                Toast.LENGTH_SHORT
            ).show()

            // 약간의 딜레이를 주어 Toast가 표시된 후 화면 전환
            kotlinx.coroutines.delay(300)
            onBack()
        }
    }

    Column(
        modifier = Modifier
            .padding(vertical = 0.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }

            Row(
                modifier = Modifier
                    .weight(6f)
                    .padding(horizontal = 10.dp)
                    .background(OnairBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = state.coverImageUrl ?: com.fm404.onair.core.common.R.drawable.sena
                    ),
                    contentDescription = "DJ 프사",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(54.dp))
                )
                Column(
                    modifier = Modifier
                        .padding(start = 14.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "세나의 K-POP 라디오",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 0.dp)
                    )
                    Text(
                        text = "#날씨 #뉴스 #연예",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 0.dp)
                    )
                }
            }

            IconButton(
                onClick = { onStoryClick(broadcastId) },
                modifier = Modifier.weight(1.3f, fill = false)
                    .padding(end = 2.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.episode),
                    contentDescription = "사연신청버튼",
                    modifier = Modifier
                        .size(32.dp)
                )
            }
        }

//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(OnairBackground)
//                .padding(vertical = 16.dp),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(90.dp)
//                    .padding(horizontal = 0.dp)
//                    .background(OnairBackground),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//            }
//        }

        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = rememberImagePainter(
                data = state.coverImageUrl ?: R.drawable.wy1
            ),
            contentDescription = "Channel Cover",
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = if (state.contentType == "음악") {"APT."}else{"세나의 K-POP 라디오"}

            ,
            fontFamily = pMedium,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = if (state.contentType == "음악") {"로제 (ROSÉ), Bruno Mars"}else{"#날씨 #뉴스 #연예"},
            fontFamily = pMedium,
            fontSize = 16.sp,
            color = Color.LightGray
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp)
            ,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Text(
                text = contentOnGoing(state.contentType),
                fontSize = 15.sp,
                fontFamily = pMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(1f)
//                    .padding(end = 8.dp)
            )

            AudioVisualizerScreen(
                amplitudes = amplitudes,
                modifier = Modifier
                    .width(100.dp)
                    .height(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            onClick = { viewModel.onEvent(BroadcastDetailEvent.ToggleStreaming) },
            modifier = Modifier
                .size(90.dp),
            shape = RectangleShape,
            color = Color.Transparent
        ) {
            Icon(
                painter = painterResource(id = if (state.isPlaying) R.drawable.stop else R.drawable.play),
//                painter = painterResource(id = if (state.isPlaying) R.drawable.stop_flat else R.drawable.play_flat),
                contentDescription = "재생 및 중지 버튼",
                modifier = Modifier
                    .fillMaxSize(),
                tint = Color.White
//                tint = OnairHighlight
            )
        }



    }
}

private fun contentOnGoing(contentType: String): String {
    return when (contentType) {
        "사연" -> "✉️  사연을 읽는중..."
        "뉴스" -> "📰  뉴스 읽는중..."
        "음악" -> "🎵  음악 재생중..."
        "날씨" -> "🌤️  날씨 예보중..."
        else -> "\uD83D\uDCFB  방송 진행중..."
    }
}

