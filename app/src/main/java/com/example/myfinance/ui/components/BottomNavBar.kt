package com.example.myfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfinance.ui.theme.*

enum class BottomNavDestination(
    val label: String,
    val icon: ImageVector
) {
    HOME("Home", Icons.Default.Home),
    TRANSACTIONS("Transaksi", Icons.Default.Receipt),
    ACCOUNT("Akun", Icons.Default.AccountBalanceWallet),
    REPORT("Laporan", Icons.Default.BarChart),
    SETTINGS("Setelan", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    currentDestination: BottomNavDestination,
    onDestinationChanged: (BottomNavDestination) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(BorderSubtle)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavDestination.entries.take(2).forEach { destination ->
                NavItem(
                    destination = destination,
                    isSelected = currentDestination == destination,
                    onClick = { onDestinationChanged(destination) }
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AccentPurple)
                    .clickable { onAddClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Transaksi",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            BottomNavDestination.entries.takeLast(2).forEach { destination ->
                NavItem(
                    destination = destination,
                    isSelected = currentDestination == destination,
                    onClick = { onDestinationChanged(destination) }
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    destination: BottomNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = destination.icon,
            contentDescription = destination.label,
            tint = if (isSelected) AccentPurple else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = destination.label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = if (isSelected) AccentPurple else TextMuted
        )
    }
}