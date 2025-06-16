package com.wjz.ui.components.base

import com.wjz.ui.components.ToolTip
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.MaxConstraint
import gg.essential.elementa.constraints.MinConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack
import gg.essential.universal.render.URenderPipeline
import gg.essential.universal.shader.BlendState.Companion.ALPHA
import gg.essential.universal.vertex.UBufferBuilder
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.sound.SoundEvents
import java.awt.Color

class BaseButton(
    text: String,
    private var onClick: (() -> Unit)? = null
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

    // ToolTip 相关属性
    private var toolTipText: String? = null
    private var toolTip: ToolTip? = null

    init {
        // 设置按钮基本约束
        constrain {
            color = originalColor.toConstraint()
            radius = 10.pixels()
            width = MaxConstraint(ChildBasedSizeConstraint() + 10.pixel, 18.pixel)
            height = ChildBasedSizeConstraint() + 8.pixel
            textScale = 3.pixel
        }

        // 添加边框效果
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


    // 设置 ToolTip 文本
    fun setToolTipText(text: String) = apply {
        this.toolTipText = text
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
            //1.播放音效
            MinecraftClient.getInstance().soundManager.play(
                PositionedSoundInstance.master(
                    SoundEvents.UI_BUTTON_CLICK,
                    1F
                )
            );
            animate {
//                setWidthAnimation(
//                    Animations.OUT_EXP, // 使用一个缓出的动画曲线
//                    0.1f,                 // 动画时长，要非常快！
//                    constraints.width - 0.pixel
//                )
//                setHeightAnimation(
//                    Animations.OUT_EXP, // 使用一个缓出的动画曲线
//                    0.1f,                 // 动画时长，要非常快！
//                    constraints.height - 1.pixel
//                )
            }
            println("点击了按钮")
            onClick?.invoke()
        }

        onMouseRelease {
            animate {
//                setWidthAnimation(
//                    Animations.OUT_EXP, // 使用一个缓出的动画曲线
//                    0.1f,                 // 动画时长，要非常快！
//                    constraints.width + 0.pixel
//                )
//                setHeightAnimation(
//                    Animations.OUT_EXP, // 使用一个缓出的动画曲线
//                    0.1f,                 // 动画时长，要非常快！
//                    constraints.height + 1.pixel
//                )
            }
        }

        // 悬停效果 - 使用更平滑的动画
        onMouseEnter {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, hoverColor.toConstraint())
            }
            // 显示边框
            outlineEffect.color = borderHover

            // 动态挂载 ToolTip
            toolTipText?.let { text ->
                if (toolTip == null) {
                    toolTip = ToolTip(text).constrain {
                        x = getLeft().pixels()
                        y = (getTop() - 25f).pixels() // 显示在按钮上方
                    } childOf parent
                }
                toolTip?.showTooltip()
            }
        }

        // 离开效果
        onMouseLeave {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, originalColor.toConstraint())
            }
            // 默认边框
            outlineEffect.color = borderNormal
            // 隐藏 ToolTip
            toolTip?.hideTooltip()
        }
    }

    companion object {
        private val PIPELINE = URenderPipeline.builderWithDefaultShader(
            "essential:menu_button",
            UGraphics.DrawMode.QUADS,
            UGraphics.CommonVertexFormats.POSITION_COLOR,
        ).apply {
            blendState = ALPHA
            depthTest = URenderPipeline.DepthTest.Always
        }.build()

        private val PIPELINE_TEXTURED = URenderPipeline.builderWithDefaultShader(
            "essential:menu_button_textured",
            UGraphics.DrawMode.QUADS,
            UGraphics.CommonVertexFormats.POSITION_TEXTURE_COLOR,
        ).apply {
            blendState = ALPHA
            depthTest = URenderPipeline.DepthTest.Always
        }.build()

        fun drawButton(
            matrixStack: UMatrixStack,
            left: Double,
            top: Double,
            right: Double,
            bottom: Double,
            baseColor: Color,
            highlightColor: Color,
            shadowColor: Color,
            outlineColor: Color,
            hasTop: Boolean,
            hasBottom: Boolean,
            hasLeft: Boolean,
            hasRight: Boolean,
        ) {
            UBufferBuilder.create(UGraphics.DrawMode.QUADS, UGraphics.CommonVertexFormats.POSITION_COLOR).apply {
                // Base
                pos(matrixStack, left, top, 0.0).color(baseColor).endVertex()
                pos(matrixStack, left, bottom, 0.0).color(baseColor).endVertex()
                pos(matrixStack, right, bottom, 0.0).color(baseColor).endVertex()
                pos(matrixStack, right, top, 0.0).color(baseColor).endVertex()

                // Highlight left
                pos(matrixStack, left, top, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left, bottom, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left + 1.0, bottom, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left + 1.0, top, 0.0).color(highlightColor).endVertex()
                // Highlight top
                pos(matrixStack, left + 1.0, top, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left + 1.0, top + 1.0, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, right, top + 1.0, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, right, top, 0.0).color(highlightColor).endVertex()

                // Shadow right
                pos(matrixStack, right, bottom, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right, top, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right - 1.0, top, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right - 1.0, bottom, 0.0).color(shadowColor).endVertex()
                // Shadow bottom
                pos(matrixStack, right - 1.0, bottom, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right - 1.0, bottom - 2.0, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, left, bottom - 2.0, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, left, bottom, 0.0).color(shadowColor).endVertex()

                // Outline
                drawOutline(matrixStack, left, top, right, bottom, outlineColor, hasTop, hasBottom, hasLeft, hasRight)
            }.build()?.drawAndClose(PIPELINE)
        }

        private fun UBufferBuilder.drawOutline(
            matrixStack: UMatrixStack,
            left: Double,
            top: Double,
            right: Double,
            bottom: Double,
            outlineColor: Color,
            hasTop: Boolean,
            hasBottom: Boolean,
            hasLeft: Boolean,
            hasRight: Boolean,
        ) {
            if (hasTop) {
                pos(matrixStack, left - if (hasLeft) 1.0 else 0.0, top - 1.0, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, left - if (hasLeft) 1.0 else 0.0, top, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right + if (hasRight) 1.0 else 0.0, top, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right + if (hasRight) 1.0 else 0.0, top - 1.0, 0.0).color(outlineColor).endVertex()
            }
            if (hasBottom) {
                pos(matrixStack, left - if (hasLeft) 1.0 else 0.0, bottom, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, left - if (hasLeft) 1.0 else 0.0, bottom + 1.0, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right + if (hasRight) 1.0 else 0.0, bottom + 1.0, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right + if (hasRight) 1.0 else 0.0, bottom, 0.0).color(outlineColor).endVertex()
            }
            if (hasLeft) {
                pos(matrixStack, left - 1.0, top, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, left - 1.0, bottom, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, left, bottom, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, left, top, 0.0).color(outlineColor).endVertex()
            }
            if (hasRight) {
                pos(matrixStack, right, top, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right, bottom, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right + 1.0, bottom, 0.0).color(outlineColor).endVertex()
                pos(matrixStack, right + 1.0, top, 0.0).color(outlineColor).endVertex()
            }
        }

    }
}
