package com.wjz.ui

import com.wjz.ui.components.base.BaseButton
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixel
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.transitions.RecursiveFadeInTransition
import net.fabricmc.fabric.api.client.screen.v1.Screens
import net.minecraft.client.gui.screen.Screen
import org.apache.logging.log4j.LogManager

class MainMenuScreen(
    screenWidth: Int,
    screenHeight: Int,
    gameScreen: Screen
) : UIContainer() {
    companion object {
        private val logger = LogManager.getLogger("wjz-nexus")
        private const val BUTTON_MARGIN: Int = 5
    }

    init {
        val container = UIContainer() constrain {
            x = 0.pixel
            y = 0.pixel
            width = screenWidth.pixel
            height = screenHeight.pixel
        } childOf this

        //按钮的约束跟原版按钮对齐。
        val buttonList = Screens.getButtons(gameScreen)

        //创建按钮
        val button = BaseButton("!", "嘻嘻哈哈", container) constrain {
            x = (buttonList[2].x + buttonList[2].width + BUTTON_MARGIN).pixels
            y = (buttonList[2].y + 1).pixels //这里+1是为了确保对齐。
        } childOf container
        //按钮要渐入

    }
}