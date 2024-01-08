/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.other

import com.google.gson.JsonElement
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.TeleportEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.network.handshake.client.*
import net.minecraft.network.login.client.*
import net.minecraft.network.login.server.*
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.*
import net.minecraft.network.status.client.*
import net.minecraft.network.status.server.*

@ModuleInfo(name = "PacketDebugger", category = ModuleCategory.OTHER)
object PacketDebugger : Module() {

    private val printFieldsValue = BoolValue("PrintFields",true)
    private val printTimeValue = BoolValue("PrintTime",true)
    private val mcpFieldValue = BoolValue("MCPField",true)
    private val packetDebugStates = hashMapOf<String,Boolean>()
    private val settings = arrayListOf<Value<*>>(printFieldsValue,printTimeValue,mcpFieldValue)
    private val simpleDebugger = BoolValue("Debugger", false)

    private val fieldMap = hashMapOf("field_148940_a" to "x","field_148938_b" to "y","field_148939_c" to "z","field_148936_d" to "yaw","field_148937_e" to "pitch","field_179835_f" to "field_179835_f","field_149018_a" to "entityId","field_149016_b" to "x","field_149017_c" to "y","field_149014_d" to "z","field_149015_e" to "speedX","field_149012_f" to "speedY","field_149013_g" to "speedZ","field_149021_h" to "pitch","field_149022_i" to "yaw","field_149019_j" to "type","field_149020_k" to "field_149020_k","field_148992_a" to "entityID","field_148990_b" to "posX","field_148991_c" to "posY","field_148988_d" to "posZ","field_148989_e" to "xpValue","field_149059_a" to "entityId","field_149057_b" to "x","field_149058_c" to "y","field_149055_d" to "z","field_149056_e" to "type","field_149042_a" to "entityId","field_149040_b" to "type","field_149041_c" to "x","field_149038_d" to "y","field_149039_e" to "z","field_149036_f" to "velocityX","field_149037_g" to "velocityY","field_149047_h" to "velocityZ","field_149048_i" to "yaw","field_149045_j" to "pitch","field_149046_k" to "headPitch","field_149043_l" to "field_149043_l","field_149044_m" to "watcher","field_148973_a" to "entityID","field_179838_b" to "position","field_179839_c" to "facing","field_148968_f" to "title","field_148957_a" to "entityId","field_179820_b" to "playerId","field_148956_c" to "x","field_148953_d" to "y","field_148954_e" to "z","field_148951_f" to "yaw","field_148952_g" to "pitch","field_148959_h" to "currentItem","field_148960_i" to "watcher","field_148958_j" to "field_148958_j","field_148981_a" to "entityId","field_148980_b" to "type","field_148976_a" to "field_148976_a","field_148852_a" to "breakerId","field_179822_b" to "position","field_148849_e" to "progress","field_179824_a" to "blockPos","field_148859_d" to "metadata","field_148860_e" to "nbt","field_179826_a" to "blockPosition","field_148872_d" to "instrument","field_148873_e" to "pitch","field_148871_f" to "block","field_179828_a" to "blockPosition","field_148883_d" to "blockState","field_179833_a" to "difficulty","field_179832_b" to "difficultyLocked","field_149632_a" to "matches","field_148919_a" to "chatComponent","field_179842_b" to "type","field_148925_b" to "chunkPosCoord","field_179845_b" to "changedBlocks","field_148894_a" to "windowId","field_148892_b" to "actionNumber","field_148893_c" to "field_148893_c","field_148896_a" to "windowId","field_148909_a" to "windowId","field_148907_b" to "inventoryType","field_148908_c" to "windowTitle","field_148905_d" to "slotCount","field_148904_f" to "entityId","field_148914_a" to "windowId","field_148913_b" to "itemStacks","field_149186_a" to "windowId","field_149184_b" to "varIndex","field_149185_c" to "varValue","field_149179_a" to "windowId","field_149177_b" to "slot","field_149178_c" to "item","field_149172_a" to "channel","field_149171_b" to "data","field_149167_a" to "reason","field_149164_a" to "entityId","field_149163_b" to "logicOpcode","field_179766_a" to "entityId","field_179765_b" to "tagCompound","field_149158_a" to "posX","field_149156_b" to "posY","field_149157_c" to "posZ","field_149154_d" to "strength","field_149155_e" to "affectedBlockPositions","field_149152_f" to "field_149152_f","field_149153_g" to "field_149153_g","field_149159_h" to "field_149159_h","field_179761_a" to "threshold","field_149142_a" to "MESSAGE_NAMES","field_149140_b" to "state","field_149141_c" to "field_149141_c","field_149136_a" to "id","field_149284_a" to "chunkX","field_149282_b" to "chunkZ","field_179758_c" to "extractedData","field_149279_g" to "field_149279_g","field_149266_a" to "xPositions","field_149264_b" to "zPositions","field_179755_c" to "chunksData","field_149267_h" to "isOverworld","field_149251_a" to "soundType","field_179747_b" to "soundPos","field_149249_b" to "soundData","field_149246_f" to "serverWide","field_179751_a" to "particleType","field_149234_b" to "xCoord","field_149235_c" to "yCoord","field_149232_d" to "zCoord","field_149233_e" to "xOffset","field_149230_f" to "yOffset","field_149231_g" to "zOffset","field_149237_h" to "particleSpeed","field_149238_i" to "particleCount","field_179752_j" to "longDistance","field_179753_k" to "particleArguments","field_149219_a" to "soundName","field_149217_b" to "posX","field_149218_c" to "posY","field_149215_d" to "posZ","field_149216_e" to "soundVolume","field_149214_f" to "soundPitch","field_149206_a" to "entityId","field_149204_b" to "hardcoreMode","field_149205_c" to "gameType","field_149202_d" to "dimension","field_149203_e" to "difficulty","field_149200_f" to "maxPlayers","field_149201_g" to "worldType","field_179745_h" to "reducedDebugInfo","field_149191_a" to "mapId","field_179739_b" to "mapScale","field_179740_c" to "mapVisiblePlayersVec4b","field_179737_d" to "mapMinX","field_179738_e" to "mapMinY","field_179735_f" to "mapMaxX","field_179736_g" to "mapMaxY","field_179741_h" to "mapDataBytes","field_149074_a" to "entityId","field_149072_b" to "posX","field_149073_c" to "posY","field_149070_d" to "posZ","field_149071_e" to "yaw","field_149068_f" to "pitch","field_179743_g" to "onGround","field_149069_g" to "field_149069_g","field_179778_a" to "signPosition","field_149119_a" to "invulnerable","field_149117_b" to "flying","field_149118_c" to "allowFlying","field_149115_d" to "creativeMode","field_149116_e" to "flySpeed","field_149114_f" to "walkSpeed","field_179776_a" to "eventType","field_179774_b" to "field_179774_b","field_179775_c" to "field_179775_c","field_179772_d" to "field_179772_d","field_179773_e" to "deathMessage","field_179770_a" to "action","field_179769_b" to "players","field_149097_a" to "playerID","field_179799_b" to "bedPos","field_149100_a" to "entityIDs","field_149079_a" to "entityId","field_149078_b" to "effectId","field_179786_a" to "url","field_179785_b" to "hash","field_149088_a" to "dimensionID","field_149086_b" to "difficulty","field_149087_c" to "gameType","field_149085_d" to "worldType","field_149384_a" to "entityId","field_149383_b" to "yaw","field_179795_a" to "action","field_179793_b" to "size","field_179794_c" to "centerX","field_179791_d" to "centerZ","field_179792_e" to "targetSize","field_179789_f" to "diameter","field_179790_g" to "timeUntilTarget","field_179796_h" to "warningTime","field_179797_i" to "warningDistance","field_179781_a" to "entityId","field_149387_a" to "heldItemHotbarIndex","field_149374_a" to "position","field_149373_b" to "scoreName","field_149379_a" to "entityId","field_149378_b" to "field_149378_b","field_149408_a" to "leash","field_149406_b" to "entityId","field_149407_c" to "vehicleEntityId","field_149417_a" to "entityID","field_149415_b" to "motionX","field_149416_c" to "motionY","field_149414_d" to "motionZ","field_149394_a" to "entityID","field_149392_b" to "equipmentSlot","field_149393_c" to "itemStack","field_149401_a" to "field_149401_a","field_149399_b" to "totalExperience","field_149400_c" to "level","field_149336_a" to "health","field_149334_b" to "foodLevel","field_149335_c" to "saturationLevel","field_149343_a" to "objectiveName","field_149341_b" to "objectiveValue","field_179818_c" to "type","field_149342_c" to "field_149342_c","field_149320_a" to "name","field_149318_b" to "displayName","field_149319_c" to "prefix","field_149316_d" to "suffix","field_179816_e" to "nameTagVisibility","field_179815_f" to "color","field_149317_e" to "players","field_149314_f" to "action","field_149315_g" to "friendlyFlags","field_149329_a" to "name","field_149327_b" to "objective","field_149328_c" to "value","field_149326_d" to "action","field_179801_a" to "spawnBlockPos","field_149369_a" to "totalWorldTime","field_149368_b" to "worldTime","field_179812_a" to "type","field_179810_b" to "message","field_179811_c" to "fadeInTime","field_179808_d" to "displayTime","field_179809_e" to "fadeOutTime","field_179706_a" to "world","field_179705_b" to "blockPos","field_149349_d" to "lines","field_179703_a" to "header","field_179702_b" to "footer","field_149357_a" to "collectedItemEntityId","field_149356_b" to "entityId","field_149458_a" to "entityId","field_149456_b" to "posX","field_149457_c" to "posY","field_149454_d" to "posZ","field_149455_e" to "yaw","field_149453_f" to "pitch","field_179698_g" to "onGround","field_149445_a" to "entityId","field_149444_b" to "field_149444_b","field_149434_a" to "entityId","field_149432_b" to "effectId","field_149433_c" to "amplifier","field_149431_d" to "duration","field_179708_e" to "hideParticles","field_149420_a" to "message","field_179710_b" to "targetBlock","field_149440_a" to "message","field_149437_a" to "status","field_149530_a" to "lang","field_149528_b" to "view","field_149529_c" to "chatVisibility","field_149526_d" to "enableColors","field_179711_e" to "modelPartFlags","field_149536_a" to "windowId","field_149534_b" to "uid","field_149535_c" to "accepted","field_149541_a" to "windowId","field_149540_b" to "button","field_149554_a" to "windowId","field_149552_b" to "slotId","field_149553_c" to "usedButton","field_149550_d" to "actionNumber","field_149551_e" to "clickedItem","field_149549_f" to "mode","field_149556_a" to "windowId","field_149562_a" to "channel","field_149561_c" to "data","field_149567_a" to "entityId","field_149566_b" to "action","field_179713_c" to "hitVec","field_149461_a" to "key","field_149479_a" to "x","field_149477_b" to "y","field_149478_c" to "z","field_149476_e" to "yaw","field_149473_f" to "pitch","field_149474_g" to "onGround","field_149480_h" to "moving","field_149481_i" to "rotating","field_149500_a" to "invulnerable","field_149498_b" to "flying","field_149499_c" to "allowFlying","field_149496_d" to "creativeMode","field_149497_e" to "flySpeed","field_149495_f" to "walkSpeed","field_179717_a" to "position","field_179716_b" to "facing","field_149508_e" to "status","field_149517_a" to "entityID","field_149515_b" to "action","field_149516_c" to "auxData","field_149624_a" to "strafeSpeed","field_149622_b" to "forwardSpeed","field_149623_c" to "jumping","field_149621_d" to "sneaking","field_179720_a" to "hash","field_179719_b" to "status","field_149615_a" to "slotId","field_149629_a" to "slotId","field_149628_b" to "stack","field_179723_a" to "pos","field_149590_d" to "lines","field_179729_a" to "id","field_179726_a" to "field_179726_a","field_179725_b" to "position","field_149579_d" to "placedBlockDirection","field_149580_e" to "stack","field_149577_f" to "facingX","field_149578_g" to "facingY","field_149584_h" to "facingZ","field_149602_a" to "profile","field_149612_a" to "hashedServerId","field_149610_b" to "publicKey","field_149611_c" to "verifyToken","field_179733_a" to "compressionTreshold","field_149605_a" to "reason","field_149305_a" to "profile","field_149302_a" to "secretKeyEncrypted","field_149301_b" to "verifyTokenEncrypted","field_149293_a" to "clientTime","field_149297_a" to "GSON","field_149296_b" to "response","field_149290_a" to "clientTime")
    private val packetClasses = arrayOf(C00PacketLoginStart::class.java, C01PacketEncryptionResponse::class.java, C00PacketServerQuery::class.java, C01PacketPing::class.java, C00PacketKeepAlive::class.java, C01PacketChatMessage::class.java, C02PacketUseEntity::class.java, C03PacketPlayer::class.java, C03PacketPlayer.C04PacketPlayerPosition::class.java, C03PacketPlayer.C05PacketPlayerLook::class.java, C03PacketPlayer.C06PacketPlayerPosLook::class.java, C07PacketPlayerDigging::class.java, C08PacketPlayerBlockPlacement::class.java, C09PacketHeldItemChange::class.java, C0APacketAnimation::class.java, C0BPacketEntityAction::class.java, C0CPacketInput::class.java, C0DPacketCloseWindow::class.java, C0EPacketClickWindow::class.java, C0FPacketConfirmTransaction::class.java, C10PacketCreativeInventoryAction::class.java, C11PacketEnchantItem::class.java, C12PacketUpdateSign::class.java, C13PacketPlayerAbilities::class.java, C14PacketTabComplete::class.java, C15PacketClientSettings::class.java, C16PacketClientStatus::class.java, C17PacketCustomPayload::class.java, C18PacketSpectate::class.java, C19PacketResourcePackStatus::class.java, C00Handshake::class.java, S00PacketDisconnect::class.java, S01PacketEncryptionRequest::class.java, S02PacketLoginSuccess::class.java, S03PacketEnableCompression::class.java, S00PacketServerInfo::class.java, S01PacketPong::class.java, S00PacketKeepAlive::class.java, S01PacketJoinGame::class.java, S02PacketChat::class.java, S03PacketTimeUpdate::class.java, S04PacketEntityEquipment::class.java, S05PacketSpawnPosition::class.java, S06PacketUpdateHealth::class.java, S07PacketRespawn::class.java, S08PacketPlayerPosLook::class.java, S09PacketHeldItemChange::class.java, S0APacketUseBed::class.java, S0BPacketAnimation::class.java, S0CPacketSpawnPlayer::class.java, S0DPacketCollectItem::class.java, S0EPacketSpawnObject::class.java, S0FPacketSpawnMob::class.java, S10PacketSpawnPainting::class.java, S11PacketSpawnExperienceOrb::class.java, S12PacketEntityVelocity::class.java, S13PacketDestroyEntities::class.java, S14PacketEntity::class.java, S14PacketEntity.S15PacketEntityRelMove::class.java, S14PacketEntity.S16PacketEntityLook::class.java, S14PacketEntity.S17PacketEntityLookMove::class.java, S18PacketEntityTeleport::class.java, S19PacketEntityHeadLook::class.java, S19PacketEntityStatus::class.java, S1BPacketEntityAttach::class.java, S1CPacketEntityMetadata::class.java, S1DPacketEntityEffect::class.java, S1EPacketRemoveEntityEffect::class.java, S1FPacketSetExperience::class.java, S20PacketEntityProperties::class.java, S21PacketChunkData::class.java, S22PacketMultiBlockChange::class.java, S23PacketBlockChange::class.java, S24PacketBlockAction::class.java, S25PacketBlockBreakAnim::class.java, S26PacketMapChunkBulk::class.java, S27PacketExplosion::class.java, S28PacketEffect::class.java, S29PacketSoundEffect::class.java, S2APacketParticles::class.java, S2BPacketChangeGameState::class.java, S2CPacketSpawnGlobalEntity::class.java, S2DPacketOpenWindow::class.java, S2EPacketCloseWindow::class.java, S2FPacketSetSlot::class.java, S30PacketWindowItems::class.java, S31PacketWindowProperty::class.java, S32PacketConfirmTransaction::class.java, S33PacketUpdateSign::class.java, S34PacketMaps::class.java, S35PacketUpdateTileEntity::class.java, S36PacketSignEditorOpen::class.java, S37PacketStatistics::class.java, S38PacketPlayerListItem::class.java, S39PacketPlayerAbilities::class.java, S3APacketTabComplete::class.java, S3BPacketScoreboardObjective::class.java, S3CPacketUpdateScore::class.java, S3DPacketDisplayScoreboard::class.java, S3EPacketTeams::class.java, S3FPacketCustomPayload::class.java, S40PacketDisconnect::class.java, S41PacketServerDifficulty::class.java, S42PacketCombatEvent::class.java, S43PacketCamera::class.java, S44PacketWorldBorder::class.java, S45PacketTitle::class.java, S46PacketSetCompressionLevel::class.java, S47PacketPlayerListHeaderFooter::class.java, S48PacketResourcePackSend::class.java, S49PacketUpdateEntityNBT::class.java).apply {
        forEach {
            settings.add(
                object : BoolValue(it.simpleName, false) {
                    val clazz = it

                    override fun onChange(oldValue: Boolean, newValue: Boolean) {
                        packetDebugStates[clazz.name] = newValue
                    }

                    override fun fromJson(element: JsonElement) {
                        super.fromJson(element)
                        onChange(value, value)
                    }
                }
            )
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        var clazz: Class<*> = packet::class.java

        if (packetDebugStates[clazz.name] == true) {
            ClientUtils.displayChatMessage("${clazz.simpleName}    ${if (printTimeValue.get()) System.currentTimeMillis() % 0xFFFFfF else ""}")

            if (printFieldsValue.get()) {
                if (clazz.isMemberClass) clazz = clazz.declaringClass

                clazz.declaredFields.forEach {
                    it.isAccessible = true
                    ClientUtils.displayChatMessage("    ${if (mcpFieldValue.get()) fieldMap[it.name] else it.name} : ${it.get(packet)}")
                }
            }
        }

        if (simpleDebugger.get()) {

        if (packet is C0DPacketCloseWindow)
            chat("Close C0D // ${event.packet}")
        if (packet is C08PacketPlayerBlockPlacement)
            chat("Place C08 // ${event.packet}")
        if (packet is C0EPacketClickWindow)
            chat("Click C0E // ${event.packet}")
        if (packet is C14PacketTabComplete)
            chat("List C14 // ${event.packet}")
        if (packet is C02PacketUseEntity)
            chat("Attack C02 // ${event.packet}")
        if (packet is C09PacketHeldItemChange)
            chat("Switch C09 // ${event.packet}")
        if (packet is C0APacketAnimation)
            chat("Swing C0A // ${event.packet}")
        }
    }

        @EventTarget
        fun onTeleport(event: TeleportEvent) {
        chat("Server Teleport")
    }

    override val values: List<Value<*>> get() = settings
}