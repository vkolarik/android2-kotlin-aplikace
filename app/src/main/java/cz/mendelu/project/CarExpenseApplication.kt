package cz.mendelu.project

import android.app.Application
import dagger.hilt.android.HiltAndroidApp


// musi do manifestu
@HiltAndroidApp
class CarExpenseApplication : Application()