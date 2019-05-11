package com.edi.mybitcoinj

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var addressA: Address
    private lateinit var addressB: Address
    private lateinit var addressC: Address

    private lateinit var restoredWallet: Wallet
    private lateinit var params: TestNet3Params
    private lateinit var wallet: Wallet
    private lateinit var peerGroup: PeerGroup

    private lateinit var _this: MainActivity




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _this = this

        params = TestNet3Params.get()
        val blockStore = MemoryBlockStore(params)

//        wallet = Wallet(params)
        val walletStorage = WalletStorage(params, this)
        wallet = walletStorage.getWallet()

        val chain = BlockChain(params, wallet, blockStore)
        peerGroup = PeerGroup(params, chain)
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

        freshRecieveAddress.setOnClickListener {
            addressC = wallet.freshReceiveAddress()
//            addressesToViews(addressA, addressB, addressC)
        }

        currentReceiveAddress.setOnClickListener(View.OnClickListener {
            addressA = wallet.currentReceiveAddress()
        })

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

        showRestoredWallet.setOnClickListener {
            val address = restoredWallet.currentReceiveAddress()
            addressesToViews(address, null, null)
        }

        // TODO
        showBalanceBtn.setOnClickListener {
//            balanceTxt.text = wallet.balance.toString()
//            balanceTxt.text = wallet.watchedBalance.toString()


            wallet.walletTransactions
            wallet.watchedBalance.toString()

/*            val pubKey = wallet.activeKeyChain.issuedReceiveKeys
            val iterator = pubKey.iterator()
            var i = 0
            var balance: Double = 0.0

            while (iterator.hasNext()) {

                val address = iterator.next().toAddress(params)

                val listener = object: HttpHelper.Companion.LoadPriceListener{
                    override fun onFailure() {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onResponse(price: String) {
                        balance += price.toDouble()
                        if(i == pubKey.size - 1){
                            updateUIOnUITread(_this.balanceTxt, balance.toString())
                        }
                        i++
                    }
                }
                loadPrice(listener, address.toString())
            }*/

        }

        currentRecieveTxt1.setOnClickListener {
            // copy value to load bitcoins to it
            copyToClipboard(currentRecieveTxt1.text.toString())
        }

        currentRecieveTxt2.setOnClickListener {
            copyToClipboard(currentRecieveTxt2.text.toString())
        }

        freshRecieveTxt.setOnClickListener {
            copyToClipboard(freshRecieveTxt.text.toString())
        }

        sendBitcoin.setOnClickListener { send1Bitcoin() }

    }

    private fun updateUIOnUITread(txtView: TextView?, balance: String) {
        runOnUiThread {
            if (txtView != null) {
                txtView.text = balance
            }
        }
    }

    private fun copyToClipboard(text: String?) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("copied to clipboard", text)
        clipboard.primaryClip = clip
    }

    private fun addressesToViews(
        addressA: Address?,
        addressB: Address?,
        addressC: Address?
    ) {
        currentRecieveTxt1.text = addressA.toString()
        currentRecieveTxt2.text = addressB?.toString()
        freshRecieveTxt.text = addressC?.toString()
    }

    private fun send1Bitcoin() {
// Get the address 1RbxbA1yP2Lebauuef3cBiBho853f7jxs in object form.
        val targetAddress = Address(params, "1RbxbA1yP2Lebauuef3cBiBho853f7jxs")
// Do the send of 1 BTC in the background. This could throw InsufficientMoneyException.
        val result = wallet . sendCoins (peerGroup, targetAddress, Coin.COIN)
// Save the wallet to disk, optional if using auto saving (see below).
//        wallet.saveToFile(....);
// Wait for the transaction to propagate across the P2P network, indicating acceptance.
        result.broadcastComplete.get()
    }


}
