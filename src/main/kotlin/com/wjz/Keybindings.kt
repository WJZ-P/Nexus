package com.wjz

import com.wjz.guiExamples.ExampleGui
import gg.essential.universal.UScreen
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object KeyBindings {
    private lateinit var openGuiKey: KeyBinding

    fun register() {
        // 注册按键绑定
        openGuiKey = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.wjz-nexus.open_gui", // 翻译键
                InputUtil.Type.KEYSYM, // 按键类型
                GLFW.GLFW_KEY_O, // 默认按键
                "category.wjz-nexus.general" // 分类
            )
        )

        // 注册按键事件
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            if (openGuiKey.wasPressed()) {
                // 打开GUI
                UScreen.displayScreen(ExampleGui())
            }
        }
    }
}