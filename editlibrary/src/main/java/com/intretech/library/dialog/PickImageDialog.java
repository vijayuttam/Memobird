package com.intretech.library.dialog;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import com.intretech.library.bundle.PickSetup;
import com.intretech.library.keep.Keep;
import com.intretech.library.listeners.IPickClick;
import com.intretech.library.listeners.IPickResult;
import com.intretech.library.resolver.IntentResolver;

import static android.app.Activity.RESULT_OK;

public class PickImageDialog extends PickImageBaseDialog {

    public static PickImageDialog newInstance(PickSetup setup) {
        PickImageDialog frag = new PickImageDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SETUP_TAG, setup);
        frag.setArguments(bundle);
        return frag;
    }

    public static PickImageDialog build(PickSetup setup, IPickResult pickResult) {
        PickImageDialog d = PickImageDialog.newInstance(setup);
        d.setOnPickResult(pickResult);
        return d;
    }

    public static PickImageDialog build(IPickResult pickResult) {
        return build(new PickSetup(), pickResult);
    }

    public static PickImageDialog build(PickSetup setup) {
        return build(setup, null);
    }

    public static PickImageDialog build() {
        return build();
    }

    public PickImageDialog show(FragmentActivity fragmentActivity) {
        return show(fragmentActivity.getSupportFragmentManager());
    }

    public PickImageDialog show(FragmentManager fragmentManager) {
        super.show(fragmentManager, DIALOG_FRAGMENT_TAG);

        return this;
    }

    @Override
    public void onCameraClick() {
        launchCamera();
    }

    @Override
    public void onGalleryClick() {
        launchGallery();
    }

    @Override
    public PickImageDialog setOnClick(IPickClick onClick) {
        return (PickImageDialog) super.setOnClick(onClick);
    }

    @Override
    public PickImageDialog setOnPickResult(IPickResult onPickResult) {
        return (PickImageDialog) super.setOnPickResult(onPickResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentResolver.REQUESTER) {
            if (resultCode == RESULT_OK) {
                //Show progress
                showProgress(true);

                //Handle the image result async
                getAsyncResult().execute(data);
            } else {
                dismissAllowingStateLoss();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == IntentResolver.REQUESTER) {
            boolean granted = true;

            for (Integer i : grantResults)
                granted = granted && i == PackageManager.PERMISSION_GRANTED;

            if (granted) {
                if (!launchSystemDialog()) {
                    if (grantResults.length == 1) {
                        launchGallery();
                    } else {
                        launchCamera();
                    }
                }
            } else {
                dismissAllowingStateLoss();

                if (grantResults.length > 1)
                    Keep.with(getActivity()).askedForPermission();
            }


        }
    }


}


