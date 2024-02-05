package com.ekeitho.bubble.views

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.ekeitho.bubble.alphabetItemSize
import com.ekeitho.bubble.domain.Contact
import com.ekeitho.bubble.getFirstUniqueSeenCharIndex
import com.ekeitho.bubble.getIndexOfCharBasedOnYPosition
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.launch

@Composable
fun ContactListWithScroller(
  contacts: ImmutableList<Contact>,
  onAlphabetListDrag: (Float?, Float) -> Unit,
) {
  val mapOfFirstLetterIndex: ImmutableMap<Char, Int> =
    remember(contacts) { contacts.getFirstUniqueSeenCharIndex() }
  val alphabetHeightInPixels: Float =
    with(LocalDensity.current) { alphabetItemSize.toPx() }
  val lazyListState = rememberLazyListState()
  val coroutineScope = rememberCoroutineScope()

  Row(
    modifier = Modifier.fillMaxSize(),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    ContactList(
      Modifier
        .fillMaxHeight()
        .weight(1F),
      contacts,
      lazyListState,
      mapOfFirstLetterIndex
    )

    AlphabetScroller(
      onAlphabetListDrag = { relativeDragYOffset, containerDistanceFromTopOfScreen ->
        onAlphabetListDrag(relativeDragYOffset, containerDistanceFromTopOfScreen)
        coroutineScope.launch {
          // null case can happen if we go through list
          // and we don't have a name that starts with I
          val indexOfChar = relativeDragYOffset?.getIndexOfCharBasedOnYPosition(
            alphabetHeightInPixels = alphabetHeightInPixels,
          )
          mapOfFirstLetterIndex[indexOfChar]?.let {
            lazyListState.scrollToItem(it)
          }
        }
      },
    )
  }
}

@Composable
fun RowScope.ContactList(
  modifier: Modifier,
  contacts: ImmutableList<Contact>,
  lazyListState: LazyListState,
  firstLetterIndexes: Map<Char, Int>,
) {
  LazyColumn(
    modifier = modifier,
    state = lazyListState,
  ) {
    itemsIndexed(contacts) { index, contact ->
      ContactItem(
        contact = contact,
        isAlphabeticallyFirstInCharGroup =
        firstLetterIndexes[contact.fullName.lowercase().first()] == index,
      )
    }
  }
}

@Composable
fun ContactItem(
  contact: Contact,
  isAlphabeticallyFirstInCharGroup: Boolean
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier.width(48.dp),
      contentAlignment = Alignment.Center,
    ) {
      if (isAlphabeticallyFirstInCharGroup) {
        Text(
          text = contact.fullName.first().toString(),
          style = MaterialTheme.typography.bodyLarge
        )
      }
    }

    Surface(
      shape = CircleShape,
      modifier = Modifier.size(32.dp),
      color = MaterialTheme.colorScheme.secondary
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = contact.fullName.first().toString(),
          style = MaterialTheme.typography.bodyLarge
        )
      }
    }

    Text(
      modifier = Modifier.padding(16.dp),
      text = contact.fullName,
      style = MaterialTheme.typography.titleLarge,
    )
  }
}

@Composable
private fun AlphabetScroller(
  onAlphabetListDrag: (relativeDragYOffset: Float?, distanceFromTopOfScreen: Float) -> Unit,
) {
  val alphabetCharList = "abcdefghijklmnopqrstuvwxyz".map { it }
  var distanceFromTopOfScreen by remember { mutableStateOf(0F) }

  Column(
    modifier = Modifier
      .width(16.dp)
      .onGloballyPositioned {
        distanceFromTopOfScreen = it.positionInRoot().y
      }
      .pointerInput(alphabetCharList) {
        detectVerticalDragGestures(
          onDragStart = {
            onAlphabetListDrag(it.y, distanceFromTopOfScreen)
          },
          onDragEnd = {
            onAlphabetListDrag(null, distanceFromTopOfScreen)
          }
        ) { change, _ ->
          onAlphabetListDrag(
            change.position.y,
            distanceFromTopOfScreen
          )
        }
      },
    verticalArrangement = Arrangement.Center,
  ) {
    for (i in alphabetCharList) {
      Text(
        modifier = Modifier.height(alphabetItemSize),
        text = i.toString(),
      )
    }
  }
}