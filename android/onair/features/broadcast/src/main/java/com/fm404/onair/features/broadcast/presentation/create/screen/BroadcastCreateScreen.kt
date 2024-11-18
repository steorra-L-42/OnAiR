package com.fm404.onair.features.broadcast.presentation.create.screen

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fm404.onair.core.common.util.BroadcastConstants
import com.fm404.onair.core.designsystem.theme.OnairBackground
import com.fm404.onair.core.designsystem.theme.pMedium
import com.fm404.onair.core.designsystem.theme.pSemiBold
import com.fm404.onair.domain.model.broadcast.CreateChannelPlayList
import com.fm404.onair.features.broadcast.R
import com.fm404.onair.features.broadcast.presentation.create.BroadcastCreateViewModel
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateEvent
import com.fm404.onair.features.broadcast.presentation.create.state.BroadcastCreateState
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberImagePainter
import com.fm404.onair.core.designsystem.theme.OnairHighlight
import com.fm404.onair.core.designsystem.theme.pRegular
import com.fm404.onair.domain.model.story.Music
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BroadcastCreateScreen(
    onBackClick: () -> Unit,
    viewModel: BroadcastCreateViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    BroadcastCreateContent(
        state = state,
        onTtsEngineChange = { viewModel.onEvent(BroadcastCreateEvent.OnTtsEngineChange(it)) },
        onPersonalityChange = { viewModel.onEvent(BroadcastCreateEvent.OnPersonalityChange(it)) },
        onNewsTopicChange = { viewModel.onEvent(BroadcastCreateEvent.OnNewsTopicChange(it)) },
        onChannelNameChange = { viewModel.onEvent(BroadcastCreateEvent.OnChannelNameChange(it)) },
        onTrackListChange = { viewModel.onEvent(BroadcastCreateEvent.OnTrackListChange(it)) },
        onCreateClick = {
            viewModel.onEvent(BroadcastCreateEvent.OnCreateClick) // 기존 로직 호출
            Toast.makeText(context, "채널 생성을 요청했어요.", Toast.LENGTH_SHORT).show() // Toast 메시지 띄우기
//            onBackClick() // onBack 호출
        },
        onBackClick = onBackClick,
        onErrorDismiss = { viewModel.onErrorDismiss() }
    )
}

private val textStyle1 = TextStyle(
    fontSize = 14.sp,
    color = Color.White,
    fontFamily = pMedium,
    textAlign = TextAlign.Center
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BroadcastCreateContent(
    state: BroadcastCreateState,
    onTtsEngineChange: (String) -> Unit,
    onPersonalityChange: (String) -> Unit,
    onNewsTopicChange: (String) -> Unit,
    onChannelNameChange: (String) -> Unit,
    onTrackListChange: (List<CreateChannelPlayList>) -> Unit,
    onCreateClick: () -> Unit,
    onBackClick: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.White,
        unfocusedIndicatorColor = Color.White,
        cursorColor = OnairHighlight,
        disabledIndicatorColor = Color.White
    )

    val context = LocalContext.current
    val musicList = remember { loadMusicListFromJson(context) }
    var showMusicDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 6.dp, horizontal = 22.dp)
            .verticalScroll(rememberScrollState())
    ) {
//        TopAppBar(
//            title = { Text("방송 만들기") },
//            navigationIcon = {
//                IconButton(onClick = onBackClick) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
//                }
//            }
//        )

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp, top = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = 0.dp)
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
                    text = " \uD83D\uDCFB 나만의 채널 만들기",
                    fontSize = 22.sp,
                    fontFamily = pSemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

//        OutlinedTextField(
//            value = state.channelName,
//            onValueChange = onChannelNameChange,
//            label = { Text("채널 이름") },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = false
//        )

        // 채널 이름
//        OutlinedTextField(
//            value = state.channelName,
//            onValueChange = onChannelNameChange,
//            label = { Text("채널 이름") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))

        // TTS 엔진 선택
//        var ttsExpanded by remember { mutableStateOf(false) }
//        ExposedDropdownMenuBox(
//            expanded = ttsExpanded,
//            onExpandedChange = { ttsExpanded = it },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            OutlinedTextField(
//                value = BroadcastConstants.TTS_ENGINE_OPTIONS[state.ttsEngine] ?: "",
//                onValueChange = {},
//                readOnly = true,
//                label = { Text("TTS 엔진") },
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ttsExpanded) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .menuAnchor()
//            )
//            ExposedDropdownMenu(
//                expanded = ttsExpanded,
//                onDismissRequest = { ttsExpanded = false }
//            ) {
//                BroadcastConstants.TTS_ENGINE_OPTIONS.forEach { (key, value) ->
//                    DropdownMenuItem(
//                        text = { Text(value) },
//                        onClick = {
//                            onTtsEngineChange(key)
//                            ttsExpanded = false
//                        }
//                    )
//                }
//            }
//        }
//
//        // 썸네일 이미지 표시
//        if (state.ttsEngine.isNotEmpty()) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Card(
//                modifier = Modifier
//                    .size(250.dp)
//                    .align(Alignment.CenterHorizontally)
//            ) {
//                Box(modifier = Modifier.fillMaxSize()) {
//                    Image(
//                        painter = painterResource(
//                            id = when (state.ttsEngine) {
//                                "TYPECAST_SENA" -> com.fm404.onair.core.common.R.drawable.sena
//                                "TYPECAST_JEROME" -> com.fm404.onair.core.common.R.drawable.jerome
//                                "TYPECAST_HYEONJI" -> com.fm404.onair.core.common.R.drawable.hyunji
//                                "TYPECAST_EUNBIN" -> com.fm404.onair.core.common.R.drawable.eunbin
//                                else -> com.fm404.onair.core.common.R.drawable.sena // 기본 이미지
//                            }
//                        ),
//                        contentDescription = "DJ 썸네일",
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Fit
//                    )
//                }
//            }
//        }


        // TTS 엔진 선택
        var ttsExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = ttsExpanded,
            onExpandedChange = { ttsExpanded = it },
            modifier = Modifier.heightIn(max = 46.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "   TTS 엔진",
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = pSemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(5f)
                        .padding(start = 30.dp)
                ) {
                    if (state.ttsEngine.isNotEmpty()) {
                        Image(
                            painter = painterResource(
                                id = when (state.ttsEngine) {
                                    "TYPECAST_SENA" -> com.fm404.onair.core.common.R.drawable.sena
                                    "TYPECAST_JEROME" -> com.fm404.onair.core.common.R.drawable.jerome
                                    "TYPECAST_HYEONJI" -> com.fm404.onair.core.common.R.drawable.hyunji
                                    "TYPECAST_EUNBIN" -> com.fm404.onair.core.common.R.drawable.eunbin
                                    else -> com.fm404.onair.core.common.R.drawable.sena // 기본 이미지
                                }
                            ) ,
                            contentDescription = "DJ 썸네일",
                            modifier = Modifier
                                .size(50.dp)
//                                .padding(end = 8.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                        )
                    }

                    Spacer(modifier = Modifier.width(21.dp))

                    OutlinedTextField(
                        value = BroadcastConstants.TTS_ENGINE_OPTIONS[state.ttsEngine]?.let { "     $it" } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        textStyle = textStyle1,
                        colors = textFieldColors,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = ttsExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                            .menuAnchor()
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = ttsExpanded,
                onDismissRequest = { ttsExpanded = false },
            ) {
                BroadcastConstants.TTS_ENGINE_OPTIONS.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                ,

                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontFamily = pMedium,
                                    textAlign = TextAlign.Center,
                                    text = value
                                )
                            }
                        },
                        onClick = {
                            onTtsEngineChange(key)
                            ttsExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 성격 선택
        var personalityExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = personalityExpanded,
            onExpandedChange = { personalityExpanded = it },
            modifier = Modifier.heightIn(max = 46.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "성격",
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = pSemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2f)
                )

                OutlinedTextField(
                    value = BroadcastConstants.PERSONALITY_OPTIONS[state.personality]?.let { "      $it" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    textStyle = textStyle1,
                    colors = textFieldColors,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = personalityExpanded) },
                    modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 20.dp)
                        .menuAnchor()
                )
            }

            ExposedDropdownMenu(
                expanded = personalityExpanded,
                onDismissRequest = { personalityExpanded = false },
            ) {
                BroadcastConstants.PERSONALITY_OPTIONS.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontFamily = pMedium,
                                    textAlign = TextAlign.Center,
                                    text = value
                                )
                            }
                        },
                        onClick = {
                            onPersonalityChange(key)
                            personalityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 뉴스 주제 선택
        var newsTopicExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = newsTopicExpanded,
            onExpandedChange = { newsTopicExpanded = it },
            modifier = Modifier.heightIn(max = 46.dp)
//            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "뉴스 주제",
                    fontSize = 15.sp,
                    color = Color.White,
                    fontFamily = pSemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(2f)
                )

                if (state.newsTopic.isBlank() && BroadcastConstants.NEWS_TOPIC_OPTIONS.isNotEmpty()) {
                    onNewsTopicChange(BroadcastConstants.NEWS_TOPIC_OPTIONS.keys.first())
                }

                OutlinedTextField(
                    value = BroadcastConstants.NEWS_TOPIC_OPTIONS[state.newsTopic]?.let { "      $it" } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    textStyle = textStyle1,
                    colors = textFieldColors,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = newsTopicExpanded) },
                    modifier = Modifier
                        .weight(4f)
                        .padding(horizontal = 20.dp)
                        .menuAnchor()
                )
            }

            ExposedDropdownMenu(
                expanded = newsTopicExpanded,
                onDismissRequest = { newsTopicExpanded = false }
            ) {
                BroadcastConstants.NEWS_TOPIC_OPTIONS.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    fontSize = 16.sp,
                                    color = Color.White,
                                    fontFamily = pMedium,
                                    textAlign = TextAlign.Center,
                                    text = value
                                )
                            }
                        },
                        onClick = {
                            onNewsTopicChange(key)
                            newsTopicExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        Text(
//            modifier = Modifier.fillMaxWidth(),
            modifier = Modifier.padding(start = 20.dp),
//            textAlign = TextAlign.Center,
            text = "플레이리스트",
            fontSize = 15.sp,
            color = Color.White,
            fontFamily = pSemiBold,
        )

        if (showMusicDialog) {
            MultiMusicSelectionDialog(
                musicList = musicList,
                initiallySelectedMusic = state.trackList.map { Music(it.title, it.artist, it.cover) },
                onDismiss = { showMusicDialog = false },
                onConfirm = { selectedMusicList ->
                    onTrackListChange(selectedMusicList.map { CreateChannelPlayList(it.title, it.artist, it.cover) })
                    showMusicDialog = false
                }
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 0.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.03f))
        ) {
            if (state.trackList.isEmpty()) {
                Text(
                    text = "'음악 선택하기' 버튼을 눌러서\n나만의 플레이리스트를 완성해보세요!",
                    fontSize = 14.sp,
                    fontFamily = pMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(16.dp)
                ){
                    items(state.trackList) { track ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = rememberImagePainter(data = track.cover),
                                contentDescription = "선택한 음악 커버",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(18.dp))
                            Column {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White,
                                    fontFamily = pMedium,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    fontFamily = pRegular,
                                    fontSize = 15.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
        Button(
            onClick = { showMusicDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "음악 선택하기",
                color = OnairHighlight,
                fontFamily = pRegular,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

//        state.trackList.forEach { track ->
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Image(
//                    painter = rememberImagePainter(data = track.cover),
//                    contentDescription = "선택한 음악 커버",
//                    contentScale = ContentScale.Crop,
//                    modifier = Modifier
//                        .size(52.dp)
//                        .clip(CircleShape)
//                )
//                Spacer(modifier = Modifier.width(12.dp))
//                Column {
//                    Text(
//                        text = track.title,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.White,
//                        fontFamily = pMedium,
//                        fontSize = 15.sp
//                    )
//                    Spacer(modifier = Modifier.height(3.dp))
//                    Text(
//                        text = track.artist,
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.Gray,
//                        fontFamily = pRegular,
//                        fontSize = 15.sp
//                    )
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp))
//        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
//                .padding(vertical = 16.dp)
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = state.channelName.isNotBlank() &&
                    state.ttsEngine.isNotBlank() &&
                    state.personality.isNotBlank() &&
                    state.newsTopic.isNotBlank() &&
                    !state.isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = OnairHighlight,
                contentColor = OnairBackground
            )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.surface
                )
            } else {
                Text(
                    text = "${state.channelName} 채널 만들기",
                    fontFamily = pSemiBold,
                    fontSize = 18.sp

                )
            }
        }

        Spacer(modifier = Modifier.height(9.dp))
    }

    state.error?.let { error ->
        AlertDialog(
            onDismissRequest = onErrorDismiss,
            title = { Text("오류") },
            text = { Text(error) },
            confirmButton = {
                TextButton(onClick = onErrorDismiss) {
                    Text("확인")
                }
            }
        )
    }
}

@Composable
fun MultiMusicSelectionDialog(
    musicList: List<Music>,
    initiallySelectedMusic: List<Music>,
    onDismiss: () -> Unit,
    onConfirm: (List<Music>) -> Unit
) {
    var selectedMusic by remember { mutableStateOf(initiallySelectedMusic.toMutableList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(selectedMusic)
                onDismiss()
            }) {
                Text("확인", color = Color.White)
            }
        },
        title = {
            Text(
                modifier = Modifier.padding(start = 2.dp, top = 8.dp, bottom = 4.dp),
                text = " \uD83C\uDFA4   플레이리스트 선택",
                color = Color.White,
                fontFamily = pSemiBold,
                fontSize = 26.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Select All and Deselect All Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        selectedMusic = musicList.toMutableList()
                    }) {
                        Text("전체 선택", color = Color.White)
                    }
                    TextButton(onClick = {
//                        selectedMusic.clear()
                        selectedMusic = mutableListOf()
                    }) {
                        Text("전체 선택해제", color = Color.White)
                    }
                }

                // Music List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    items(musicList.size) { index ->
                        val music = musicList[index]
                        val isSelected = selectedMusic.contains(music)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .clickable {
                                    if (isSelected) {
                                        selectedMusic = selectedMusic
                                            .filter { it != music }
                                            .toMutableList()
                                    } else {
                                        selectedMusic = (selectedMusic + music).toMutableList()
                                    }
                                }
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = {
                                    if (it) {
                                        selectedMusic = (selectedMusic + music).toMutableList()
                                    } else {
                                        selectedMusic = selectedMusic.filter { it != music }.toMutableList()
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
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
                                    fontSize = 15.sp,
                                    color = Color.Gray
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
        modifier = Modifier.fillMaxSize(),
        properties = DialogProperties(usePlatformDefaultWidth = false)
    )
}


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
