package org.monora.uprotocol.client.android.protocol

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import org.monora.uprotocol.core.ClientLoader
import org.monora.uprotocol.core.persistence.PersistenceProvider
import org.monora.uprotocol.core.protocol.ConnectionFactory
import org.monora.uprotocol.core.spec.v1.Config
import org.monora.uprotocol.core.spec.v1.Config.SERVICE_UPROTOCOL_DNS_SD
import javax.inject.Inject
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
@Singleton
class NsdDaemon @Inject constructor(
    @ApplicationContext val context: Context,
    val connectionFactory: ConnectionFactory,
    val persistenceProvider: PersistenceProvider,
) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager

    private var discoveryListener: DiscoveryListener? = DiscoveryListener()

    private var registrationListener: RegistrationListener? = RegistrationListener()

    fun registerService() {
        val localServiceInfo = NsdServiceInfo()
        localServiceInfo.serviceName = persistenceProvider.clientNickname
        localServiceInfo.serviceType = SERVICE_UPROTOCOL_DNS_SD
        localServiceInfo.port = Config.PORT_UPROTOCOL
        try {
            nsdManager.registerService(localServiceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun startDiscovering() {
        try {
            nsdManager.discoverServices(SERVICE_UPROTOCOL_DNS_SD, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (ignored: Exception) {
        }
    }

    fun stopDiscovering() {
        try {
            if (discoveryListener != null)
                nsdManager.stopServiceDiscovery(discoveryListener)
        } catch (ignored: Exception) {
        }
    }

    fun unregisterService() {
        try {
            if (registrationListener != null)
                nsdManager.unregisterService(registrationListener)
        } catch (ignored: Exception) {
        }
    }

    private inner class RegistrationListener : NsdManager.RegistrationListener {
        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e(TAG, "Failed to register self service with error code $errorCode")
            clear()
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e(TAG, "Failed to unregister self service with error code $errorCode")
        }

        override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
            Log.v(TAG, "Self service is now registered: " + serviceInfo.serviceName)
        }

        override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) {
            Log.i(TAG, "Self service is unregistered: " + serviceInfo.serviceName)
            clear()
        }

        fun clear() {
            registrationListener = null
        }
    }

    private inner class DiscoveryListener : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "NSD discovery failed to start with error code $errorCode")
            clear()
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "NSD discovery failed to stop with error code $errorCode")
        }

        override fun onDiscoveryStarted(serviceType: String) {
            Log.v(TAG, "NSD discovery started")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.v(TAG, "NSD discovery stopped")
            clear()
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Log.v(TAG, "'" + serviceInfo.serviceName + "' service has been discovered.")
            nsdManager.resolveService(serviceInfo, ResolveListener())
        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.i(TAG, "'" + serviceInfo.serviceName + "' service is now lost.")
        }

        fun clear() {
            discoveryListener = null
        }
    }

    private inner class ResolveListener : NsdManager.ResolveListener {
        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            Log.e(TAG, "Could not resolve " + serviceInfo.serviceName)
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.v(TAG, "Resolved service " + serviceInfo.serviceName + " on " + serviceInfo.host.hostAddress)

            val thread = Thread {
                try {
                    val client = ClientLoader.load(connectionFactory, persistenceProvider, serviceInfo.host)
                    Log.v(TAG, "Resolved client " + client.clientNickname + " (" + client.clientUid + ")")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
    }

    companion object {
        private val TAG = NsdDaemon::class.simpleName
    }
}