package com.fm404.onair.features.broadcast.presentation.list.screen.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.fm404.onair.core.common.util.BroadcastConstants
import com.fm404.onair.core.designsystem.component.image.NetworkImage
import com.fm404.onair.domain.model.broadcast.ChannelList
import com.fm404.onair.features.broadcast.R

@Composable
fun ChannelItem(
    channelList: ChannelList,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 19.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = when (channelList.ttsEngine) {
                        "TYPECAST_SENA" -> com.fm404.onair.core.common.R.drawable.sena
                        "TYPECAST_JEROME" -> com.fm404.onair.core.common.R.drawable.jerome
                        "TYPECAST_HYEONJI" -> com.fm404.onair.core.common.R.drawable.hyunji
                        "TYPECAST_EUNBIN" -> com.fm404.onair.core.common.R.drawable.eunbin
                        else -> com.fm404.onair.core.common.R.drawable.sena // 기본 이미지
                    }
                ) ,
                contentDescription = "DJ 썸네일",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channelList.channelName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = channelList.newsTopic,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
            }

            if (!channelList.isEnded) {
//                Spacer(modifier = Modifier.width(8.dp))
                Image(
                    painter = painterResource(id = com.fm404.onair.core.common.R.drawable.ic_onair),
                    contentDescription = "온에어 아이콘",
                    modifier = Modifier
                        .size(60.dp)
                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Badge(
//                    containerColor = MaterialTheme.colorScheme.primary
//                ) {
//                    Text(
//                        text = "LIVE",
//                        modifier = Modifier.padding(horizontal = 4.dp)
//                    )
//                }
            }
        }
    }
}