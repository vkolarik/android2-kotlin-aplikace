package cz.mendelu.project.extensions

import android.content.Context
import android.content.pm.PackageInfo

fun Context.getAppVersionName(): String {
    val pInfo: PackageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
    return pInfo.versionName
}