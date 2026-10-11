package com.jossephus.chuchu.ui.screens.terminal

import com.jossephus.chuchu.service.terminal.TabSpec
import com.jossephus.chuchu.ui.screens.Terminal.matchingSavedTabIndex
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SavedProfileTabSelectionTest {
    private val saved = TabSpec(hostId = 1, host = "new.example", username = "alice")
    private val old = saved.copy(host = "old.example")

    @Test
    fun changedProfileDoesNotResumeAnOldTab() {
        assertNull(matchingSavedTabIndex(listOf(old), activeIndex = 0, savedSpec = saved))
    }

    @Test
    fun matchingActiveTabWinsOverOtherMatchingTabs() {
        assertEquals(0, matchingSavedTabIndex(listOf(saved, old, saved), 0, saved))
    }

    @Test
    fun staleActiveTabFallsBackToLatestMatchingTab() {
        assertEquals(2, matchingSavedTabIndex(listOf(saved, old, saved), 1, saved))
    }
}
