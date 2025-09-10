package com.example.quicknbiteapp.utils

import android.content.Context
import android.util.Log
import java.io.IOException
import java.nio.charset.StandardCharsets

object HtmlLoader {
    private const val TAG = "HtmlLoader"
    private val htmlCache = mutableMapOf<String, String>()

    fun loadHtmlFromAssets(context: Context, fileName: String): String {
        // Check cache first
        htmlCache[fileName]?.let {
            return it
        }

        return try {
            context.assets.open(fileName).use { inputStream ->
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                val content = String(buffer, StandardCharsets.UTF_8)

                // Cache the content
                htmlCache[fileName] = content
                content
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading HTML file: $fileName", e)
            createFallbackContent(fileName)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error loading HTML file: $fileName", e)
            createFallbackContent(fileName)
        }
    }

    // Helper function to load multiple documents
    fun loadDocuments(context: Context): Map<String, String> {
        return mapOf(
            "privacy_policy" to loadHtmlFromAssets(context, "privacy-policy.html"),
            "terms_and_conditions" to loadHtmlFromAssets(context, "terms-and-conditions.html"),
            "vendor_agreement" to loadHtmlFromAssets(context, "vendor-agreement.html")
        )
    }

    // Clear cache if needed
    fun clearCache() {
        htmlCache.clear()
    }

    private fun createFallbackContent(fileName: String): String {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body { 
                    font-family: Arial, sans-serif; 
                    padding: 20px; 
                    line-height: 1.6; 
                    color: #333;
                    background: linear-gradient(to bottom, #ffffff, #f8f9fa);
                }
                .container { 
                    max-width: 800px; 
                    margin: 0 auto; 
                    background: white;
                    padding: 30px;
                    border-radius: 15px;
                    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                }
                h1 { color: #ff6b00; text-align: center; }
                .error { color: #d32f2f; text-align: center; }
            </style>
        </head>
        <body>
            <div class="container">
                <h1>⚠️ Content Unavailable</h1>
                <p class="error">Unable to load: $fileName</p>
                <p>This content is temporarily unavailable. Please try again later.</p>
            </div>
        </body>
        </html>
        """.trimIndent()
    }
}