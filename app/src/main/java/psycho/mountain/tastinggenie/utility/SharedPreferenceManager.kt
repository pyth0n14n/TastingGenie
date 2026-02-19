package psycho.mountain.tastinggenie.utility

import android.content.Context
import org.jetbrains.anko.defaultSharedPreferences

// 画像を保存時，無圧縮のファイルも保存するか
const val storeUncompressed = "storeUncompressed"
// 画像を保存時，無圧縮のファイルも保存するかの確認が必要か
const val storeUncompressedConfirmed = "storeUncompressedConfirmed"


fun isStoreUncompressed(context: Context): Boolean {
    return context.defaultSharedPreferences.getBoolean(storeUncompressed, true)
}

fun setStoreUncompressed(context: Context, bool: Boolean) {
    context.defaultSharedPreferences.edit().putBoolean(storeUncompressed, bool)
}

fun isStoreUncompressedConfirmed(context: Context): Boolean {
    return context.defaultSharedPreferences.getBoolean(storeUncompressedConfirmed, true)
}

fun setStoreUncompressedConfirmed(context: Context, bool: Boolean) {
    context.defaultSharedPreferences.edit().putBoolean(storeUncompressedConfirmed, bool)
}

