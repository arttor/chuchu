package com.jossephus.chuchu.service.mosh

import java.net.Inet4Address
import java.net.InetAddress
import java.net.UnknownHostException

/** Resolves profile host names to the numeric literal required by the Mosh UDP client. */
object MoshEndpointResolver {

    fun resolve(host: String): String = resolve(host, InetAddress::getAllByName)

    internal fun resolve(
        host: String,
        lookup: (String) -> Array<InetAddress>,
    ): String {
        val addresses =
            try {
                lookup(host)
            } catch (error: UnknownHostException) {
                throw IllegalStateException(
                    "Could not resolve Mosh host '$host' to an IPv4 address",
                    error,
                )
            }

        check(addresses.isNotEmpty()) { "No IP addresses found for Mosh host '$host'" }
        val address = addresses.filterIsInstance<Inet4Address>().firstOrNull()
            ?: throw IllegalStateException(
                "Mosh host '$host' has no IPv4 address; the Mosh client does not support IPv6",
            )
        return address.hostAddress
    }
}
