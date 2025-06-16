package com.wjz.ui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UMatrixStack
import java.awt.Color

class ToolTip(
    private val text: String,
    private val maxWidth: Float = 150f
) : UIComponent() {
    private var visible: Boolean = false
    private val background = UIBlock(Color(20, 20, 20, 230))
    private val textComponent = UIWrappedText(text) constrain {
        width = (maxWidth - 8).pixels()
    }

    init {
        constrain {
            width = ChildBasedSizeConstraint(padding = 4f).coerceAtMost(maxWidth.pixels())
            height = ChildBasedSizeConstraint(padding = 4f)
            visible = false // 初始隐藏
        }
        background constrain {
            radius = 3.pixel
            effect(OutlineEffect(Color(80, 80, 80, 200), 1f))
        }
        textComponent constrain {
            x = 4.pixels()
            y = 4.pixels()
        } childOf background
    }

    override fun draw(matrixStack: UMatrixStack) {
        beforeDraw(matrixStack)
        super.draw(matrixStack)
    }

    fun showTooltip() {
        if (visible) return
        visible = true
        unhide()

        // 淡入动画
        val originalColor = background.getColor()
        val transparentColor = Color(originalColor.red, originalColor.green, originalColor.blue, 0)
        val targetColor = Color(originalColor.red, originalColor.green, originalColor.blue, 230)
        background.setColor(transparentColor.toConstraint())
        background.animate {
            setColorAnimation(Animations.OUT_EXP, 0.2f, targetColor.toConstraint())
        }
    }

    fun hideTooltip() {
        if (!visible) return
        visible = false

        // 淡出动画
        val currentColor = background.getColor()
        val transparentColor = Color(currentColor.red, currentColor.green, currentColor.blue, 0)
        background.animate {
            setColorAnimation(Animations.OUT_EXP, 0.2f, transparentColor.toConstraint())
            onComplete {
                this@ToolTip.hide(true) // 调用 UIComponent.hide(true) 隐藏组件
            }
        }
    }
}