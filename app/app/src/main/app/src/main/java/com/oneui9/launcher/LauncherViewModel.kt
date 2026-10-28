package com.oneui9.launcher

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class AppModel(
    val title: String,
    val packageName: String,
    val launchIntent: Intent?
)

class LauncherViewModel : ViewModel() {
    var appList = mutableStateListOf<AppModel>()
        private set

    fun loadApps(packageManager: PackageManager) {
        viewModelScope.launch(Dispatchers.IO) {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolves = packageManager.queryIntentActivities(intent, 0)
            val loaded = resolves.map {
                val title = it.loadLabel(packageManager).toString()
                val pkg = it.activityInfo.packageName
                val launchIntent = packageManager.getLaunchIntentForPackage(pkg)
                AppModel(title, pkg, launchIntent)
            }.sortedBy { it.title }

            viewModelScope.launch(Dispatchers.Main) {
                appList.clear()
                appList.addAll(loaded)
            }
        }
    }
}
