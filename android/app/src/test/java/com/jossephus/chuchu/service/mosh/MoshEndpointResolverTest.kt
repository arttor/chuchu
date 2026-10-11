package com.jossephus.chuchu.service.mosh

import java.net.InetAddress
import java.net.UnknownHostException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MoshEndpointResolverTest {
    @Test
    fun preservesNumericIpv4Literal() {
        assertEquals("100.72.23.13", MoshEndpointResolver.resolve("100.72.23.13"))
    }

    @Test
    fun resolvesDnsNameToIpv4Literal() {
        val address = InetAddress.getByName("100.72.23.13")

        val result =
            MoshEndpointResolver.resolve("solom-vps.tail36346.ts.net") { arrayOf(address) }

        assertEquals("100.72.23.13", result)
    }

    @Test
    fun choosesIpv4WhenIpv6AppearsFirst() {
        val ipv6 = InetAddress.getByName("2001:db8::13")
        val ipv4 = InetAddress.getByName("100.72.23.13")

        val result = MoshEndpointResolver.resolve("vps.example") { arrayOf(ipv6, ipv4) }

        assertEquals("100.72.23.13", result)
    }

    @Test
    fun rejectsIpv6OnlyHostWithClearReason() {
        val ipv6 = InetAddress.getByName("2001:db8::13")

        val error =
            try {
                MoshEndpointResolver.resolve("ipv6.example") { arrayOf(ipv6) }
                throw AssertionError("Expected IPv6-only resolution to fail")
            } catch (error: IllegalStateException) {
                error
            }

        assertTrue(error.message.orEmpty().contains("ipv6.example"))
        assertTrue(error.message.orEmpty().contains("IPv4"))
    }

    @Test
    fun namesHostWhenResolutionFails() {
        val error =
            try {
                MoshEndpointResolver.resolve("missing.example") {
                    throw UnknownHostException("missing.example")
                }
                throw AssertionError("Expected resolution to fail")
            } catch (error: IllegalStateException) {
                error
            }

        assertTrue(error.message.orEmpty().contains("missing.example"))
        assertTrue(error.message.orEmpty().contains("IPv4"))
    }

    @Test
    fun namesHostWhenLookupReturnsNoAddresses() {
        val error =
            try {
                MoshEndpointResolver.resolve("empty.example") { emptyArray() }
                throw AssertionError("Expected empty resolution to fail")
            } catch (error: IllegalStateException) {
                error
            }

        assertTrue(error.message.orEmpty().contains("empty.example"))
        assertTrue(error.message.orEmpty().contains("No IP addresses"))
    }
}
