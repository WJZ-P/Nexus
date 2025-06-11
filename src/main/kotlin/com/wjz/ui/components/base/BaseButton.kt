package com.wjz.ui.components.base

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

open class BaseButton(
    text: String = "Button",
    // 使用Minecraft风格的颜色
    private val defaultColor: Color = Color(50, 85, 0, 200), // 深绿色
    private val hoverColor: Color = Color(105, 170, 0, 220), // 亮绿色
    private val pressedColor: Color = Color(30, 50, 0, 220), // 暗绿色
    private val textColor: Color = Color(240, 240, 240), // 淡灰色
    private val disabledColor: Color = Color(70, 70, 70, 150), // 暗灰色
    private val cornerRadius: Float = 3f, // 轻微圆角
    private val outlineWidth: Float = 1f
) : UIBlock() {

    private val buttonText = UIText(text).constrain {
        color = textColor.toConstraint()
        textScale = 2.pixel
        x = CenterConstraint()
        y = CenterConstraint()
    }

    private var enabled = true

    init {
        constrain {
            width = 100.pixels().coerceAtLeast(ChildBasedSizeConstraint(padding = 8f))
            height = 20.pixels().coerceAtLeast(ChildBasedSizeConstraint(padding = 3f))
            radius = cornerRadius.pixel
            color = defaultColor.toConstraint()
        }.onMouseEnter {
            if (enabled) setColor(hoverColor)
        }.onMouseLeave {
            if (enabled) setColor(defaultColor)
        }.onMouseClick {
            if (enabled) {
                setColor(pressedColor)
                animate { setColorAnimation(Animations.OUT_EXP, 0.3f, hoverColor.toConstraint()) }
            }
        }

        // 更精细的描边效果
        enableEffect(OutlineEffect(Color(20, 20, 20, 220), outlineWidth))

    }

    //更新按钮文本
    fun setText(newText: String) = apply {
        buttonText.setText(newText)
    }

    // 添加悬停时的工具提示
    fun withTooltip(text: String) = apply {
        this.onMouseEnter {
            // 这里可以添加自定义tooltip组件
        }
    }

    //初始化后执行的操作
    override fun afterInitialization() {
        super.afterInitialization()
        buttonText childOf this
    }

}