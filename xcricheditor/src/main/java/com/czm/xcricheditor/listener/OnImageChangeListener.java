package com.czm.xcricheditor.listener;

import com.facebook.imagepipeline.image.ImageInfo;

/**
 * Created by deejan on 12/4/17.
 */

public interface OnImageChangeListener {

  void onImageLoading(int percent);

  void onImageLoaded(ImageInfo imageInfo);

  void onImageLoadError();

}
