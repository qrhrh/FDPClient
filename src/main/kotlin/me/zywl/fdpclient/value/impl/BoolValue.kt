/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package me.zywl.fdpclient.value.impl

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import me.zywl.fdpclient.value.Value
import net.ccbluex.liquidbounce.utils.AnimationHelper

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean) : Value<Boolean>(name, value) {

    val animation = AnimationHelper(this)
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) {
            value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
        }
    }
    init {
        animation.animationX = if (value) 5F else -5F
    }
    open fun toggle(){
        this.value = !this.value
    }

}