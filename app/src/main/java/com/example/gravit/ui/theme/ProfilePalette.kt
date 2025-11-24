package com.example.gravit.ui.theme

import androidx.compose.ui.graphics.Color

object ProfilePalette {
    // 1-based ID <-> index(0-based) 매핑
    val colors: List<Color> = listOf(
        Color(0xFFEB1D64), // 1
        Color(0xFFF44336), // 2
        Color(0xFFFFEA3F), // 3
        Color(0xFF4CAE51), // 4
        Color(0xFF53A8EB), // 5
        Color(0xFF9D27B0), // 6
        Color(0xFF673AB7), // 7
        Color(0xFF3F50B5), // 8
        Color(0xFF808081), // 9
        Color(0xFF000000), // 10
        Color(0xFFFF9900),
        Color(0xFFFE9370),
        Color(0xFFFBC6DC),
        Color(0xFFA4ECAD),
        Color(0xFF6DD6CA),
        Color(0xFF2989C3),
        Color(0xFF1430E5),
        Color(0xFF340D91),
        Color(0xFF8138C5)
    )

    const val DEFAULT_ID = 1

    /** 서버/DB의 1-based id를 Compose Color로 */
    fun idToColor(id: Int): Color {
        val idx = id - 1
        return if (idx in colors.indices) colors[idx] else colors[DEFAULT_ID - 1]
    }

    /** Compose 쪽 index(0-based) -> 서버로 보낼 1-based id */
    fun indexToId(index: Int): Int = (index % colors.size).let { it + 1 }

    /** 서버 id(1-based) -> UI에서 쓸 index(0-based) */
    fun idToIndex(id: Int): Int {
        val idx = id - 1
        return if (idx in colors.indices) idx else DEFAULT_ID - 1
    }

    val size: Int get() = colors.size
}