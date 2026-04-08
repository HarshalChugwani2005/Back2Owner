package com.back2owner.app.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Utility object for image processing operations:
 * - Converting URIs to compressed ByteArrays
 * - Generating blurred preview images for the security verification gate
 * - Creating temp URIs for camera capture
 */
object ImageUtils {

    /**
     * Converts a content URI to a compressed JPEG ByteArray.
     * Images are scaled down to [maxWidth] x [maxHeight] to save bandwidth and storage.
     */
    fun uriToByteArray(
        context: Context,
        uri: Uri,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024,
    ): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: return ByteArray(0)

        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        if (originalBitmap == null) return ByteArray(0)

        val scaledBitmap = scaleBitmap(originalBitmap, maxWidth, maxHeight)

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)

        if (scaledBitmap !== originalBitmap) scaledBitmap.recycle()
        originalBitmap.recycle()

        return outputStream.toByteArray()
    }

    /**
     * Generates a heavily blurred version of the photo.
     * Uses a scale-down/scale-up technique which is fast and works on all API levels.
     * The blurred image is used in the feed to hide item details until ownership is verified.
     */
    fun generateBlurredBytes(photoBytes: ByteArray): ByteArray {
        if (photoBytes.isEmpty()) return ByteArray(0)

        val original = BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
            ?: return ByteArray(0)

        // Scale down to a very tiny size and back up → creates a natural pixelated blur
        val tinyWidth = 20
        val tinyHeight = (tinyWidth * original.height.toFloat() / original.width).toInt()
            .coerceAtLeast(1)

        val tiny = Bitmap.createScaledBitmap(original, tinyWidth, tinyHeight, true)
        val blurred = Bitmap.createScaledBitmap(tiny, original.width / 2, original.height / 2, true)

        val outputStream = ByteArrayOutputStream()
        blurred.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

        tiny.recycle()
        blurred.recycle()
        original.recycle()

        return outputStream.toByteArray()
    }

    /**
     * Creates a temporary file URI for the camera to save a captured photo.
     * Uses FileProvider for secure file sharing between the app and the camera activity.
     */
    fun createTempImageUri(context: Context): Uri {
        val imagesDir = File(context.cacheDir, "images").also { it.mkdirs() }
        val imageFile = File(imagesDir, "photo_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
    }

    private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) return bitmap

        val ratio = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
