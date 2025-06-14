package com.wjz.listeners

import com.wjz.ui.components.base.BaseButton
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.screen.TitleScreen
import org.apache.logging.log4j.LogManager
import java.awt.Color

class MainMenuListener {
    private val window: Window = Window(ElementaVersion.V8)
    private val logger = LogManager.getLogger("wjz-nexus")

    init {
        //对window进行调整

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            println("当前的窗口是：$screen")
            if (screen is TitleScreen) {
                //创建一个检查器
                Inspector(window).constrain {
                    x = 20.pixel(true)
                    y = 15.pixel()
                } childOf window

                //创建按钮
                val button = BaseButton("hi！我是一个按钮哦！！！！！") childOf window

                button.constrain {
                    x = CenterConstraint()
                    y = CenterConstraint() - 10.percent
                    textScale = 3.pixel
                }

                //注册渲染事件
                ScreenEvents.afterRender(screen).register { screen1, matrices, mouseX, mouseY, tickDelta ->
                    window.draw(UMatrixStack(matrices))
                }


                logger.info("渲染主界面按钮成功")
            }
        }
    }
}