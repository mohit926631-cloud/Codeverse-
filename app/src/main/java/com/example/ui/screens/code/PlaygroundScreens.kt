package com.example.ui.screens.code

import android.annotation.SuppressLint
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.domain.model.CodeFileTab
import com.example.domain.model.CodeProjectModel
import com.example.ui.components.CodeverseCard
import com.example.ui.components.CodeverseGradientButton
import com.example.ui.components.CodeverseTopBar
import com.example.ui.components.highlightCodeSyntax
import com.example.ui.theme.CardNavy
import com.example.ui.theme.CodeTextStyle
import com.example.ui.theme.CoralError
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberPurple
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.DeepSpaceGradient
import com.example.ui.theme.NeonLavender
import com.example.ui.theme.OutlineNavy
import com.example.ui.theme.SoftGreen
import com.example.ui.theme.SpaceVoid
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmYellow
import com.example.ui.viewmodel.CodeverseUiState

private val SyntaxVisualTransformation = VisualTransformation { text ->
    TransformedText(
        text = highlightCodeSyntax(text.text),
        offsetMapping = OffsetMapping.Identity
    )
}

// 13. CODING PLAYGROUND SCREEN
@Composable
fun CodingPlaygroundScreen(
    uiState: CodeverseUiState,
    onSelectTab: (CodeFileTab) -> Unit,
    onUpdateCode: (String) -> Unit,
    onRunPreview: () -> Unit,
    onResetCode: () -> Unit,
    onSaveProject: (String) -> Unit,
    onOpenGallery: () -> Unit
) {
    var showSaveDialog by remember { mutableStateOf(false) }
    var draftTitle by remember(uiState.activeProjectTitle) { mutableStateOf(uiState.activeProjectTitle) }

    val activeCode = when (uiState.activeFileTab) {
        CodeFileTab.HTML -> uiState.htmlCode
        CodeFileTab.CSS -> uiState.cssCode
        CodeFileTab.JS -> uiState.jsCode
    }

    val quickSnippets = remember(uiState.activeFileTab) {
        when (uiState.activeFileTab) {
            CodeFileTab.HTML -> listOf("<div></div>", "<button>Click</button>", "<h2>Title</h2>", "class=\"\"")
            CodeFileTab.CSS -> listOf("display: flex;", "gap: 12px;", "border-radius: 16px;", "color: #22C5E9;")
            CodeFileTab.JS -> listOf("const ", "() => {}", "console.log();", "document.querySelector(\"\")")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
        ) {
            // Header with project title + Gallery / Save / Reset actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CODE PLAYGROUND",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyberCyan
                    )
                    Text(
                        text = uiState.activeProjectTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onOpenGallery,
                        modifier = Modifier.testTag("playground_gallery_button")
                    ) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = "Open Project Gallery", tint = NeonLavender)
                    }
                    IconButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.testTag("playground_save_button")
                    ) {
                        Icon(Icons.Filled.Save, contentDescription = "Save Project", tint = SoftGreen)
                    }
                    IconButton(
                        onClick = onResetCode,
                        modifier = Modifier.testTag("playground_reset_button")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset Template", tint = WarmYellow)
                    }
                }
            }

            // Multi-file tabs (HTML / CSS / JS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CodeFileTab.entries.forEach { tab ->
                    val selected = uiState.activeFileTab == tab
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (selected) CyberPurple.copy(alpha = 0.28f) else CardNavy)
                            .border(
                                1.dp,
                                if (selected) CyberCyan else OutlineNavy,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelectTab(tab) }
                            .testTag("code_tab_${tab.extension.lowercase()}")
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tab.fileName,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (selected) TextPrimary else TextSecondary
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Mobile-friendly code snippet insert bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickSnippets.forEach { snippet ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(CardNavy)
                            .border(1.dp, OutlineNavy, RoundedCornerShape(10.dp))
                            .clickable { onUpdateCode(activeCode + "\n" + snippet) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = snippet,
                            style = CodeTextStyle,
                            color = CyberCyan
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // Syntax-highlighted editor pane
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(SpaceVoid)
                    .border(1.dp, OutlineNavy, RoundedCornerShape(18.dp))
            ) {
                val lines = remember(activeCode) { activeCode.lines().size.coerceAtLeast(1) }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    // Line numbers gutter
                    Column(
                        modifier = Modifier.padding(end = 10.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        for (i in 1..lines) {
                            Text(
                                text = i.toString(),
                                style = CodeTextStyle,
                                color = TextMuted
                            )
                        }
                    }

                    BasicTextField(
                        value = activeCode,
                        onValueChange = onUpdateCode,
                        textStyle = CodeTextStyle.copy(color = TextPrimary),
                        cursorBrush = SolidColor(CyberCyan),
                        visualTransformation = SyntaxVisualTransformation,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("playground_code_editor")
                    )
                }
            }

            // Console mini-strip if logs exist
            if (uiState.consoleLogs.isNotEmpty()) {
                val latest = uiState.consoleLogs.last()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CardNavy)
                        .border(1.dp, OutlineNavy, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Terminal,
                        contentDescription = null,
                        tint = if (latest.level == "error") CoralError else SoftGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = latest.message,
                        style = CodeTextStyle,
                        color = if (latest.level == "error") CoralError else TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Run Live Preview CTA
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                CodeverseGradientButton(
                    text = "${uiState.strings.runCode} & Open ${uiState.strings.livePreview}",
                    icon = Icons.Filled.PlayArrow,
                    onClick = onRunPreview,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "playground_run_button"
                )
            }
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = CardNavy,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Save Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Save your HTML, CSS, and JavaScript files to your local Room database & Cloud Gallery.")
                    OutlinedTextField(
                        value = draftTitle,
                        onValueChange = { draftTitle = it },
                        label = { Text("Project Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onSaveProject(draftTitle)
                        showSaveDialog = false
                    },
                    modifier = Modifier.testTag("confirm_save_project_button")
                ) {
                    Text("Save", color = CyberCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

// 14. LIVE CODE PREVIEW SCREEN (Sandboxed WebView with Console Capture)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LiveCodePreviewScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onReload: () -> Unit,
    onConsoleMessage: (level: String, message: String) -> Unit
) {
    val bundledHtml = remember(uiState.htmlCode, uiState.cssCode, uiState.jsCode, uiState.previewNonce) {
        buildSandboxedHtmlDocument(uiState.htmlCode, uiState.cssCode, uiState.jsCode)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = "Live Code Preview",
                subtitle = "Sandboxed Runtime • ${uiState.activeProjectTitle}",
                onBack = onBack,
                trailing = {
                    IconButton(
                        onClick = onReload,
                        modifier = Modifier.testTag("preview_reload_button")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reload Preview", tint = CyberCyan)
                    }
                }
            )

            // Sandboxed WebView (NO file access, NO unrestricted native JS interfaces)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.5.dp, CyberCyan.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.allowFileAccess = false
                            settings.allowContentAccess = false
                            settings.domStorageEnabled = false
                            settings.databaseEnabled = false
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            settings.setSupportZoom(true)
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    // Block external navigation to keep sandbox strictly contained
                                    return true
                                }
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                    if (consoleMessage != null) {
                                        val lvl = when (consoleMessage.messageLevel()) {
                                            ConsoleMessage.MessageLevel.ERROR -> "error"
                                            ConsoleMessage.MessageLevel.WARNING -> "warn"
                                            else -> "log"
                                        }
                                        onConsoleMessage(lvl, consoleMessage.message())
                                    }
                                    return true
                                }
                            }
                            loadDataWithBaseURL(
                                "https://codeverse.sandbox.local/",
                                bundledHtml,
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                    },
                    update = { webView ->
                        webView.loadDataWithBaseURL(
                            "https://codeverse.sandbox.local/",
                            bundledHtml,
                            "text/html",
                            "UTF-8",
                            null
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(10.dp))

            // Console Drawer
            CodeverseCard(
                backgroundColor = SpaceVoid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(136.dp)
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Filled.Terminal, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(16.dp))
                    Text("Console Output", style = MaterialTheme.typography.labelMedium, color = CyberCyan)
                }
                Spacer(Modifier.height(6.dp))
                if (uiState.consoleLogs.isEmpty()) {
                    Text(
                        text = "No console output yet. Tap interactive elements or call console.log().",
                        style = CodeTextStyle,
                        color = TextMuted
                    )
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(uiState.consoleLogs, key = { it.id }) { entry ->
                            val color = when (entry.level) {
                                "error" -> CoralError
                                "warn" -> WarmYellow
                                else -> SoftGreen
                            }
                            Text(
                                text = "[${entry.level.uppercase()}] ${entry.message}",
                                style = CodeTextStyle,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun buildSandboxedHtmlDocument(html: String, css: String, js: String): String {
    val safeCss = css.replace("</style>", "<\\/style>")
    val safeJs = js.replace("</script>", "<\\/script>")
    return """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <meta http-equiv="Content-Security-Policy" content="default-src 'none'; style-src 'unsafe-inline'; script-src 'unsafe-inline'; img-src data:;" />
  <style>$safeCss</style>
</head>
<body>
  $html
  <script>
    window.onerror = function(msg, src, line) {
      console.error("Line " + line + ": " + msg);
      return true;
    };
    try {
      $safeJs
    } catch (e) {
      console.error(e.name + ": " + e.message);
    }
  </script>
</body>
</html>
    """.trimIndent()
}

// 24. PROJECT GALLERY SCREEN
@Composable
fun ProjectGalleryScreen(
    uiState: CodeverseUiState,
    onBack: () -> Unit,
    onCreateNewProject: () -> Unit,
    onOpenProject: (CodeProjectModel) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpaceGradient)
            .statusBarsPadding()
            .navigationBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxSize()
        ) {
            CodeverseTopBar(
                title = uiState.strings.projectGalleryTitle,
                subtitle = "Room + Cloud Synced Projects",
                onBack = onBack,
                trailing = {
                    IconButton(
                        onClick = onCreateNewProject,
                        modifier = Modifier.testTag("gallery_new_project_button")
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "New Project", tint = CyberCyan)
                    }
                }
            )

            if (uiState.savedProjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Code, contentDescription = null, tint = NeonLavender, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No Saved Projects Yet", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))
                        Text("Create your first HTML/CSS/JS web app in the Playground.", color = TextSecondary)
                        Spacer(Modifier.height(18.dp))
                        CodeverseGradientButton(
                            text = "Create First Project",
                            icon = Icons.Filled.Add,
                            onClick = onCreateNewProject
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.savedProjects, key = { it.id }) { project ->
                        CodeverseCard(
                            borderColor = CyberPurple.copy(alpha = 0.5f),
                            onClick = { onOpenProject(project) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("gallery_project_${project.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = project.templateTag,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyberCyan
                                    )
                                    Text(
                                        text = project.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = project.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                                IconButton(onClick = { onDeleteProject(project.id) }) {
                                    Icon(
                                        Icons.Filled.DeleteOutline,
                                        contentDescription = "Delete project",
                                        tint = CoralError
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
