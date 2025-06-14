package com.wjz.guiExamples

import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.input.UITextInput
import gg.essential.elementa.components.inspector.Inspector
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import java.awt.Color

class ExampleGui : WindowScreen(ElementaVersion.V8) {

    init {
        //创建一个检查器
        Inspector(window).constrain {
            x = 10.pixel(true)
            y = 10.pixel()
        } childOf window

        val createNoteButton = UIBlock(Color(24, 183, 231)).constrain {
            x = 2.pixels()
            y = 2.pixels()

            width = ChildBasedSizeConstraint() + 4.pixels()

            height = ChildBasedMaxSizeConstraint() + 4.pixels()
        }.onMouseClick {
            StickyNote() childOf window
        }.onMouseEnter {

            this.animate {

                setColorAnimation(Animations.OUT_EXP, 0.5f, Color(120, 120, 100).toConstraint(), 0f)
            }
        }.onMouseLeave {

            animate {
                setColorAnimation(
                    Animations.OUT_EXP,
                    0.5f,
                    // All parameters are the same, except for the target color.
                    Color(207, 207, 196).toConstraint()
                )
            }
        } childOf window

        UIText("Create notes!", shadow = false).constrain {
            x = 2.pixels()
            y = CenterConstraint()
            textScale = 2.pixels()
            color = Color.GREEN.darker().toConstraint()
        } childOf createNoteButton

        //Modifier.gradient(top = Color(0x091323), Color.BLACK).applyToComponent(window)
    }

    // Now, since we want to create a bunch of sticky notes, it makes sense
    // to have them be a custom UIComponent. We extend from [UIBlock] in order
    // to draw a background color, which will be used as an outline for the sticky note.
    // Instead, we want to delegate drawing to the normal hierarchy based drawing,
    // rendering all of our children.
    class StickyNote : UIBlock(Color.BLACK) {
        // We will need to hold some state about this component.
        // For example, we will need to know if the component is actively being dragged.
        private var isDragging: Boolean = false

        // In addition, we need to know the offset position into the note
        // where the drag began.
        private var dragOffset: Pair<Float, Float> = 0f to 0f

        private val textArea: UITextInput

        init {
            // For clarity, we will start by constraining the sticky note component as a whole.
            constrain {
                // Start our note in the middle of the screen, as we're assuming
                // that we will be a child of [Window]
                x = CenterConstraint()
                y = CenterConstraint()

                // Default our sticky note's size to be rectangular.
                // This will be changed when the user resizes the note.
                width = 150.pixels()
                height = 100.pixels()
            }

            onMouseClick {
                // Finally, we also want to bring this sticky note to the front of all the other notes
                // when clicked. To do so, we need to first remove it from its parent, and then
                // re-add it, which will place it on top. We want to do all of this to the sticky note as a whole.
                // Note: Calling [removeChild] will not un-set the [parent] property,
                // which is what allows us to refer to it again in the following line.
                parent.removeChild(this)
                parent.addChild(this)
            }

            // Now, we want to have our sticky notes have a yellow top bar
            // that will be what the user drags, and holds the delete button.
            // We want this block to be a child of [this] because it will
            // be contained inside our sticky note.
            val topBar = UIBlock(Color.YELLOW).constrain {
                // We want our block to start in the top-left corner
                // of this sticky note component, but we want to leave a bit of room,
                // so we can let the sticky note's background show,
                // giving us an outline.
                x = 1.pixel()
                y = 1.pixel()

                // We want our yellow top bar to take up the entire width of the sticky note,
                // so we use a [RelativeConstraint]. A relative constraint describes what percentage
                // of its parent's width/height the component should take up. In our case,
                // we want it to fill 100% of the parent's width, so we pass 100 to the
                // extension function `percent`.
                // We can't simply say 150 pixels (the sticky note's default width) because
                // when the user resizes the note, that width might change, hence the use of
                // the "variable" constraint (one that adapts dynamically to the current layout).
                // Of course, we also need to account for the 1 pixel outline
                // (we use 2 pixels to account for our X position being 1), so we will subtract that from our width.
                width = 100.percent() - 2.pixels()

                // As for the height of the top bar, we don't want it to rely on the sticky note's
                // height, because that would look awkward. Instead, a constant height is a lot
                // more intuitive for the user. The choice of 24 pixels here isn't completely arbitrary,
                // the 'X' button to delete the note will likely be 18 pixels, allowing for 3 pixels
                // of padding on this bar.
                height = 24.pixels()
            }.onMouseClick { event ->
                // Now, we need to modify our state to say that we are actively dragging
                // this note around.
                isDragging = true

                // Here we are storing the absolute position of our mouse.
                // The UIClickEvent provides us the ability to directly access those properties.
                // It also offers [relativeX] and [relativeY] properties if necessary.
                dragOffset = event.absoluteX to event.absoluteY
            }.onMouseRelease {
                // As opposed to [onMouseClick], [onMouseRelease] will be called on all components
                // no matter where the mouse currently is.

                // When we release the mouse, we need to update our state again to reflect
                // the fact that we are no longer dragging.
                isDragging = false
            }.onMouseDrag { mouseX, mouseY, _ ->
                // This event is called whenever the mouse is moved while being held,
                // which means it's perfect for updating this sticky note's current position.

                // If we're not actively being dragged, we definitely
                // do not want to move!
                if (!isDragging) return@onMouseDrag

                // We begin by calculating the mouse's absolute position.
                // We can access the bounding box of any component with the [getLeft], [getTop], etc. functions
                val absoluteX = mouseX + getLeft()
                val absoluteY = mouseY + getTop()

                // We then need to find the change in x & y since our most recently
                // stored position, which will be our delta.
                val deltaX = absoluteX - dragOffset.first
                val deltaY = absoluteY - dragOffset.second

                // Make sure to update the currently stored position to our new position.
                dragOffset = absoluteX to absoluteY

                // To find the new target position, we need to take the sticky note's current position
                // and add the delta we calculated.
                // Note: We want to call these methods on the parent sticky note.
                // If we were to change the x & y constraint on the current [this]
                // value, we would change the yellow top bar's relative position
                // rather than the sticky note's position as a whole.
                val newX = this@StickyNote.getLeft() + deltaX
                val newY = this@StickyNote.getTop() + deltaY

                // Finally, we simply need to update the sticky note's position.
                // Here we are directly setting the x & y position of the note rather than using
                // an animation. Using an animation would look awkward and clunky instead
                // of fast and responsive, so we will instantly update these values.
                this@StickyNote.setX(newX.pixels())
                this@StickyNote.setY(newY.pixels())
            } childOf this

            // Now we need to create a "delete" button. For simplicity,
            // this is a simple text component with the text "X", but this could
            // be exchanged for a [UIImage] with ease.
            // We'll also disable the shadow on this text.
            UIText("X", shadow = false).constrain {
                // We want our delete button to be in the top right of the sticky note top bar,
                // so we need to align it with the right side of its parent.
                // Luckily there is an easy way to do this with a [PixelConstraint] by simply
                // setting [alignOpposite] to be true. This means that there will be a 4 pixel offset
                // from the right side of [topBar] to the left side of this component itself.
                x = 4.pixels(alignOpposite = true)

                // If you recall, we set up the top bar's height to allow for a 3 pixel padding
                // on the top and bottom of this button, so we'll utilize [CenterConstraint] to
                // take advantage of that automatically.
                y = CenterConstraint()

                // We'll default our color to black.
                color = Color.BLACK.toConstraint()

                // We also want to make this an easily recognizable button, so we'll scale our text
                // by two.
                textScale = 2.pixels()
            }.onMouseEnter {
                animate {
                    // When we hover this button, we'll make the text fully red to show that this is
                    // destructive.
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color.RED.toConstraint())
                }
            }.onMouseLeave {
                animate {
                    // And when we unhover this button, we'll move the text back to its original black color.
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color.BLACK.toConstraint())
                }
            }.onMouseClick { event ->
                // When we click the delete button, we want to remove the sticky note in its
                // entirety from its parent, removing it from the hierarchy and essentially deleting it,
                // as it is no longer referenced anywhere.
                this@StickyNote.parent.removeChild(this@StickyNote)

                // Don't continue passing this event up the component hierarchy because
                // sticky notes will always try to re-add themselves to the window when clicked.
                event.stopPropagation()
            } childOf topBar

            // Now we need to make the background of the area that will actually hold
            // the sticky note's text area. A darkish gray color will suffice.
            val textHolder = UIBlock(Color(80, 80, 80)).constrain {
                // We want this to also start at the far left of the parent.
                x = 1.pixel()

                // However, we want this area to begin on the Y axis directly
                // after the top-bar has ended. In this specific case we know how tall the top bar
                // happens to be, so we could hardcode that value, but again, that's a brittle solution.
                // Instead, there is another constraint well suited to this problem, the [SiblingConstraint].
                // A sibling constraint will position this component directly after the end of it's directly
                // previous sibling. "Directly previous sibling" means the most recent component to have been
                // added to the same parent at the time of this component's [childOf] call.
                // In our case, that is the top bar.
                y = SiblingConstraint()

                // Again, we want this box to take the entire width of the sticky note,
                // leaving a pixel for an outline (we use 2 pixels to account for our X position being 1).
                width = RelativeConstraint(1f) - 2.pixels()

                // This background block needs to be whatever height will take up all the remaining space
                // in the sticky note (i.e. this area needs to go all the way to the bottom of the note).
                // In this instance, a [RelativeConstraint] will not suffice because it will not account
                // for our current Y position. In our case, we start after the top bar, so the entire height
                // of our parent would make us taller than we need. This is where [FillConstraint] comes in.
                // It will make sure we are the correct height to go exactly to the bottom of our parent,
                // the sticky note.
                height = FillConstraint()
            } childOf this

            // Currently, text input areas will simply overflow if they have too much text inside them.
            // In future Elementa releases this will likely be changed, but for now there are a couple of
            // workarounds. For a production product, the best solution would be to use a [ScrollComponent]
            // to allow the user to scroll through all the text in the input, but a simpler,
            // albeit worse, solution is to simply hide any overflowing text. The easiest way to do this is
            // to use an effect that will cut off any rendering that happens outside a component's boundaries.
            // Now, this sounds complicated, but in reality it is a simple concept. The [ScissorEffect],
            // when applied to a component, will stop all of that component's children from rendering
            // outside said component's bounding box (x, y, x + width, y + width).
            // In this case, this means that textHolder's child, the text area, will be cut off
            // from rendering any text that goes past the end of the sticky note.
            textHolder effect ScissorEffect()

            // Next, we need to actually add the text input area so the user can type their notes!
            // We can provide the text input component with a blurb of placeholder text so the
            // user knows they can type in this area. By default, a text input component
            // will also wrap its text, so we don't need to worry about setting that up.
            // We want this text input component to be a child of the [textHolder] component.
            textArea = (UITextInput(placeholder = "Enter your note...").constrain {
                // We want to occupy all the text holder's area, but leave
                // 2 pixels of padding on all sides.
                x = 2.pixels()
                y = 2.pixels()
                // As we've seen before, we could have used a [RelativeConstraint] here
                // and subtracted by 4 pixels, but this way, using a [FillConstraint] is a little easier to grasp.
                height = FillConstraint() - 2.pixels()
                width = FillConstraint()
            }.onMouseClick {
                // When we click inside of this text area, we want to activate it. To do so, we need to make sure
                // that this text input has the Window's focus. This means that the Window will route keyboard
                // events to our component while it is focused. Later, when we click away from this text input area,
                // we will automatically lose focus.
                // Both the [UITextInput] and [UIMultilineTextInput] classes automatically activate/deactivate
                // themselves when they receive/lose focus respectively, so there is no need to manually add
                // [onFocus] or [onFocusLost] listeners, unless you wish to override the default behavior.
                grabWindowFocus()
            } childOf textHolder) as UITextInput
        }
    }
}