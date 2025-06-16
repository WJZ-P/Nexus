package com.wjz.listeners

import com.wjz.ui.components.base.BaseButton
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.RelativeWindowConstraint
import gg.essential.elementa.dsl.*
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import org.apache.logging.log4j.LogManager
import java.awt.Color
import kotlin.math.log
import kotlin.math.roundToInt

//回到主界面时进行渲染
class MainMenuListener {
    companion object {
        private val window: Window = Window(ElementaVersion.V8)
        private val logger = LogManager.getLogger("wjz-nexus")
        private const val BUTTON_MARGIN: Int = 5
    }


    init {
        ScreenEvents.AFTER_INIT.register { client, screen, scaledWidth, scaledHeight ->
            println("当前的窗口是：$screen")
            if (screen !is TitleScreen) return@register //不是就直接退出

            window.clearChildren() // 清除所有旧组件
            //创建一个检查器
            Inspector(window).constrain {
                x = 20.pixel(true)
                y = 15.pixel()
            } childOf window

            //创建按钮
            val button = BaseButton("!") childOf window

            //按钮的约束跟原版按钮对齐。
            val buttonList = Screens.getButtons(screen)

                button.constrain {
                    x = (buttonList[2].x + buttonList[2].width + BUTTON_MARGIN).pixels
                    y = (buttonList[2].y + 1).pixels //这里+1是为了确保对齐。
                }
            //button.setToolTipText("哇哈哈")

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