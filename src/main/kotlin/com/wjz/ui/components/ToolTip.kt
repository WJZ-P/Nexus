package com.wjz.ui.components

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIWrappedText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import java.awt.Color

class ToolTip(private val text: String) : UIBlock() {
    // 定义内边距
    private val padding = 4f

    // 创建文本组件，宽度为最大宽度减去两侧内边距
    private val textComponent = UIWrappedText(text)

    init {
        // 设置背景的宽度为固定最大宽度，高度根据子组件自适应
        constrain {
            width =  ChildBasedSizeConstraint() + (2 * padding).pixels
            height = ChildBasedSizeConstraint() + (1 * padding).pixels
        }

        // 设置背景颜色为深色半透明
        setColor(Color(20, 20, 20, 230))
        // 添加灰色边框效果
        effect(OutlineEffect(Color(80, 80, 80, 200), 1f))

        // 将文本居中显示
        textComponent constrain {
            x = CenterConstraint()
            y = CenterConstraint()
        } childOf this

        // 初始状态隐藏

    }

}