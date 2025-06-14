package com.wjz.listeners

import com.wjz.ui.components.base.BaseButton
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.minecraft.client.gui.screen.TitleScreen
import org.apache.logging.log4j.LogManager
import java.awt.Color
import kotlin.math.log

//回到主界面时进行渲染
class MainMenuListener {
    private val window: Window = Window(ElementaVersion.V8)
    private val logger = LogManager.getLogger("wjz-nexus")

    init {
        //渲染事件
        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            println("当前的窗口是：$screen")
            if (screen !is TitleScreen) return@register //不是就直接退出

            window.clearChildren() // 清除所有旧组件
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
            }

            logger.info("渲染主界面按钮成功")

            //这里不需要担心重复注册的问题，实测如果只注册一次，再次返回主菜单的时候就不渲染了。
            ScreenEvents.afterRender(screen).register { screen1, matrices, mouseX, mouseY, tickDelta ->
                window.draw(UMatrixStack(matrices))
            }
            //注册鼠标事件处理函数
            ScreenMouseEvents.afterMouseClick(screen).register { _, mouseX, mouseY, buttonCode ->
                window.mouseClick(mouseX, mouseY, buttonCode)
            }
            ScreenMouseEvents.afterMouseRelease(screen).register { _, mouseX, mouseY, buttonCode ->
                window.mouseRelease()
            }
            ScreenMouseEvents.afterMouseRelease(screen).register { _, mouseX, mouseY, buttonCode ->
                window.mouseRelease()
            }
            logger.info("Screen事件注册成功!")
        }

    }
}