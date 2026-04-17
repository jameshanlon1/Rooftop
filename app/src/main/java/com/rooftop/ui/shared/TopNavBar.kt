package com.rooftop.ui.shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

// §1.7 Top Navigation Bar
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopNavBar(
    selectedTab: TopNavTab,
    onTabSelected: (TopNavTab) -> Unit,
    isHome: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isHome) Color.Transparent
                  else MaterialTheme.colorScheme.background.copy(alpha = 0.97f)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(bgColor)
            .padding(horizontal = 48.dp)
    ) {
        // Rooftop wordmark — far left
        Text(
            text = "Rooftop",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        // Nav tabs — centred in remaining space
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            TopNavTab.values().forEach { tab ->
                NavBarItem(
                    tab = tab,
                    isSelected = tab == selectedTab,
                    onSelected = { onTabSelected(tab) }
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun NavBarItem(
    tab: TopNavTab,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Surface(
        onClick = onSelected,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 16.dp, vertical = 7.dp)
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                modifier = Modifier.size(16.dp),
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                       else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = tab.label,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
