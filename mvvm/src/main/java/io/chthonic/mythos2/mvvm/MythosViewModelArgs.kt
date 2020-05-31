package io.chthonic.mythos2.mvvm

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.SavedStateHandle

/**
 * Created by jhavatar on 3/15/2020.
 */
data class MythosViewModelArgs(val application: Application, val savedState: SavedStateHandle, val args: Bundle)