package com.edi.mybitcoinj

import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.json.JSONObject
import java.io.IOException


class HttpHelper {



    // its like static in java
    companion object{

        public interface LoadPriceListener{
            fun onFailure()
            fun onResponse(price: String)
        }

         fun loadPrice(listener: LoadPriceListener, address: String) {
//             val BPI_ENDPOINT = "https://chain.so/api/v2/get_address_balance/BTCTEST/n3HKEVjFQzod5xTVV2Q5q7xaKRw1ju2wph/1"
             val url = "https://chain.so/api/v2/get_address_balance/BTCTEST/$address/1"
             val okHttpClient = OkHttpClient()


             val request: Request = Request.Builder().url(url).build()

            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(request: Request?, e: IOException?) {
                    listener.onFailure()
                }

                override fun onResponse(response: Response?) {
                    val json = response?.body()?.string()
                    val price = JSONObject(json).getJSONObject("data")["confirmed_balance"] as String

                    listener.onResponse(price)
                }

            })
        }
    }

}