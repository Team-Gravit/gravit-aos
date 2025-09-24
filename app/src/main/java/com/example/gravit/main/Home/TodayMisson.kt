package com.example.gravit.main.Home

private val wordNum = mapOf(
    "ONE" to 1, "TWO" to 2, "THREE" to 3,
    "TEN" to 10, "FIFTEEN" to 15, "TWENTY" to 20
)
private val missionXpTable = mapOf(
    "COMPLETE_LESSON_ONE" to 10,
    "COMPLETE_LESSON_TWO" to 15,
    "COMPLETE_LESSONS_THREE" to 20,
    "PERFECT_LESSON_ONE" to 30,
    "PERFECT_LESSONS_TWO" to 35,
    "PERFECT_LESSONS_THREE" to 40,
    "LEARNING_MINUTES_TEN" to 25,
    "LEARNING_MINUTES_FIFTEEN" to 35,
    "LEARNING_MINUTES_TWENTY" to 40,
    "FOLLOW_NEW_FRIEND" to 40,
)
data class MissionInfo(
    val label: String?,
    val xp: Int
)

sealed class Mission {
    data class CompleteLesson(val count: Int) : Mission()
    data class PerfectLesson(val count: Int) : Mission()
    data class LearningMinutes(val minutes: Int) : Mission()
    object FollowNewFriend : Mission()
    data class Unknown(val raw: String?) : Mission()
}
fun parseMission(type: String?): Mission {
    val parts = type?.split('_')
    val last = parts?.lastOrNull().orEmpty()
    val n = wordNum[last]

    return when {
        type?.startsWith("COMPLETE_LESSON") == true -> Mission.CompleteLesson(n ?: 1)
        type?.startsWith("PERFECT_LESSON") == true -> Mission.PerfectLesson(n ?: 1)
        type?.startsWith("LEARNING_MINUTES") == true -> Mission.LearningMinutes(n ?: 0)
        type == "FOLLOW_NEW_FRIEND" -> Mission.FollowNewFriend
        else -> Mission.Unknown(type)
    }
}
fun labelOf(m: Mission) = when (m) {
    is Mission.CompleteLesson -> "레슨 ${m.count}개 완료하기"
    is Mission.PerfectLesson -> "레슨 정답률 100%로 ${m.count}개 완료하기"
    is Mission.LearningMinutes -> "학습 ${m.minutes}분 완료하기"
    Mission.FollowNewFriend -> "새로운 친구 팔로우하기"
    is Mission.Unknown -> m.raw
}

fun todayMisson(missionType: String?): MissionInfo {
    val mission = parseMission(missionType)
    val label = labelOf(mission)
    val xp = missionXpTable[missionType] ?: 0
    return MissionInfo(label, xp)
}