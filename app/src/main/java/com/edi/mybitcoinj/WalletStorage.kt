package com.edi.mybitcoinj

import android.Manifest
import android.app.Activity
import android.os.Environment
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet
import java.io.*
import android.content.ContentValues.TAG
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.PermissionChecker.checkSelfPermission
import android.util.Log
import org.bitcoinj.wallet.Wallet.loadFromFileStream

class WalletStorage(params: TestNet3Params, activity: Activity) {

    private val params = params
    private val activity = activity

    fun getWallet(): Wallet {
        var wallet: Wallet?
        wallet = null

        if (isStoragePermissionGranted()) {
            wallet = loadData()
        }

        if (wallet != null) {
            return wallet!!
        }

        wallet = Wallet(params)
        if (isStoragePermissionGranted()) {
            saveData(wallet)
        }

        return wallet
    }

    private fun loadData(): Wallet? {

        var wallet: Wallet?
        wallet = null
        try{
            val inFile = File(Environment.getExternalStorageDirectory(), "appSaveState.data")
            wallet = loadFromFileStream(FileInputStream(inFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return wallet
    }

    private fun saveData(wallet: Wallet) {

        try {
            val outFile = File(Environment.getExternalStorageDirectory(), "appSaveState.data")
            wallet.saveToFileStream(FileOutputStream(outFile))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) === PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted")
                return true
            } else {

                Log.v(TAG, "Permission is revoked")
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted")
            return true
        }
    }

}
