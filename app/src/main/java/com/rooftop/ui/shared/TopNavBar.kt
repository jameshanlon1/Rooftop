package com.rooftop.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text

enum class TopNavTab(val label: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    SEARCH("Search", Icons.Default.Search),
    LIVE_TV("Live TV", Icons.Default.LiveTv),
    MOVIES("Movies", Icons.Default.Movie),
    SERIES("Series", Icons.Default.VideoLibrary),
    SETTINGS("Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopNavBar(
    selectedTab: TopNavTab,
    onTabSelected: (TopNavTab) -> Unit,
    isHome: Boolean = false,
    focusRequester: FocusRequester? = null,
    onFocusChanged: (hasFocus: Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .onFocusChanged { onFocusChanged(it.hasFocus) }
                .background(
                    color = Color.Black.copy(alpha = if (isHome) 0.60f else 0.80f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            TopNavTab.values().forEach { tab ->
                NavBarItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onSelected = { onTabSelected(tab) },
                    itemFocusRequester = if (tab == selectedTab) focusRequester else null
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun NavBarItem(
    tab: TopNavTab,
    isSelected: Boolean,
    onSelected: () -> Unit,
    itemFocusRequester: FocusRequester? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Navigate on D-pad focus — only when focusing a non-active tab to prevent nav loops
    LaunchedEffect(isFocused) {
        if (isFocused && !isSelected) onSelected()
    }

    Surface(
        onClick = onSelected,
        interactionSource = interactionSource,
        modifier = Modifier
            .padding(horizontal = 1.dp)
            .then(if (itemFocusRequester != null) Modifier.focusRequester(itemFocusRequester) else Modifier),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            pressedContainerColor = Color.Transparent
        ),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1f, pressedScale = 1f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = if (isSelected) Color.Black.copy(alpha = 0.45f) else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                modifier = Modifier.size(15.dp),
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.55f)
            )
        }
    }
}
