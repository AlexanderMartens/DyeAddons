package anlg.dyeaddons.utils

import com.mojang.blaze3d.platform.InputConstants

/**
 * Credits: Feesh
 */
object InputUtils {

    fun unboundKey(): Int = InputConstants.UNKNOWN.value

    fun keyType(): InputConstants.Type {
        //?if >=26.3 {
        return InputConstants.Type.KEYBOARD
        //?} else {
        /*return InputConstants.Type.KEYSYM
        *///?}
    }

    fun isLeftMouseButton(button: Int): Boolean = button == InputConstants.MOUSE_BUTTON_LEFT

    fun isRightMouseButton(button: Int): Boolean = button == InputConstants.MOUSE_BUTTON_RIGHT

    fun isTabKey(keyCode: Int): Boolean = keyCode == InputConstants.KEY_TAB

    fun isZeroKey(keyCode: Int): Boolean = keyCode == InputConstants.KEY_0

}