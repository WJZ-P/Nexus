package com.wjz.ui.components.base

import com.wjz.ui.components.ToolTip
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.transitions.RecursiveFadeInTransition
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
    text: String?,
    toolTipText: String? = null,
    private val container: UIContainer? = null,
    private val svgName: String? = null,
    private var onClick: (() -> Unit)? = null,
) : UIBlock() {

    // 使用Minecraft风格的灰黑色主题
    private val originalColor = Color(50, 50, 50, 230) // 浅灰色
    private val hoverColor = Color(70, 70, 70, 230)    // 悬停时稍微亮
    private val pressedColor = Color(30, 30, 30, 230)  // 按下时更暗的灰色
    private val borderNormal = Color.BLACK
    private val borderHover = Color.WHITE
    private var timerId: Int? = null;
    private val outlineEffect = OutlineEffect(borderNormal, 1f)// 动态边框效果

    //设置toolTip
    private val toolTip: ToolTip? = toolTipText?.let { ToolTip(it) }

    //设置text
    private val textComponent: UIText? = text?.let {
        UIText(text).constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = Color.WHITE.toConstraint()
        }
    }

    //设置svg
    private val svgComponent: SVGComponent? = svgName?.let {
        println("当前的地址是icon/${it}.svg")
        //尝试创建一个URL路径

        val svgComponent= SVGComponent.ofResource("textures/icon/${it}.svg") constrain {
            x = 2.pixels()
            y = SiblingConstraint(padding = 2f)
            width = 50.pixels()
            height = 50.pixels()
        }
        return svgComponent;
    }

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

        // 添加文本到组件中
        textComponent?.childOf(this)
        //添加图标
        svgComponent?.childOf(this)

        //延后添加toolTip到container中，否则会报错。
        Window.Companion.enqueueRenderOperation { toolTip?.childOf(container!!)?.hide() }

        // 设置按钮交互效果
        setupInteractions()
    }

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
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, pressedColor.toConstraint())
            }
            println("点击了按钮")
            onClick?.invoke()
        }

        onMouseRelease {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, originalColor.toConstraint())
            }
        }

        // 悬停效果 - 使用更平滑的动画
        onMouseEnter {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, hoverColor.toConstraint())
            }
            // 显示边框
            outlineEffect.color = borderHover
            //加一个延时实现
            timerId = startDelay(400) {
                toolTip?.let {
                    Window.enqueueRenderOperation {
                        it.unhide();
                        positionToolTip(it)
                        RecursiveFadeInTransition(0.2f, Animations.OUT_CIRCULAR).transition(it)
                    }
                }
            }

        }


        // 离开效果
        onMouseLeave {
            animate {
                setColorAnimation(Animations.OUT_CIRCULAR, 0.25f, originalColor.toConstraint())
            }
            // 默认边框
            outlineEffect.color = borderNormal
            Window.enqueueRenderOperation { toolTip?.hide() }
            if (timerId != null) {
                stopDelay(timerId!!);timerId = null
            }
        }
    }

    // 动态调整 ToolTip 位置，确保不溢出屏幕
    private fun positionToolTip(toolTip: ToolTip) {
        // 获取按钮位置和大小
        val buttonX = getLeft()
        val buttonY = getTop()
        val buttonWidth = getWidth()
        val buttonHeight = getHeight()

        // 获取屏幕尺寸
        val screenWidth = Window.of(this).getWidth()
        val screenHeight = Window.of(this).getHeight()

        // 默认位置：按钮上方，水平居中
        var toolTipX = buttonX + (buttonWidth / 2) - (toolTip.getWidth() / 2)
        var toolTipY = buttonY - toolTip.getHeight() - 5 // 上方，留 5 像素间隙

        // 如果溢出顶部，尝试放置在按钮下方
        if (toolTipY < 0) {
            toolTipY = buttonY + buttonHeight + 5 // 移到按钮下方
        }
        // 如果下方也溢出，贴近屏幕底部
        if (toolTipY + toolTip.getHeight() > screenHeight) {
            toolTipY = screenHeight - toolTip.getHeight() - 5
        }
        // 如果溢出右侧，贴近屏幕右边
        if (toolTipX + toolTip.getWidth() > screenWidth) {
            toolTipX = screenWidth - toolTip.getWidth() - 5
        }
        // 如果溢出左侧，贴近屏幕左边
        if (toolTipX < 0) {
            toolTipX = 5F
        }

        // 设置 ToolTip 位置
        toolTip.setX(toolTipX.pixels())
        toolTip.setY(toolTipY.pixels())
    }

    override fun draw(matrixStack: UMatrixStack) {
        super.draw(matrixStack)

        val left = getLeft().toDouble()
        val top = getTop().toDouble()
        val right = getRight().toDouble()
        val bottom = getBottom().toDouble()


        // 定义颜色
        val baseColor = Color(50, 50, 50, 150)      // 深灰色主体，带透明度
        val highlightColor = Color(70, 70, 70, 220) // 稍亮的灰色高亮
        val shadowColor = Color(30, 30, 30, 220)    // 稍暗的灰色阴影

        // 调用 drawButton 函数绘制按钮
        drawButton(
            matrixStack,
            left, top, right, bottom,
            baseColor, highlightColor, shadowColor, outlineEffect.color,
            true, true, true, true  // 绘制所有四边的轮廓
        )

    }


    companion object {
        private val PIPELINE = URenderPipeline.builderWithDefaultShader(
            "nexus:base_button",
            UGraphics.DrawMode.QUADS,
            UGraphics.CommonVertexFormats.POSITION_COLOR,
        ).apply {
            blendState = ALPHA
            depthTest = URenderPipeline.DepthTest.Always
        }.build()

        private val PIPELINE_TEXTURED = URenderPipeline.builderWithDefaultShader(
            "nexus:base_button_textured",
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
                // 基础颜色
//                pos(matrixStack, left, top, 0.0).color(baseColor).endVertex()
//                pos(matrixStack, left, bottom, 0.0).color(baseColor).endVertex()
//                pos(matrixStack, right, bottom, 0.0).color(baseColor).endVertex()
//                pos(matrixStack, right, top, 0.0).color(baseColor).endVertex()

                // 左侧高光颜色
                pos(matrixStack, left, top, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left, bottom, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left + 1.0, bottom, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left + 1.0, top, 0.0).color(highlightColor).endVertex()
                // 上方高光
                pos(matrixStack, left + 1.0, top, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, left + 1.0, top + 1.0, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, right, top + 1.0, 0.0).color(highlightColor).endVertex()
                pos(matrixStack, right, top, 0.0).color(highlightColor).endVertex()

                // 右侧阴影
                pos(matrixStack, right, bottom, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right, top, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right - 1.0, top, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right - 1.0, bottom, 0.0).color(shadowColor).endVertex()
                // 底部阴影
                pos(matrixStack, right - 1.0, bottom, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, right - 1.0, bottom - 2.0, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, left, bottom - 2.0, 0.0).color(shadowColor).endVertex()
                pos(matrixStack, left, bottom, 0.0).color(shadowColor).endVertex()

                // 绘制Outline
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
