package com.fhnav.app.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fhnav.app.data.model.Friendship
import com.fhnav.app.data.model.Phrase
import com.fhnav.app.data.model.PhraseCategory
import com.fhnav.app.data.model.PhraseMessage
import com.fhnav.app.data.model.User
import com.fhnav.app.domain.usecase.SocialUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SocialUiState(
    val friends: List<User> = emptyList(),
    val pendingRequests: List<Friendship> = emptyList(),
    val sentRequests: List<Friendship> = emptyList(),
    val searchResults: List<User> = emptyList(),
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val phrases: List<Phrase> = emptyList(),
    val selectedCategory: PhraseCategory? = null,
    val unreadMessages: List<PhraseMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val socialUseCase: SocialUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SocialUiState())
    val uiState: StateFlow<SocialUiState> = _uiState.asStateFlow()

    init {
        loadFriends()
        loadPhrases()
        loadUnreadMessages()
    }

    private fun loadFriends() {
        viewModelScope.launch {
            socialUseCase.getFriends().collect { friends ->
                _uiState.value = _uiState.value.copy(friends = friends)
            }
        }
        viewModelScope.launch {
            socialUseCase.getPendingRequests().collect { requests ->
                _uiState.value = _uiState.value.copy(pendingRequests = requests)
            }
        }
        viewModelScope.launch {
            socialUseCase.getSentRequests().collect { requests ->
                _uiState.value = _uiState.value.copy(sentRequests = requests)
            }
        }
    }

    private fun loadPhrases() {
        viewModelScope.launch {
            socialUseCase.getPhrases().collect { phrases ->
                _uiState.value = _uiState.value.copy(phrases = phrases)
            }
        }
    }

    private fun loadUnreadMessages() {
        viewModelScope.launch {
            socialUseCase.getUnreadMessages().collect { messages ->
                _uiState.value = _uiState.value.copy(unreadMessages = messages)
            }
        }
    }

    fun searchUsers(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearching = true)
            socialUseCase.searchUsers(query)
                .onSuccess { results ->
                    _uiState.value = _uiState.value.copy(searchResults = results, isSearching = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        error = e.message ?: "搜索失败"
                    )
                }
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            socialUseCase.sendFriendRequest(userId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "发送好友请求失败")
                }
        }
    }

    fun acceptRequest(friendshipId: String) {
        viewModelScope.launch {
            socialUseCase.acceptFriendRequest(friendshipId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "接受请求失败")
                }
        }
    }

    fun rejectRequest(friendshipId: String) {
        viewModelScope.launch {
            socialUseCase.rejectFriendRequest(friendshipId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "拒绝请求失败")
                }
        }
    }

    fun removeFriend(friendId: String) {
        viewModelScope.launch {
            socialUseCase.removeFriend(friendId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "删除好友失败")
                }
        }
    }

    fun filterPhrases(category: PhraseCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        viewModelScope.launch {
            socialUseCase.getPhrases(category).collect { phrases ->
                _uiState.value = _uiState.value.copy(phrases = phrases)
            }
        }
    }

    fun sendPhrase(friendId: String, phraseId: String) {
        viewModelScope.launch {
            socialUseCase.sendPhraseMessage(friendId, phraseId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message ?: "发送失败")
                }
        }
    }

    fun markMessageRead(messageId: String) {
        viewModelScope.launch {
            socialUseCase.markMessageRead(messageId)
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(searchQuery = "", searchResults = emptyList())
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
