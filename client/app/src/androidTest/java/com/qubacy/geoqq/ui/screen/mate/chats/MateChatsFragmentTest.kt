package com.qubacy.geoqq.ui.screen.mate.chats

import android.view.View
import androidx.core.view.marginTop
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoActivityResumedException
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.card.MaterialCardView
import com.qubacy.geoqq.domain.common.operation.error.HandleErrorOperation
import com.qubacy.geoqq.domain.mate.chats.operation.UpdateChatOperation
import com.qubacy.geoqq.domain.mate.chats.state.MateChatsState
import com.qubacy.geoqq.ui.screen.mate.chats.list.adapter.MateChatsAdapter
import com.qubacy.geoqq.ui.screen.mate.chats.model.MateChatsViewModel
import com.qubacy.geoqq.ui.screen.mate.chats.model.state.MateChatsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.runner.RunWith
import com.qubacy.geoqq.R
import com.qubacy.geoqq.common.error.common.Error
import com.qubacy.geoqq.domain.mate.chats.operation.AddChatOperation
import com.qubacy.geoqq.domain.mate.chats.operation.UpdateRequestCountOperation
import com.qubacy.geoqq.databinding.FragmentMateChatsBinding
import com.qubacy.geoqq.domain.common.model.User
import com.qubacy.geoqq.domain.common.model.message.Message
import com.qubacy.geoqq.domain.mate.chats.model.MateChat
import com.qubacy.geoqq.ui.screen.common.ScreenContext
import com.qubacy.geoqq.ui.screen.common.fragment.chat.ChatFragmentContext
import com.qubacy.geoqq.ui.screen.common.fragment.common.FragmentTestBase
import com.qubacy.geoqq.ui.util.IsChildWithIndexViewAssertion
import com.qubacy.geoqq.ui.util.WaitingViewAction
import kotlinx.coroutines.launch
import org.hamcrest.Matchers
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class MateChatsFragmentTest : FragmentTestBase() {
    class MateChatsUiStateTestData(
        private val mModel: MateChatsViewModel,
        private val mAdapter: MateChatsAdapter,
        private val mMateChatsStateFlow: MutableStateFlow<MateChatsState?>,
        private val mMateChatsUiState: LiveData<MateChatsUiState?>
    ) {
        fun setChats(chats: List<MateChat>, users: List<User>) {
            val chatsState = MateChatsState(chats, users, 0, listOf())

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }

            mAdapter.setItems(chats)
        }

        fun addChat(chatPreview: MateChat, users: List<User>) {
            val newChats = mutableListOf<MateChat>()

            if (mMateChatsUiState.value != null)
                newChats.addAll(mMateChatsUiState.value!!.chats)

            newChats.add(chatPreview)

            val newUsers = mutableListOf<User>()

            if (mMateChatsUiState.value != null)
                newUsers.addAll(mMateChatsUiState.value!!.users)

            newUsers.addAll(users)

            val requestCount =
                if (mMateChatsUiState.value == null) 0 else mMateChatsUiState.value!!.requestCount

            val operations = listOf(
                AddChatOperation(chatPreview.chatId)
            )

            val chatsState = MateChatsState(newChats, newUsers, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }
        }

        fun updateChat(chatPreview: MateChat, users: List<User>) {
            val chatPreviews =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.chats

            val modifiedChats: MutableList<MateChat> = mutableListOf()

            modifiedChats.apply {
                addAll(chatPreviews)
            }

            val newUsers = mutableListOf<User>()

            if (mMateChatsUiState.value != null)
                newUsers.addAll(mMateChatsUiState.value!!.users)

            newUsers.addAll(users)

            var isUpdated = false

            for (chatPos in 0 until modifiedChats.size) {
                if (modifiedChats[chatPos].chatId == chatPreview.chatId) {
                    modifiedChats[chatPos] = chatPreview
                    isUpdated = true

                    break
                }
            }

            Assert.assertTrue(isUpdated)

            val requestCount =
                if (mMateChatsUiState.value == null) 0 else mMateChatsUiState.value!!.requestCount

            val operations = listOf(
                UpdateChatOperation(chatPreview.chatId)
            )

            val chatState = MateChatsState(modifiedChats, newUsers, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatState)
            }
        }

        fun setRequestCount(requestCount: Int) {
            val users =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.users
            val chats =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.chats

            val operations = listOf(
                UpdateRequestCountOperation()
            )

            val chatsState = MateChatsState(chats, users, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }
        }

        fun showError(error: Error) {
            val users =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.users
            val chats =
                if (mMateChatsUiState.value == null) listOf() else mMateChatsUiState.value!!.chats
            val requestCount =
                if (mMateChatsUiState.value == null) 0 else mMateChatsUiState.value!!.requestCount

            val operations = listOf(
                HandleErrorOperation(error)
            )

            val chatsState = MateChatsState(chats, users, requestCount, operations)

            runBlocking {
                mMateChatsStateFlow.emit(chatsState)
            }
        }
    }

    private lateinit var mMateChatsFragmentScenarioRule: FragmentScenario<MateChatsFragment>

    private lateinit var mModel: MateChatsViewModel
    private lateinit var mNavHostController: TestNavHostController
    private lateinit var mMateChatsUiStateTestData: MateChatsUiStateTestData

    private fun generateTestMateChats(
        users: List<User>, lastMessages: List<Message>, count: Int = users.size
    ): List<MateChat> {
        return IntRange(1, count - 1).map {
            MateChat(it.toLong() - 1, users[it].id, ScreenContext.DEFAULT_URI, lastMessages[it - 1])
        }
    }

    @Before
    override fun setup() {
        super.setup()

        mMateChatsFragmentScenarioRule = launchFragmentInContainer(
            themeResId = R.style.Theme_Geoqq_Mates)
        mMateChatsFragmentScenarioRule.moveToState(Lifecycle.State.RESUMED)

        var fragment: MateChatsFragment? = null

        mNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())

        mMateChatsFragmentScenarioRule.onFragment {
            mNavHostController.setGraph(R.navigation.nav_graph)
            mNavHostController.setCurrentDestination(R.id.mateChatsFragment)
            Navigation.setViewNavController(it.requireView(), mNavHostController)

            fragment = it
        }

        val mModelFieldReflection = MateChatsFragment::class.java.superclass.superclass
            .getDeclaredField("mModel").apply { isAccessible = true }
        val adapterFieldReflection = MateChatsFragment::class.java
            .getDeclaredField("mAdapter")
            .apply {
                isAccessible = true
            }
        val mateChatsStateFlowFieldReflection =
            MateChatsViewModel::class.java.getDeclaredField("mMateChatsStateFlow")
                .apply {
                    isAccessible = true
                }
        val mateChatsUiStateFieldReflection = MateChatsViewModel::class.java
            .getDeclaredField("mateChatsUiStateFlow")
            .apply {
                isAccessible = true
            }

        mModel = mModelFieldReflection.get(fragment) as MateChatsViewModel

        mMateChatsUiStateTestData = MateChatsUiStateTestData(
            mModel,
            adapterFieldReflection.get(fragment) as MateChatsAdapter,
            mateChatsStateFlowFieldReflection.get(mModel) as MutableStateFlow<MateChatsState?>,
            mateChatsUiStateFieldReflection.get(mModel) as LiveData<MateChatsUiState?>
        )
    }

    @Test
    fun allElementsInPlaceTest() {
        // it's hidden by default now:
//        Espresso.onView(withId(R.id.friend_requests_card_button))
//            .check(
//                ViewAssertions.matches(
//                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(withId(R.id.chats_recycler_view))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    @Test
    fun allElementsEnabled() {
        // it's hidden by default now:
//        Espresso.onView(withId(R.id.friend_requests_card_button))
//            .perform(ViewActions.click())
//            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withId(R.id.chats_recycler_view))
            .perform(ViewActions.swipeDown())
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun addedChatHasCorrectChatNameLastMessageTextAndTimestampTest() {
        val users = ScreenContext.generateTestUsers(2, true)
        val lastMessages = listOf(
            Message(0L, 0L, "test", 1696847478000)
        )
        val chat = generateTestMateChats(users, lastMessages).first()

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.addChat(chat, users)
        }

        Espresso.onView(withText(users.last().username))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(chat.lastMessage!!.text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText("05:31 PM"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun settingThreeChatsLeadsToShowingThreeChatPreviewsTest() {
        val users = ScreenContext.generateTestUsers(4, true)
        val lastMessages = ChatFragmentContext.generateTestMessages(users.size - 1)
        val chats = generateTestMateChats(users, lastMessages)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chats, users)
        }

        Espresso.onView(withId(R.id.chats_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(chats.size)))
    }

    @Test
    fun newChatGoesAtTopTest() {
        val users = ScreenContext.generateTestUsers(4, true)
        val lastMessages = ChatFragmentContext.generateTestMessages(users.size - 1)
        val chats = generateTestMateChats(users, lastMessages, 3)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chats, users)
        }

        val newChat = MateChat(chats.size.toLong(), users.last().id, ScreenContext.DEFAULT_URI,
            Message(0, 0, "hi", 1696847478000))

        mMateChatsFragmentScenarioRule.onFragment {
            it.lifecycleScope.launch {
                mMateChatsUiStateTestData.addChat(newChat, listOf())
            }
        }

        Espresso.onView(isRoot()).perform(WaitingViewAction(1000))

        Espresso.onView(
            Matchers.allOf(
            isDescendantOfA(withId(R.id.chats_recycler_view)),
            isAssignableFrom(MaterialCardView::class.java),
            hasDescendant(withText(users.last().username))
        )).check(IsChildWithIndexViewAssertion(0))
    }

    @Test
    fun scrollingUpOnNewItemsAddingTest() {
        val users = ScreenContext.generateTestUsers(13, true)
        val lastMessages = ChatFragmentContext.generateTestMessages(users.size - 1)
        val chats = generateTestMateChats(users, lastMessages)

        for (chat in chats) {
            mMateChatsFragmentScenarioRule.onFragment {
                mMateChatsUiStateTestData.addChat(chat, users)
            }

            val curUser = users.find { it.id ==  chat.interlocutorUserId }!!

            Espresso.onView(withText(curUser.username))
                .perform(WaitingViewAction(500))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun chatPreviewGoesUpOnChatUpdateTest() {
        val users = ScreenContext.generateTestUsers(6, true)
        val lastMessages = ChatFragmentContext.generateTestMessages(users.size - 1)
        val chats = generateTestMateChats(users, lastMessages)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chats, users)
        }

        Espresso.onView(isRoot()).perform(WaitingViewAction(1000))

        val updatedChat = MateChat(0, 1, ScreenContext.DEFAULT_URI,
            Message(1, 0, "aloooo", 1696847480000))

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.updateChat(updatedChat, listOf())
        }

        Espresso.onView(Matchers.allOf(
            isDescendantOfA(withId(R.id.chats_recycler_view)),
            isAssignableFrom(MaterialCardView::class.java),
            hasDescendant(withText(updatedChat.lastMessage!!.text))
        )).check(IsChildWithIndexViewAssertion(0))
    }

    @Test
    fun newRequestsCardAppearsOnRequestCountSetToOneTest() {
        Espresso.onView(withId(R.id.mate_requests_card))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(1)
        }

        Espresso.onView(withId(R.id.mate_requests_card))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    class TopBoundViewAssertion(private val mTop: Float) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (view == null)
                throw IllegalArgumentException()
            if (view.top.toFloat() != mTop)
                throw NoMatchingViewException.Builder().build()
        }
    }

    @Test
    fun newRequestsCardAppearanceChangesChatsListTopBoundTest() {
        val users = ScreenContext.generateTestUsers(6, true)
        val lastMessages = ChatFragmentContext.generateTestMessages(users.size - 1)
        val chats = generateTestMateChats(users, lastMessages)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chats, users)
        }

        Espresso.onView(isRoot())
            .perform(WaitingViewAction(1000))
            .check(TopBoundViewAssertion(0f))

        var binding: FragmentMateChatsBinding? = null

        mMateChatsFragmentScenarioRule.onFragment {
            binding = FragmentMateChatsBinding.bind(it.view!!)

            mMateChatsUiStateTestData.setRequestCount(1)
        }

        val topBound = binding!!.mateRequestsCard.measuredHeight.toFloat() +
                binding!!.mateRequestsCard.marginTop.toFloat()

        Espresso.onView(withId(R.id.chats_recycler_view))
            .perform(WaitingViewAction(1000))
            .check(TopBoundViewAssertion(topBound))
    }

    @Test
    fun mateRequestsCardDisappearsOnRequestCounterSettingToZeroTest() {
        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(1)
        }

        Espresso.onView(withId(R.id.mate_requests_card))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(0)
        }

        Espresso.onView(withId(R.id.mate_requests_card))
            .perform(WaitingViewAction(1000))
            .check(ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }

    @Test
    fun handlingNormalErrorOperationLeadsToShowingDialogTest() {
        val error = Error(0, "Test", false)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.showError(error)
        }

        Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
            .perform(ViewActions.click())
            .check(ViewAssertions.doesNotExist())
    }

    @Test
    fun handlingCriticalErrorOperationLeadsToAppClosingTest() {
        val error = Error(0, "Test", true)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.showError(error)
        }

        try {
            Espresso.onView(withText(R.string.component_dialog_error_neutral_button_caption))
                .perform(ViewActions.click())

        } catch (e: Exception) {
            Assert.assertEquals(NoActivityResumedException::class, e::class)
        }
    }

    @Test
    fun chatPreviewClickLeadsToTransitionToMateChatFragmentTest() {
        val users = ScreenContext.generateTestUsers(2, true)
        val lastMessages = ChatFragmentContext.generateTestMessages(users.size - 1)
        val chats = generateTestMateChats(users, lastMessages)

        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setChats(chats, users)
        }

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.isAssignableFrom(MaterialCardView::class.java),
                ViewMatchers.isDescendantOfA(withId(R.id.chats_recycler_view)),
                ViewMatchers.hasDescendant(withText(users.last().username))
            )
        ).perform(ViewActions.click())

        Assert.assertEquals(R.id.mateChatFragment, mNavHostController.currentDestination?.id)
    }

    @Test
    fun mateRequestsCheckButtonClickLeadsToTransitionToMateRequestsFragmentTest() {
        mMateChatsFragmentScenarioRule.onFragment {
            mMateChatsUiStateTestData.setRequestCount(1)
        }

        Espresso.onView(withId(R.id.mate_requests_card_button))
            .perform(ViewActions.click())

        Assert.assertEquals(R.id.mateRequestsFragment, mNavHostController.currentDestination?.id)
    }
}