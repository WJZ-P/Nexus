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
            Inspector(mainMenuScreen).constrain {
                x = 20.pixel(true)
                y = 15.pixel()
            } childOf window

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


// 检查这个屏幕是不是主菜单 (TitleScreen)
//            if (screen is TitleScreen) {
//                // 获取屏幕的按钮列表并添加一个我们自己的新按钮
//
//                val buttons = Screens.getButtons(screen)//获取当前屏幕上的按钮。
//
//                val x = (scaledWidth * 0.75).roundToInt()
//                val y = buttons[2].y
//
//                Screens.getButtons(screen)
//                    .add(
//                        ButtonWidget(
//                            x,
//                            y,
//                            100,
//                            20,
//                            TranslatableText("text"),
//                            ButtonWidget.PressAction {
//                                println("点击了按钮")
//                            })
//                    );
//            }
//}


//        //渲染事件
//        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
//
//        }