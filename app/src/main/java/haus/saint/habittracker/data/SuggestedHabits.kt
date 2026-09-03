package haus.saint.habittracker.data

data class SuggestedHabit(
    val name: String,
    val emoji: String,
    val category: String,
    val colorHex: String,
    val frequencyType: FrequencyType = FrequencyType.DAILY,
    val targetCount: Int = 1
)

/**
 * Curated starting points shown in the "add habit" flow, grouped by category so the
 * question feels like "what do you want to work on?" rather than a blank text field.
 */
object SuggestedHabits {

    const val FITNESS = "Fitness"
    const val NUTRITION = "Nutrition"
    const val SLEEP = "Sleep"
    const val MINDFULNESS = "Mindfulness"
    const val PRODUCTIVITY = "Productivity"
    const val DIGITAL = "Digital wellbeing"
    const val LEARNING = "Learning"
    const val SOCIAL = "Social"

    val categories = listOf(FITNESS, NUTRITION, SLEEP, MINDFULNESS, PRODUCTIVITY, DIGITAL, LEARNING, SOCIAL)

    /** One prompt per category, shown above the suggestion grid to frame the question. */
    val categoryPrompts = mapOf(
        FITNESS to "What movement do you want to keep up with?",
        NUTRITION to "What do you want to be more consistent about eating or drinking?",
        SLEEP to "What would help your sleep the most?",
        MINDFULNESS to "What helps you reset during the day?",
        PRODUCTIVITY to "What's one thing that would make your days run better?",
        DIGITAL to "What's worth cutting back on?",
        LEARNING to "What do you want to be learning, a little at a time?",
        SOCIAL to "Who do you want to stay in better touch with?"
    )

    val all: List<SuggestedHabit> = listOf(
        // Fitness
        SuggestedHabit("Go to the gym", "🏋️", FITNESS, "#2F5F73", FrequencyType.TIMES_PER_WEEK, 3),
        SuggestedHabit("10-minute walk", "🚶", FITNESS, "#3E7C63"),
        SuggestedHabit("Stretch / mobility", "🤸", FITNESS, "#3E7C63"),
        SuggestedHabit("Hit today's step goal", "👣", FITNESS, "#3E7C63"),
        SuggestedHabit("Cardio session", "🏃", FITNESS, "#2F5F73", FrequencyType.TIMES_PER_WEEK, 2),
        SuggestedHabit("Track today's workout", "📋", FITNESS, "#2F5F73", FrequencyType.TIMES_PER_WEEK, 3),

        // Nutrition
        SuggestedHabit("Hit protein target", "🍗", NUTRITION, "#8A4B26"),
        SuggestedHabit("Drink 2L of water", "💧", NUTRITION, "#2F6F8F"),
        SuggestedHabit("Log today's meals", "📓", NUTRITION, "#8A4B26"),
        SuggestedHabit("Eat a piece of fruit", "🍎", NUTRITION, "#8A4B26"),
        SuggestedHabit("No alcohol today", "🚫", NUTRITION, "#8A4B26"),
        SuggestedHabit("Cook instead of ordering out", "🍳", NUTRITION, "#8A4B26"),

        // Sleep
        SuggestedHabit("Sleep 7+ hours", "😴", SLEEP, "#4A4E9E"),
        SuggestedHabit("Consistent bedtime", "🌙", SLEEP, "#4A4E9E"),
        SuggestedHabit("Phone out of the bedroom", "📵", SLEEP, "#4A4E9E"),
        SuggestedHabit("No screens 30 min before bed", "🌙", SLEEP, "#4A4E9E"),

        // Mindfulness
        SuggestedHabit("Meditate", "🧘", MINDFULNESS, "#6B4E9E"),
        SuggestedHabit("Journal", "✍️", MINDFULNESS, "#6B4E9E"),
        SuggestedHabit("5 minutes of deep breathing", "🌬️", MINDFULNESS, "#6B4E9E"),
        SuggestedHabit("Write down one thing you're grateful for", "🙏", MINDFULNESS, "#6B4E9E"),
        SuggestedHabit("Time outdoors", "🌳", MINDFULNESS, "#3E7C63"),

        // Productivity
        SuggestedHabit("Plan tomorrow tonight", "🗒️", PRODUCTIVITY, "#7A5C1E"),
        SuggestedHabit("Inbox zero", "📬", PRODUCTIVITY, "#7A5C1E"),
        SuggestedHabit("Deep work session", "🎯", PRODUCTIVITY, "#7A5C1E"),
        SuggestedHabit("Tidy your desk / room", "🧹", PRODUCTIVITY, "#7A5C1E"),
        SuggestedHabit("Make your bed", "🛏️", PRODUCTIVITY, "#7A5C1E"),

        // Digital wellbeing
        SuggestedHabit("Under 1 hour of social media", "📱", DIGITAL, "#8A3B3B"),
        SuggestedHabit("No phone in the first hour awake", "🌅", DIGITAL, "#8A3B3B"),
        SuggestedHabit("Screen-free during meals", "🍽️", DIGITAL, "#8A3B3B"),

        // Learning
        SuggestedHabit("Read 10 pages", "📖", LEARNING, "#2F6F5C"),
        SuggestedHabit("Practice a language", "🗣️", LEARNING, "#2F6F5C"),
        SuggestedHabit("Practice an instrument", "🎸", LEARNING, "#2F6F5C"),
        SuggestedHabit("Learn something new for 15 minutes", "💡", LEARNING, "#2F6F5C"),

        // Social
        SuggestedHabit("Call a family member", "📞", SOCIAL, "#B0562F"),
        SuggestedHabit("Message a friend you've lost touch with", "💬", SOCIAL, "#B0562F"),
        SuggestedHabit("Compliment someone", "😊", SOCIAL, "#B0562F")
    )

    fun byCategory(category: String): List<SuggestedHabit> = all.filter { it.category == category }
}
