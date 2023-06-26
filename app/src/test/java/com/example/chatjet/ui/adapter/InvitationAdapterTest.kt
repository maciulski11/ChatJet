package com.example.chatjet.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.chatjet.R
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.chatjet.models.data.InvitationReceived
import com.example.chatjet.models.data.User
import com.example.chatjet.services.repository.FirebaseRepository
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class InvitationAdapterTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockOnAccept: (String) -> Unit

    @Mock
    private lateinit var mockOnDelete: (String) -> Unit

    @Mock
    private lateinit var mockLayoutInflater: LayoutInflater

    @Mock
    private lateinit var mockParent: ViewGroup

    @Mock
    private lateinit var mockView: View

    @Mock
    private lateinit var mockInvitationReceived: InvitationReceived

    private lateinit var invitationAdapter: InvitationAdapter

    @Before
    fun setup() {
        invitationAdapter = InvitationAdapter(
            ArrayList(),
            mockContext,
            mockOnAccept,
            mockOnDelete
        )
    }

        @Test
        fun onCreateViewHolder_shouldInflateItemView() {
            // Symuluj zwracanie atrapy LayoutInflater przez Context
            `when`(mockContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .thenReturn(mockLayoutInflater)

            // Symuluj wywołanie inflate dla LayoutInflater z odpowiednimi argumentami
            `when`(mockLayoutInflater.inflate(R.layout.item_ivitation, mockParent, false))
                .thenReturn(mockView)

            // Wywołaj metodę onCreateViewHolder na InvitationAdapter
            val viewHolder = invitationAdapter.onCreateViewHolder(mockParent, 0)

            // Sprawdź, czy zwrócony itemView jest równy atrapie widoku (mockView)
            assertEquals(viewHolder.itemView, mockView)
        }

    @Test
    fun onBindViewHolder_shouldBindInvitation() {
        // Przygotuj atrapę ViewHoldera oraz atrapy widoków wewnątrz ViewHoldera
        val mockViewHolder = invitationAdapter.MyViewHolder(mockView)
        val mockPhoto = mock(ImageView::class.java)
        val mockNameUser = mock(TextView::class.java)
        val mockLocation = mock(TextView::class.java)
        val mockAcceptButton = mock(ImageButton::class.java)
        val mockUnacceptedButton = mock(ImageButton::class.java)

        // Symuluj wywołanie findViewById dla poszczególnych widoków
        `when`(mockView.findViewById<ImageView>(R.id.photo)).thenReturn(mockPhoto)
        `when`(mockView.findViewById<TextView>(R.id.nameUser)).thenReturn(mockNameUser)
        `when`(mockView.findViewById<TextView>(R.id.locationTV)).thenReturn(mockLocation)
        `when`(mockView.findViewById<ImageButton>(R.id.acceptButton)).thenReturn(mockAcceptButton)
        `when`(mockView.findViewById<ImageButton>(R.id.unacceptedButton)).thenReturn(mockUnacceptedButton)

        // Przygotuj dane testowe
        val mockUid = "mock_uid"
        val mockUser = User("John Doe", "New York", "https://example.com/photo.jpg")
        val mockFirebaseRepository = mock(FirebaseRepository::class.java)

        // Symuluj zachowanie fetchFriends z FirebaseRepository
        `when`(mockInvitationReceived.uid).thenReturn(mockUid)
        doAnswer { invocation ->
            val callback = invocation.getArgument<(User?) -> Unit>(1)
            callback.invoke(mockUser)
        }.`when`(mockFirebaseRepository).fetchFriends(eq(mockUid), any())


        // Dodaj zaproszenie do listy w InvitationAdapter
        invitationAdapter.invitationsList.add(mockInvitationReceived)

        // Wywołaj metodę bind na ViewHolderze
        mockViewHolder.bind(mockInvitationReceived)

        // Sprawdź, czy nazwa użytkownika i lokalizacja zostały poprawnie ustawione
        assertEquals(mockNameUser.text, mockUser.full_name)
        assertEquals(mockLocation.text, mockUser.location)

        // TODO: Przetestuj ładowanie obrazu za pomocą Glide

        // TODO: Przetestuj nasłuchiwanie kliknięć przycisków
    }

    // TODO: Dodaj testy dla getItemCount i innych funkcji

}
