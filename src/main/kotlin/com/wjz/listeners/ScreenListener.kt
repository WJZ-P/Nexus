package com.wjz.listeners

import com.wjz.ui.MainMenuScreen
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixel
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.gui.screen.TitleScreen
import org.apache.logging.log4j.LogManager

//回到主界面时进行渲染
class ScreenListener {
    companion object {
        private val window: Window = Window(ElementaVersion.V8)
        private val logger = LogManager.getLogger("wjz-nexus")
    }

    init {
        ScreenEvents.AFTER_INIT.register { client, screen, scaledWidth, scaledHeight ->
            println("当前的窗口是：$screen")
            if (screen !is TitleScreen) return@register //不是就直接退出
            window.clearChildren() // 清除所有旧组件

            val mainMenuScreen = MainMenuScreen(scaledWidth, scaledHeight, screen) childOf window
            //创建一个检查器
//            Inspector(mainMenuScreen).constrain {
//                x = 20.pixel(true)
//                y = 15.pixel()
//            } childOf window

            //这里不需要担心重复注册的问题，实测如果只注册一次，再次返回的时候就不渲染了。
            ScreenEvents.afterRender(screen).register { screen1, matrices, mouseX, mouseY, tickDelta ->
                window.draw(UMatrixStack(matrices))
            }
            //注册鼠标事件处理函数
            ScreenMouseEvents.afterMouseClick(screen).register { _, mouseX, mouseY, buttonCode ->
                window.mouseClick(mouseX, mouseY, buttonCode)
                mainMenuScreen.mouseClick(mouseX, mouseY, buttonCode)
            }
            ScreenMouseEvents.afterMouseRelease(screen).register { _, mouseX, mouseY, buttonCode ->
                window.mouseRelease()
                mainMenuScreen.mouseRelease()
            }

        }
    }
}
