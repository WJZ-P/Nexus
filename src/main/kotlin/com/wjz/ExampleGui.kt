package com.wjz

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.events.UIClickEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.awt.Color
import gg.essential.elementa.layoutdsl.Modifier
import gg.essential.elementa.layoutdsl.gradient

class ExampleGui : WindowScreen(ElementaVersion.V8) {
    val logger = LogManager.getLogger("wjz-nexus")

    init {
        //创建一个"新建便利贴"按钮的背景块
        val createNoteButton = UIBlock(Color.decode("#66ccff")).constrain {
            //如果直接写数值，就是距离窗口左上角的距离
            x = 5.pixels()
            y = 5.pixels()

            //元素总宽度
            width = ChildBasedSizeConstraint() + 4.pixels()
            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        }.onMouseClick {
            //点击的时候创建新便利贴
            StickyNote() childOf window
        }.onMouseEnter {
            //鼠标悬停的时候，来点特效家人们
            this.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 120).toConstraint(), 0f)
            }
        }.onMouseLeave { // 鼠标离开时恢复原色
            animate {
                setColorAnimation(
                    Animations.OUT_EXP,
                    0.5f,
                    Color(207, 207, 196).toConstraint()
                )
            }
        } childOf window

        // 按钮文字
        UIText("Create notes!", shadow = false).constrain {
            // 左侧2像素边距（总边距4像素/2）
            x = 2.pixels()
            // 垂直居中
            y = CenterConstraint()
            // 文字放大2倍
            textScale = 2.pixels()
            // 深绿色文字
            color = Color.GREEN.darker().toConstraint()
        } childOf createNoteButton

        // 渐变背景
        Modifier.gradient(top = Color(0x091323), Color.BLACK).applyToComponent(window)
    }

    //自定义一个便利贴组件
    class StickyNote : UIBlock(Color.decode("#9999ff")) {
        private val logger: Logger = LogManager.getLogger("wjz-nexus")

        //记录拖拽状态
        private var isDragging = false
        private var dragOffset = 0f to 0f
        private val textArea: UITextInput

        //private val textArea: UITextInput

        init {
            constrain {
                //默认是居中
                x = CenterConstraint()
                y = CenterConstraint()
                //控制默认尺寸
                width = 150.pixels()
                height = 100.pixels()
            }

            onMouseClick {
                //点击时，置顶展示，方案就是先移除再插入，就在顶部了
                parent.removeChild(this)
                parent.addChild(this)
            }

            //黄色顶部拖拽条
            val topBar = UIBlock(Color.YELLOW).constrain {
                x = 1.pixel()
                y = 1.pixel()
                //宽度填满父组件
                width = 100.percent() - 2.pixels()
                //高度固定
                height = 30.pixels()
            }.onMouseClick { event: UIClickEvent ->
                isDragging = true
                dragOffset = event.absoluteX to event.absoluteY
            }.onMouseRelease {
                isDragging = false
            }.onMouseDrag { mouseX, mouseY, mouseButton ->
                //处理拖拽移动
                if (!isDragging) return@onMouseDrag

                val absoluteX = mouseX + getLeft()
                val absoluteY = mouseY + getTop()
                val (deltaX, deltaY) = dragOffset
                //更新拖拽offset
                dragOffset = absoluteX to absoluteY

                val newX = this@StickyNote.getLeft() + deltaX
                val newY = this@StickyNote.getTop() + deltaY
                this@StickyNote.setX(newX.pixels())
                this@StickyNote.setY(newY.pixels)
            } childOf this

            //再创建一个删除按钮，X文字
            UIText("X", shadow = false).constrain {
                //右侧对齐
                x = 4.pixels(alignOpposite = true)//反着排，就是靠近右边
                y = CenterConstraint()
                color = Color.BLACK.toConstraint()
                textScale = 2.pixels
            }.onMouseEnter {
                //悬停的时候变成红色
                animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color.RED.toConstraint())
                }
            }.onMouseLeave {
                animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5F, Color.BLACK.toConstraint())
                }
            }.onMouseClick { event: UIClickEvent ->
                //点击删除元素
                this@StickyNote.parent.removeChild(this@StickyNote)
                event.stopPropagation()//停止事件冒泡
            } childOf topBar

            //设置一个输入框的背景
            val textHolder = UIBlock(Color(80, 80, 80)).constrain {
                x = 1.pixel
                y = SiblingConstraint()
                width = RelativeConstraint(1f) - 2.pixels
                //高度填满剩余空间
                height = FillConstraint()
            } childOf this
            //启用裁剪效果来防止文本溢出
            textHolder effect ScissorEffect()

            //创建文本输入框了
            textArea = (UITextInput(placeholder = "请输入文字...").constrain {
                x = 2.pixel
                y = 2.pixel
                height = FillConstraint() - 2.pixel
            }.onMouseClick {
                //点击获取焦点
                grabWindowFocus()
                logger.info("文本输入框被点击了")
            } childOf textHolder) as UITextInput
        }

    }
}