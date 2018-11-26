package com.edi.mybitcoinj

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.store.MemoryBlockStore


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val params = TestNet3Params.get()
        val blockStore = MemoryBlockStore(params)


        val wallet = Wallet(params)
        val chain = BlockChain(params, wallet, blockStore)
        val peerGroup = PeerGroup(params, chain)
        peerGroup.addWallet(wallet)
//        peerGroup.startAndWait()
        peerGroup.start()

        //obtain keys and addresses from the wallet with the following API calls:
        val addressA = wallet.currentReceiveAddress()
        val keyB = wallet.currentReceiveKey()
        val addressC = wallet.freshReceiveAddress()

        assert(keyB.toAddress(wallet.params).equals(addressA))
        assert(!addressC.equals(addressA))

    }
}
