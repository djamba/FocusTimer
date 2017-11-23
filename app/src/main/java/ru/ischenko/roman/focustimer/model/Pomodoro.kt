package ru.ischenko.roman.focustimer.model

/**
 * User: roman
 * Date: 21.11.17
 * Time: 18:27
 */
data class Pomodoro(
    val task: Task? = null,
    val target: String? = null,
    val timeLeftSec: Long = 0,
    val goalAchieved: Boolean = false,
    val efficiencyMark: Int = 0,
    val retrospectiveComment: String? = null,
    val distractionCount: Int = 0
)