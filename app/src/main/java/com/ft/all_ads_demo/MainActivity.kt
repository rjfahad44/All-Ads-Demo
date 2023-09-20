package com.ft.all_ads_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.ft.all_ads_demo.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mInterstitialAd: InterstitialAd? = null
    private final val TAG = "MainActivity"
    private lateinit var adRequest : AdRequest
    private lateinit var adLoader: AdLoader
    private var rewardedAd: RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MobileAds.initialize(this) {}
        //addTestDeviceId()
        initialize()
    }

    private fun addTestDeviceId() {
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(
            listOf(
                "6DC9062A205771E36F91A03DEDC3D1D6",
                "16BA09A6038CD5475886D3F43E381347",
                "11AD95B86DAA27616E55E22BE16BF0F1"
            )
        ).build()
        MobileAds.setRequestConfiguration(configuration)
    }

    private fun initInterstitialAd() {
        binding.progressBar.isVisible = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad loaded")
                binding.progressBar.isVisible = false
                mInterstitialAd = interstitialAd
                mInterstitialAd?.show(this@MainActivity)
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    private fun initialize() {
        binding.bannerBtn.setOnClickListener {
            bannerAds()
        }

        binding.interstitialBtn.setOnClickListener {
            initInterstitialAd()
        }

        binding.nativeBtn.setOnClickListener {
            initNativeAd()
        }

        binding.rewardAd.setOnClickListener {
            initRewardAd()
        }
    }

    private fun bannerAds() {
        binding.progressBar.isVisible = true
        adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun initRewardAd() {
        binding.progressBar.isVisible = true
        adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.toString())
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                binding.progressBar.isVisible = false
                rewardedAd = ad
                showRewordAds()
            }
        })

        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }
    }

    private fun showRewordAds() {
        rewardedAd?.let { ad ->
            ad.show(this) { rewardItem ->
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d(TAG, "rewardAmount : \$${rewardAmount},  rewardType : \$${rewardType}")
            }
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    private fun initNativeAd() {
        binding.progressBar.isVisible = true
        binding.nativeAd.isVisible = true
        adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd {
                binding.progressBar.isVisible = false
                binding.nativeAd.setNativeAd(it)
                Log.e("","")
            }.build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    public override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }
    public override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }
    public override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }
}