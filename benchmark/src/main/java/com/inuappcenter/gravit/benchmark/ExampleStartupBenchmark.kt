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
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,

        setupBlock = {
            //기기 홈 화면 이동
            pressHome()

            //Gravit 실행
            device.executeShellCommand(
                "am start -W -n com.inuappcenter.gravit/.MainActivity"
            )

            //하단 네비게이션이 나타날 때까지 대기
            val homeButton = device.wait(
                Until.findObject(By.desc("홈")),
                10_000
            )

            checkNotNull(homeButton) {
                "홈 버튼을 찾지 못했습니다."
            }

            //Home으로 이동
            homeButton.click()

            //Home 화면이 나타날 때까지 대기
            check(
                device.wait(
                    Until.hasObject(By.text("연속 학습일")),
                    10_000
                )
            ) {
                "Home 화면이 나타나지 않았습니다."
            }

            //Home 화면이 안정된 뒤 측정 시작
            device.waitForIdle()
        }
    ) {
        //Home → League 성능 측정

        val leagueButton = device.findObject(By.desc("리그"))

        checkNotNull(leagueButton) {
            "리그 버튼을 찾지 못했습니다."
        }

        leagueButton.click()

        //League 화면 진입 완료 확인
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