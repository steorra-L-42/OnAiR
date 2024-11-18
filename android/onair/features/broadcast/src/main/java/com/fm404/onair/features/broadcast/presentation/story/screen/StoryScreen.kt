package com.fm404.onair.features.broadcast.presentation.story.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.fm404.onair.core.designsystem.theme.pMedium
import com.fm404.onair.domain.model.story.Music
import com.fm404.onair.features.broadcast.R
import com.fm404.onair.features.broadcast.presentation.story.StoryViewModel
import com.fm404.onair.features.broadcast.presentation.story.state.StoryEvent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.fm404.onair.core.designsystem.theme.OnairBackground
import com.fm404.onair.core.designsystem.theme.OnairHighlight
import com.fm404.onair.core.designsystem.theme.pRegular
import com.fm404.onair.core.designsystem.theme.pSemiBold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    broadcastId: String,
    onBackClick: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showMusicDialog by remember { mutableStateOf(false) }
    var selectedMusic by remember { mutableStateOf(state.selectedMusic) }
    val musicList by remember { mutableStateOf(loadMusicListFromJson(context)) }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect {
            onBackClick()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(paddingValues)
    ) {

        if (showMusicDialog) {
            MusicSelectionDialog(
                musicList = musicList,
                onDismiss = { showMusicDialog = false },
                onSelect = {
                    selectedMusic = it
                    viewModel.onEvent(StoryEvent.OnMusicSelect(it))
                    showMusicDialog = false
                }
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

//                OutlinedTextField(
//                    value = state.title,
//                    onValueChange = { viewModel.onEvent(StoryEvent.OnTitleChange(it)) },
//                    label = { Text("제목") },
//                    modifier = Modifier.fillMaxWidth()
//                )


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f, fill = false).padding(start = 4.dp)
            ) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }

            Row(
                modifier = Modifier
                    .weight(9f)
                    .background(OnairBackground),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = " \uD83D\uDC8C  사연 신청",
                    fontSize = 24.sp,
                    fontFamily = pSemiBold,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(0.dp)
                .padding(horizontal = 21.dp, vertical = 4.dp)
//                .padding(horizontal = 21.dp, vertical = 16.dp)
        ) {

//            Spacer(modifier = Modifier.height(16.dp))

//        Text(
//            modifier = Modifier.padding(horizontal = 1.dp),
//            text = "\uD83D\uDC8C  사연 신청",
//            fontSize = 26.sp,
//            fontFamily = pSemiBold,
//            color = Color.White
//        )
//
//        Spacer(modifier = Modifier.height(38.dp))

            Text(
                modifier = Modifier.padding(horizontal = 1.dp),
                text = "사연 제목",
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(9.dp))

            CompositionLocalProvider(
                LocalTextSelectionColors provides customTextSelectionColors
            ) {
                BasicTextField(
                    value = state.title,
                    onValueChange = { viewModel.onEvent(StoryEvent.OnTitleChange(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(3.dp))
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontFamily = pRegular,
                        color = Color.White
                    ),
                    cursorBrush = SolidColor(OnairHighlight)
                )
            }
            Spacer(modifier = Modifier.height(25.dp))

//                OutlinedTextField(
//                    value = state.content,
//                    onValueChange = { viewModel.onEvent(StoryEvent.OnContentChange(it)) },
//                    label = { Text("내용") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp),
//                    maxLines = 10
//                )

            Text(
                modifier = Modifier.padding(horizontal = 1.dp),
                text = "사연 내용",
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(9.dp))

            CompositionLocalProvider(
                LocalTextSelectionColors provides customTextSelectionColors
            ) {
                BasicTextField(
                    value = state.content,
                    onValueChange = { viewModel.onEvent(StoryEvent.OnContentChange(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .border(1.dp, Color.White, RoundedCornerShape(3.dp))
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        fontFamily = pRegular,
                        color = Color.White
                    ),
                    cursorBrush = SolidColor(OnairHighlight)
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

//                Button(
//                    onClick = { showMusicDialog = true },
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    Text(if (selectedMusic != null) "음악 변경하기" else "음악 선택하기")
//                }

            Text(
                modifier = Modifier.padding(horizontal = 1.dp),
                text = "사연곡",
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.height(11.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMusicDialog = true }
                    .padding(horizontal = 4.dp)
            ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        modifier = Modifier.fillMaxWidth() // No clickable here
//                    ) {
                selectedMusic?.let { music ->
//                        Row(
//                            verticalAlignment = Alignment.CenterVertically,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { showMusicDialog = true }
//                                .padding(vertical = 8.dp)
//                                .background(Color.LightGray)
//                        ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberImagePainter(data = music.cover),
                            contentDescription = "선택한 음악 커버",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = music.title,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontFamily = pMedium,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = music.artist,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                fontFamily = pRegular,
                                fontSize = 15.sp
                            )
                        }
                    }
                } ?: run {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Image(
                            painter = rememberImagePainter(data = R.drawable.wy1), // Replace with your default image resource
                            contentDescription = "기본음악",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "신청곡 선택하기",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontFamily = pMedium,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "여기를 눌러 신청곡을 선택해주세요.",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = pRegular,
                                color = Color.LightGray,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.weight(1f))

            val commonShape = RoundedCornerShape(12.dp)

            Button(
                onClick = { viewModel.onEvent(StoryEvent.OnSubmit) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = state.title.isNotBlank() && state.content.isNotBlank() && !state.isLoading,
                shape = commonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnairHighlight,
                    contentColor = Color.DarkGray
                )
            ) {
                Text(text = "사연 보내기", color = OnairBackground, fontSize = 16.sp, fontFamily = pMedium)
            }

            Spacer(modifier = Modifier.height(18.dp))
        }


//        Spacer(modifier = Modifier.height(8.dp))

//        OutlinedButton(
//            onClick = onBackClick,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(48.dp),
//            border = BorderStroke(1.dp, OnairHighlight),
//            colors = ButtonDefaults.outlinedButtonColors(
//                containerColor = Color.Transparent,
//                contentColor = Color.White
//            ),
//            shape = commonShape
//        ) {
//            Text(text = "방송으로 돌아가기", color = Color.White, fontSize = 16.sp, fontFamily = pMedium)
//        }

    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        Scaffold(
//            topBar = {
//                TopAppBar(
//                    title = { Text("사연 작성") },
//                    navigationIcon = {
//                        IconButton(onClick = onBackClick) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "뒤로가기"
//                            )
//                        }
//                    }
//                )
//            }
//        ) { paddingValues ->
//
//        }
//
//        if (showMusicDialog) {
//            MusicSelectionDialog(
//                musicList = musicList,
//                onDismiss = { showMusicDialog = false },
//                onSelect = {
//                    selectedMusic = it
//                    viewModel.onEvent(StoryEvent.OnMusicSelect(it))
//                    showMusicDialog = false
//                }
//            )
//        }
//
//        if (state.isLoading) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//
//        state.error?.let { error ->
//            LaunchedEffect(error) {
//                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
}

@Composable
fun MusicSelectionDialog(
    musicList: List<Music>,
    onDismiss: () -> Unit,
    onSelect: (Music) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
                    Text(
                        modifier = Modifier.padding(start = 2.dp, top = 8.dp, bottom = 4.dp),
                        text = " \uD83C\uDFA4   신청곡 선택",
                        color = Color.White,
                        fontFamily = pSemiBold,
                        fontSize = 26.sp
                    )
//                    Spacer(modifier = Modifier.height(6.dp))
                },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight() // Ensures it wraps height based on content
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    items(musicList.size) { index ->
                        val music = musicList[index]
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable { onSelect(music) }
                        ) {
//                        Checkbox(
//                            checked = false,
//                            onCheckedChange = { onSelect(music) }
//                        )
//                        Spacer(modifier = Modifier.width(6.dp))
                            Image(
                                painter = rememberImagePainter(data = music.cover),
                                contentDescription = "Music Cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = music.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontFamily = pMedium,
                                    fontSize = 15.sp

                                )
                                Text(
                                    text = music.artist,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontFamily = pMedium,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "취소",
                    color = Color.White,
                    fontFamily = pMedium,
                    fontSize = 15.sp
                )
            }
        },
        modifier = Modifier.wrapContentHeight()
    )
}

val customTextSelectionColors = TextSelectionColors(
    handleColor = OnairHighlight,
    backgroundColor = OnairHighlight.copy(alpha = 0.4f)
)

fun loadMusicListFromJson(context: Context): List<Music> {
    return try {
        val inputStream = context.assets.open("music_list.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Music>>() {}.type
        Gson().fromJson(reader, type)
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}