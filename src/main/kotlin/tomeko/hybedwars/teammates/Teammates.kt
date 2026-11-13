package tomeko.hybedwars.teammates

import net.minecraft.client.Minecraft
//? if 1.8.9 {
/*import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation
import net.minecraft.world.WorldSettings
*///?} else {
import net.minecraft.client.multiplayer.PlayerInfo
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.GameType
import net.minecraft.world.scores.DisplaySlot
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.ScoreHolder
import java.util.Optional
//?}
import tomeko.hybedwars.location.HypixelPackets
import tomeko.hybedwars.utils.removeFormatting

//? if 1.8.9 {
/*private typealias TabEntry = NetworkPlayerInfo
private typealias PlayerEntity = EntityPlayer
/** Texture id of a player's skin. */
typealias SkinId = ResourceLocation
*///?} else {
private typealias TabEntry = PlayerInfo
private typealias PlayerEntity = Player
/** Texture id of a player's skin. */
typealias SkinId = Identifier
//?}

/**
 * Finds your teammates and their health.
 * Teammates are matched by the color of their name in the tab list (e.g. red "R PlayerName"),
 * not by scoreboard team: Hypixel often gives every player their own team for nametag prefixes.
 */
object Teammates {
    class Teammate(
        /** Name with the team's color code in front. */
        val displayName: String,
        /** Health + Absorption in half hearts, or -1 if unknown. */
        val health: Float,
        val maxHealth: Float,
        val dead: Boolean = false,
        /** Skin to draw the head from, or null for the default skin. */
        val skin: SkinId? = null
    )

    //? if !1.8.9 {
    // Vanilla RGB values of the legacy color codes
    private val LEGACY_COLORS = mapOf(
        0x000000 to '0', 0x0000AA to '1', 0x00AA00 to '2', 0x00AAAA to '3',
        0xAA0000 to '4', 0xAA00AA to '5', 0xFFAA00 to '6', 0xAAAAAA to '7',
        0x555555 to '8', 0x5555FF to '9', 0x55FF55 to 'a', 0x55FFFF to 'b',
        0xFF5555 to 'c', 0xFF55FF to 'd', 0xFFFF55 to 'e', 0xFFFFFF to 'f'
    )
    //?}

    /**
     * Everyone seen alive on our team this game, with their skin. When a player dies Hypixel either
     * turns them into a gray spectator or drops them from the tab list completely, so after that
     * their team can't be read from the tab list any more.
     */
    private val knownTeammates = LinkedHashMap<String, SkinId>()
    private var knownColor: Char? = null
    private var knownServer: String? = null

    /**
     * Teammates sorted by name, or by distance when [range] is above 0 (then only
     * teammates loaded within that many blocks are listed). At most [max] are returned.
     */
    fun collect(includeSelf: Boolean, includeDead: Boolean, range: Float, max: Int): List<Teammate> {
        val mc =
            //? if 1.8.9 {
            /*Minecraft.getMinecraft()
            *///?} else {
            Minecraft.getInstance()
            //?}

        //? if 1.8.9 {
        /*val me = mc.thePlayer ?: return emptyList()
        val world = mc.theWorld ?: return emptyList()
        val net = mc.netHandler ?: return emptyList()
        val myInfo = net.getPlayerInfo(me.uniqueID)
        val entries: Collection<TabEntry> = net.playerInfoMap
        val myName = me.name
        *///?} else {
        val me = mc.player ?: return emptyList()
        val world = mc.level ?: return emptyList()
        val net = mc.connection ?: return emptyList()
        val myInfo = net.getPlayerInfo(me.uuid)
        val entries: Collection<TabEntry> = net.onlinePlayers
        val myName = me.gameProfile.name()
        //?}

        // Forget the old team when we move to another server (a new game)
        val server = HypixelPackets.currentServerName
        if (server != knownServer) {
            knownServer = server
            knownTeammates.clear()
            knownColor = null
        }

        var myColor = myInfo?.let(::teamColor)
        // Gray is used for spectators and players without a team, so it never counts as a team.
        if (myColor == null || myColor == '7') {
            // We may have died ourselves (or dropped out of the tab list), keep showing the team we were on.
            if (!includeDead) return emptyList()
            myColor = knownColor ?: return emptyList()
        } else if (myColor != knownColor) {
            knownTeammates.clear()
            knownColor = myColor
        }

        val nearbyOnly = range > 0
        val rangeSq = range.toDouble() * range
        val distances = HashMap<Teammate, Double>()
        val result = ArrayList<Teammate>()
        val inTabList = HashSet<String>()

        for (info in entries) {
            val name = nameOf(info)
            val skin = skinOf(info)
            val color = teamColor(info)
            val spectator = isSpectator(info)
            val alive = !spectator && color == myColor
            if (alive) {
                knownTeammates[name] = skin
            } else if (!spectator && color != null && color != '7') {
                // Alive on another team, so never one of ours
                knownTeammates.remove(name)
            }
            inTabList.add(name)
            if (!includeSelf && name == myName) continue

            val displayName = "§$myColor$name"

            //? if 1.8.9 {
            /*val player: PlayerEntity? = world.getPlayerEntityByName(name)
            *///?} else {
            val player: PlayerEntity? = world.players().firstOrNull { it.gameProfile.name() == name }
            //?}

            if (nearbyOnly) {
                // Unloaded players and spectators have no usable position, so they can't be "nearby".
                if (!alive || player == null) continue

                //? if 1.8.9 {
                /*val distSq = player.getDistanceSqToEntity(me)
                *///?} else {
                val distSq = player.distanceToSqr(me)
                //?}
                if (distSq > rangeSq) continue

                val teammate =
                    if (isDead(player)) Teammate(displayName, 0f, player.maxHealth, true, skin)
                    else Teammate(displayName, player.health + player.absorptionAmount, player.maxHealth, skin = skin)

                if (!teammate.dead || includeDead) {
                    distances[teammate] = distSq
                    result.add(teammate)
                }
                continue
            }

            if (!alive) {
                if (includeDead && name in knownTeammates) result.add(Teammate(displayName, 0f, 20f, true, skin))
                continue
            }

            when {
                // Death animation, before Hypixel switches them to spectator.
                player != null && isDead(player) ->
                    if (includeDead) result.add(Teammate(displayName, 0f, player.maxHealth, true, skin))

                player != null -> result.add(Teammate(displayName, player.health + player.absorptionAmount, player.maxHealth, skin = skin))

                // Out of render distance, fall back to the health Hypixel shows in the tab list.
                else -> result.add(Teammate(displayName, tabListHealth(name) ?: -1f, 20f, skin = skin))
            }
        }

        // Teammates who dropped out of the tab list after dying (they can't be "nearby", so not in Castle mode)
        if (includeDead && !nearbyOnly) {
            for ((name, skin) in knownTeammates) {
                if (name in inTabList || (!includeSelf && name == myName)) continue
                result.add(Teammate("§$myColor$name", 0f, 20f, true, skin))
            }
        }

        if (nearbyOnly) {
            result.sortBy { distances[it] }
        } else {
            result.sortBy { it.displayName.removeFormatting().lowercase() }
        }

        return result.take(max.coerceAtLeast(0))
    }

    private fun nameOf(info: TabEntry): String =
        //? if 1.8.9 {
        /*info.gameProfile.name
        *///?} else {
        info.profile.name()
        //?}

    private fun skinOf(info: TabEntry): SkinId =
        //? if 1.8.9 {
        /*info.locationSkin
        *///?} else {
        info.skin.body().texturePath()
        //?}

    private fun isSpectator(info: TabEntry): Boolean =
        //? if 1.8.9 {
        /*info.gameType == WorldSettings.GameType.SPECTATOR
        *///?} else {
        info.gameMode == GameType.SPECTATOR
        //?}

    private fun isDead(player: PlayerEntity): Boolean =
        //? if 1.8.9 {
        /*player.isDead || player.health <= 0
        *///?} else {
        player.isDeadOrDying
        //?}

    private fun tabListHealth(name: String): Float? {
        //? if 1.8.9 {
        /*val scoreboard = Minecraft.getMinecraft().theWorld?.scoreboard ?: return null
        val objective = scoreboard.getObjectiveInDisplaySlot(0) ?: return null
        // getObjectivesForEntity doesn't create an empty score like getValueFromObjective would.
        return scoreboard.getObjectivesForEntity(name)[objective]?.scorePoints?.toFloat()
        *///?} else {
        val scoreboard = Minecraft.getInstance().level?.scoreboard ?: return null
        val objective = scoreboard.getDisplayObjective(DisplaySlot.LIST) ?: return null
        return scoreboard.getPlayerScoreInfo(ScoreHolder.forNameOnly(name), objective)?.value()?.toFloat()
        //?}
    }

    /** The player's tab list name with legacy color codes. */
    private fun tabListName(info: TabEntry): String {
        //? if 1.8.9 {
        /*return Minecraft.getMinecraft().ingameGUI.tabList.getPlayerName(info)
        *///?} else {
        val component = info.tabListDisplayName
            ?: PlayerTeam.formatNameForTeam(info.team, Component.literal(nameOf(info)))

        val text = StringBuilder()
        component.visit(FormattedText.StyledContentConsumer<Unit> { style, part ->
            LEGACY_COLORS[style.color?.value]?.let { text.append('§').append(it) }
            text.append(part)
            Optional.empty()
        }, Style.EMPTY)
        return text.toString()
        //?}
    }

    /**
     * The last color code before the player's name in the tab list, or null if the player isn't on a team.
     *
     * Only a BedWars team letter right before the name counts as a team, e.g. "[1152✫] B Name"
     * (the level in front is optional). Before the game starts the tab list is colored by rank
     * ("[MVP+] Name"), and players who share your rank color there must not be remembered as teammates.
     */
    private fun teamColor(info: TabEntry): Char? {
        val formatted = tabListName(info)
        val nameIndex = formatted.indexOf(nameOf(info))
        val prefix = if (nameIndex >= 0) formatted.substring(0, nameIndex) else formatted

        val tag = prefix.removeFormatting().trim().substringAfterLast(' ')
        if (tag.length != 1 || !tag[0].isUpperCase()) return null

        var color: Char? = null
        for (i in 0 until prefix.length - 1) {
            if (prefix[i] != '§') continue
            val code = prefix[i + 1].lowercaseChar()
            if (code in '0'..'9' || code in 'a'..'f') color = code
        }
        return color
    }
}
