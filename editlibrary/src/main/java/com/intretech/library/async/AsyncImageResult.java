package com.intretech.library.async;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import com.intretech.library.R;
import com.intretech.library.bean.PickResult;
import com.intretech.library.bundle.PickSetup;
import com.intretech.library.enums.EPickType;
import com.intretech.library.img.ImageHandler;
import com.intretech.library.resolver.IntentResolver;
import java.lang.ref.WeakReference;

public class AsyncImageResult extends AsyncTask<Intent, Void, PickResult> {

    private WeakReference<IntentResolver> weakIntentResolver;
    private WeakReference<PickSetup> weakSetup;
    private OnFinish onFinish;

    public AsyncImageResult(Activity activity, PickSetup setup) {
        this.weakIntentResolver = new WeakReference<IntentResolver>(new IntentResolver(activity, setup));
        this.weakSetup = new WeakReference<PickSetup>(setup);
    }

    public AsyncImageResult setOnFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    @Override
    protected PickResult doInBackground(Intent... intents) {

        //Create a PickResult instance
        PickResult result = new PickResult();

        IntentResolver resolver = weakIntentResolver.get();

        if (resolver == null) {
            result.setError(new Error(resolver.getActivity().getString(R.string.activity_destroyed)));
            return result;
        }

        try {
            //Get the data intent from onActivityResult()
            Intent data = intents[0];

            //Define if it was pick from camera
            boolean fromCamera = resolver.fromCamera(data);

            //Instance of a helper class
            ImageHandler imageHandler = ImageHandler
                    .with(resolver.getActivity()).setup(weakSetup.get())
                    .provider(fromCamera ? EPickType.CAMERA : EPickType.GALLERY)
                    .uri(fromCamera ? resolver.cameraUri() : data.getData());

            //Setting uri and path for result
            result.setUri(imageHandler.getUri())
                    .setPath(imageHandler.getUriPath())
                    .setBitmap(imageHandler.decode());


            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(e);
            return result;
        }
    }


    @Override
    protected void onPostExecute(PickResult r) {
        if (onFinish != null)
            onFinish.onFinish(r);
    }

    public interface OnFinish {
        void onFinish(PickResult pickResult);
    }

}
