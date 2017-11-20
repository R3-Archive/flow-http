package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.client.rpc.CordaRPCClient
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.InitiatingFlow
import net.corda.core.flows.StartableByRPC
import net.corda.core.messaging.startFlow
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.loggerFor
import okhttp3.OkHttpClient
import okhttp3.Request
import net.corda.core.utilities.NetworkHostAndPort.Companion.parse

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class Initiator : FlowLogic<String>() {

    override val progressTracker: ProgressTracker = ProgressTracker()

    @Suspendable
    override fun call(): String {
        val httpRequest = Request.Builder().url("https://www.corda.net/").build()

        // The request must be executed in a BLOCKING way. Flows don't
        // currently support suspending to await an HTTP call's response.
        val httpResponse = OkHttpClient().newCall(httpRequest).execute()

        return httpResponse.body().string()
    }
}

class TemplateClient {
    companion object {
        val logger = loggerFor<TemplateClient>()
    }

    fun main(args: Array<String>) {
        require(args.size == 1) { "Usage: TemplateClient <node address>" }
        val nodeAddress = parse(args[0])
        val client = CordaRPCClient(nodeAddress)

        // Can be amended in the build.gradle file.
        val proxy = client.start("user1", "test").proxy

        // Grab all existing TemplateStates and all future TemplateStates.
        val returnValue = proxy.startFlow(::Initiator).returnValue.get()

        logger.info(returnValue)
    }
}

/**
 * Demonstration of how to use the CordaRPCClient to connect to a Corda Node and
 * stream the contents of the node's vault.
 */
fun main(args: Array<String>) {
    TemplateClient().main(args)
}