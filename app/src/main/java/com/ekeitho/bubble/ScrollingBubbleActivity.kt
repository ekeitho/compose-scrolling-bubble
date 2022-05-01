package com.ekeitho.bubble

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ekeitho.bubble.domain.Contact
import com.ekeitho.bubble.ui.theme.ScrollingBubbleTheme
import com.ekeitho.bubble.views.*

val alphabetItemSize = 24.dp

internal fun Float.getIndexOfCharBasedOnYPosition(
    alphabetHeightInPixels: Float,
): Char {
    val alphabetCharList = "abcdefghijklmnopqrstuvwxyz".map { it }

    var index = ((this) / alphabetHeightInPixels).toInt()
    index = when {
        index > 25 -> 25
        index < 0 -> 0
        else -> index
    }
    return alphabetCharList[index]
}

class ScrollingBubbleActivity : ComponentActivity() {
    private val names =
        "Aaren, Aarika, Abagael, Bab, Babara, Babb, Cacilia, Cacilie, Cahra, Dacey, Dacia, Dacie, Eachelle, Eada, Eadie, Fae, Faina, Faith, Gabbey, Gabbi, Gabbie, Hadria, Hailee, Haily, Ianthe, Ibbie, Ibby, Jacenta, Jacinda, Jacinta, Kacey, Kacie, Kacy, La Verne, Lacee, Lacey, Mab, Mabel, Mabelle, Nada, Nadean, Nadeen, Octavia, Odele, Odelia, Page, Paige, Paloma, Queenie, Quentin, Querida, Rachael, Rachel, Rachele, Saba, Sabina, Sabine, Tabatha, Tabbatha, Tabbi, Ula, Ulla, Ulrica, Val, Valaree, Valaria, Wallie, Wallis, Walliw, Xaviera, Xena, Xenia, Yalonda, Yasmeen, Yasmin, Zabrina, Zahara, Zandra"
    private val contacts = names.split(", ").map { Contact(it) }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalDensity.current
            val alphabetHeightInPixels = remember { with(context) { alphabetItemSize.toPx() } }
            var alphabetRelativeDragYOffset: Float? by remember { mutableStateOf(null) }
            var alphabetDistanceFromTopOfScreen: Float by remember { mutableStateOf(0F) }
            ScrollingBubbleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BoxWithConstraints {
                        ContactListWithScroller(
                            contacts = contacts,
                            onAlphabetListDrag = { relativeDragYOffset, containerDistance ->
                                alphabetRelativeDragYOffset = relativeDragYOffset
                                alphabetDistanceFromTopOfScreen = containerDistance
                            }
                        )

                        val yOffset = alphabetRelativeDragYOffset
                        if (yOffset != null) {
                            ScrollingBubble(
                                boxConstraintMaxWidth = this.maxWidth,
                                bubbleOffsetYFloat = yOffset + alphabetDistanceFromTopOfScreen,
                                currAlphabetScrolledOn = yOffset.getIndexOfCharBasedOnYPosition(
                                    alphabetHeightInPixels = alphabetHeightInPixels,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}


fun List<Contact>.getFirstUniqueSeenCharIndex(): Map<Char, Int> {
    val firstLetterIndexes = mutableMapOf<Char, Int>()
    this
        .map { it.fullName.lowercase().first() }
        .forEachIndexed { index, char ->
            if (!firstLetterIndexes.contains(char)) {
                firstLetterIndexes[char] = index
            }
            // else don't care about letters that don't exist
        }
    return firstLetterIndexes
}