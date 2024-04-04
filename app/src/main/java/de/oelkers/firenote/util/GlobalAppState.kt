package de.oelkers.firenote.util

import android.app.Application
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack

class GlobalAppState : Application() {

    lateinit var iconPack: IconPack
        private set

    override fun onCreate() {
        super.onCreate()
        loadIconPack()
    }

    private fun loadIconPack() {
        val loader = IconPackLoader(this)
        val iconPack = createDefaultIconPack(loader)
        iconPack.loadDrawables(loader.drawableLoader)
        this.iconPack = iconPack
    }
}
