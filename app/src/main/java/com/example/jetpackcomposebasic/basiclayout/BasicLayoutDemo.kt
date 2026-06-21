package com.example.jetpackcomposebasic.basiclayout

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ArtistCardColumn() {
    Column {
        Text("Alfred Pasta")
        Text("3 Minutes Ago")
    }
}

@Preview(showBackground = true)
@Composable
fun ArtistCardColumnPreview() {
    ArtistCardColumn()
}