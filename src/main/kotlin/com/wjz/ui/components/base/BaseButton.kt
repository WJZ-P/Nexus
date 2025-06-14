package com.wjz.ui.components.base

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import java.awt.Color

class BaseButton(
    text: String, initialWidth: Float = 60f, initialHeight: Float = 20f, private var onClick: (() -> Unit)? = null
) : UIBlock(Color.gray) {

    // 保存原始颜色用于动画效果
    private val originalColor = Color.decode("#66ccff")

    // 内部文本组件
    private val textComponent: UIText

    init {
        // 设置按钮基本约束
        constrain {
            color = originalColor.toConstraint()
            radius = 2.pixels()
        }

        // 添加文本
        textComponent = UIText(text).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = Color.WHITE.toConstraint()
        } childOf this

        // 设置按钮交互效果
        setupInteractions()
    }

    // 按钮尺寸更新方法
    fun setSize(width: Float, height: Float) = apply {
        constrain {
            this.width = width.pixels()
            this.height = height.pixels()
        }
    }

    // 更新按钮文本
    fun setText(text: String) = apply {
        textComponent.setText(text)
    }

    // 获取当前文本
    fun getText() = textComponent.getText()

    // 自定义点击监听器
    fun setClickListener(listener: () -> Unit) = apply {
        this.onClick = listener
    }

    // 内部方法：设置按钮交互效果
    private fun setupInteractions() {
        // 点击效果：模拟按钮按下
        onMouseClick {
            animate {
                setColorAnimation(Animations.OUT_EXP, 0.1f, Color.GRAY.toConstraint())
                setColorAnimation(Animations.OUT_EXP, 0.1f, originalColor.toConstraint(), delay = 0.1f)
            }
            onClick?.invoke() // 执行回调
        }

        // 悬停效果
        onMouseEnter {
            animate { setColorAnimation(Animations.OUT_EXP, 0.85f, Color.DARK_GRAY.toConstraint()) }
        }

        // 离开效果
        onMouseLeave {
            animate { setColorAnimation(Animations.OUT_EXP, 0.2f, originalColor.toConstraint()) }
        }
    }
}
