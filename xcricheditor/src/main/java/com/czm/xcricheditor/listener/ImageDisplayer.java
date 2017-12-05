package com.czm.xcricheditor.listener;

import android.net.Uri;

import com.facebook.imagepipeline.image.ImageInfo;


public interface ImageDisplayer {

    Uri getUri();

    void setUri(Uri uri);

    boolean getWebPEnabled();

    void setWebPEnabled(boolean value);

    void setOnImageChangeListener(OnImageChangeListener onImageChangeListener);

}
