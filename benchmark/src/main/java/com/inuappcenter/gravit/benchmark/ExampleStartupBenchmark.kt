package com.inuappcenter.gravit.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun homeToLeague() = benchmarkRule.measureRepeated(
        packageName = "com.inuappcenter.gravit",
        metrics = listOf(
            FrameTimingMetric()
        ),
        iterations = 5,
        setupBlock = {
            pressHome()

            device.executeShellCommand(
                "am start -W -n com.inuappcenter.gravit/.MainActivity"
            )

            check(
                device.wait(
                    Until.hasObject(By.desc("리그")),
                    10_000
                )
            ) {
                "Home 화면이 나타나지 않았습니다."
            }
        }
    ) {
        val leagueButton = device.findObject(By.desc("리그"))

        checkNotNull(leagueButton) {
            "리그 버튼을 찾지 못했습니다."
        }

        leagueButton.click()

        check(
            device.wait(
                Until.hasObject(By.desc("tier")),
                10_000
            )
        ) {
            "League 화면이 나타나지 않았습니다."
        }
    }
}