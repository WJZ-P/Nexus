package com.wjz.ui

import com.wjz.ui.components.base.BaseButton
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.dsl.*

class MainMenuScreen : WindowScreen(ElementaVersion.V8, drawDefaultBackground = false) {
    init {
        //创建一个检查器
        Inspector(window).constrain {
            x = 10.pixel(true)
            y = 10.pixel()
        } childOf window

        BaseButton("嘻嘻哈哈哇嘎哇嘎") constrain {
            x = 50.percentOfWindow
            y = 70.percentOfWindow
        } childOf window
    }
}