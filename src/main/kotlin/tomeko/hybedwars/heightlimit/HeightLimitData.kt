package tomeko.hybedwars.heightlimit

import com.google.gson.JsonParser
import tomeko.hybedwars.utils.Debug
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Locale
import java.util.concurrent.CompletableFuture

object HeightLimitData {
    private const val URL =
        "https://data-v2.polyfrost.org/hlm/trans-rights-are-human-rights.json"

    private val client = HttpClient.newHttpClient()

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
        CompletableFuture.runAsync {
            try {
                val request = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build()
                val response = client.send(request, HttpResponse.BodyHandlers.ofString())

                if (response.statusCode() !in 200..299) {
                    Debug.log("Height limit HTTP error: ${response.statusCode()}")
                    return@runAsync
                }

                val root = JsonParser.parseString(response.body())

                if (!root.isJsonArray) {
                    Debug.log("[HyBedwars] Height limit JSON root is not an array")
                    return@runAsync
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
            }
        }
    }

    fun getBedwarsLimits(mapName: String): Limits? {
        return bedwarsMaps[normalize(mapName)]
    }
}