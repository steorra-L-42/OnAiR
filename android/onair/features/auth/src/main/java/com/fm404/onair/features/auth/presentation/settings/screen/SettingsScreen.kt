package com.fm404.onair.features.auth.presentation.settings.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.fm404.onair.core.designsystem.component.image.NetworkImage
import com.fm404.onair.features.auth.presentation.settings.SettingsViewModel
import com.fm404.onair.features.auth.presentation.settings.state.SettingsEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onEvent(SettingsEvent.OnImageSelected(it)) }
    }

    // Profile image view dialog
    if (state.showImageDialog && state.userInfo?.profilePath != null) {
        Dialog(
            onDismissRequest = { viewModel.onEvent(SettingsEvent.OnHideImageDialog) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { viewModel.onEvent(SettingsEvent.OnHideImageDialog) }
            ) {
                NetworkImage(
                    imageUrl = state.userInfo?.profilePath,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .align(Alignment.Center),
                    placeholderContent = {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                )
            }
        }
    }

    // Nickname update dialog
    if (state.showNicknameDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(SettingsEvent.OnHideNicknameDialog) },
            title = { Text("닉네임 변경") },
            text = {
                OutlinedTextField(
                    value = state.newNickname,
                    onValueChange = { viewModel.onEvent(SettingsEvent.OnNicknameChange(it)) },
                    label = { Text("닉네임") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.onEvent(SettingsEvent.OnUpdateNickname) }
                ) {
                    Text("변경")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(SettingsEvent.OnHideNicknameDialog) }
                ) {
                    Text("취소")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("마이페이지") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(SettingsEvent.OnBackClick) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 프로필 섹션
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 프로필 이미지
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .align(Alignment.CenterHorizontally)
                            // hover 효과
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        when (event.type) {
                                            PointerEventType.Enter -> {
                                                if (state.userInfo?.profilePath != null) {
                                                    viewModel.onEvent(SettingsEvent.OnShowImageDialog)
                                                }
                                            }
                                            PointerEventType.Exit -> {
                                                viewModel.onEvent(SettingsEvent.OnHideImageDialog)
                                            }
                                        }
                                    }
                                }
                            }
                            .clickable { launcher.launch("image/*") }
                    ) {
                        NetworkImage(
                            imageUrl = state.userInfo?.profilePath,
                            contentDescription = "Profile",
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop,
                            placeholderContent = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )

                        // 카메라 아이콘 오버레이
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Camera,
                                contentDescription = "Change profile image",
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 사용자 정보
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.userInfo?.nickname ?: "사용자",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        IconButton(
                            onClick = { viewModel.onEvent(SettingsEvent.OnShowNicknameDialog) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit nickname",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = state.userInfo?.username ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = state.userInfo?.phoneNumber ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 메뉴 아이템들
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsMenuItem(
                        icon = Icons.Default.Logout,
                        title = "로그아웃",
                        onClick = { viewModel.onEvent(SettingsEvent.OnLogoutClick) }
                    )
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SettingsMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}