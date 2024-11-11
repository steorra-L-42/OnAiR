package com.fm404.onair.core.designsystem.component.image

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest

/**
 * 네트워크 URL로부터 이미지를 로드하는 컴포넌트
 * @param imageUrl 이미지 URL
 */
@Composable
fun NetworkImage(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    placeholderContent: @Composable BoxScope.() -> Unit = {}
) {
    Box(modifier = modifier) {
        if (!imageUrl.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                loading = {
                    Box(modifier = Modifier.matchParentSize()) {
                        placeholderContent()
                    }
                },
                error = {
                    Box(modifier = Modifier.matchParentSize()) {
                        placeholderContent()
                    }
                },
                success = {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.matchParentSize(),
                        contentScale = contentScale
                    )
                },
                modifier = Modifier.matchParentSize()
            )
        } else {
            placeholderContent()
        }
    }
}

/**
 * 로컬 Uri로부터 이미지를 로드하는 컴포넌트
 * @param imageUri 로컬 이미지 Uri
 */
@Composable
fun LocalImage(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
    placeholderContent: @Composable BoxScope.() -> Unit = {}
) {
    Box(modifier = modifier) {
        if (imageUri != null) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = contentDescription,
                loading = {
                    Box(modifier = Modifier.matchParentSize()) {
                        placeholderContent()
                    }
                },
                error = {
                    Box(modifier = Modifier.matchParentSize()) {
                        placeholderContent()
                    }
                },
                success = {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.matchParentSize(),
                        contentScale = contentScale
                    )
                },
                modifier = Modifier.matchParentSize()
            )
        } else {
            placeholderContent()
        }
    }
}



//// 서버 이미지 URL을 사용할 때
//NetworkImage(
//imageUrl = state.userInfo?.profilePath,
//contentDescription = "Profile"
//)
//
//// 갤러리에서 선택한 이미지를 사용할 때
//LocalImage(
//imageUri = selectedUri,
//contentDescription = "Selected Image"
//)