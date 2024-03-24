package dev.sanmer.sac.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.sanmer.sac.compat.MediaStoreCompat.copyToDir
import dev.sanmer.sac.io.Endian
import dev.sanmer.sac.io.Sac
import dev.sanmer.sac.io.SacFileType
import dev.sanmer.sac.io.SacHeader
import dev.sanmer.sac.repository.UserPreferencesRepository
import dev.sanmer.sac.utils.extensions.tmpDir
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.themes.elementBlank
import org.jetbrains.letsPlot.themes.theme
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    application: Application
) : AndroidViewModel(application) {
    private val context: Context by lazy { getApplication() }
    var isFullScreen by mutableStateOf(false)

    private var dataKey = Uri.EMPTY to Endian.Little
    var filename by mutableStateOf("")
        private set

    private var headerOrNull: SacHeader? by mutableStateOf(null)
    val header get() = checkNotNull(headerOrNull)

    private var x: FloatArray by mutableStateOf(floatArrayOf())
    private var y: FloatArray by mutableStateOf(floatArrayOf())

    private var figureOrNull: Figure? by mutableStateOf(null)
    val figure get() = checkNotNull(figureOrNull)

    var isFailed by mutableStateOf(false)
        private set
    var error by mutableStateOf("")
        private set

    val isHeaderReady get() = headerOrNull != null && !isFailed
    val isFigureReady get() = figureOrNull != null && !isFailed

    init {
        Timber.d("HomeViewModel init")
        reloadData()
    }

    private fun clearData() {
        isFailed = false
        error = ""

        headerOrNull = null
        x = floatArrayOf()
        y = floatArrayOf()

        figureOrNull = null
    }

    private fun createFigure(): Figure {
        val data = mapOf("x" to x, "y" to y)

        return letsPlot(data) + geomLine {
            x = "x"
            y = "y"
        } + theme(
            axis = elementBlank()
        )
    }

    private fun reloadData() {
        userPreferencesRepository.data
            .onEach {
                val uri = dataKey.first
                loadSacFile(uri, it.endian)
            }
            .launchIn(viewModelScope)
    }

    fun loadSacFile(uri: Uri, endian: Endian) = viewModelScope.launch(Dispatchers.IO) {
        val key = uri to endian
        if (dataKey == key || uri == Uri.EMPTY) {
            return@launch
        } else {
            dataKey = key
            clearData()
        }

        val tmp = context.copyToDir(uri, context.tmpDir)
            .apply {
                filename = name
            }

        runCatching {
            Sac.read(tmp, endian).use { sac ->
                headerOrNull = sac.header

                val fileType = SacFileType.valueBy(header.iftype)
                if (fileType == SacFileType.Time && header.leven) {
                    x = FloatArray(header.npts) { it.toFloat() }
                    y = sac.first
                } else {
                    x = sac.first
                    y = sac.second
                }
            }
        }.onFailure {
            isFailed = true
            error = it.stackTraceToString()

            Timber.e(it)
        }.onSuccess {
           figureOrNull = createFigure()
        }

        tmp.delete()
    }

    fun setEndian(value: Endian) =
        userPreferencesRepository.setEndian(value)
}