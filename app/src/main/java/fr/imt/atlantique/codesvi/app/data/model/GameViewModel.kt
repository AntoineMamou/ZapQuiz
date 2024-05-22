package fr.imt.atlantique.codesvi.app.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserId
import fr.imt.atlantique.codesvi.app.ui.screens.game.getUserInfoDatabase
import fr.imt.atlantique.codesvi.app.ui.screens.game.user
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    var userLoad = false

    fun loadUser(username: String?) {
        viewModelScope.launch {
            username?.let {
                val user = getUserInfoDatabase(it)
                _userState.value = user
                userLoad = true
            }
        }
    }

    suspend fun loadUserAndUpdate(username: String?,trophies: Int? = _userState.value?.trophies,
                                  playerIcon: String? = _userState.value?.playerIcon,
                                  title: String? = _userState.value?.title,
                                  connectionState: Boolean? = _userState.value?.connectionState,
                                  victory: Int? = _userState.value?.victory,
                                  game_played: Int? = _userState.value?.game_played,
                                  peak_trophy: Int? = _userState.value?.peak_trophy,
                                  favorite_category: String? = _userState.value?.favorite_category,
                                  money: Int? = _userState.value?.money,
                                  newAvailableIcon: String? = null,
                                  newAvailableTitle: String? = null,){
        viewModelScope.launch {
            username?.let {
                val user = getUserInfoDatabase(it)
                _userState.value = user
                userLoad = true
                if (trophies != null) {
                    if (money != null) {
                        changeAnyAtomicV2(trophies + _userState.value?.trophies!!, playerIcon, title, connectionState, victory, game_played, peak_trophy, favorite_category, money + _userState.value!!.money,newAvailableIcon,newAvailableTitle)
                    }
                }
            }
        }
    }

    fun changeAnyAtomicV2(
        trophies: Int? = _userState.value?.trophies,
        playerIcon: String? = _userState.value?.playerIcon,
        title: String? = _userState.value?.title,
        connectionState: Boolean? = _userState.value?.connectionState,
        victory: Int? = _userState.value?.victory,
        game_played: Int? = _userState.value?.game_played,
        peak_trophy: Int? = _userState.value?.peak_trophy,
        favorite_category: String? = _userState.value?.favorite_category,
        money: Int? = _userState.value?.money,
        newAvailableIcon: String? = null,
        newAvailableTitle: String? = null,
    ) {
        getUserId(user!!.username) { userId ->
            val database =
                FirebaseDatabase.getInstance("https://zapquiz-dbfb8-default-rtdb.europe-west1.firebasedatabase.app/")
            val usersRef = database.getReference("utilisateurs/$userId")

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var existingUser = dataSnapshot.getValue(User::class.java)

                    if (existingUser != null) {
                        var isUpdated = false

                        if (existingUser.playerIcon != playerIcon) {
                            if (playerIcon != null) {
                                existingUser.playerIcon = playerIcon
                                isUpdated = true
                            }
                        }

                        if (existingUser.title != title) {
                            if (title != null) {
                                existingUser.title = title
                                isUpdated = true
                            }
                        }

                        if (existingUser.victory != victory) {
                            if (victory != null) {
                                existingUser.victory = victory
                                isUpdated = true
                            }
                        }

                        if (existingUser.game_played != game_played) {
                            if (game_played != null) {
                                existingUser.game_played = game_played
                                isUpdated = true
                            }
                        }

                        if (existingUser.peak_trophy != peak_trophy) {
                            if (peak_trophy != null) {
                                existingUser.peak_trophy = peak_trophy
                                isUpdated = true
                            }
                        }

                        if (existingUser.favorite_category != favorite_category) {
                            if (favorite_category != null) {
                                existingUser.favorite_category = favorite_category
                                isUpdated = true
                            }
                        }

                        if (existingUser.money != money) {
                            if (money != null) {
                                existingUser.money = money
                                isUpdated = true
                            }
                        }

                        if (existingUser.connectionState != connectionState) {
                            if (connectionState != null) {
                                existingUser.connectionState = connectionState
                                isUpdated = true
                            }
                        }

                        if (existingUser.trophies != trophies) {
                            if (trophies != null) {
                                if(trophies>0) {
                                    existingUser.trophies = trophies
                                }
                                else{
                                    existingUser.trophies = 0
                                }
                                isUpdated = true
                            }
                        }

                        if (newAvailableIcon != null && !existingUser.availableIcons.contains(newAvailableIcon)) {
                            existingUser.availableIcons =
                                existingUser.availableIcons + newAvailableIcon
                            isUpdated = true
                        }

                        if (newAvailableTitle != null && !existingUser.availableTitles.contains(newAvailableTitle)) {
                            existingUser.availableTitles =
                                existingUser.availableTitles + newAvailableTitle
                            isUpdated = true
                        }

                        if (isUpdated) {
                            usersRef.setValue(existingUser)
                                .addOnSuccessListener {
                                    // Update the user state only if Firebase update was successful
                                    _userState.value = existingUser
                                }
                                .addOnFailureListener { e ->
                                    println("Error updating user: $e")
                                }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("Error fetching user: $databaseError")
                }
            })
        }
    }
}