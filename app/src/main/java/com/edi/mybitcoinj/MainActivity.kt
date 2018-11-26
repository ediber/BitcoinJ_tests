package com.edi.mybitcoinj

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val params = TestNet3Params.get()

        val wallet = Wallet(params)
        val chain = BlockChain(params, wallet, null)
        val peerGroup = PeerGroup(params, chain)
        peerGroup.addWallet(wallet)
//        peerGroup.startAndWait()
        peerGroup.start()

    }
}
