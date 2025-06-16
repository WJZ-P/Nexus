
```java
/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * 在 Apache License, Version 2.0 ("License") 下获得许可；
 * 除非符合许可，否则你不得使用此文件。
 * 你可以在以下地址获取许可证副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，否则根据许可证分发的软件
 * 是基于“按原样”提供的，没有任何明示或暗示的保证或条件。
 * 请参阅许可证以了解特定语言下的权限和限制。
 */

package net.fabricmc.fabric.api.client.screen.v1;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.client.screen.ScreenExtensions;

/**
 * 包含了与 {@link Screen} (屏幕/界面) 相关的事件，喵~
 *
 * <p>有些事件需要一个屏幕实例才能获取对应的事件实例。
 * 这类事件可以通过它们需要传入一个屏幕实例的方法来识别。
 * 比如 {@link ScreenKeyboardEvents} 和 {@link ScreenMouseEvents} 中的所有事件都需要一个屏幕实例。
 * 之所以采用这种注册模型，是因为一个屏幕在被（重新）初始化时会将其重置为默认状态，
 * 这会撤销掉 Mod 开发者可能对屏幕所做的所有更改。
 * 此外，选择这种设计也是为了减少事件的无效迭代次数，因为 Mod 开发者只需要在确实需要时，
 * 才为特定的屏幕实例注册渲染、逻辑更新(tick)、键盘和鼠标事件。
 *
 * <p>与屏幕交互的主要入口点是它被打开的时候，这个过程由两个事件来标志：
 * 屏幕初始化 {@link ScreenEvents#BEFORE_INIT 之前} 和 {@link ScreenEvents#AFTER_INIT 之后}。
 *
 * @see Screens
 * @see ScreenKeyboardEvents
 * @see ScreenMouseEvents
 */
@Environment(EnvType.CLIENT)
public final class ScreenEvents {
	/**
	 * 这个事件在一个屏幕 {@link Screen#init(MinecraftClient, int, int) 初始化} 为其默认状态之前被调用。
	 * 要注意的是，此时 {@link Screens} 中的某些方法，例如获取屏幕的 {@link Screens#getTextRenderer(Screen) 文本渲染器}，可能还没有被初始化，因此不建议使用它们哦。
	 *
	 * * 你仍然可以使用 {@link ScreenEvents#AFTER_INIT} 来注册像键盘和鼠标这样的事件。
	 *
	 * <p>你可以使用 {@code info} 参数提供的 {@link ScreenExtensions} 来注册 tick、渲染、键盘、鼠标事件，以及添加/移除子元素（包括按钮）的事件。
	 * 举个例子，要在一个类似物品栏的屏幕上注册一个“渲染后”事件，可以使用下面的代码：
	 * <pre>{@code
	 * @Override
	 * public void onInitializeClient() {
	 * ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
	 * // 如果这个屏幕是物品栏类型的屏幕
	 * if (screen instanceof AbstractInventoryScreen) {
	 * // 就为它注册一个“渲染后”事件
	 * ScreenEvents.getAfterRenderEvent(screen).register((matrices, mouseX, mouseY, tickDelta) -> {
	 * ... // 在这里写你的渲染代码
	 * });
	 * }
	 * });
	 * }
	 * }</pre>
	 *
	 * <p>这个事件也表明屏幕尺寸被改变了，因此正在被重新初始化。
	 * 它还可能表明上一个屏幕已经被切换掉了。
	 * @see ScreenEvents#AFTER_INIT
	 */
	public static final Event<BeforeInit> BEFORE_INIT = EventFactory.createArrayBacked(BeforeInit.class, callbacks -> (client, screen, scaledWidth, scaledHeight) -> {
		for (BeforeInit callback : callbacks) {
			callback.beforeInit(client, screen, scaledWidth, scaledHeight);
		}
	});

	/**
	 * 这个事件在一个屏幕 {@link Screen#init(MinecraftClient, int, int) 初始化} 为其默认状态之后被调用。
	 *
	 * <p>通常这个事件被用来在屏幕初始化完毕后对其进行修改。
	 * 诸如更改按钮大小、移除按钮以及添加/移除子元素等操作，都可以安全地使用这个事件来完成。
	 *
	 * <p>举个例子，要给主菜单添加一个按钮，可以使用下面的代码：
	 * <pre>{@code
	 * ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
	 * // 如果这个屏幕是主菜单 (TitleScreen)
	 * if (screen instanceof TitleScreen) {
	 * // 获取屏幕的按钮列表并添加一个我们自己的新按钮
	 * Screens.getButtons(screen).add(new ButtonWidget(...));
	 * }
	 * });
	 * }</pre>
	 *
	 * <p>要注意哦，将一个元素添加到屏幕上，并不会让它自动被 {@link net.minecraft.client.gui.screen.TickableElement tick (逻辑更新)} 或 {@link net.minecraft.client.gui.Drawable drawn (绘制)}。
	 * 除非这个元素是按钮，否则你需要自己在对应的屏幕事件中调用元素的 {@link TickableElement#tick() tick()} 和 {@link net.minecraft.client.gui.Drawable#render(MatrixStack, int, int, float) render(..)} 方法。
	 *
	 * <p>这个事件也可能表明上一个屏幕已经被关闭了。
	 * @see ScreenEvents#BEFORE_INIT
	 */
	public static final Event<AfterInit> AFTER_INIT = EventFactory.createArrayBacked(AfterInit.class, callbacks -> (client, screen, scaledWidth, scaledHeight) -> {
		for (AfterInit callback : callbacks) {
			callback.afterInit(client, screen, scaledWidth, scaledHeight);
		}
	});

	/**
	 * 这个事件在 {@link Screen#removed()} 方法被调用后触发。
	 * 它标志着这个屏幕现在已经被关闭了。
	 *
	 * <p>这个事件通常用来撤销任何特定于屏幕的状态更改，
	 * 比如之前设置了让键盘接收 {@link net.minecraft.client.Keyboard#setRepeatEvents(boolean) 按键重复事件}，或者用来终止由该屏幕启动的线程。
	 * 这个事件可能会在初始化事件 {@link ScreenEvents#BEFORE_INIT} 之前发生，但并不能保证初始化事件会紧随其后被调用哦。
	 */
	public static Event<Remove> remove(Screen screen) {
		Objects.requireNonNull(screen, "屏幕 (Screen) 不能为空");

		return ScreenExtensions.getExtensions(screen).fabric_getRemoveEvent();
	}

	/**
	 * 获取一个在屏幕渲染之前调用的事件。
	 *
	 * @return 该事件
	 */
	public static Event<BeforeRender> beforeRender(Screen screen) {
		Objects.requireNonNull(screen, "屏幕 (Screen) 不能为空");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeRenderEvent();
	}

	/**
	 * 获取一个在屏幕渲染之后调用的事件。
	 *
	 * @return 该事件
	 */
	public static Event<AfterRender> afterRender(Screen screen) {
		Objects.requireNonNull(screen, "屏幕 (Screen) 不能为空");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterRenderEvent();
	}

	/**
	 * 获取一个在屏幕进行逻辑更新 (tick) 之前调用的事件。
	 *
	 * @return 该事件
	 */
	public static Event<BeforeTick> beforeTick(Screen screen) {
		Objects.requireNonNull(screen, "屏幕 (Screen) 不能为空");

		return ScreenExtensions.getExtensions(screen).fabric_getBeforeTickEvent();
	}

	/**
	 * 获取一个在屏幕进行逻辑更新 (tick) 之后调用的事件。
	 *
	 * @return 该事件
	 */
	public static Event<AfterTick> afterTick(Screen screen) {
		Objects.requireNonNull(screen, "屏幕 (Screen) 不能为空");

		return ScreenExtensions.getExtensions(screen).fabric_getAfterTickEvent();
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeInit {
		/**
		 * 在屏幕初始化之前调用。
		 */
		void beforeInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterInit {
		/**
		 * 在屏幕初始化之后调用。
		 */
		void afterInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface Remove {
		/**
		 * 当屏幕被移除时调用。
		 */
		void onRemove(Screen screen);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeRender {
		/**
		 * 在屏幕渲染之前调用。
		 */
		void beforeRender(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
	}



	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterRender {
		/**
		 * 在屏幕渲染之后调用。
		 */
		void afterRender(Screen screen, MatrixStack matrices, int mouseX, int mouseY, float tickDelta);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface BeforeTick {
		/**
		 * 在屏幕进行逻辑更新 (tick) 之前调用。
		 */
		void beforeTick(Screen screen);
	}

	@Environment(EnvType.CLIENT)
	@FunctionalInterface
	public interface AfterTick {
		/**
		 * 在屏幕进行逻辑更新 (tick) 之后调用。
		 */
		void afterTick(Screen screen);
	}

	private ScreenEvents() {
	}
}
```
