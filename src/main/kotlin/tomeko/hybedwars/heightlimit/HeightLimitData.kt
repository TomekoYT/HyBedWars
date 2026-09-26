package tomeko.hybedwars.heightlimit

import com.google.gson.JsonParser
import tomeko.hybedwars.utils.Debug
import java.net.HttpURLConnection
import java.net.URI
import java.util.Locale

object HeightLimitData {
    private const val URL = "https://data-v2.polyfrost.org/hlm/trans-rights-are-human-rights.json"

    @Volatile
    private var bedwarsMaps: Map<String, Limits> = emptyMap()

    data class Limits(
        val minBuild: Int,
        val maxBuild: Int
    )

    private fun normalize(name: String): String {
        return name.trim().lowercase(Locale.ROOT).replace(" ", "_")
    }

    fun register() {
        Thread {
            var connection: HttpURLConnection? = null

            try {
                connection = URI.create(URL).toURL().openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode

                if (responseCode !in 200..299) {
                    Debug.log("Height limit HTTP error: $responseCode")
                    return@Thread
                }

                val body = connection.inputStream.bufferedReader().use { it.readText() }
                val root =
                    //? if forge
                    //JsonParser().parse(body)
                    //? else
                    JsonParser.parseString(body)

                if (!root.isJsonArray) {
                    Debug.log("[HyBedwars] Height limit JSON root is not an array")
                    return@Thread
                }

                val result = HashMap<String, Limits>()

                root.asJsonArray.forEach { element ->
                    if (!element.isJsonObject) {
                        return@forEach
                    }

                    val obj = element.asJsonObject

                    if (obj.get("gameType")?.asString != "BEDWARS") {
                        return@forEach
                    }

                    val name = obj.get("name")?.asString ?: return@forEach
                    val minBuild = obj.get("minBuild")?.asInt ?: return@forEach
                    val maxBuild = obj.get("maxBuild")?.asInt ?: return@forEach

                    if (maxBuild <= 0) {
                        return@forEach
                    }

                    result[normalize(name)] = Limits(
                        minBuild = minBuild,
                        maxBuild = maxBuild
                    )
                }

                bedwarsMaps = result
                Debug.log("Loaded ${result.size} BedWars height limits")
            } catch (e: Exception) {
                Debug.log("Failed to load height limits")
                e.printStackTrace()
            } finally {
                connection?.disconnect()
            }
        }.start()
    }

    fun getBedwarsLimits(mapName: String): Limits? {
        return bedwarsMaps[normalize(mapName)]
    }
}