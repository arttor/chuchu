package com.jossephus.chuchu.service.terminal

import com.jossephus.chuchu.model.AuthMethod
import com.jossephus.chuchu.model.HostProfile
import com.jossephus.chuchu.model.MultiplexerType
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TabSpecSavedProfileTest {
    private val saved = TabSpec.fromHostProfile(
        HostProfile(
            id = 1,
            name = "server",
            host = "first.example",
            username = "alice",
            authMethod = AuthMethod.KeyWithPassphrase,
            keyPassphrase = "saved passphrase",
            multiplexer = MultiplexerType.Tmux,
        ),
        publicKeyOpenSsh = "public key",
        privateKeyPem = "private key",
    )

    @Test
    fun runtimeSessionNameAndPromptedPassphraseDoNotMakeSavedProfileStale() {
        val running = saved.copy(
            keyPassphrase = "prompted passphrase",
            multiplexerSessionName = "chuchu-2",
            multiplexerCreateIfMissing = false,
        )

        assertTrue(running.matchesSavedProfile(saved))
    }

    @Test
    fun editedEndpointAndKeyMaterialRequireFreshSettings() {
        assertFalse(saved.matchesSavedProfile(saved.copy(host = "second.example")))
        assertFalse(saved.matchesSavedProfile(saved.copy(privateKeyPem = "replacement key")))
        assertFalse(saved.matchesSavedProfile(saved.copy(keyPassphrase = "replacement passphrase", savedKeyPassphrase = "replacement passphrase")))
    }
}
