package com.jossephus.chuchu.ui.screens.AddServer

import com.jossephus.chuchu.model.AuthMethod
import com.jossephus.chuchu.model.HostProfile
import com.jossephus.chuchu.model.MultiplexerType
import com.jossephus.chuchu.model.SshKey
import com.jossephus.chuchu.model.Transport
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AddServerFormCopyTest {
    @Test
    fun `copy form has new identity while preserving connection settings and key`() {
        val key =
            SshKey(
                id = 7L,
                name = "deployment",
                privateKeyPem = "private key",
                publicKeyOpenSsh = "public key",
                createdAtEpochMs = 123L,
            )
        val original =
            HostProfile(
                id = 42L,
                name = "production",
                host = "example.com",
                port = 2222,
                username = "deployer",
                password = "saved password",
                keyId = key.id,
                keyPassphrase = "passphrase",
                transport = Transport.SSH,
                authMethod = AuthMethod.KeyWithPassphrase,
                requireAuthOnConnect = true,
                postConnectCommand = "cd /srv/app",
                multiplexer = MultiplexerType.Zellij,
            )

        val form = original.toAddServerForm(key, isCopy = true)

        assertNull(form.id)
        assertEquals("production - copy", form.name)
        assertEquals(original.host, form.host)
        assertEquals(original.port.toString(), form.port)
        assertEquals(original.username, form.username)
        assertEquals(original.password, form.password)
        assertEquals(original.keyId, form.keyId)
        assertEquals(key.privateKeyPem, form.privateKeyPem)
        assertEquals(key.publicKeyOpenSsh, form.publicKeyOpenSsh)
        assertEquals(original.keyPassphrase, form.keyPassphrase)
        assertEquals(original.transport, form.transport)
        assertEquals(original.authMethod, form.authMethod)
        assertEquals(original.requireAuthOnConnect, form.requireAuthOnConnect)
        assertEquals(original.postConnectCommand, form.postConnectCommand)
        assertEquals(original.multiplexer, form.multiplexer)
        assertTrue(form.canSave())

        val saved = form.toHostProfile(isCopy = true)
        assertEquals(original.copy(id = 0L, name = "production - copy"), saved)
    }
}
