package com.example.vinilos_grupo11

import android.app.UiAutomation
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.FileInputStream

/**
 * Disables all window/transition/animator scales via shell commands (UiAutomation runs as shell,
 * which has SET_ANIMATION_SCALE) and also turns off Android 14+ predictive-back animation.
 * Calls device.waitForIdle() so the previous test's window is fully gone before the next
 * ActivityScenario.launch() runs — prevents the RootViewWithoutFocusException on Android 15.
 */
class DisableAnimationsRule : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            shell(automation, "settings put global window_animation_scale 0")
            shell(automation, "settings put global transition_animation_scale 0")
            shell(automation, "settings put global animator_duration_scale 0")
            shell(automation, "settings put global enable_back_animation 0")
            device.wakeUp()
            device.waitForIdle(5000)
            try {
                base.evaluate()
            } finally {
                shell(automation, "settings put global window_animation_scale 1")
                shell(automation, "settings put global transition_animation_scale 1")
                shell(automation, "settings put global animator_duration_scale 1")
                shell(automation, "settings put global enable_back_animation 1")
            }
        }
    }

    private fun shell(automation: UiAutomation, cmd: String) {
        automation.executeShellCommand(cmd).use { pfd ->
            FileInputStream(pfd.fileDescriptor).use { it.readBytes() }
        }
    }
}
