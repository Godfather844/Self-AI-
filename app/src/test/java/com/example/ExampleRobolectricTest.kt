package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.DemoData
import com.example.model.QuizQuestion
import com.example.model.UserProfile
import com.example.navigation.Screen
import com.example.viewmodel.SelfAiViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private fun createViewModel(): SelfAiViewModel {
        val app = ApplicationProvider.getApplicationContext<Application>()
        return SelfAiViewModel(app)
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Self AI", appName)
    }

    @Test
    fun `viewModel initial state and navigation`() {
        val viewModel = createViewModel()
        val state = viewModel.uiState.value
        assertEquals(Screen.Home, state.currentScreen)
        assertEquals(120, state.userProfile.xp)
        assertTrue(state.userProfile.currentStreak >= 1)

        viewModel.navigateTo(Screen.PlayUpload)
        assertEquals(Screen.PlayUpload, viewModel.uiState.value.currentScreen)

        viewModel.navigateTo(Screen.Leaderboard)
        assertEquals(Screen.Leaderboard, viewModel.uiState.value.currentScreen)

        viewModel.navigateTo(Screen.Profile)
        assertEquals(Screen.Profile, viewModel.uiState.value.currentScreen)
    }

    @Test
    fun `dynamic leaderboard calculation for current user`() {
        val testProfile = UserProfile(
            name = "Lucky Kushwaha",
            email = "kushwahalucky939@gmail.com",
            college = "National Institute of Technology",
            xp = 120,
            currentStreak = 1
        )
        val (weekly, college) = DemoData.getDynamicLeaderboards(testProfile)
        val userWeeklyEntry = weekly.firstOrNull { it.isCurrentUser }
        assertNotNull(userWeeklyEntry)
        assertEquals("Lucky Kushwaha", userWeeklyEntry?.name)
        assertEquals(120, userWeeklyEntry?.xp)
        assertEquals(1, userWeeklyEntry?.rank)

        val userCollegeEntry = college.firstOrNull { it.isCurrentUser }
        assertNotNull(userCollegeEntry)
        assertEquals("Lucky Kushwaha", userCollegeEntry?.name)
        assertEquals("National Institute of Technology", userCollegeEntry?.college)
        assertEquals(1, userCollegeEntry?.rank)

        // Verify zero fake names
        assertFalse(weekly.any { it.name.contains("Riya", true) })
        assertFalse(weekly.any { it.name.contains("Devansh", true) })
        assertFalse(weekly.any { it.name.contains("Sneha", true) })
    }

    @Test
    fun `empty leaderboard when no user exists`() {
        val (weekly, college) = DemoData.getDynamicLeaderboards(null)
        assertTrue(weekly.isEmpty())
        assertTrue(college.isEmpty())
    }

    @Test
    fun `aarav dummy user filtered out from leaderboard`() {
        val dummyProfile = UserProfile(name = "Aarav Sharma", email = "aarav.cs26@college.edu")
        val (weekly, college) = DemoData.getDynamicLeaderboards(dummyProfile)
        assertTrue(weekly.isEmpty())
        assertTrue(college.isEmpty())
    }

    @Test
    fun `fill in blanks interaction logic`() {
        val viewModel = createViewModel()
        viewModel.setQuizQuestions(DemoData.dbmsDuolingoQuest)
        val state = viewModel.uiState.value
        val questions = state.quizState.questions
        assertTrue(questions.isNotEmpty())

        val firstQ = questions.first()
        if (firstQ is QuizQuestion.FillInTheBlanks) {
            val chip = firstQ.wordPool.first()
            viewModel.tapPoolChip(chip)
            assertEquals(listOf(chip), viewModel.uiState.value.quizState.filledBlanks)
            assertFalse(viewModel.uiState.value.quizState.availablePool.contains(chip))

            viewModel.removeFilledBlank(0)
            assertTrue(viewModel.uiState.value.quizState.filledBlanks.isEmpty())
            assertTrue(viewModel.uiState.value.quizState.availablePool.contains(chip))
        }
    }

    @Test
    fun `concept match and voice evaluation`() {
        val viewModel = createViewModel()
        viewModel.setQuizQuestions(DemoData.dbmsDuolingoQuest)

        viewModel.updateSpokenVoiceText("A deletion anomaly occurs when unintended loss of vital data happens")
        assertTrue(viewModel.uiState.value.quizState.spokenText.contains("anomaly"))

        viewModel.selectMatchTerm("p1")
        assertEquals("p1", viewModel.uiState.value.quizState.selectedTermId)

        viewModel.selectMatchDef("p1")
        assertTrue(viewModel.uiState.value.quizState.matchedPairIds.contains("p1"))
    }
}
