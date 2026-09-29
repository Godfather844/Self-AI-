package com.example.data

import com.example.model.BadgeItem
import com.example.model.LeaderboardEntry
import com.example.model.MatchPair
import com.example.model.QuizQuestion
import com.example.model.RevisionQuest

object DemoData {

    val sampleQuests = listOf(
        RevisionQuest(
            id = "quest_dbms",
            topicName = "DBMS Normalization",
            subject = "Database Systems",
            progressPercent = 0.0f,
            totalQuestions = 4,
            solvedQuestions = 0,
            difficulty = "Medium",
            iconEmoji = "🗄️",
            accentColorHex = 0xFF0284C7
        ),
        RevisionQuest(
            id = "quest_os",
            topicName = "OS Semaphores & Locks",
            subject = "Operating Systems",
            progressPercent = 0.0f,
            totalQuestions = 4,
            solvedQuestions = 0,
            difficulty = "Hard",
            iconEmoji = "🚦",
            accentColorHex = 0xFF8B5CF6
        ),
        RevisionQuest(
            id = "quest_cn",
            topicName = "TCP 3-Way Handshake",
            subject = "Computer Networks",
            progressPercent = 0.0f,
            totalQuestions = 4,
            solvedQuestions = 0,
            difficulty = "Easy",
            iconEmoji = "🌐",
            accentColorHex = 0xFFFB923C
        ),
        RevisionQuest(
            id = "quest_dsa",
            topicName = "Binary Tree Traversals",
            subject = "Data Structures",
            progressPercent = 0.0f,
            totalQuestions = 4,
            solvedQuestions = 0,
            difficulty = "Medium",
            iconEmoji = "🌳",
            accentColorHex = 0xFF10B981
        )
    )

    // Complete 4-format Duolingo-style quest cycle for DBMS Normalization
    val dbmsDuolingoQuest = listOf(
        // Format 1: Tap-to-Fill Blank (Word Bank / Sentence Puzzle)
        QuizQuestion.FillInTheBlanks(
            id = "dbms_blank_1",
            prompt = "Tap the word chips below to complete the core rule of 2NF:",
            sentencePrefix = "A relation is in 2NF if it is in ",
            blankPlaceholders = listOf("1NF", "partial dependencies"),
            sentenceMiddle = " and contains no ",
            sentenceSuffix = " on any candidate key.",
            wordPool = listOf("1NF", "partial dependencies", "BCNF", "transitive dependencies", "superkeys", "atomic"),
            explanation = "Second Normal Form (2NF) mandates 1NF first, and completely eliminates partial dependencies of non-prime attributes on composite keys.",
            topic = "DBMS Normalization",
            xpReward = 25
        ),

        // Format 2: Concept Pair Matching (Term <-> Definition)
        QuizQuestion.ConceptMatch(
            id = "dbms_match_2",
            prompt = "Tap a Normal Form and its matching mathematical criterion:",
            pairs = listOf(
                MatchPair("p1", "1NF", "Atomic values only, no repeated groups"),
                MatchPair("p2", "2NF", "No partial functional dependencies"),
                MatchPair("p3", "3NF", "No transitive dependencies across non-prime keys"),
                MatchPair("p4", "BCNF", "Every determinant X in X -> Y must be a Superkey")
            ),
            explanation = "Awesome matching! Each normal form progressively resolves redundant functional dependencies to eliminate anomalies.",
            topic = "DBMS Normalization",
            xpReward = 30
        ),

        // Format 3: Speech-to-Answer (Voice Recall with Android Mic)
        QuizQuestion.VoiceAnswer(
            id = "dbms_voice_3",
            questionPrompt = "In your own words, explain what a Deletion Anomaly is in a database table.",
            requiredKeywords = listOf("delete", "loss", "unintended", "data", "information", "record", "attribute"),
            sampleModelAnswer = "A deletion anomaly occurs when deleting certain data unintentionally deletes other vital, unrelated information.",
            minKeywordsRequired = 2,
            explanation = "A deletion anomaly happens when losing one piece of data inadvertently destroys unrelated facts because data is improperly combined.",
            topic = "DBMS Normalization",
            xpReward = 35
        ),

        // Format 4: Quick True/False Rapid-Fire Card
        QuizQuestion.MultipleChoice(
            id = "dbms_tf_4",
            questionText = "True or False: Every relation schema in BCNF is guaranteed to also satisfy 3NF.",
            options = listOf("TRUE - BCNF is strictly stronger than 3NF", "FALSE - BCNF violates 3NF properties"),
            correctOptionIndex = 0,
            isTrueFalse = true,
            explanation = "BCNF is a stricter refinement of 3NF. Therefore, any table satisfying BCNF is inherently in 3NF!",
            topic = "DBMS Normalization",
            xpReward = 20
        )
    )

    // Complete 4-format Duolingo-style quest cycle for OS Semaphores
    val osDuolingoQuest = listOf(
        // Format 1: Tap-to-Fill Blank
        QuizQuestion.FillInTheBlanks(
            id = "os_blank_1",
            prompt = "Tap chips to fill the two fundamental semaphore atomic operations:",
            sentencePrefix = "A thread enters critical section using ",
            blankPlaceholders = listOf("wait()", "signal()"),
            sentenceMiddle = " and exits by calling ",
            sentenceSuffix = " to increment the counter.",
            wordPool = listOf("wait()", "signal()", "yield()", "fork()", "sleep()", "join()"),
            explanation = "wait() (P operation) decrements the semaphore counter and blocks if <= 0; signal() (V operation) increments and wakes waiting processes.",
            topic = "OS Semaphores",
            xpReward = 25
        ),

        // Format 2: Concept Pair Matching
        QuizQuestion.ConceptMatch(
            id = "os_match_2",
            prompt = "Match each synchronization primitive to its behavior:",
            pairs = listOf(
                MatchPair("os1", "Binary Semaphore", "Integer restricted to 0 or 1 (Mutex)"),
                MatchPair("os2", "Counting Semaphore", "Integer spanning unrestricted domain of resources"),
                MatchPair("os3", "Spinlock", "Busy-waits in a loop until lock is acquired"),
                MatchPair("os4", "Deadlock", "Processes blocked indefinitely waiting for circular resource")
            ),
            explanation = "All synchronization primitives correctly paired! Spinlocks avoid context switch overhead for short waits, while semaphores sleep.",
            topic = "OS Semaphores",
            xpReward = 30
        ),

        // Format 3: Speech-to-Answer
        QuizQuestion.VoiceAnswer(
            id = "os_voice_3",
            questionPrompt = "Explain in 10 seconds what a Mutex does in concurrent programming.",
            requiredKeywords = listOf("lock", "mutual", "exclusion", "thread", "critical", "one", "protect"),
            sampleModelAnswer = "A mutex provides mutual exclusion so only one thread can access a shared resource at a time.",
            minKeywordsRequired = 2,
            explanation = "A Mutex (Mutual Exclusion) ensures only one executing thread enters the critical section at any given moment.",
            topic = "OS Semaphores",
            xpReward = 35
        ),

        // Format 4: Quick True/False
        QuizQuestion.MultipleChoice(
            id = "os_tf_4",
            questionText = "True or False: If Preemption is allowed by the OS scheduler, Deadlock can still theoretically persist indefinitely.",
            options = listOf("TRUE - Preemption does not affect Coffman conditions", "FALSE - No Preemption is a mandatory Coffman condition"),
            correctOptionIndex = 1,
            isTrueFalse = true,
            explanation = "False! 'No Preemption' is one of the four essential Coffman conditions. If resources can be preempted, deadlock is broken!",
            topic = "OS Semaphores",
            xpReward = 20
        )
    )

    // Complete 4-format quest cycle for Computer Networks
    val cnDuolingoQuest = listOf(
        QuizQuestion.FillInTheBlanks(
            id = "cn_blank_1",
            prompt = "Complete the sequence of TCP 3-Way Handshake packets:",
            sentencePrefix = "The client sends ",
            blankPlaceholders = listOf("SYN", "SYN-ACK"),
            sentenceMiddle = ", the server responds with ",
            sentenceSuffix = ", and the client finishes with ACK.",
            wordPool = listOf("SYN", "SYN-ACK", "FIN", "RST", "PUSH", "PING"),
            explanation = "TCP handshake begins with client SYN, server responds with SYN-ACK, and client finishes connection with ACK.",
            topic = "TCP Handshake",
            xpReward = 25
        ),
        QuizQuestion.ConceptMatch(
            id = "cn_match_2",
            prompt = "Match the transport protocol feature to its protocol:",
            pairs = listOf(
                MatchPair("cn1", "TCP Flow Control", "Sliding window size advertised by receiver"),
                MatchPair("cn2", "TCP Congestion Control", "Slow start, AIMD, and fast retransmit"),
                MatchPair("cn3", "UDP Datagram", "Connectionless, zero-handshake minimal overhead"),
                MatchPair("cn4", "TCP Teardown", "4-way handshake using FIN and ACK packets")
            ),
            explanation = "Great job! TCP delivers reliable ordered byte streams, while UDP optimizes for low latency.",
            topic = "TCP Handshake",
            xpReward = 30
        ),
        QuizQuestion.VoiceAnswer(
            id = "cn_voice_3",
            questionPrompt = "State the main difference between TCP and UDP in one sentence.",
            requiredKeywords = listOf("connection", "reliable", "unreliable", "speed", "overhead", "flow", "order"),
            sampleModelAnswer = "TCP is connection-oriented and reliable, whereas UDP is connectionless and fast.",
            minKeywordsRequired = 2,
            explanation = "TCP guarantees delivery and sequencing through acknowledgements, while UDP is connectionless without retransmissions.",
            topic = "TCP Handshake",
            xpReward = 35
        ),
        QuizQuestion.MultipleChoice(
            id = "cn_tf_4",
            questionText = "True or False: UDP headers include sequence numbers to guarantee packets arrive in order.",
            options = listOf("TRUE - UDP guarantees in-order arrival", "FALSE - UDP headers do not track sequence numbers"),
            correctOptionIndex = 1,
            isTrueFalse = true,
            explanation = "False! UDP has a lightweight 8-byte header without sequence numbers, making it completely unordered and connectionless.",
            topic = "TCP Handshake",
            xpReward = 20
        )
    )

    fun getDynamicLeaderboards(
        currentUser: com.example.model.UserProfile?,
        realPeers: List<LeaderboardEntry> = emptyList()
    ): Pair<List<LeaderboardEntry>, List<LeaderboardEntry>> {
        val validUser = currentUser?.takeIf {
            it.name.isNotBlank() && !it.name.contains("Aarav", ignoreCase = true) && !it.email.contains("aarav", ignoreCase = true)
        }

        val currentUserEntry = validUser?.let { user ->
            val userBadge = when {
                user.xp >= 600 -> "Mastermind"
                user.xp >= 400 -> "Scholar"
                user.xp >= 200 -> "Achiever"
                else -> "Rising Star"
            }
            LeaderboardEntry(
                rank = 0,
                name = user.name,
                avatarEmoji = user.avatarEmoji,
                college = user.college,
                xp = user.xp,
                streak = user.currentStreak,
                badgeLabel = userBadge,
                isCurrentUser = true
            )
        }

        val allReal = (listOfNotNull(currentUserEntry) + realPeers.filter {
            !it.isCurrentUser && !it.name.contains("Aarav", ignoreCase = true)
        })
            .distinctBy { it.name.trim().lowercase() }
            .sortedByDescending { it.xp }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

        if (allReal.isEmpty()) {
            return Pair(emptyList(), emptyList())
        }

        val collegeList = if (validUser != null) {
            allReal.filter { entry ->
                entry.isCurrentUser || entry.college.equals(validUser.college, ignoreCase = true)
            }.mapIndexed { index, entry -> entry.copy(rank = index + 1) }
        } else {
            allReal
        }

        return Pair(allReal, collegeList)
    }

    val weeklyLeaderboard: List<LeaderboardEntry> = emptyList()

    val collegeLeaderboard: List<LeaderboardEntry> = emptyList()

    val userBadges = listOf(
        BadgeItem(
            id = "badge_streak_3",
            title = "Streak Starter",
            description = "Maintained a 3-day quest learning streak",
            iconEmoji = "🔥",
            isUnlocked = true,
            dateUnlocked = "Yesterday"
        ),
        BadgeItem(
            id = "badge_note_master",
            title = "Note Alchemist",
            description = "Brewed 10 AI Quests from classroom notes",
            iconEmoji = "🧪",
            isUnlocked = true,
            dateUnlocked = "2 days ago"
        ),
        BadgeItem(
            id = "badge_accuracy",
            title = "Bullseye",
            description = "Scored 100% accuracy on a DBMS Quiz",
            iconEmoji = "🎯",
            isUnlocked = true,
            dateUnlocked = "Last week"
        ),
        BadgeItem(
            id = "badge_voice_scholar",
            title = "Orator Scholar",
            description = "Completed a speech-to-answer voice challenge",
            iconEmoji = "🎙️",
            isUnlocked = true,
            dateUnlocked = "Today"
        ),
        BadgeItem(
            id = "badge_pair_master",
            title = "Match Master",
            description = "Matched all concept pairs without errors",
            iconEmoji = "🧩",
            isUnlocked = true,
            dateUnlocked = "Today"
        ),
        BadgeItem(
            id = "badge_streak_7",
            title = "Unstoppable Fire",
            description = "Maintain a 7-day revision streak",
            iconEmoji = "⚡",
            isUnlocked = false
        )
    )

    val sampleNoteTexts = listOf(
        """
        [Lecture Notes - Prof. Ramanathan]
        Subject: Database Management Systems - Unit 3: Normalization
        Key Concepts:
        1. 1NF: Atomic values only. No repeating multi-valued attributes.
        2. 2NF: Must be in 1NF + No partial dependency! Every non-prime attribute is fully dependent on primary key.
        3. 3NF: Must be in 2NF + No transitive dependency (X -> Y and Y -> Z where Z is non-prime).
        4. BCNF: Stricter than 3NF. For every FD X -> Y, X must be a superkey.
        Anomalies avoided: Insertion, Deletion, Update anomalies.
        """.trimIndent(),
        """
        [Lecture Notes - Prof. Meenakshi]
        Subject: Operating Systems - Unit 2: Process Synchronization
        Key Concepts:
        - Critical Section Problem: Mutual Exclusion, Progress, Bounded Waiting.
        - Semaphores: Integer variable manipulated only through wait() [P] and signal() [V].
        - Counting Semaphore vs Binary Semaphore (Mutex).
        - Deadlock Conditions: Mutual exclusion, Hold & wait, No preemption, Circular wait.
        """.trimIndent(),
        """
        [Lecture Notes - Prof. K. Singhania]
        Subject: Computer Networks - Transport Layer Protocols
        Key Concepts:
        - TCP: Connection-oriented, reliable byte stream, flow control (Sliding Window), congestion control (Slow Start).
        - 3-Way Handshake: SYN -> SYN-ACK -> ACK.
        - Connection teardown: FIN -> ACK -> FIN -> ACK (4-way).
        - UDP: Connectionless, lightweight, unreliable, zero handshake latency.
        """.trimIndent()
    )
}
