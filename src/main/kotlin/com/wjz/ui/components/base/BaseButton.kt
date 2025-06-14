package com.wjz.ui.components.base

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

class BaseButton(
    text: String, initialWidth: Float = 60f, initialHeight: Float = 20f, private var onClick: (() -> Unit)? = null
) : UIBlock() {

    // 使用Minecraft风格的灰黑色主题
    private val originalColor = Color(50, 50, 50, 200) // 浅灰色
    private val hoverColor = Color(70, 70, 70, 220)    // 悬停时稍微亮
    private val pressedColor = Color(30, 30, 30, 220)  // 按下时更暗的灰色
    private val borderNormal = Color.BLACK
    private val borderHover = Color.WHITE

    // 动态边框效果
    private val outlineEffect = OutlineEffect(borderNormal, 1f)

    // 内部文本组件
    private val textComponent: UIText

    init {
        // 设置按钮基本约束
        constrain {
            color = originalColor.toConstraint()
            radius = 10.pixels()
            width = ChildBasedSizeConstraint(25f)
            height = ChildBasedSizeConstraint(25f)
        }

        // 添加白色边框效果
        enableEffect(outlineEffect)

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
                setColorAnimation(Animations.OUT_ELASTIC, 0.15f, pressedColor.toConstraint())
                setColorAnimation(Animations.OUT_BOUNCE, 0.3f, originalColor.toConstraint(), delay = 0.15f)
            }
            // 添加边框闪烁效果
            outlineEffect.color = Color.WHITE
            animate {
                // 边框淡出
                Thread {
                    Thread.sleep(100)
                    outlineEffect.color = Color(150, 150, 150, 0)
                }.start()
            }
            onClick?.invoke()
        }

        // 悬停效果 - 使用更平滑的动画
        onMouseEnter {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, hoverColor.toConstraint())
            }
            // 显示边框
            outlineEffect.color = borderHover
        }

        // 离开效果
        onMouseLeave {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, originalColor.toConstraint())
            }
            // 隐藏边框
            outlineEffect.color = borderNormal
        }
    }
}
