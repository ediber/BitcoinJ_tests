package com.edi.mybitcoinj

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.common.base.Joiner
import kotlinx.android.synthetic.main.activity_main.*
import org.bitcoinj.core.Address
import org.bitcoinj.core.BlockChain
import org.bitcoinj.core.Coin
import org.bitcoinj.core.PeerGroup
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet
import org.bitcoinj.store.MemoryBlockStore
import org.bitcoinj.wallet.DeterministicSeed
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE




class MainActivity : AppCompatActivity() {

    private lateinit var addressA: Address
    private lateinit var addressB: Address
    private lateinit var addressC: Address

    private lateinit var restoredWallet: Wallet


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
        addressA = wallet.currentReceiveAddress()
        val keyB = wallet.currentReceiveKey()
        addressC = wallet.freshReceiveAddress()

        addressB = keyB.toAddress(wallet.params)

        addressesToViews(addressA, addressB, addressC)

        assert(addressB.equals(addressA))
        assert(!addressC.equals(addressA))

        showAddresses.setOnClickListener({
            addressesToViews(addressA, addressB, addressC)
        })

        refreshCAddress.setOnClickListener {
            addressC = wallet.freshReceiveAddress()
//            addressesToViews(addressA, addressB, addressC)
        }

        showSeed.setOnClickListener({
            val seed = wallet.keyChainSeed
            seedView.text = Joiner.on(" ").join(seed.mnemonicCode)
            seedDate.text = seed.creationTimeSeconds.toString()
        })

        restoreWalletFromSeed.setOnClickListener {
            val seedCode = "yard impulse luxury drive today throw farm pepper survey wreck glass federal"
            val creationtime = 1409478661L
            val seed = DeterministicSeed(seedCode, null, "", creationtime)
            restoredWallet = Wallet.fromSeed(params, seed)
// now sync the restored wallet as described below.
        }

        showRestoredWallet.setOnClickListener(View.OnClickListener {
            val address = restoredWallet.currentReceiveAddress()
//            addressesToViews(address, null, null)
        })

        showBalanceBtn.setOnClickListener {
            balanceTxt.text = wallet.balance.toString()
        }

        txtA.setOnClickListener {
            // copy value to load bitcoins to it
            copyToClipboard(txtA.text.toString())
        }

        txtB.setOnClickListener {
            copyToClipboard(txtB.text.toString())
        }

        txtC.setOnClickListener {
            copyToClipboard(txtC.text.toString())
        }

    }

    private fun copyToClipboard(text: String?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied to clipboard", text)
        clipboard.primaryClip = clip
    }

    private fun addressesToViews(
        addressA: Address,
        addressB: Address,
        addressC: Address
    ) {
        txtA.text = addressA.toString()
        txtB.text = addressB.toString()
        txtC.text = addressC.toString()
    }
}
