package com.wjz.ui.components

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.RelativeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color
import kotlin.math.max

class ToolTip(
    private val text: String,
    private val parentComponent: UIComponent,
    private val delayMs: Long = 500,
    private val maxWidth: Float = 150f
) : UIComponent() {
    private var timerId: Int? = null
    private var visible: Boolean = false
    private val background = UIBlock(Color(20, 20, 20, 230))
    private val textComponent = UIWrappedText(text) constrain {
        width = (maxWidth - 8).pixels()
    }

    init {
        constrain {
            // 默认放在父组件的上方
            x = RelativeConstraint(0.5f) - (width / 2f)
            y = SiblingConstraint(alignOpposite = true) - 5.pixels()
            //自动调整行数
            width = ChildBasedSizeConstraint(padding = 4f).coerceAtMost(maxWidth.pixels())
            height = ChildBasedSizeConstraint(padding = 4f)
            //初始时要隐藏元素
            visible = false
        }
        //设置背景样式
        background constrain {
            radius = 3.pixel
            effect(OutlineEffect(Color(80, 80, 80, 200), 1f))
        }
        //设置文本样式
        textComponent constrain {
            x = 4.pixels()
            y = 4.pixels()
        } childOf background
        //事件处理
        parentComponent.apply {
            onMouseEnter {
                timerId = startDelay(1000) {
                    showTooltip()
                }
            }
            onMouseLeave {
                timerId?.let {
                    stopDelay(it)
                    timerId = null
                    hideTooltip()
                }
            }
        }
    }

    private fun showTooltip() {
        if (visible) return
        visible = true

        // 计算位置
        val parentLocation: Pair<Float, Float> = parentComponent.getLeft() to parentComponent.getTop()

        constrain {
            // 根据光标位置调整提示位置
            y = if (parentLocation.second > getHeight() + 15) {
                // 上方有足够空间
                SiblingConstraint(alignOpposite = true) - 5.pixels()
            } else {
                // 放在下方
                SiblingConstraint() + 10.pixels()
            }

            // 自动水平位置
            val windowWidth = Window.of(this@ToolTip).getWidth()
            x = if (parentLocation.first + maxWidth > windowWidth) {
                (windowWidth - maxWidth).pixels()
            } else {
                max(0f, parentLocation.first - (maxWidth - windowWidth) / 2).pixels()
            }
        }
        //显示组件，并设置初始的透明度
        unhide()

        // 淡入动画
        // 淡入动画 - 使用标准的颜色动画
        val originalColor = background.getColor()
        val transparentColor = Color(originalColor.red, originalColor.green, originalColor.blue, 0)
        val targetColor = Color(originalColor.red, originalColor.green, originalColor.blue, 230)

        background.setColor(transparentColor.toConstraint())
        background.animate {
            setColorAnimation(Animations.OUT_EXP, 0.2f, targetColor.toConstraint())
        }
    }

    private fun hideTooltip() {
        if (!visible) return
        visible = false

        // 淡出动画
        val currentColor = background.getColor()
        val transparentColor = Color(currentColor.red, currentColor.green, currentColor.blue, 0)

        background.animate {
            setColorAnimation(Animations.OUT_EXP, 0.2f, transparentColor.toConstraint())

            onComplete {
                hide()
            }
        }
    }
}