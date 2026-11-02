package tomeko.hybedwars.teammates

//? if 1.8.9 {
/*import tomeko.hybedwars.event.ClientTickEvents
*///?} else {
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
//?}
import tomeko.hybedwars.config.HyBedWarsConfig
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.NotifySounds
import tomeko.hybedwars.utils.removeFormatting

object TeammateDeathNotifier {
    private const val CHECK_INTERVAL_TICKS = 2

    private val seenAlive = HashSet<String>()
    private var server: String? = null
    private var ticks = 0

    fun register() {
        ClientTickEvents.START_CLIENT_TICK.register { onTick() }
    }

    private fun onTick() {
        if (!HyBedWarsConfig.teammateDeathSound || !HypixelPackets.onHypixel || !HypixelPackets.inBedwars || !HypixelPackets.inGame) {
            seenAlive.clear()
            return
        }

        ticks = (ticks + 1) % CHECK_INTERVAL_TICKS
        if (ticks != 0) return

        val currentServer = HypixelPackets.currentServerName
        if (currentServer != server) {
            server = currentServer
            seenAlive.clear()
        }

        val alive = HashSet<String>()
        var someoneDied = false
        for (teammate in Teammates.collect(includeSelf = false, includeDead = true, range = 0f, max = Int.MAX_VALUE)) {
            val name = teammate.displayName.removeFormatting()
            if (!teammate.dead) alive.add(name)
            else if (name in seenAlive) someoneDied = true
        }
        seenAlive.clear()
        seenAlive.addAll(alive)

        if (someoneDied) NotifySounds.play(HyBedWarsConfig.teammateDeathSoundType, survivalOnly = false)
    }
}
