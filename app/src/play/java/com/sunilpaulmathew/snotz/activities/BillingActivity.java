package com.sunilpaulmathew.snotz.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.sunilpaulmathew.snotz.R;
import com.sunilpaulmathew.snotz.adapters.BillingAdapter;
import com.sunilpaulmathew.snotz.utils.BillingItems;
import com.sunilpaulmathew.snotz.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import in.sunilpaulmathew.sCommon.Utils.sUtils;

/*
 * Created by sunilpaulmathew <sunil.kde@gmail.com> on November 14, 2020
 */
public class BillingActivity extends AppCompatActivity {

    private final ArrayList <BillingItems> mData = new ArrayList<>();
    private BillingClient mBillingClient;
    private boolean mClientInitialized = false;
    private final List<String> mSkuList = new ArrayList<>();

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        AppCompatImageButton mBack = findViewById(R.id.back_button);
        MaterialTextView mCancel = findViewById(R.id.cancel_button);

        mData.add(new BillingItems(getString(R.string.donation_app), sUtils.getDrawable(R.drawable.ic_donation_app, this)));
        mData.add(new BillingItems(getString(R.string.support_coffee), sUtils.getDrawable(R.drawable.ic_coffee, this)));
        mData.add(new BillingItems(getString(R.string.support_meal), sUtils.getDrawable(R.drawable.ic_meal, this)));
        mData.add(new BillingItems(getString(R.string.support_dinner), sUtils.getDrawable(R.drawable.ic_dinner, this)));

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        BillingAdapter mRecycleViewAdapter = new BillingAdapter(mData);
        mRecyclerView.setAdapter(mRecycleViewAdapter);
        mRecyclerView.setVisibility(View.VISIBLE);

        mRecycleViewAdapter.setOnItemClickListener((position, v) -> {
            if (position == 0) {
                buyDonationApp();
            } else if (position == 1) {
                buyMeACoffee();
            } else if (position == 2) {
                buyMeAMeal();
            } else if (position == 3) {
                buyMeADinner();
            }
        });

        mBack.setOnClickListener(v -> super.onBackPressed());
        mCancel.setOnClickListener(v -> super.onBackPressed());

        mBillingClient = BillingClient.newBuilder(BillingActivity.this).enablePendingPurchases().setListener((billingResult, list) -> {
            if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (Purchase purchase : list) {
                    handlePurchases(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.support_retry_message)).show();
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.support_already_received_message)).show();
            }
        }).build();

        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    mClientInitialized = true;
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.billing_client_disconnected)).show();
            }
        });
    }

    private void buyDonationApp() {
        if (!Utils.isNotDonated(this)) {
            sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.support_already_received_message)).show();
            return;
        }
        sUtils.launchUrl("https://play.google.com/store/apps/details?id=com.smartpack.donate",this);
    }

    private void buyMeACoffee() {
        if (!mClientInitialized) {
            sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.billing_client_disconnected)).show();
            return;
        }
        mSkuList.clear();
        mSkuList.add("donation_coffee");
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(mSkuList).setType(BillingClient.SkuType.INAPP);

        mBillingClient.querySkuDetailsAsync(params.build(), (billingResult, list) -> {
            if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (final SkuDetails skuDetails : list) {

                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();

                    mBillingClient.launchBillingFlow(BillingActivity.this, flowParams);

                }
            }
        });
    }

    private void buyMeADinner() {
        if (!mClientInitialized) {
            sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.billing_client_disconnected)).show();
            return;
        }
        mSkuList.clear();
        mSkuList.add("donation_dinner");
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(mSkuList).setType(BillingClient.SkuType.INAPP);

        mBillingClient.querySkuDetailsAsync(params.build(), (billingResult, list) -> {
            if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (final SkuDetails skuDetails : list) {

                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();

                    mBillingClient.launchBillingFlow(BillingActivity.this, flowParams);

                }
            }
        });
    }

    private void buyMeAMeal() {
        if (!mClientInitialized) {
            sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.billing_client_disconnected)).show();
            return;
        }
        mSkuList.clear();
        mSkuList.add("donation_meal");
        final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(mSkuList).setType(BillingClient.SkuType.INAPP);

        mBillingClient.querySkuDetailsAsync(params.build(), (billingResult, list) -> {
            if (list != null && billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                for (final SkuDetails skuDetails : list) {

                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();

                    mBillingClient.launchBillingFlow(BillingActivity.this, flowParams);

                }
            }
        });
    }

    private void handlePurchases(Purchase purchase) {
        try {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (purchase.getSkus().contains("donation_coffee") || purchase.getSkus().contains("donation_meal") || purchase.getSkus().contains("donation_dinner")) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

                    ConsumeResponseListener mConsumeResponseListener = (billingResult, s) -> sUtils.snackBar(findViewById(android.R.id.content), getString(R.string.support_acknowledged)).show();

                    mBillingClient.consumeAsync(consumeParams, mConsumeResponseListener);
                    new MaterialAlertDialogBuilder(this)
                            .setMessage(getString(R.string.support_received_message))
                            .setPositiveButton(getString(R.string.cancel), (dialogInterface, i) -> {
                            })
                            .show();
                }
            }
        } catch (Exception ignored) {}
    }

}