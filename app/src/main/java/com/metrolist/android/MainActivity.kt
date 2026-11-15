package com.metrolist.android

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class AppIconVariant(
    val aliasName: String,
    @StringRes val labelRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val previewRes: Int,
    @DrawableRes val previewBackgroundRes: Int? = null,
    val enabledByDefault: Boolean = false,
) {
    ORIGINAL(
        aliasName = "MainActivityOriginal",
        labelRes = R.string.app_icon_original,
        descriptionRes = R.string.desc_icon_original,
        previewRes = R.drawable.ic_launcher_foreground,
        previewBackgroundRes = R.drawable.app_icon_original_background,
        enabledByDefault = true,
    ),
    ARCTIC_HAZE(
        aliasName = "MainActivityArcticHaze",
        labelRes = R.string.app_icon_arctic_haze,
        descriptionRes = R.string.desc_icon_arctic_haze,
        previewRes = R.drawable.app_icon_arctic_haze_foreground,
        previewBackgroundRes = R.drawable.app_icon_arctic_haze_background,
    ),
    LAVENDER_LEAP(
        aliasName = "MainActivityLavenderLeap",
        labelRes = R.string.app_icon_lavender_leap,
        descriptionRes = R.string.desc_icon_lavender_leap,
        previewRes = R.drawable.app_icon_lavender_leap_foreground,
        previewBackgroundRes = R.drawable.app_icon_lavender_leap_background,
    ),
    METROMALLOW(
        aliasName = "MainActivityMetromallow",
        labelRes = R.string.app_icon_metromallow,
        descriptionRes = R.string.desc_icon_metromallow,
        previewRes = R.drawable.app_icon_metromallow_foreground,
        previewBackgroundRes = R.drawable.app_icon_metromallow_background,
    ),
    PRIDE_SPECIAL(
        aliasName = "MainActivityPrideSpecial",
        labelRes = R.string.app_icon_pride_special,
        descriptionRes = R.string.desc_icon_pride_special,
        previewRes = R.drawable.app_icon_pride_special_foreground,
        previewBackgroundRes = R.drawable.app_icon_pride_special_background,
    ),
    STATIC(
        aliasName = "MainActivityKayeStatic",
        labelRes = R.string.app_icon_static,
        descriptionRes = R.string.desc_icon_static,
        previewRes = R.drawable.app_icon_static_foreground,
        previewBackgroundRes = R.drawable.app_icon_static_background,
    ),
    ZENITH_BLACK(
        aliasName = "MainActivityZenithBlack",
        labelRes = R.string.app_icon_zenith_black,
        descriptionRes = R.string.desc_icon_zenith_black,
        previewRes = R.drawable.app_icon_zenith_black_full,
    ),
    ZENITH_WHITE(
        aliasName = "MainActivityZenithWhite",
        labelRes = R.string.app_icon_zenith_white,
        descriptionRes = R.string.desc_icon_zenith_white,
        previewRes = R.drawable.app_icon_zenith_white_full,
    ),
    GRADIENT(
        aliasName = "MainActivityGradient",
        labelRes = R.string.app_icon_gradient,
        descriptionRes = R.string.desc_icon_gradient,
        previewRes = R.drawable.app_icon_gradient_full,
    ),
}

private fun applyLauncherIcon(context: Context, selected: AppIconVariant) {
    val pm = context.packageManager
    val manifestPkg = "com.metrolist.android"
    val targetComponent = ComponentName(context, "$manifestPkg.${selected.aliasName}")

    pm.setComponentEnabledSetting(
        targetComponent,
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
        PackageManager.DONT_KILL_APP,
    )

    AppIconVariant.entries.filterNot { it == selected }.forEach { other ->
        val component = ComponentName(context, "$manifestPkg.${other.aliasName}")
        pm.setComponentEnabledSetting(
            component,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP,
        )
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YuruTuneTheme {
                YuruTuneRoot()
            }
        }
    }
}

@Composable
private fun YuruTuneTheme(content: @Composable () -> Unit) {
    val scheme = darkColorScheme(
        primary = Color(0xFFF3E6D3),
        onPrimary = Color(0xFF1A1612),
        primaryContainer = Color(0xFF332C25),
        onPrimaryContainer = Color(0xFFF8EFE2),
        secondary = Color(0xFFD5C4B0),
        background = Color(0xFF121110),
        surface = Color(0xFF1A1816),
        surfaceVariant = Color(0xFF25221F),
        onBackground = Color(0xFFF6EFE6),
        onSurface = Color(0xFFF6EFE6),
        onSurfaceVariant = Color(0xFFC5B9AA),
    )
    MaterialTheme(colorScheme = scheme, content = content)
}

@Composable
fun AppIconPreviewBox(
    icon: AppIconVariant,
    size: Dp = 52.dp,
    cornerRadius: Dp = 13.dp,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f), RoundedCornerShape(cornerRadius)),
    ) {
        icon.previewBackgroundRes?.let { background ->
            Image(
                painter = painterResource(background),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize(),
            )
        }
        Image(
            painter = painterResource(icon.previewRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YuruTuneRoot() {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(2) }
    var selectedIcon by remember { mutableStateOf(AppIconVariant.ORIGINAL) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0.38f) }

    val tracks = listOf(
        R.string.track_1_title to R.string.track_1_artist,
        R.string.track_2_title to R.string.track_2_artist,
        R.string.track_3_title to R.string.track_3_artist,
        R.string.track_4_title to R.string.track_4_artist,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppIconPreviewBox(icon = selectedIcon, size = 34.dp, cornerRadius = 9.dp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        bottomBar = {
            Column {
                MiniPlayerBar(
                    icon = selectedIcon,
                    titleRes = tracks[currentTrackIndex].first,
                    artistRes = tracks[currentTrackIndex].second,
                    isPlaying = isPlaying,
                    progress = progress,
                    onProgressChange = { progress = it },
                    onTogglePlay = { isPlaying = !isPlaying },
                    onPrevious = {
                        currentTrackIndex = (currentTrackIndex - 1 + tracks.size) % tracks.size
                    },
                    onNext = {
                        currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                    },
                )
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_home)) },
                    )
                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_library)) },
                    )
                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(stringResource(R.string.nav_settings)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (currentTab) {
            0 -> HomeScreenContent(
                padding = innerPadding,
                selectedIcon = selectedIcon,
                tracks = tracks,
                currentTrackIndex = currentTrackIndex,
                onSelectTrack = {
                    currentTrackIndex = it
                    isPlaying = true
                },
                onOpenIconSettings = { currentTab = 2 },
            )
            1 -> LibraryScreenContent(
                padding = innerPadding,
                selectedIcon = selectedIcon,
                tracks = tracks,
                onSelectTrack = {
                    currentTrackIndex = it
                    isPlaying = true
                },
            )
            else -> SettingsIconPickerScreen(
                padding = innerPadding,
                selectedIcon = selectedIcon,
                onSelectIcon = { variant ->
                    selectedIcon = variant
                    val label = context.getString(variant.labelRes)
                    runCatching { applyLauncherIcon(context, variant) }
                    Toast.makeText(
                        context,
                        context.getString(R.string.settings_icon_applied, label),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
        }
    }
}

@Composable
private fun SettingsIconPickerScreen(
    padding: PaddingValues,
    selectedIcon: AppIconVariant,
    onSelectIcon: (AppIconVariant) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIconPreviewBox(icon = selectedIcon, size = 68.dp, cornerRadius = 18.dp)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_appearance),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(selectedIcon.labelRes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(selectedIcon.descriptionRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        )
                    }
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(top = 4.dp)) {
                Text(
                    text = stringResource(R.string.settings_app_icon_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = stringResource(R.string.settings_app_icon_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(AppIconVariant.entries) { icon ->
            val isSelected = icon == selectedIcon
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.surfaceVariant
                    } else {
                        MaterialTheme.colorScheme.surface
                    },
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectIcon(icon) },
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIconPreviewBox(icon = icon, size = 50.dp, cornerRadius = 12.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(icon.labelRes),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(icon.descriptionRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    AnimatedVisibility(visible = isSelected) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.settings_active_badge),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.settings_platform_icons_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.settings_platform_icons_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            val platformIcons = listOf(
                R.string.platform_desktop_light to R.drawable.preview_desktop_logo,
                R.string.platform_desktop_dark to R.drawable.preview_desktop_logo_dark,
                R.string.platform_ios_primary to R.drawable.preview_ios_logo,
                R.string.platform_ios_tinted to R.drawable.preview_ios_logo_tinted,
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                platformIcons.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        pair.forEach { (titleRes, drawableRes) ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.weight(1f),
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Image(
                                        painter = painterResource(drawableRes),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.14f),
                                                RoundedCornerShape(10.dp),
                                            ),
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        text = stringResource(titleRes),
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    padding: PaddingValues,
    selectedIcon: AppIconVariant,
    tracks: List<Pair<Int, Int>>,
    currentTrackIndex: Int,
    onSelectTrack: (Int) -> Unit,
    onOpenIconSettings: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(22.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIconPreviewBox(icon = selectedIcon, size = 60.dp, cornerRadius = 16.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(selectedIcon.labelRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(8.dp))
                        FilledTonalButton(onClick = onOpenIconSettings) {
                            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(stringResource(R.string.home_open_icon_picker))
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = stringResource(R.string.home_speed_dial),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        items(tracks.size) { index ->
            val (titleRes, artistRes) = tracks[index]
            val active = index == currentTrackIndex
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTrack(index) },
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIconPreviewBox(
                        icon = AppIconVariant.entries[index % AppIconVariant.entries.size],
                        size = 46.dp,
                        cornerRadius = 11.dp,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(titleRes),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = stringResource(artistRes),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = if (active) Icons.Default.Headphones else Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryScreenContent(
    padding: PaddingValues,
    selectedIcon: AppIconVariant,
    tracks: List<Pair<Int, Int>>,
    onSelectTrack: (Int) -> Unit,
) {
    val collections = listOf(
        R.string.library_liked_songs,
        R.string.library_downloaded,
        R.string.library_cozy_playlist,
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(collections.size) { idx ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectTrack(idx % tracks.size) },
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppIconPreviewBox(icon = selectedIcon, size = 48.dp, cornerRadius = 12.dp)
                    Spacer(Modifier.width(14.dp))
                    Text(
                        text = stringResource(collections[idx]),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPlayerBar(
    icon: AppIconVariant,
    @StringRes titleRes: Int,
    @StringRes artistRes: Int,
    isPlaying: Boolean,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    onTogglePlay: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppIconPreviewBox(icon = icon, size = 42.dp, cornerRadius = 10.dp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(titleRes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(artistRes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null)
                }
                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, contentDescription = null)
                }
            }
            Slider(
                value = progress,
                onValueChange = onProgressChange,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}
